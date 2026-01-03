package net.scholnick.lbdb.isbn;

import net.scholnick.lbdb.coverphoto.*;
import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.NullSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleClient implements BookProvider {
    private final RestTemplate restTemplate;

    private static final String URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:%s&maxResults=1";

    @Autowired
    public GoogleClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Book search(String isbn) {
        // https://www.googleapis.com/books/v1/volumes?q=isbn:9780670451937&maxResults=1
        BookResults results = restTemplate.getForObject(URL.formatted(isbn), BookResults.class);
        if (results == null || NullSafe.isEmpty(results.items())) return null;

        VolumeInfo v = results.items().getFirst().volumeInfo();

        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(v.getTitle());
        book.setNumberOfPages(v.getPageCount());

        if (!NullSafe.isEmpty(v.getAuthors())) {
            book.setAuthors( v.getAuthors().stream().map(Author::of).toList() );
        }

        return book;
    }
}
