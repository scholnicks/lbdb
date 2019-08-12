package net.scholnick.lbdb.dao;


import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.domain.Book;
import net.scholnick.lbdb.util.NullSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class AuthorDAOImpl implements AuthorDAO {
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public AuthorDAOImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Author> search(String criteria) {
		if (NullSafe.isEmpty(criteria)) return Collections.emptyList();

		String sql = "select * from author where auth_id is not null" +
		 	" and lower(auth_name) like lower(?)" +
		 	" order by auth_name";

		return jdbcTemplate.query(sql,this::mapRow,criteria.toLowerCase() + "%");
	}

	@Override
	public Long create(Author a) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update( (connection) -> {
                PreparedStatement s = connection.prepareStatement("insert into author(auth_name,auth_website) values(?,?,?)");
                s.setString(1,a.getName());
                s.setString(2,a.getWebSite());
				return s;
            },
            keyHolder
        );

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
	}
	
	@Override
	public void delete(Author a) {
		jdbcTemplate.update("delete from author_book_xref where auth_id=?",a.getId());
		jdbcTemplate.update("delete from author where auth_id=?",a.getId());
	}

	@Override
	public List<Author> get(Book b) {
		return jdbcTemplate.query(
		   "select * from author where auth_id in (select auth_id from author_book_xref where book_id=?) order by auth_name",
		   this::mapRow,
		   b.getId()
		);
	}

	@Override
	public Set<Long> getEditors(Book b) {
		return new HashSet<>(jdbcTemplate.queryForList(
			"select auth_id from author_book_xref where book_id=? and abx_editor='y'",
			new Long[] { b.getId() },
			Long.class
		));
	}
	
	@Override
	public Author get(Long id) {
		return jdbcTemplate.queryForObject("select * from author where auth_id=?",this::mapRow,id);
	}

	@Override
	public Long count() {
		return jdbcTemplate.queryForObject("select count(auth_id) from author",Long.class);
	}
	
	@Override
	public void update(Author a) {
		jdbcTemplate.update(UPDATE,
			a.getName(),
			a.getWebSite(),
			a.getId()
		);
	}

	private static final String UPDATE = 
		"update author "
		+ "set auth_last_name=?,auth_first_name=?,auth_website=?,auth_modified_date=datetime(current_timestamp,'localtime') "
		+ "where auth_id=?";

	private Author mapRow(ResultSet rs, int rowCount) throws SQLException {
		Author a = new Author();
		a.setId(rs.getLong("auth_id"));
		a.setName(rs.getString("auth_name"));
		a.setWebSite(rs.getString("auth_website"));
		a.setAddedTimestamp(rs.getString("auth_created_date"));
		return a;
    }
}
