package net.scholnick.lbdb.isbn;

import net.scholnick.lbdb.domain.Book;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * DefaultBookProvider - Implementation of BookProvider that uses Google and OpenLibrary clients to search for book information by ISBN.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@Service
@Primary
public class DefaultBookProvider implements BookProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultBookProvider.class);

    private final GoogleClient      googleClient;
    private final OpenLibraryClient openLibraryClient;
    private final HardcoverClient   hardcoverClient;

    @Autowired
    public DefaultBookProvider(GoogleClient googleClient, OpenLibraryClient openLibraryClient, HardcoverClient hardcoverClient) {
        this.googleClient      = googleClient;
        this.openLibraryClient = openLibraryClient;
        this.hardcoverClient   = hardcoverClient;
    }

    @Override
    public Book search(String isbn) {
        try {
            log.debug("Hardcover results, {}",hardcoverClient.search(isbn));
            Book results = googleClient.search(isbn);

            if (results == null) {
                results = openLibraryClient.search(isbn);
            }

            return results;
        }
        catch (Exception e) {
            log.error("Error searching isbn", e);
            return null;
        }
    }
}
