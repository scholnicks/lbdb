package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.Book;

@FunctionalInterface
public interface CoverPhotoService {
    void setCoverPhoto(Book book);
}
