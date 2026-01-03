package net.scholnick.lbdb.isbn;

import net.scholnick.lbdb.domain.Book;

/**
 * BookProvider defines a service that can look up books by ISBN.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@FunctionalInterface
public interface BookProvider {
    /** Search for a book by its ISBN. */
    Book search(String isbn);
}
