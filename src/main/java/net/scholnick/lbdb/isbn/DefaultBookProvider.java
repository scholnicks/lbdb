package net.scholnick.lbdb.isbn;

import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.NullSafe;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Comparator.comparing;

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
            Set<Author> authors = new HashSet<>();

            Book google = googleClient.search(isbn);
            if (google != null) authors.addAll(NullSafe.nvl(google.getAuthors()));

            Book open = openLibraryClient.search(isbn);
            if (open != null) authors.addAll(NullSafe.nvl(open.getAuthors()));

            Book hardCover = hardcoverClient.search(isbn);
            if (hardCover != null) authors.addAll(NullSafe.nvl(hardCover.getAuthors()));

            Book results = google == null ? (open == null ? hardCover : open) : google;
            if (results == null) return null;

            results.setAuthors(authors.stream().sorted(comparing(Author::getName)).toList());
            results.setIsbn(isbn);

            return results;
        }
        catch (Exception e) {
            log.error("Error searching isbn", e);
            return null;
        }
    }
}
