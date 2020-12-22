package net.scholnick.lbdb.util;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public final class FileUtils {
    @Deprecated
    public static void clear() {
        try {
            Arrays.stream(requireNonNull(getDestinationDirectory().listFiles())).forEach(File::delete);
        }
        catch (IOException e) {
            // empty
        }
    }

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
