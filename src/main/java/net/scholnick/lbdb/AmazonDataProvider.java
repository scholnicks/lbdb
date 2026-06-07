package net.scholnick.lbdb;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.util.NullSafe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.*;

import static java.util.stream.Collectors.toSet;

@Service
public class AmazonDataProvider {
    private final AuthorService authorService;

    private static final Logger log = LoggerFactory.getLogger(AmazonDataProvider.class);
    private static final String BASE_URL = "https://www.amazon.com/dp/%s";
    private static final Pattern ASIN_PATTERN = Pattern.compile("/(?:dp|gp/product|product)/([A-Z0-9]{10})(?:[/?]|$)", Pattern.CASE_INSENSITIVE);
    private static final String KINDLE_PREFIX = "B0";
    private static final AmazonData EMPTY_DATA = new AmazonData(null, null, Set.of());

    @Autowired
    public AmazonDataProvider(AuthorService authorService) {
        this.authorService = authorService;
    }

    public AmazonData get(String identifier) {
        try {
            String asin = identifier.startsWith("http") ? extractAsin(identifier) : identifier;
            if (asin == null) return EMPTY_DATA;

            Document doc = Jsoup.connect(BASE_URL.formatted(asin))
                .userAgent("Mozilla/5.0")
                .header("Accept-Language", "en-US,en;q=0.9")
                .timeout(15_000)
                .get();

            List<String> names = new ArrayList<>();

            for (Element el : doc.select(".author a.a-link-normal")) {
                String name = el.text().trim();

                if (!name.isEmpty() && !name.equalsIgnoreCase("Visit Amazon's Author Page")) {
                    names.add(name);
                }
            }

            log.debug("Found {} authors", names.size());

            return new AmazonData(
                asin,
                asin.startsWith(KINDLE_PREFIX) ? null : asin,
                names.stream().map(this::convert).filter(Objects::nonNull).collect(toSet())
            );
        }
        catch (IOException e) {
            log.error("Error while fetching authors", e);
            return EMPTY_DATA;
        }
    }

    private Author convert(String name) {
        if (NullSafe.isEmpty(name)) return null;

        Author a = authorService.find(name);
        return a == null ? new Author().setName(name) : a;
    }

    private String extractAsin(String url) {
        Matcher m = ASIN_PATTERN.matcher(url);
        if (m.find()) {
            return m.group(1).toUpperCase();
        }
        return null;
    }

    public record AmazonData(String asin, String isbn, Set<Author> authors) {
        public boolean isKindle() {
            return asin != null && asin.startsWith(KINDLE_PREFIX);
        }
    }
}
