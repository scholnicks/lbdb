package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.Book;

/**
 * CoverPhotoService defines a service that can set the cover photo for a book.
 */
@FunctionalInterface
public interface CoverPhotoService {
    void setCoverPhoto(Book book);
}
