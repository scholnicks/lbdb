package net.scholnick.lbdb.isbn;

import net.scholnick.lbdb.domain.Book;

@FunctionalInterface
public interface BookProvider {
    /** Search for a book by its ISBN. */
    Book search(String isbn);
}
