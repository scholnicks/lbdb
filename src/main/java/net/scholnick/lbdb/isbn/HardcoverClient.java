package net.scholnick.lbdb.isbn;

import net.scholnick.lbdb.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

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
        Map<String, Object> body = Map.of("query", QUERY, "variables", Map.of("isbn", isbn));

        // 978-1668057551

        String json = restClient.post()
            .uri("https://api.hardcover.app/v1/graphql")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + System.getenv("HARDCOVER_TOKEN"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .body(String.class);

        if (json == null || json.isBlank()) return null;

        return null;
    }

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
