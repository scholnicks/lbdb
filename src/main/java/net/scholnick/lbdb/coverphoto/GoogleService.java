package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static net.scholnick.lbdb.util.NullSafe.isClose;

/**
 * GoogleService - Service to get cover photos from Google Books API
 */
@Service
public class GoogleService implements CoverPhotoService {
    private static final Logger log = LoggerFactory.getLogger(GoogleService.class);

    @Value("${google.books.api.key}") private String googleBooksApiKey;

    private static final String BOOK_SEARCH = "https://www.googleapis.com/books/v1/volumes?q=\"%s\"&printType=books&key=%s";
    private static final String ISBN_SEARCH = "https://www.googleapis.com/books/v1/volumes?key=%s&q=isbn:%s&maxResults=1";

    private final RestTemplate restTemplate;

    @Autowired
    public GoogleService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /** Build the URL to search for the book */
    public String buildURL(Book book) {
        if (book.getIsbn() == null) {
            // https://www.googleapis.com/books/v1/volumes?q=isbn:9780670451937&maxResults=1
            return BOOK_SEARCH.formatted(URLEncoder.encode(book.getTitle(),StandardCharsets.UTF_8),googleBooksApiKey);
        }
        else {
            return ISBN_SEARCH.formatted(googleBooksApiKey,book.getIsbn());
        }
    }

    @Override
    public void setCoverPhoto(Book book) {
        try {
            BookResults results = restTemplate.getForObject(buildURL(book),BookResults.class);
            if (results != null) findImage(results,book);
        }
        catch (RestClientException e) {
            log.error("Unable to retrieve photo {}",e.getMessage());
        }
    }

    /** Find an image in the results that matches the book */
    private void findImage(BookResults results, Book book) {
        if (results.items() == null) return;

        for (BookResults.BookData data: results.items()) {
            VolumeInfo info = data.volumeInfo();
            if (info.getImageLinks() == null) continue;

            if (book.getIsbn() != null) {
                loadData(info,book);
                return;
            }

            // make sure we have the data that we need
            if (! isClose(info.getTitle(),book.getTitle())) continue;
            if (info.getAuthors() == null)                  continue;

            for (Author a: book.getAuthors()) {
                for (String n: info.getAuthors()) {
                    if (isClose(a.getName(),n)) {
                        loadData(info,book);
                    }
                }
            }
        }
    }

    /** Load data from the volume info into the book */
    private void loadData(VolumeInfo info, Book book) {
        log.debug("Volume info: {}",info);

        book.setCoverURL(info.getImageLinks().getImageURL());
        book.setNumberOfPages(info.getPageCount());

        if (book.getIsbn() == null && info.getIndustryIdentifiers() != null) {
            info.getIndustryIdentifiers().stream().filter(i -> "ISBN_13".equals(i.type())).findFirst()
                .ifPresent(isbn13 -> book.setIsbn(isbn13.identifier()));
        }

        try {
            if (info.getPublishedDate() != null && info.getPublishedDate().contains("-")) {
                book.setPublishedYear(info.getPublishedDate().substring(0,info.getPublishedDate().indexOf("-")));
            }
        }
        catch (Exception e) {
            log.error("Unable to parse: {}",info.getPublishedDate(),e);
        }
    }
}
