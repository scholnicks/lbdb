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

import static java.util.stream.Collectors.toSet;

@Service
public class AmazonDataProvider {
    private final AuthorService authorService;

    private static final Logger log = LoggerFactory.getLogger(AmazonDataProvider.class);
    private static final String BASE_URL = "https://www.amazon.com/dp/%s";

    @Autowired
    public AmazonDataProvider(AuthorService authorService) {
        this.authorService = authorService;
    }

    public Set<Author> authors(String identifier) {
        try {
            Document doc = Jsoup.connect(BASE_URL.formatted(identifier))
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

            return names.stream()
                .map(this::convert)
                .filter(Objects::nonNull)
                .collect(toSet())
            ;
        }
        catch (IOException e) {
            log.error("Error while fetching authors", e);
            return Set.of();
        }
    }

    private Author convert(String name) {
        if (NullSafe.isEmpty(name)) return null;

        Author a = authorService.find(name);
        return a == null ? new Author().setName(name) : a;
    }
}
