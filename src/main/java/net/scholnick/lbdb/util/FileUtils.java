package net.scholnick.lbdb.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public final class FileUtils {
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
        if (! path.toFile().exists()) path.toFile().mkdir();
        return path.toFile();
    }

    private FileUtils() {}
}
