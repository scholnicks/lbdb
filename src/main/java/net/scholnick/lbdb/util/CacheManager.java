package net.scholnick.lbdb.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Deprecated
public final class CacheManager {
	public static void clear() {
		try {
			Arrays.stream(getDestinationDirectory().listFiles()).forEach(File::delete);
		}
		catch (IOException e) {
			//LogManager.error(CacheManager.class,e);
		}
	}

	public static File getDestinationDirectory() throws IOException {
		Path path = Paths.get(System.getProperty("user.home"),"Public/Images");
		
		if (! path.toFile().exists()) {
			//LogManager.info(CacheManager.class, "Creating directory: " + path.toFile());
			path.toFile().mkdir();
		}
		
		return path.toFile();
	}
	
	private CacheManager() {}
}
