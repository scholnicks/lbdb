package net.scholnick.lbdb.isbn;

import net.scholnick.lbdb.domain.Book;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DefaultBookProvider implements BookProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultBookProvider.class);

    private final GoogleClient      googleClient;
    private final OpenLibraryClient openLibraryClient;

    @Autowired
    public DefaultBookProvider(GoogleClient googleClient, OpenLibraryClient openLibraryClient) {
        this.googleClient      = googleClient;
        this.openLibraryClient = openLibraryClient;
    }

    @Override
    public Book search(String isbn) {
        try {
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
