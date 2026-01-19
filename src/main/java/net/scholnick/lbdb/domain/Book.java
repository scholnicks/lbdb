package net.scholnick.lbdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 * Book is a representation of a physical or e-book.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@Accessors(chain=true)
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public final class Book implements Comparable<Book> {
    private Long id;
    private String title;
    private BookType type;
    private Media media;
    private String series;
    private String addedTimestamp;      // TODO: change this to a LocalDateTime
    private String publishedYear;
    private String isbn;
    private String comments;
    private boolean anthology;
    private Integer numberOfPages;
    private String coverURL;
    private List<Author> authors = new ArrayList<>();

    /** Parse the year from a release date string in the format "YYYY-MM-DD" or "YYYY". */
    public static String parseYear(String releaseDate) {
        if (releaseDate == null || releaseDate.isBlank()) return null;
        String[] parts = releaseDate.split("-");
        return parts.length > 0 ? parts[0] : null;
    }

    public String getAuthorNames() {
        return authors.stream().sorted().map(Author::getName).collect(joining(", "));
    }

    @Override
    public int compareTo(Book o) {
        int cmp = getTitle().compareTo(o.getTitle());
        if (cmp != 0) return cmp;
        return getAuthorNames().compareTo(o.getAuthorNames());
    }

    public void clearAuthors() {
        authors.clear();
    }

    public void addAuthor(Author a) {
        if (! authors.contains(a)) {
            authors.add(a);
        }
    }

    public void addEditor(Author editor) {
        if (! authors.contains(editor)) {
            editor.setEditor(true);
            authors.add(editor);
        }
    }
}
