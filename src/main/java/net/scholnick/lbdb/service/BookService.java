package net.scholnick.lbdb.service;

import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.repository.*;
import net.scholnick.lbdb.util.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * BookService handles business logic related to books.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@Service
public class BookService {
    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final AuthorService authorService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.authorService = authorService;
    }

    /** Get a book by its ID. */
    @Transactional(readOnly=true)
    public Book get(Long id) {
        Book b = bookRepository.get(id);
        b.setAuthors(authorRepository.get(b));

        Set<Long> editorIds = authorRepository.getEditors(b);

        for (Author a : b.getAuthors()) {
            if (editorIds.contains(a.getId())) {
                a.setEditor(true);
            }
        }

        b.getAuthors().sort(AUTHOR_SORTER);

        return b;
    }

    private static final Comparator<Author> AUTHOR_SORTER = (a1, a2) -> {
        if (a1.isEditor() && !a2.isEditor()) return -1;
        if (!a1.isEditor() && a2.isEditor()) return +1;
        return NullSafe.compare(a1.getName(), a2.getName());
    };

    /** Count the total number of books. */
    @Transactional(readOnly=true)
    public Long count() {
        return bookRepository.count();
    }

    /** Delete a book. */
    @Transactional
    public void delete(Book b) {
        log.info("Deleting book {}",b.getTitle());
        bookRepository.removeJoinRecords(b);
        bookRepository.delete(b);
    }

    /** Search for books matching the given criteria. */
    @Transactional(readOnly=true)
    public List<Book> search(Book searchCriteria) {
        List<Book> results = bookRepository.search(searchCriteria);
        results.forEach(b -> b.setAuthors(authorRepository.get(b)));
        return results;
    }

    /** Save a book, creating or updating as necessary. */
    @Transactional
    public void save(Book b) {
        if (b.getId() == null) {
            create(b);
        }
        else {
            update(b);
        }
    }

    /** Create a new book. */
    private void create(Book b) {
        List<Book> searchResults = search(b);
        if (searchResults != null && searchResults.contains(b)) {
            log.info("Entry already present. Not creating duplicate for {}",b);
            GUIUtilities.showMessageDialog(null,b.getTitle() + " already exists. Not creating duplicate","Duplicate entry");
            throw new RuntimeException("Existing entry found");
        }

        log.info("Creating new book {}",b);
        Long id = bookRepository.create(b);
        b.setId(id);

        handleAuthors(b);
    }

    /** Update an existing book. */
    private void update(Book b) {
        log.info("Updating existing book {}",b);
        bookRepository.update(b);
        handleAuthors(b);
    }

    /** Handle saving authors and updating join records for a book. */
    private void handleAuthors(Book b) {
        bookRepository.removeJoinRecords(b);
        authorService.save(b.getAuthors());
        bookRepository.addJoinRecords(b);
    }
}
