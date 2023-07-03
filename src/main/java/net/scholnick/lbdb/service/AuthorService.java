package net.scholnick.lbdb.service;

import lombok.extern.slf4j.Slf4j;
import net.scholnick.lbdb.repository.AuthorRepository;
import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.NullSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class AuthorService {
    private final Set<Author> authorsCache = ConcurrentHashMap.newKeySet();

    private final AuthorRepository authorDAO;

    @Autowired
    public AuthorService(AuthorRepository authorDAO) {
        this.authorDAO = authorDAO;
    }

    public void loadCache() {
        authorsCache.addAll(authorDAO.all());
    }

    @Transactional(readOnly = true)
    public List<Author> search(String criteria) {
        if (NullSafe.isEmpty(criteria)) return List.of();

        return authorsCache.stream()
            .filter(a -> a.getName().toLowerCase().startsWith(criteria.toLowerCase()))
            .collect(toList());
    }

    @Transactional(readOnly = true)
    public Author get(Long id) {
        return authorDAO.get(id);
    }

    @Transactional(readOnly = true)
    public Long count() {
        return authorDAO.count();
    }

    @Transactional
    public void delete(Author a) {
        log.info("Deleting author : " + a);
        authorDAO.delete(a);
        authorsCache.remove(a);
    }

    @Transactional(readOnly = true)
    public List<Author> get(Book b) {
        log.debug("Returning authors for book " + b);
        return authorDAO.get(b);
    }

    @Transactional
    public Author save(Author a, boolean createOnly) {
        log.debug("Saving author information " + a);
        return a.getId() == null ? create(a) : createOnly ? a : update(a);
    }

    private Author create(Author a) {
        log.info("Creating new author : " + a);
        Long id = authorDAO.create(a);
        a.setId(id);
        authorsCache.add(a);
        return a;
    }

    private Author update(Author a) {
        log.info("Updating existing author : " + a);
        authorDAO.update(a);
        authorsCache.remove(a);
        authorsCache.add(a);
        return get(a.getId());
    }
}
