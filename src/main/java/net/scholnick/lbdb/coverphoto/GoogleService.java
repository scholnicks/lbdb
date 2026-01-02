package net.scholnick.lbdb.coverphoto;

import lombok.extern.slf4j.Slf4j;
import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.ApplicationException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import static net.scholnick.lbdb.util.NullSafe.isClose;

@Slf4j
@Service
public class GoogleService implements CoverPhotoService {
    private static final String BOOK_SEARCH = "https://www.googleapis.com/books/v1/volumes?q=\"%s\"&printType=books";
    private static final String ISBN_SEARCH = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    private final RestTemplate restTemplate;
    private final byte[] invalidCoverImage;

    @Autowired
    public GoogleService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        try {
            invalidCoverImage = IOUtils.toByteArray(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("images/invalid-book-cover.jpg")));
        }
        catch (IOException e) {
            throw new ApplicationException("Unable to load invalid cover image data",e);
        }
    }

    @Override
    public void setCoverPhoto(Book book) {
        Path existingPhoto = getDownloadedCoverPhoto(book);

        if (existingPhoto != null) {
            book.setCoverPhotoPath(existingPhoto);
        }

        String url = book.getIsbn() != null ? ISBN_SEARCH + book.getIsbn() :
            String.format(BOOK_SEARCH, URLEncoder.encode(book.getTitle(), StandardCharsets.UTF_8));

        BookResults results = restTemplate.getForObject(url,BookResults.class);
        if (results != null) findImage(results,book);
    }

    private void findImage(BookResults results, Book book) {
        if (results.items() == null) return;

        for (BookResults.BookData data: results.items()) {
            VolumeInfo info = data.volumeInfo();
            if (info.getImageLinks() == null || info.getImageLinks().isEmpty()) continue;

            if (book.getIsbn() != null) {
                loadData(info,book);
                return;
            }

            // make sure we have the data that we need
            if (! isClose(info.getTitle(),book.getTitle())) continue;
            if (info.getAuthors() == null)                  continue;

            for (Author a: book.getAuthors()) {
                for (String n: info.getAuthors()) {
                    if (isClose(a.getName(),n)) {
                        loadData(info,book);
                    }
                }
            }
        }
    }

    private void loadData(VolumeInfo info, Book book) {
        log.debug("Volume info: {}",info);

        book.setCoverPhotoPath(null);
        book.setCoverPhotoPath( downloadImage(info.getImageLinks().get("thumbnail"), book) );
        book.setNumberOfPages(info.getPageCount());

        if (book.getIsbn() == null && info.getIndustryIdentifiers() != null) {
            info.getIndustryIdentifiers().stream().filter(i -> "ISBN_13".equals(i.type())).findFirst()
                .ifPresent(isbn13 -> book.setIsbn(isbn13.identifier()));
        }

        try {
            if (info.getPublishedDate() != null && info.getPublishedDate().contains("-")) {
                book.setPublishedYear(info.getPublishedDate().substring(0,info.getPublishedDate().indexOf("-")));
            }
        }
        catch (Exception e) {
            log.error("Unable to parse: {}",info.getPublishedDate(),e);
        }
    }

    private Path getBookCoverPath(Book b) {
        return FileSystems.getDefault().getPath(getDestinationDirectory().getAbsolutePath(), b.getId() + ".jpg");
    }

    private Path getDownloadedCoverPhoto(Book b) {
        Path imageFilePath = getBookCoverPath(b);

        if (Files.exists(imageFilePath) && Files.isReadable(imageFilePath)) {
            log.info("Using cached file path {}",imageFilePath);
            return imageFilePath.toAbsolutePath();
        }
        else {
            return null;
        }
    }

    private Path downloadImage(String url, Book b)  {
        try {
            Path imageFilePath = getDownloadedCoverPhoto(b);

            if (imageFilePath != null) {
                log.debug("Using cached file path {}",imageFilePath);
                return imageFilePath.toAbsolutePath();
            }

            url = url.replace("&edge=curl","");
            log.info("Downloading image from {}",url);

            byte []downloaded = IOUtils.toByteArray(new URI(url).toURL());

            if (Arrays.equals(invalidCoverImage,downloaded)) {
                log.debug("Invalid cover image");
                return null;
            }

            Path bookCoverPath = getBookCoverPath(b);
            Files.write(bookCoverPath,downloaded);
            return bookCoverPath;
        }
        catch (IOException | URISyntaxException e) {
            throw new ApplicationException("Unable to download cover image",e);
        }
    }
}
