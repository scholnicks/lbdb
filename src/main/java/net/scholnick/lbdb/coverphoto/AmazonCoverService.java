package net.scholnick.lbdb.coverphoto;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

/**
 * AmazonCoverService is a service that fetches the cover image of a book from Amazon based on a given URL.
 */
@Service
@Log4j2
public class AmazonCoverService {
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    /** Fetches the cover image of a book from Amazon based on the given URL. */
    public byte[] getCover(String imageUrl) {
        byte[] image = downloadImage(imageUrl);
        return convertToJpeg(image);
    }

    /** Downloads the image from the given URL. */
    private byte[] downloadImage(String imageUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .header("User-Agent", CoverPhotoService.USER_AGENT)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

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

    /** Converts the given image data to JPEG format. */
    private byte[] convertToJpeg(byte[] imageData) {
        BufferedImage source;

        try (ByteArrayInputStream input = new ByteArrayInputStream(imageData)) {
            source = ImageIO.read(input);
        }
        catch (IOException e) {
            log.error("Failed to read image data: {}", e.getMessage());
            return null;
        }

        if (source == null) {
            log.warn("Failed to read image data: Unsupported image format");
            return null;
        }

        BufferedImage jpeg = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = jpeg.createGraphics();

        try {
            // JPEG has no transparency.
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, jpeg.getWidth(), jpeg.getHeight());
            graphics.drawImage(source, 0, 0, null);
        }
        finally {
            graphics.dispose();
        }

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (!ImageIO.write(jpeg, "jpg", output)) {
                log.error("Failed to write JPEG image");
                return null;
            }

            return output.toByteArray();
        }
        catch (IOException e) {
            log.error("Failed to convert image to JPEG: {}", e.getMessage());
            return null;
        }
    }
}