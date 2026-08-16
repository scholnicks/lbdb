package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.Book;

/**
 * CoverPhotoService defines a service that can set the cover photo for a book.
 */
@FunctionalInterface
public interface CoverPhotoService {
    /** Sets the cover photo for the given book. */
    void setCoverPhoto(Book book);
}
