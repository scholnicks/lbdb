package net.scholnick.lbdb.service;

import net.scholnick.lbdb.coverphoto.CoverPhotoService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Map;

/**
 * AmazonCoverService is a service that fetches the cover image of a book from Amazon based on a given URL.
 * It uses Jsoup to scrape the Amazon product page and extract the cover image URL,
 * then downloads and converts the image to JPEG format.
 */
@Service
public class AmazonCoverService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AmazonCoverService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    /** Fetches the cover image of a book from Amazon based on the given URL. */
    public byte[] getCover(String amazonUrl) throws IOException, InterruptedException {
        String imageUrl = findCoverUrl(amazonUrl);
        byte[] image = downloadImage(imageUrl);

        return convertToJpeg(image);
    }

    /** Finds the cover image URL from the Amazon product page. */
    private String findCoverUrl(String amazonUrl) throws IOException {
        Document document = Jsoup.connect(amazonUrl)
            .userAgent(CoverPhotoService.USER_AGENT)
            .header("Accept-Language", "en-US,en;q=0.9")
            .timeout(30_000)
            .get();

        Element image = document.selectFirst("#landingImage, #imgBlkFront");

        if (image == null) {
            throw new IOException("Amazon cover image not found");
        }

        String dynamicImages = image.attr("data-a-dynamic-image");

        if (!dynamicImages.isBlank()) {
            String url = findLargestImage(dynamicImages);

            if (url != null) {
                return url;
            }
        }

        String src = image.absUrl("src");

        if (src.isBlank()) {
            src = image.attr("src");
        }

        if (src.isBlank()) {
            throw new IOException("Amazon cover image URL not found");
        }

        return src;
    }

    /** Finds the largest image URL from the JSON string of dynamic images. */
    private String findLargestImage(String json)  {
        Map<String, int[]> images = objectMapper.readValue(json, new TypeReference<>() {});

        String largestUrl = null;
        long largestArea = 0;

        for (Map.Entry<String, int[]> entry : images.entrySet()) {
            int[] dimensions = entry.getValue();

            if (dimensions.length < 2) {
                continue;
            }

            long area = (long) dimensions[0] * dimensions[1];

            if (area > largestArea) {
                largestArea = area;
                largestUrl = entry.getKey();
            }
        }

        return largestUrl;
    }

    /** Downloads the image from the given URL. */
    private byte[] downloadImage(String imageUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(imageUrl))
            .header("User-Agent", CoverPhotoService.USER_AGENT)
            .timeout(Duration.ofSeconds(30))
            .GET()
            .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Failed to download Amazon cover: HTTP " + response.statusCode());
        }

        return response.body();
    }

    /** Converts the given image data to JPEG format. */
    private byte[] convertToJpeg(byte[] imageData) throws IOException {
        BufferedImage source;

        try (ByteArrayInputStream input = new ByteArrayInputStream(imageData)) {
            source = ImageIO.read(input);
        }

        if (source == null) {
            throw new IOException("Unsupported image format");
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
                throw new IOException("JPEG writer not available");
            }

            return output.toByteArray();
        }
    }
}