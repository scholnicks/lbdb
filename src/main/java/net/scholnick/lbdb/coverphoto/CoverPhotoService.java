package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.Book;

/**
 * CoverPhotoService defines a service that can set the cover photo for a book.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@FunctionalInterface
public interface CoverPhotoService {
    void setCoverPhoto(Book book);
}
