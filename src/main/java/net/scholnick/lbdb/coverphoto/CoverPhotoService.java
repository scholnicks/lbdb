package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.Book;

import java.io.*;

public interface CoverPhotoService {
    void setCoverPhoto(Book book) throws IOException;

    default File getDestinationDirectory() {
        return new File("production".equalsIgnoreCase(System.getProperty("lbdb.environment","dev")) ?
            "/Users/steve/data/cover-photos/" :
            "/Users/steve/development/java/lbdb/cover-photos/"
        );
    }

    default String getDownloadedImage(Book b) throws IOException {
        File imageFilePath = new File(getDestinationDirectory(), b.getId() + ".jpg");
        return imageFilePath.exists() && imageFilePath.canRead() ? imageFilePath.getCanonicalPath() : null;
    }
}
