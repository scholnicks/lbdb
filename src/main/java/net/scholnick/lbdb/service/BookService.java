package net.scholnick.lbdb.service;

import lombok.extern.slf4j.Slf4j;
import net.scholnick.lbdb.dao.*;
import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.NullSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static net.scholnick.lbdb.util.GUIUtilities.showMessageDialog;

@Slf4j
@Service
public class BookService {
    private final BookRepository bookDAO;
    private final AuthorRepository authorDAO;
    private final AuthorService authorService;

    @Autowired
    public BookService(BookRepository bookDAO, AuthorRepository authorDAO, AuthorService authorService) {
        this.bookDAO = bookDAO;
        this.authorDAO = authorDAO;
        this.authorService = authorService;
    }

    @Transactional(readOnly = true)
    public Book get(Long id) {
        Book b = bookDAO.get(id);
        b.setAuthors(authorDAO.get(b));

        Set<Long> editorIds = authorDAO.getEditors(b);

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

    @Transactional(readOnly = true)
    public Long count() {
        return bookDAO.count();
    }

    @Transactional
    public void delete(Book b) {
        log.info("Deleting book " + b.getTitle());
        bookDAO.removeJoinRecords(b);
        bookDAO.delete(b);
    }

    @Transactional(readOnly = true)
    public List<Book> search(Book searchCriteria) {
        List<Book> results = bookDAO.search(searchCriteria);
        results.forEach(b -> b.setAuthors(authorDAO.get(b)));
        return results;
    }

    @Transactional
    public Book save(Book b) {
        return b.getId() == null ? create(b) : update(b);
    }

    private Book create(Book b) {
        List<Book> searchResults = search(b);
        if (searchResults != null && searchResults.contains(b)) {
            log.info("Entry already present. Not creating duplicate for " + b);
            showMessageDialog(b.getTitle() + " already exists. Not creating duplicate");
            throw new RuntimeException("Existing entry found");
        }

        log.info("Creating new book " + b);
        Long id = bookDAO.create(b);
        b.setId(id);

        handleAuthors(b);
        //b.setAuthors(authorDAO.get(b));

        return get(id);
    }

    private Book update(Book b) {
        log.info("Updating existing book " + b);
        bookDAO.update(b);

        handleAuthors(b);

        return get(b.getId());
    }

    private void handleAuthors(Book b) {
        bookDAO.removeJoinRecords(b);
        b.getAuthors().forEach(a -> authorService.save(a, true));
        bookDAO.addJoinRecords(b);
    }
}
