package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * GoogleClient - Service to get cover photos from Google Books API
 */
@Service
public class GoogleClient implements CoverProvider {
    private static final Logger log = LoggerFactory.getLogger(GoogleClient.class);

    @Value("${google.books.api.key}") private String googleBooksApiKey;

    private static final String BOOK_SEARCH = "https://www.googleapis.com/books/v1/volumes?q=\"%s\"&printType=books&key=%s";
    private static final String ISBN_SEARCH = "https://www.googleapis.com/books/v1/volumes?key=%s&q=isbn:%s&maxResults=1";

    private final RestTemplate restTemplate;
    private final PhotoDownloader downloader;

    @Autowired
    public GoogleClient(RestTemplate restTemplate, PhotoDownloader downloader) {
        this.restTemplate = restTemplate;
        this.downloader   = downloader;
    }

    @Override
    public byte[] downloadCover(CoverInquiry inquiry) {
        try {
            BookResults results = restTemplate.getForObject(buildURL(inquiry),BookResults.class);
            if (results != null) {
                String imageUrl = findImage(results,inquiry);
                if (imageUrl != null) {
                    return downloader.downloadImage(imageUrl);
                }
            }
        }
        catch (RestClientException e) {
            log.error("Unable to retrieve photo {}",e.getMessage());
        }
        return null;
    }

    /** Build the URL to search for the book */
    private String buildURL(CoverInquiry inquiry) {
        if (inquiry.isbn() == null) {
            // https://www.googleapis.com/books/v1/volumes?q=isbn:9780670451937&maxResults=1
            return BOOK_SEARCH.formatted(URLEncoder.encode(inquiry.title(),StandardCharsets.UTF_8),googleBooksApiKey);
        }
        else {
            return ISBN_SEARCH.formatted(googleBooksApiKey,inquiry.isbn());
        }
    }

    /** Find an image in the results that matches the book */
    private String findImage(BookResults results, CoverInquiry inquiry) {
        if (results.items() == null) return null;

        for (BookResults.BookData data: results.items()) {
            VolumeInfo info = data.volumeInfo();
            if (info.getImageLinks() != null) {
                return info.getImageLinks().getImageURL();
            }
        }
        return null;
    }

    /** VolumeInfo - VolumeInfo from Google Books API */
    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static final class VolumeInfo {
        private ImageLinks imageLinks;

        /** Returns the ISBN-13 identifier for this volume, or null if not found. */
        public record ImageLinks(String smallThumbnail, String thumbnail) {
            public String getImageURL() {
                return thumbnail != null ? thumbnail.replace("&edge=curl","") : smallThumbnail.replace("&edge=curl","");
            }
        }
    }
}
