package net.scholnick.lbdb.isbn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.JSONUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
        if (results == null || results.isBlank()) return null;

        results = results.replaceAll("ISBN:%s".formatted(isbn), "data");
        Data data = JSONUtilities.fromJSON(results, Wrapper.class).data;

        Book book = new Book();
        book.setTitle(data.title);
        book.setNumberOfPages(data.number_of_pages);
        book.setPublishedYear(data.publish_date);
        book.setIsbn(isbn);

        if (data.authors() != null && !data.authors().isEmpty()) {
            book.setAuthors( data.authors().stream().map(OLAuthor::name).map(Author::of).toList() );
        }

        return book;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record Data(String title, int number_of_pages, List<OLAuthor> authors, String publish_date) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record OLAuthor(String name) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record Wrapper(Data data) {}
}
