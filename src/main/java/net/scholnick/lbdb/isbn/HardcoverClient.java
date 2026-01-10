package net.scholnick.lbdb.isbn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;

/**
 * HardcoverClient - Client for Hardcover API
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@Component
public class HardcoverClient implements BookProvider {
    private final RestClient restClient;

    @Autowired
    public HardcoverClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Book search(String isbn) {
        // 978-1668057551

        String json = restClient.post()
            .uri("https://api.hardcover.app/v1/graphql")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + System.getenv("HARDCOVER_TOKEN"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(Map.of("query", QUERY, "variables", Map.of("isbn", isbn)))
            .retrieve()
            .body(String.class);

        if (json == null || json.isBlank()) return null;

        List<Edition> editions = JSONUtilities.fromJSON(json,OutsideWrapper.class).data().editions();
        if (NullSafe.isEmpty(editions)) return null;

        Edition edition = editions.getFirst();
        if (edition == null) return null;

        return new Book()
            .setIsbn(NullSafe.isEmpty(edition.isbn_13) ? edition.isbn_10() : edition.isbn_13())
            .setTitle(edition.book().title())
            .setAuthors(NullSafe.stream(edition.book.contributions).map(Contribution::author).map(HCAuthor::name).map(Author::of).toList())
            .setPublishedYear(Book.parseYear(edition.book.release_date))
        ;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record OutsideWrapper(Data data) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record Data(List<Edition> editions) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    public record Edition(String isbn_13, String isbn_10, HCBook book) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    public record HCBook(String title, String release_date, List<Contribution> contributions) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    public record Contribution(String contribution, HCAuthor author) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    public record HCAuthor(String name) {}

    private static final String QUERY = """
        query SearchByIsbn($isbn: String!) {
           editions(
             where: {
               _or: [
                 { isbn_13: { _eq: $isbn } }
                 { isbn_10: { _eq: $isbn } }
               ]
             }
             limit: 10
           ) {
             id
             isbn_13
             isbn_10
             book {
               id
               title
               slug
               description
               release_date
               cached_image
               contributions {
                 contribution
                 author { id name slug }
               }
             }
           }
         }
      """;
}
