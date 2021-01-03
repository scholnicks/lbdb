package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import static net.scholnick.lbdb.util.NullSafe.isClose;

@Service
public class GoogleService implements CoverPhotoService {
    private static final Logger log = LoggerFactory.getLogger(GoogleService.class);

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
            throw new RuntimeException("Unable to load invalid cover image data",e);
        }
    }

    @Override
    public void setCoverPhoto(Book book) throws IOException {
        Path existingPhoto = getDownloadedCoverPhoto(book);

        if (existingPhoto != null) {
            book.setCoverPhotoPath(existingPhoto);
        }

        String url = book.getIsbn() != null ? ISBN_SEARCH + book.getIsbn() :
            String.format(BOOK_SEARCH, URLEncoder.encode(book.getTitle(), StandardCharsets.UTF_8));

        BookResults results = restTemplate.getForObject(url,BookResults.class);
        if (results != null) findImage(results,book);
    }

    private void findImage(BookResults results, Book book) throws IOException {
        if (results.getItems() == null) return;

        for (BookData data: results.getItems()) {
            VolumeInfo info = data.getVolumeInfo();
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

    private void loadData(VolumeInfo info, Book book) throws IOException {
        log.debug("Volume info: {}",info);

        book.setCoverPhotoPath(null);
        book.setCoverPhotoPath( downloadImage(info.getImageLinks().get("thumbnail"), book) );
        book.setNumberOfPages(info.getPageCount());

        if (book.getIsbn() == null && info.getIndustryIdentifiers() != null) {
            info.getIndustryIdentifiers().stream().filter(i -> "ISBN_13".equals(i.getType())).findFirst()
                .ifPresent(isbn13 -> book.setIsbn(isbn13.getIdentifier()));
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
            log.info("Using cached file path " + imageFilePath);
            return imageFilePath.toAbsolutePath();
        }
        else {
            return null;
        }
    }

    private Path downloadImage(String url, Book b) throws IOException {
        Path imageFilePath = getDownloadedCoverPhoto(b);

        if (imageFilePath != null) {
            log.debug("Using cached file path " + imageFilePath);
            return imageFilePath.toAbsolutePath();
        }

        url = url.replace("&edge=curl","");
        log.info("Downloading image from " + url);

        byte []downloaded = IOUtils.toByteArray(new URL(url));

        if (Arrays.equals(invalidCoverImage,downloaded)) {
            log.debug("Invalid cover image");
            return null;
        }

        Path bookCoverPath = getBookCoverPath(b);
        Files.write(bookCoverPath,downloaded);
        return bookCoverPath;
    }
}
