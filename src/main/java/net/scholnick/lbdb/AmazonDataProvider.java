package net.scholnick.lbdb;

import net.scholnick.lbdb.coverphoto.CoverPhotoService;
import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.util.NullSafe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.regex.*;

import static java.util.stream.Collectors.toSet;

/**
 * AmazonDataProvider is a service that fetches author data from Amazon based on a given identifier (ASIN or URL).
 * It uses Jsoup to scrape the Amazon product page and extract relevant information such as ASIN.
 */
@Service
public class AmazonDataProvider {
    private final AuthorService authorService;

    private static final String BASE_URL = "https://www.amazon.com/dp/%s";
    private static final Pattern ASIN_PATTERN = Pattern.compile("/(?:dp|gp/product|product)/([A-Z0-9]{10})(?:[/?]|$)", Pattern.CASE_INSENSITIVE);
    private static final String KINDLE_PREFIX = "B0";
    private static final AmazonData EMPTY_DATA = new AmazonData(null, null, null, Set.of(), null);

    private static final Logger log = LoggerFactory.getLogger(AmazonDataProvider.class);

    @Autowired
    public AmazonDataProvider(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * Fetches author data from Amazon for the given identifier (ASIN or URL).
     *
     * @param identifier The ASIN or URL of the Amazon product.
     * @return An {@link AmazonData} object containing the ASIN, ISBN (if applicable), and a set of authors.
     */
    public AmazonData get(String identifier) {
        try {
            String asin = identifier.startsWith("http") ? extractAsin(identifier) : identifier;
            if (asin == null) return EMPTY_DATA;

            Document doc = Jsoup.connect(BASE_URL.formatted(asin))
                .userAgent(CoverPhotoService.USER_AGENT)
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
                doc.selectFirst("#productTitle") == null ? null : Objects.requireNonNull(doc.selectFirst("#productTitle")).text().strip(), // quiet IJ wit the redundant null check
                names.stream().map(this::convert).filter(Objects::nonNull).collect(toSet()),
                findCoverUrl(doc)
            );
        }
        catch (IOException e) {
            log.error("Error while fetching authors", e);
            return EMPTY_DATA;
        }
    }

    /** Finds the cover image URL from the Amazon product page. */
    private String findCoverUrl(Document document)  {
        Element image = document.selectFirst("#landingImage, #imgBlkFront");

        if (image == null) {
            return null;
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
            return null;
        }

        return src;
    }

    /** Finds the largest image URL from the JSON string of dynamic images. */
    private String findLargestImage(String json)  {
        Map<String, int[]> images = new ObjectMapper().readValue(json, new TypeReference<>() {});

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

    /** Represents the data fetched from Amazon. */
    public record AmazonData(String asin, String isbn, String title, Set<Author> authors, String coverURL) {
        public boolean isKindle() {
            return asin != null && asin.startsWith(KINDLE_PREFIX);
        }
    }
}
