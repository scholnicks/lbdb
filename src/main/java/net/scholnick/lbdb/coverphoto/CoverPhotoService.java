package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.Book;

import java.io.IOException;

@FunctionalInterface
public interface CoverPhotoService {
    void setCoverPhoto(Book book) throws IOException;
}
