package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import net.scholnick.lbdb.util.NullSafe;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * GoogleClient - Service to get cover photos from Google Books API
 */
@Service
public class GoogleClient implements CoverProvider {
    private static final Logger log = LoggerFactory.getLogger(GoogleClient.class);

    private static final String BOOK_SEARCH = "https://www.googleapis.com/books/v1/volumes?q=\"%s\"&printType=books&key=%s";
    private static final String ISBN_SEARCH = "https://www.googleapis.com/books/v1/volumes?key=%s&q=isbn:%s&maxResults=1";

    private final RestTemplate restTemplate;
    private final PhotoDownloader downloader;
    private final String apiKey;

    @Autowired
    public GoogleClient(RestTemplate restTemplate, PhotoDownloader downloader) {
        this.restTemplate = restTemplate;
        this.downloader   = downloader;
        this.apiKey       = NullSafe.getPassword("google-books-api-token");
    }

    @Override
    public byte[] downloadCover(CoverInquiry inquiry) {
        if (NullSafe.isEmpty(inquiry.isbn()) && NullSafe.isEmpty(inquiry.title())) {
            return null;
        }

        try {
            String url = buildURL(inquiry);
//            log.info("Downloading book information from {}", url);
            BookResults results = restTemplate.getForObject(url,BookResults.class);
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
            return BOOK_SEARCH.formatted(URLEncoder.encode(inquiry.title(),StandardCharsets.UTF_8),apiKey);
        }
        else {
            return ISBN_SEARCH.formatted(apiKey,inquiry.isbn());
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

    /** BookResults record to hold results from Google Books API */
    public record BookResults(List<BookData> items) {
        public record BookData(VolumeInfo volumeInfo) {}
    }
}

