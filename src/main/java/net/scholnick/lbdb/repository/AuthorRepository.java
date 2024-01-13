package net.scholnick.lbdb.repository;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.domain.Book;
import net.scholnick.lbdb.util.NullSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class AuthorRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Author> all() {
        return jdbcTemplate.query("select * from author",this::mapRow);
    }

    public List<Author> search(String criteria) {
        if (NullSafe.isEmpty(criteria)) return Collections.emptyList();

        String sql = "select * from author where auth_id is not null" +
                " and lower(auth_name) like lower(?)" +
                " order by auth_name";

        return jdbcTemplate.query(sql, this::mapRow, criteria.toLowerCase() + "%");
    }

    public Long create(Author a) {
        jdbcTemplate.update("insert into author(auth_name) values(?)",a.getName());
        return jdbcTemplate.queryForObject("select last_insert_rowid()",Long.class);
    }

    public void delete(Author a) {
        jdbcTemplate.update("delete from author_book_xref where auth_id=?", a.getId());
        jdbcTemplate.update("delete from author where auth_id=?", a.getId());
    }

    public List<Author> get(Book b) {
        return jdbcTemplate.query(
                "select * from author where auth_id in (select auth_id from author_book_xref where book_id=?) order by auth_name",
                this::mapRow,
                b.getId()
        );
    }

    public Set<Long> getEditors(Book b) {
        return new HashSet<>(jdbcTemplate.queryForList(
            "select auth_id from author_book_xref where book_id=? and abx_editor='y'",
            new Long[] {b.getId()},
            new int[] {Types.INTEGER},
            Long.class
        ));
    }

    public Author get(Long id) {
        return jdbcTemplate.queryForObject("select * from author where auth_id=?", this::mapRow, id);
    }

    public Long count() {
        return jdbcTemplate.queryForObject("select count(auth_id) from author", Long.class);
    }

    public void update(Author a) {
        jdbcTemplate.update("update author set auth_name=? where auth_id=?", a.getName(), a.getId());
    }

    public List<String> allNames() {
        return jdbcTemplate.queryForList("select a.auth_name as \"Name\" from Author a order by 1",String.class);
    }

    private Author mapRow(ResultSet rs, int rowCount) throws SQLException {
        Author a = new Author();
        a.setId(rs.getLong("auth_id"));
        a.setName(rs.getString("auth_name"));
        return a;
    }
}
