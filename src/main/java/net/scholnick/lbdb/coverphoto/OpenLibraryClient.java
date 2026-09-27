package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.scholnick.lbdb.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * OpenLibraryClient - Client for Open Library Books API
 */
@Component
public class OpenLibraryClient implements CoverProvider {
    private final RestTemplate restTemplate;
    private final PhotoDownloader downloader;

    private static final String URL = "https://openlibrary.org/api/books?format=json&jscmd=data&bibkeys=ISBN:%s";

    @Autowired
    public OpenLibraryClient(RestTemplate restTemplate, PhotoDownloader downloader) {
        this.restTemplate = restTemplate;
        this.downloader   = downloader;
    }

    @Override
    public byte[] downloadCover(CoverInquiry inquiry) {
        if (NullSafe.isEmpty(inquiry.isbn())) return null;

        // https://openlibrary.org/api/books?format=json&jscmd=data&bibkeys=ISBN:9780670451937
        String results = restTemplate.getForObject(URL.formatted(inquiry.isbn()), String.class);
        if (NullSafe.isEmpty(results)) return null;

        results = results.replaceAll("ISBN:%s".formatted(inquiry.isbn()), "data");
        Data data = JSONUtilities.fromJSON(results, Wrapper.class).data;
        if (data == null) return null;

        return downloader.downloadImage(data.cover().image());
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record Wrapper(Data data) {}

    @JsonIgnoreProperties(ignoreUnknown=true)
    private record Data(Cover cover) {}

    private record Cover(String small, String medium, String large) {
        public String image() {
            if (large != null) return large;
            if (medium != null) return medium;
            return small;
        }
    }
}
