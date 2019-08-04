package net.scholnick.lbdb.domain;

import net.scholnick.lbdb.util.NullSafe;

import java.nio.file.Path;
import java.util.*;


public final class Book extends BasicObject implements Validatable, Comparable<Book> {
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

//	public boolean isComplete() {
//		return getPublishedYear() != null && getIsbn() != null && getNumberOfPages() != null;
//	}

	@Override
	public String validate() {
		if (NullSafe.isEmpty(getTitle())) {
			return "Title is required";
		}

		if (getType() == null) {
			return "Type is required";
		}

		if (authors.isEmpty()) {
			return "At least one author must be specified";
		}

		if (!NullSafe.isEmpty(getPublishedYear()) && !getPublishedYear().matches("[0-9]+")) {
			return "Published year must be in the proper format";
		}

		return "";
	}

	public String getAuthorNames() {
		Collections.sort(getAuthors());

		StringBuilder buf = new StringBuilder(228);

		for (Author a : getAuthors()) {
			buf.append(a.getName()).append("; ");
		}

		if (buf.length() > 2) {
			buf.setLength(buf.length() - 2);
		}

		return buf.toString();
	}

	@Override
	public int compareTo(Book o) {
		int cmp = getTitle().compareTo(o.getTitle());
		if (cmp != 0)
			return cmp;

		return getAuthorNames().compareTo(o.getAuthorNames());
	}

	public Author getPrimaryAuthor() {
		for (Author each : getAuthors()) {
			if (each.isEditor()) {
				return each;
			}
		}

		return getAuthors().get(0);
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

//	public Set<Author> getEditors() {
//		Set<Author> editors = new HashSet<Author>();
//
//		for (Author each : getAuthors()) {
//			if (each.isEditor()) {
//				editors.add(each);
//			}
//		}
//		return editors;
//	}

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
