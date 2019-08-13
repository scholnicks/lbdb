package net.scholnick.lbdb.domain;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;


public final class Book extends BasicObject implements Comparable<Book> {
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

	public List<Author> getAuthors() {
		return authors;
	}

	public void clearAuthors() {
		authors.clear();
	}

	public void addAuthor(Author a) {
		if (! authors.contains(a)) {
			authors.add(a);
		}
	}

	public void setEditors(Set<Author> editors) {
		for (Author a : editors) {
			for (Author each : getAuthors()) {
				if (a.equals(each))
					each.setEditor(true);
				else
					each.setEditor(false);
			}
		}
	}

	@Override
	public String toString() {
		return getTitle();
	}

	public boolean isAnthology() {
		return anthology;
	}

	public void setAnthology(boolean anthology) {
		this.anthology = anthology;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BookType getType() {
		return this.type;
	}

	public void setType(BookType type) {
		this.type = type;
	}

	public String getSeries() {
		return this.series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getPublishedYear() {
		return this.publishedYear;
	}

	public void setPublishedYear(String publishedYear) {
		this.publishedYear = publishedYear;
	}

	public String getIsbn() {
		return this.isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Integer getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public Media getMedia() {
		return media;
	}

	public Path getCoverPhotoPath() {
		return coverPhotoPath;
	}

	public void setCoverPhotoPath(Path coverPhotoPath) {
		this.coverPhotoPath = coverPhotoPath;
	}

	public String getAddedTimestamp() {
		return addedTimestamp;
	}

	public void setAddedTimestamp(String addedTimestamp) {
		this.addedTimestamp = addedTimestamp;
	}
}
