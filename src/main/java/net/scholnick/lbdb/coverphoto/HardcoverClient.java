package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.scholnick.lbdb.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;

/**
 * HardcoverClient - Client for Hardcover API
 */
@Component
public class HardcoverClient implements CoverProvider {
    private final RestClient      restClient;
    private final PhotoDownloader downloader;

    @Autowired
    public HardcoverClient(RestClient restClient, PhotoDownloader downloader) {
        this.restClient = restClient;
        this.downloader = downloader;
    }

    @Override
    public byte[] downloadCover(CoverInquiry inquiry) {
        // 978-1668057551

        String json = restClient.post()
                .uri("https://api.hardcover.app/v1/graphql")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + System.getenv("HARDCOVER_TOKEN"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("query", QUERY, "variables", Map.of("isbn", inquiry.isbn())))
                .retrieve()
                .body(String.class);

        if (NullSafe.isEmpty(json)) return null;

        List<Edition> editions = JSONUtilities.fromJSON(json,OutsideWrapper.class).data().editions();
        if (NullSafe.isEmpty(editions)) return null;

        Edition edition = editions.getFirst();
        if (edition == null || edition.book.cached_image == null) return null;

        return downloader.downloadImage(edition.book.cached_image.url());
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record OutsideWrapper(Data data) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record Data(List<Edition> editions) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    public record Edition(String isbn_13, String isbn_10, HCBook book) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    public record HCBook(Cover cached_image) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    public record Cover(String url) {}

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
