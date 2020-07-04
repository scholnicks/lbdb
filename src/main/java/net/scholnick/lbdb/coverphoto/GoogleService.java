package net.scholnick.lbdb.coverphoto;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.domain.Book;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.scholnick.lbdb.util.FileUtils.getDestinationDirectory;

@Service
public class GoogleService {
    private static final Logger log = LoggerFactory.getLogger(GoogleService.class);

    private static final String BOOK_SEARCH = "https://www.googleapis.com/books/v1/volumes?q=\"%s\"&printType=books";
    private static final String ISBN_SEARCH = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    private final RestTemplate restTemplate;

    @Autowired
    public GoogleService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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
        book.setCoverPhotoPath(null);
        book.setCoverPhotoPath( downloadImage(info.getImageLinks().get("thumbnail"), book) );
        book.setNumberOfPages(info.getPageCount());

        if (book.getIsbn() == null && info.getIndustryIdentifiers() != null) {
            info.getIndustryIdentifiers().stream().filter(i -> "ISBN_13".equals(i.getType())).findFirst()
                .ifPresent(isbn13 -> book.setIsbn(isbn13.getIdentifier()));
        }
    }

    private boolean isClose(String s1, String s2) {
        if (s1 == null || s2 == null) return false;

        return LevenshteinDistance.getDefaultInstance().apply(
            s1.replaceAll("\\s*","").trim().toLowerCase(),
            s2.replaceAll("\\s*","").trim().toLowerCase()
        ) < 2;
    }

    private Path getBookCoverPath(Book b) throws IOException {
        return FileSystems.getDefault().getPath(getDestinationDirectory().getAbsolutePath(), b.getId() + ".jpg");
    }

    private Path getDownloadedCoverPhoto(Book b) throws IOException {
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

        Path bookCoverPath = getBookCoverPath(b);
        FileUtils.copyURLToFile(new URL(url),bookCoverPath.toFile());
        return bookCoverPath;
    }
}
