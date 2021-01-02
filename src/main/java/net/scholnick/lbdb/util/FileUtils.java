package net.scholnick.lbdb.util;

import java.io.*;
import java.nio.file.*;

@Deprecated
public final class FileUtils {
    public static File getDestinationDirectory() throws IOException {
        Path path = Paths.get(System.getProperty("user.home"), "Public/Images");

        File imagesFile = path.toFile();
        if (! imagesFile.exists()) {
            if (! imagesFile.mkdir()) {
                throw new IOException("Cannot create directory " + imagesFile);
            }
        }

        return imagesFile;
    }

    private FileUtils() {}
}
