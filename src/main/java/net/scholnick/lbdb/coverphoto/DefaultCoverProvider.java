package net.scholnick.lbdb.coverphoto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DefaultCoverProvider - Implementation of CoverProvider that uses Google and OpenLibrary clients to search for cover information by ISBN.
 */
@Service("defaultCoverProvider")
@Primary
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
        for (CoverProvider provider : List.of(googleClient, openLibraryClient, hardcoverClient, amazonCoverService)) {
            byte[] data = provider.downloadCover(inquiry);
            if (data != null && data.length > 0) {
                return data;
            }
        }

        return null;
    }
}
