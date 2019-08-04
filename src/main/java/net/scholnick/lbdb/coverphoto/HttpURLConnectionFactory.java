package net.scholnick.lbdb.coverphoto;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface HttpURLConnectionFactory {
	InputStream generateURLInputStream(String urlIn) throws IOException;
}