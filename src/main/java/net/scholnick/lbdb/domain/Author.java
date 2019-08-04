package net.scholnick.lbdb.domain;

import net.scholnick.lbdb.util.NullSafe;

import java.util.Objects;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
public final class Author extends BasicObject implements Validatable, Comparable<Author> {
	private String   lastName;
	private String   firstName;
	private String   webSite;
	private boolean  editor;
	private String addedTimestamp;

	public Author() {
		// empty
	}

	public Author(String lastName, String firstName) {
		this();
		this.lastName = lastName;
		this.firstName = firstName;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof Author)) { return false; }

		Author other = (Author) obj;
		return Objects.equals(lastName,other.lastName) && Objects.equals(firstName,other.firstName);
	}

	@Override
	public String validate() {
		if (NullSafe.isEmpty(getFirstName()))
			return "First Name is required";

		if (NullSafe.isEmpty(getLastName()))
			return "Last Name is required";

		if (! NullSafe.isEmpty(getWebSite())) {
			if (!(getWebSite().startsWith("http://") || getWebSite().startsWith("https://"))) {
				return "Web site URL must be in the proper format";
			}
		}

		return "";
	}

	public String getName() {
		return NullSafe.concatenate(getLastName(), ",", getFirstName());
	}

	public String toCanonicalName() {
		return NullSafe.toCanonical(NullSafe.notNullSubst(getFirstName(),true) + NullSafe.notNullSubst(getLastName(),true));
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(Author other) {
		if (this.editor && !other.editor)
			return -1;
		if (!this.editor && other.editor)
			return 1;

		int cmp = NullSafe.compare(getLastName(), other.getLastName());
		if (cmp != 0)
			return cmp;

		return NullSafe.compare(getFirstName(), other.getFirstName());
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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
}
