package net.scholnick.lbdb.isbn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * OpenLibraryClient - Client for Open Library Books API
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@Component
public class OpenLibraryClient implements BookProvider {
    private final RestTemplate restTemplate;

    private static final String URL = "https://openlibrary.org/api/books?format=json&jscmd=data&bibkeys=ISBN:%s";

    @Autowired
    public OpenLibraryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Book search(String isbn) {
        // https://openlibrary.org/api/books?format=json&jscmd=data&bibkeys=ISBN:9780670451937
        String results = restTemplate.getForObject(URL.formatted(isbn), String.class);
        if (NullSafe.isEmpty(results)) return null;

        results = results.replaceAll("ISBN:%s".formatted(isbn), "data");
        Data data = JSONUtilities.fromJSON(results, Wrapper.class).data;
        if (data == null) return null;

        Book book = new Book()
                .setTitle(data.title)
                .setNumberOfPages(data.number_of_pages)
                .setPublishedYear(data.publish_date)
                .setIsbn(isbn);

        if (!NullSafe.isEmpty(data.authors)) {
            book.setAuthors( data.authors().stream().map(OLAuthor::name).map(Author::of).toList() );
        }

        if (data.cover != null) {
            book.setCoverURL(data.cover().image());
        }

        return book;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record Data(String title, int number_of_pages, List<OLAuthor> authors, String publish_date, Cover cover) {}

    private record Cover(String small, String medium, String large) {
        public String image() {
            if (large != null) return large;
            if (medium != null) return medium;
            return small;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record OLAuthor(String name) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record Wrapper(Data data) {}
}
