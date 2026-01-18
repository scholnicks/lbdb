package net.scholnick.lbdb.service;

import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.repository.AuthorRepository;
import net.scholnick.lbdb.util.NullSafe;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.*;

/**
 * AuthorService handles business logic related to {@link Author}s.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */

@Service
public class AuthorService {
    private static final Logger log = LoggerFactory.getLogger(AuthorService.class);

    private final Set<Author> authorsCache = ConcurrentHashMap.newKeySet();

    private final AuthorRepository repository;

    @Autowired
    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    /** Load all authors into the cache. */
    public void loadCache() {
        authorsCache.addAll(repository.all());
    }

    /** Search for authors whose names start with the given criteria. */
    @Transactional(readOnly=true)
    public List<Author> search(String criteria) {
        if (NullSafe.isEmpty(criteria)) return List.of();

        return authorsCache.stream()
            .filter(a -> a.getName().toLowerCase().startsWith(criteria.toLowerCase()))
            .collect(toList());
    }

    /** Get an author by ID. */
    @Transactional(readOnly=true)
    public Author get(Long id) {
        return repository.get(id);
    }

    /** Count the total number of authors. */
    @Transactional(readOnly=true)
    public Long count() {
        return repository.count();
    }

    /** Delete an author. */
    @Transactional
    public void delete(Author a) {
        log.info("Deleting author : {}",a);
        repository.delete(a);
        authorsCache.remove(a);
    }

    /** Get the authors for a given book. */
    @Transactional(readOnly=true)
    public List<Author> get(Book b) {
        log.debug("Returning authors for book {}",b);
        return repository.get(b);
    }

    /** Save an author, creating or updating as necessary. */
    @Transactional
    public void save(Author a) {
        log.debug("Saving author information {}",a);
        if (a.getId() == null) {
            create(a);
        }
        else {
            update(a);
        }
    }

    /** Save a list of authors, creating any that do not already exist. Existing authors will have their IDs set appropriately. */
    @Transactional
    public void save(List<Author> authors) {
        for (Author a : authors) {
            Author existing = authorsCache.stream()
                .filter(ac -> ac.getName().equalsIgnoreCase(a.getName()))
                .findFirst()
                .orElse(null);

            if (existing == null) {
                create(a);
            } else {
                a.setId(existing.getId());
            }
        }
    }

    /** Create a new author. */
    private void create(Author a) {
        log.info("Creating new author : {}",a);
        Long id = repository.create(a);
        a.setId(id);
        authorsCache.add(a);
    }

    /** Update an existing author. */
    private void update(Author a) {
        log.info("Updating existing author : {}",a);
        repository.update(a);
        authorsCache.remove(a);
        authorsCache.add(a);
    }
}
