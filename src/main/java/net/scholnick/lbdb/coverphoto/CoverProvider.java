package net.scholnick.lbdb.coverphoto;

/**
 * CoverProvider defines a service that can download cover photos for books.
 */
@FunctionalInterface
public interface CoverProvider {
    /** Download the cover photo for a book given an inquiry. */
    byte [] downloadCover(CoverInquiry inquiry);

    /** Inquiry record for cover photo requests */
    record CoverInquiry(String isbn, String title, String url) {}

    /** User agent string to use for HTTP requests */
    String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
        "AppleWebKit/537.36 (KHTML, like Gecko) " +
        "Chrome/140.0 Safari/537.36";
}
