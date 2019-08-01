package net.scholnick.lbdb.dao;


import net.scholnick.lbdb.util.NullSafe;

/**
 * @author Steve Scholnick
 */
public final class BookSearchFactory {
	private final StringBuilder sql;
	private final String title;
	private final String authorLastName;
	private final String authorFirstName;
	private final String series;
	private final String media;

	public BookSearchFactory(String title, String authorLastName, String authorFirstName, String series, String media) {
		sql = new StringBuilder(512);

		this.title = NullSafe.asNull(title);
		this.authorFirstName = NullSafe.asNull(authorFirstName);
		this.authorLastName = NullSafe.asNull(authorLastName);
		this.series = NullSafe.asNull(series);
		this.media = NullSafe.asNull(series);
	}

	public String toSQL() {
		sql.append(SELECT);

		addTitleClause();

		if (authorLastName != null || authorFirstName != null) {
			addLastNameClause();
			addFirstNameClause();
		}

		if (series != null) {
			sql.append("and  book_series like '%").append(series.replaceAll("'", "''")).append("%' ");
		}

		if (media != null) {
			sql.append("and book_media = ? ");
		}

		sql.append(ORDER_BY);

		return toString();
	}

	private void addTitleClause() {
		if (title != null) {
			sql.append("and book_title like '%").append(title.replaceAll("'", "''")).append("%' ");
		}
	}

	private void addLastNameClause() {
		if (authorLastName != null) {
			sql.append("and auth_last_name like '").append(authorLastName.replaceAll("'", "''")).append("%' ");
		}
	}

	private void addFirstNameClause() {
		if (authorFirstName != null) {
			sql.append("and auth_first_name like '").append(authorFirstName.replaceAll("'", "''")).append("%' ");
		}
	}

	public String toString() {
		return sql.toString();
	}

	private static final String SELECT = "select * from v_search where book_id is not null ";

	private static final String ORDER_BY = "order by book_title";
}
