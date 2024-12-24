package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.Book;
import net.scholnick.lbdb.util.ApplicationException;

import java.io.File;
import java.io.IOException;

public interface CoverPhotoService {
    void setCoverPhoto(Book book);

    default File getDestinationDirectory() {
        return new File("production".equalsIgnoreCase(System.getProperty("lbdb.environment","dev")) ?
            "/Users/steve/data/cover-photos/" :
            "/Users/steve/development/java/lbdb/cover-photos/"
        );
    }

    default String getDownloadedImage(Book b) {
        try {
            File imageFilePath = new File(getDestinationDirectory(), b.getId() + ".jpg");
            return imageFilePath.exists() && imageFilePath.canRead() ? imageFilePath.getCanonicalPath() : null;
        }
        catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}
