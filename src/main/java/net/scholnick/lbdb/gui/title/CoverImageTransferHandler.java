package net.scholnick.lbdb.gui.title;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * CoverImageTransferHandler is a {@link TransferHandler} that allows the user to drag and drop an image file onto a {@link JLabel} to set the cover image.
 */
public final class CoverImageTransferHandler extends TransferHandler {
    private final JLabel coverLabel;
    private final Consumer<byte[]> imageConsumer;
    private final int previewWidth;
    private final int previewHeight;

    public CoverImageTransferHandler(JLabel coverLabel, Consumer<byte[]> imageConsumer, int previewWidth, int previewHeight) {
        this.coverLabel = coverLabel;
        this.imageConsumer = imageConsumer;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDrop() && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        try {
            List<File> files = getDroppedFiles(support);

            if (files.size() != 1) {
                return false;
            }

            BufferedImage image = ImageIO.read(files.getFirst());

            if (image == null) {
                return false;
            }

            byte[] imageBytes = toJpeg(image);

            imageConsumer.accept(imageBytes);
            coverLabel.setIcon(createPreview(image));

            return true;

        }
        catch (IOException | UnsupportedFlavorException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private List<File> getDroppedFiles(TransferSupport support)
            throws UnsupportedFlavorException, IOException {

        return (List<File>) support
                .getTransferable()
                .getTransferData(DataFlavor.javaFileListFlavor);
    }

    private ImageIcon createPreview(BufferedImage image) {
        double widthScale = (double) previewWidth / image.getWidth();

        double heightScale = (double) previewHeight / image.getHeight();

        double scale = Math.min(widthScale, heightScale);

        int width = (int) Math.round(image.getWidth() * scale);
        int height = (int) Math.round(image.getHeight() * scale);

        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }

    private byte[] toJpeg(BufferedImage image) throws IOException {
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = rgbImage.createGraphics();

        try {
            graphics.drawImage(image, 0, 0, null);
        }
        finally {
            graphics.dispose();
        }

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (!ImageIO.write(rgbImage, "jpg", output)) {
                throw new IOException("No JPEG ImageIO writer available");
            }

            return output.toByteArray();
        }
    }
}