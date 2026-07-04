package net.scholnick.lbdb.isbn;

import net.scholnick.lbdb.coverphoto.*;
import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.NullSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * GoogleClient - Client for Google Books API
 */
@Component
public class GoogleClient implements BookProvider {
    private final RestTemplate restTemplate;
    private final GoogleService googleService;

    @Autowired
    public GoogleClient(RestTemplate restTemplate, GoogleService googleService) {
        this.restTemplate = restTemplate;
        this.googleService = googleService;
    }

    @Override
    public Book search(String isbn) {
        BookResults results = restTemplate.getForObject(googleService.buildURL(new Book().setIsbn(isbn)), BookResults.class);
        if (results == null || NullSafe.isEmpty(results.items())) return null;

        VolumeInfo v = results.items().getFirst().volumeInfo();

        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(v.getTitle());
        book.setNumberOfPages(v.getPageCount());

        if (!NullSafe.isEmpty(v.getAuthors())) {
            book.setAuthors( v.getAuthors().stream().map(n -> new Author().setName(n)).toList() );
        }

        if (v.getImageLinks() != null) {
            book.setCoverURL(v.getImageLinks().thumbnail() == null ? v.getImageLinks().smallThumbnail() : v.getImageLinks().thumbnail());
        }

        return book;
    }
}
