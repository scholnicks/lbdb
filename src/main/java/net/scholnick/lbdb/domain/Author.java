package net.scholnick.lbdb.domain;

import java.util.Objects;
import java.util.Set;

public final class Author extends BasicObject implements Comparable<Author> {
	private String   name;
	private String   webSite;
	private boolean  editor;
	private String   addedTimestamp;

	public static Author of(String name) {
		Author a = new Author();
		a.name = name;
		return a;
	}

	public String lastName() {
		int lastSpace = name.lastIndexOf(' ');
		if (lastSpace == -1) return name;

		String lastName = name.substring(lastSpace).trim();
		if (SUFFIXES.contains(lastName.toLowerCase())) {
			lastSpace = name.substring(0,lastSpace).trim().lastIndexOf(' ');
			if (lastSpace == -1) return name;
			return name.substring(0,lastSpace).trim();
		}
		else {
			return lastName;
		}
	}

	@Override
	public int compareTo(Author o) {
		return name.compareTo(o.name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Author author = (Author) o;
		return Objects.equals(name, author.name) && Objects.equals(webSite, author.webSite);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, webSite);
	}

	@Override
	public String toString() {
		return "Author{" +
				"name='" + name + '\'' +
				", webSite='" + webSite + '\'' +
				", editor=" + editor +
				", addedTimestamp='" + addedTimestamp + '\'' +
				'}';
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebSite() {
		return this.webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public void setEditor(boolean editor) {
		this.editor = editor;
	}

	public boolean isEditor() {
		return editor;
	}

	public String getAddedTimestamp() {
		return addedTimestamp;
	}

	public void setAddedTimestamp(String addedTimestamp) {
		this.addedTimestamp = addedTimestamp;
	}

	private static final Set<String> SUFFIXES = Set.of("i","ii","iii","iv","jr","sr","jr.","sr.");
}
