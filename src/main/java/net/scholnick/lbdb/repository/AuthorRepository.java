package net.scholnick.lbdb.repository;

import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.NullSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

/**
 * AuthorRepository is a repository for managing {@link Author} records in the database.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@Repository
public class AuthorRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Retrieve all authors. */
    public List<Author> all() {
        return jdbcTemplate.query("select * from author",this::mapRow);
    }

    /** Search for authors whose names start with the given criteria. */
    public List<Author> search(String criteria) {
        if (NullSafe.isEmpty(criteria)) return Collections.emptyList();

        String sql = "select * from author where auth_id is not null" +
                " and lower(auth_name) like lower(?)" +
                " order by auth_name";

        return jdbcTemplate.query(sql, this::mapRow, criteria.toLowerCase() + "%");
    }

    /** Create a new author record. */
    public Long create(Author a) {
        jdbcTemplate.update("insert into author(auth_name) values(?)",a.getName());
        return jdbcTemplate.queryForObject("select last_insert_rowid()",Long.class);
    }

    /** Delete an author record. */
    public void delete(Author a) {
        jdbcTemplate.update("delete from author_book_xref where auth_id=?", a.getId());
        jdbcTemplate.update("delete from author where auth_id=?", a.getId());
    }

    /** Get all authors for a given book. */
    public List<Author> get(Book b) {
        return jdbcTemplate.query(
            "select * from author where auth_id in (select auth_id from author_book_xref where book_id=?) order by auth_name",
            this::mapRow,
            b.getId()
        );
    }

    /** Get all editor IDs for a given book. */
    public Set<Long> getEditors(Book b) {
        return new HashSet<>(jdbcTemplate.queryForList(
            "select auth_id from author_book_xref where book_id=? and abx_editor='y'",
            new Long[] {b.getId()},
            new int[] {Types.INTEGER},
            Long.class
        ));
    }

    /** Get an author by ID. */
    public Author get(Long id) {
        return jdbcTemplate.queryForObject("select * from author where auth_id=?", this::mapRow, id);
    }

    /** Count the total number of author records. */
    public Long count() {
        return jdbcTemplate.queryForObject("select count(auth_id) from author", Long.class);
    }

    /** Update an existing author record. */
    public void update(Author a) {
        jdbcTemplate.update("update author set auth_name=? where auth_id=?", a.getName(), a.getId());
    }

    private Author mapRow(ResultSet rs, int rowCount) throws SQLException {
        Author a = new Author();
        a.setId(rs.getLong("auth_id"));
        a.setName(rs.getString("auth_name"));
        return a;
    }
}
