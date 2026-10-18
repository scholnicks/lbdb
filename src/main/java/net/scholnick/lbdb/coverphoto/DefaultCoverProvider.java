package net.scholnick.lbdb.coverphoto;

import lombok.extern.log4j.Log4j2;
import net.scholnick.lbdb.util.NullSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DefaultCoverProvider - Implementation of CoverProvider that uses Google and OpenLibrary clients to search for cover information by ISBN.
 */
@Service("defaultCoverProvider")
@Primary
@Log4j2
public class DefaultCoverProvider implements CoverProvider {
    private final GoogleClient       googleClient;
    private final OpenLibraryClient  openLibraryClient;
    private final HardcoverClient    hardcoverClient;
    private final AmazonCoverService amazonCoverService;

    @Autowired
    public DefaultCoverProvider(GoogleClient googleClient, OpenLibraryClient openLibraryClient, HardcoverClient hardcoverClient, AmazonCoverService amazonCoverService) {
        this.googleClient       = googleClient;
        this.openLibraryClient  = openLibraryClient;
        this.hardcoverClient    = hardcoverClient;
        this.amazonCoverService = amazonCoverService;
    }

    @Override
    public byte[] downloadCover(CoverInquiry inquiry) {
        try {
            log.info("Searching for cover image for inquiry: {}", inquiry);
            for (CoverProvider provider : List.of(googleClient, openLibraryClient, hardcoverClient, amazonCoverService)) {
                log.info("Trying to download cover image for inquiry: {}", provider);
                byte[] data = provider.downloadCover(inquiry);
                if (!NullSafe.isEmpty(data)) {
                    log.info("Found cover image for inquiry: {}", inquiry);
                    return data;
                }
            }

            return null;
        }
        catch (Exception e) {
            log.error("Error downloading cover image for inquiry: {}", inquiry, e);
            return null;
        }
    }
}
