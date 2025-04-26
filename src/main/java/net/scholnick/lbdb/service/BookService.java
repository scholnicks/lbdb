package net.scholnick.lbdb.service;

import lombok.extern.slf4j.Slf4j;
import net.scholnick.lbdb.repository.*;
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
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final AuthorService authorService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.authorService = authorService;
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Long count() {
        return bookRepository.count();
    }

    @Transactional
    public void delete(Book b) {
        log.info("Deleting book {}",b.getTitle());
        bookRepository.removeJoinRecords(b);
        bookRepository.delete(b);
    }

    @Transactional(readOnly = true)
    public List<Book> search(Book searchCriteria) {
        List<Book> results = bookRepository.search(searchCriteria);
        results.forEach(b -> b.setAuthors(authorRepository.get(b)));
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
        Long id = bookRepository.create(b);
        b.setId(id);

        handleAuthors(b);
        //b.setAuthors(authorDAO.get(b));

        return get(id);
    }

    private Book update(Book b) {
        log.info("Updating existing book " + b);
        bookRepository.update(b);

        handleAuthors(b);

        return get(b.getId());
    }

    private void handleAuthors(Book b) {
        bookRepository.removeJoinRecords(b);
        b.getAuthors().forEach(a -> authorService.save(a, true));
        bookRepository.addJoinRecords(b);
    }
}
