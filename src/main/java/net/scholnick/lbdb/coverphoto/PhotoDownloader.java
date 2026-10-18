package net.scholnick.lbdb.coverphoto;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Locale;

/**
 * PhotoDownloader is a utility class that provides functionality to download images from a given URL.
 */
@Component
@Log4j2
public class PhotoDownloader {
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()
    ;

    /** Downloads the image from the given URL. */
    public byte[] downloadImage(String imageUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .header("User-Agent", CoverProvider.USER_AGENT)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

            HttpResponse<byte[]> response = httpClient.send(request, BodyHandlers.ofByteArray());

            String contentType = response.headers().firstValue("Content-Type").orElse("");

            if (!contentType.toLowerCase(Locale.ENGLISH).startsWith("image/")) {
                log.warn("Failed to download image from {}: Content-Type is not an image: {}", imageUrl, contentType);
                return null;
            }

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn("Failed to download image from {}: HTTP {}", imageUrl, response.statusCode());
                return null;
            }

            return response.body();
        }
        catch (IOException | InterruptedException e) {
            log.error("Failed to download image from {}: {}", imageUrl, e.getMessage());
            return null;
        }
    }
}
