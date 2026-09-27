package net.scholnick.lbdb.coverphoto;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * AmazonCoverService is a service that fetches the cover image of a book from Amazon based on a given URL.
 */
@Service
@Log4j2
public class AmazonCoverService implements CoverProvider {
    private final PhotoDownloader downloader;

    @Autowired
    public AmazonCoverService(PhotoDownloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public byte[] downloadCover(CoverInquiry inquiry) {
        byte[] image = downloader.downloadImage(inquiry.url());
        return convertToJpeg(image);
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