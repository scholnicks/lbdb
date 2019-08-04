package net.scholnick.lbdb.dao;


import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.domain.Book;
import net.scholnick.lbdb.util.NullSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class AuthorDAOImpl implements AuthorDAO {
    private static final Logger log = LoggerFactory.getLogger(AuthorDAOImpl.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public AuthorDAOImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Author> search(Author a) {
		String sql = BASE_SEARCH;
		List<String> criteria = new ArrayList<>(2);
		
		if (a.getId() != null) {
			sql += " and auth_id=?";
			criteria.add(a.getId().toString());
		}
		
		if (! NullSafe.isEmpty(a.getFirstName())) {
			sql += " and lower(auth_first_name) like lower(?)";
			criteria.add("%" + a.getFirstName() + "%");
		}
		
		if (! NullSafe.isEmpty(a.getLastName())) {
			sql += " and lower(auth_last_name) like lower(?)";
			criteria.add("%" + a.getLastName() + "%");
		}
		
		sql += " order by auth_last_name";
		
		return jdbcTemplate.query(sql,this::mapRow,criteria.toArray());
	}
    
	private static final String BASE_SEARCH = "select * from author where auth_id is not null";
	
	@Override
	public Long create(final Author a) {
		log.debug("Creating author " + a);
        
		KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update( (connection) -> {
                PreparedStatement s = connection.prepareStatement(ADD);
                s.setString(1,a.getLastName());
                s.setString(2,a.getFirstName());
                s.setString(3,a.getWebSite());
    				return s;
            },
            keyHolder
        );

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
	}
	
	private static final String ADD = "insert into author(auth_last_name,auth_first_name,auth_website) values(?,?,?)";

	@Override
	public void delete(Author a) {
		jdbcTemplate.update("delete from author_book_xref where auth_id=?",a.getId());
		jdbcTemplate.update("delete from author where auth_id=?",a.getId());
	}

	@Override
	public List<Author> get(Book b) {
		return jdbcTemplate.query(
		   "select * from author where auth_id in (select auth_id from author_book_xref where book_id=?) order by auth_last_name",
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
		jdbcTemplate.update(
			UPDATE,
			a.getLastName(),
			a.getFirstName(),
			a.getWebSite(),
			a.getId()
		);
	}

	private static final String UPDATE = 
		"update author "
		+ "set auth_last_name=?,auth_first_name=?,auth_website=?,auth_modified_date=datetime(current_timestamp, 'localtime') "
		+ "where auth_id=?";

	private Author mapRow(ResultSet rs, int rowCount) throws SQLException {
		Author a = new Author();
		a.setId(rs.getLong("auth_id"));
		a.setLastName(rs.getString("auth_last_name"));
		a.setFirstName(rs.getString("auth_first_name"));
		a.setWebSite(rs.getString("auth_website"));
		a.setAddedTimestamp(rs.getString("auth_created_date"));
		return a;
    }
}
