package net.scholnick.lbdb.coverphoto;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class HttpURLConnectionFactory {
    public InputStream generateURLInputStream(String urlIn) throws IOException {
        URL url = new URL(urlIn);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        HttpURLConnection.setFollowRedirects(false);
        huc.setConnectTimeout(TIMEOUT);
        huc.setReadTimeout(TIMEOUT * 2);
        huc.setRequestMethod("GET");
        huc.connect();

        return huc.getInputStream();
    }

    private static final int TIMEOUT = 5 * 1000;
}
