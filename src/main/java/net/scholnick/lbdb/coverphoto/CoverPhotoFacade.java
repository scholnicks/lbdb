package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Primary
public class CoverPhotoFacade implements CoverPhotoService {
    private final GoogleService googleService;

    @Autowired
    public CoverPhotoFacade(GoogleService googleService) {
        this.googleService = googleService;
    }

    @Override
    public void setCoverPhoto(Book book) {
        googleService.setCoverPhoto(book);
    }

    @Override
    public File getDestinationDirectory() {
        return googleService.getDestinationDirectory();
    }

    @Override
    public String getDownloadedImage(Book b) {
        return googleService.getDownloadedImage(b);
    }
}
