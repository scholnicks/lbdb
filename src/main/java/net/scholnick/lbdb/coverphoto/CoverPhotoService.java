package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.Book;

/**
 * CoverPhotoService defines a service that can set the cover photo for a book.
 */
@FunctionalInterface
public interface CoverPhotoService {
    /** Sets the cover photo for the given book. */
    void setCoverPhoto(Book book);

    String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
        "AppleWebKit/537.36 (KHTML, like Gecko) " +
        "Chrome/140.0 Safari/537.36";
}
