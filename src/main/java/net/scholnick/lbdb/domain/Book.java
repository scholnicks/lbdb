package net.scholnick.lbdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.joining;

@Accessors(chain=true)
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public final class Book implements Comparable<Book> {
    private Long id;
    private String title;
    private BookType type;
    private Media media;
    private String series;
    private String addedTimestamp;
    private String publishedYear;
    private String isbn;
    private String comments;
    private boolean anthology;
    private Integer numberOfPages;

    private List<Author> authors;

    private Path coverPhotoPath;

    public Book() {
        authors = new ArrayList<>();
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

    public Author getPrimaryAuthor() {
        return authors.stream().sorted().filter(Author::isEditor).findFirst().orElse(authors.get(0));
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
