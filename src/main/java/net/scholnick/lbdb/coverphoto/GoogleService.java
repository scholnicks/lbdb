package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.domain.Book;
import net.scholnick.lbdb.util.NullSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.scholnick.lbdb.util.CacheManager.getDestinationDirectory;

@Service
public class GoogleService {
    private static final Logger log = LoggerFactory.getLogger(GoogleService.class);

    private static final ObjectMapper mapper = new ObjectMapper(new JsonFactory());
    private static final TypeReference<HashMap<String,Object>> typeReference = new TypeReference<>(){};

    private final HttpURLConnectionFactory connectionFactory;

    private static final String ISBN_TYPE = "ISBN_13";

    @Autowired
    public GoogleService(HttpURLConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setCoverPhoto(Book book) throws IOException {
        Path existingPhoto = getDownloadedCoverPhoto(book);

        if (existingPhoto != null) {
            book.setCoverPhotoPath(existingPhoto);
        }

        String url = String.format(SEARCH_URL,
            URLEncoder.encode(book.getTitle(), StandardCharsets.UTF_8),
            URLEncoder.encode(book.getPrimaryAuthor().lastName(), StandardCharsets.UTF_8)
        );

        log.info("Searching with GET: " + url);
        try (InputStream is = connectionFactory.generateURLInputStream(url)) {
            parse(is, book);
        }
    }

    @SuppressWarnings("unchecked")
    private void parse(InputStream is, Book book) throws IOException {
        book.setCoverPhotoPath(null);  // clear

        Map<String,Object> o = mapper.readValue(is, typeReference);

        List<Object> items = (List<Object>) o.get("items");
        if (items == null) {
            return;
        }

        for (Object record: items) {
            Map<Object,Object> volumeInfo = (Map<Object,Object>) ((Map<Object,Object>) record).get("volumeInfo");

            if (canonicalEquals((String) volumeInfo.get("title"),book.getTitle())) {
                for (String author: ((List<String>) volumeInfo.get("authors")) ) {
                    for (Author a: book.getAuthors()) {
                        if (canonicalEquals(author,a)) {
                            log.info("Found cover photo match for " + book);
                            Map<String,String> imageLinks = (Map<String,String>) volumeInfo.get("imageLinks");
                            log.info("Image Links: " + imageLinks);

                            book.setCoverPhotoPath( downloadImage(imageLinks.get("thumbnail"),book));
                            book.setNumberOfPages(Integer.parseInt(volumeInfo.get("pageCount").toString()));

                            ((List<Map<String,String>>) volumeInfo.get("industryIdentifiers")).forEach(identitier -> {
                                if (ISBN_TYPE.equals(identitier.get("type"))) {
                                    book.setIsbn(identitier.get("identifier"));
                                }
                            });

                            // if (volumeInfo.get("publishedDate") != null) {
                            // 	log.info(getClass(),"Published Date: " + volumeInfo.get("publishedDate"));
                            // 	String publishedDate = (String) volumeInfo.get("publishedDate");
                            // 	book.setPublishNullSafe.toCanonical(author)edYear(publishedDate.substring(0,publishedDate.indexOf("-")));
                            // }

                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean canonicalEquals(String s, Author a) {
        return NullSafe.equals(toCanonical(s), toCanonical(a.getName()));
    }

    private boolean canonicalEquals(String s1, String s2) {
        if (s1 == null || s2 == null) return false;
        return Objects.equals(toCanonical(s1), toCanonical(s2));
    }

    private String toCanonical(String s) {
        return s == null ? null : s.replaceAll("\\s*","").trim().toLowerCase();
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

        URLConnection uc = new URL(url).openConnection();
        int contentLength = uc.getContentLength();

        if (contentLength < 50) {
            return null;
        }

        imageFilePath = getBookCoverPath(b);
        try (InputStream in=uc.getInputStream()) {
            Files.copy(in, imageFilePath);
            return imageFilePath.toAbsolutePath().toAbsolutePath();
        }
    }

    private static final String SEARCH_URL = "https://www.googleapis.com/books/v1/volumes?q=\"%s\"+inauthor:%s&printType=books";
}
