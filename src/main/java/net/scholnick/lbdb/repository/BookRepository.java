package net.scholnick.lbdb.repository;

import net.scholnick.lbdb.domain.*;
import net.scholnick.lbdb.util.NullSafe;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class BookRepository {
    private static final Logger log = LoggerFactory.getLogger(BookRepository.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Book get(Long id) {
        return jdbcTemplate.queryForObject("select * from book where book_id=?", this::mapRow, id);
    }

    public Long create(final Book b) {
        // https://stackoverflow.com/questions/4298302/sqlite-jdbc-driver-not-supporting-return-generated-keys
        jdbcTemplate.update(ADD,
            b.getTitle(),
            b.getType().getId(),
            b.getMedia().getId(),
            b.isAnthology() ? "y" : "n",
            b.getSeries(),
            b.getPublishedYear(),
            b.getIsbn(),    
            Objects.requireNonNullElse(b.getNumberOfPages(),0),
            b.getComments()
        );

        return jdbcTemplate.queryForObject("select last_insert_rowid()",Long.class);
    }

    private static final String ADD =
        "insert into book(book_title,bot_id,med_id,book_anthology,book_series,book_published_year,book_isbn,book_number_of_pages,book_comments) " +
        "values(?,?,?,?,?,?,?,?,?)";

    public void addJoinRecords(Book b) {
        removeJoinRecords(b);

        for (Author a : b.getAuthors()) {
            jdbcTemplate.update(ADD_JOIN, b.getId(), a.getId(), a.isEditor() ? 'y' : 'n');
        }
    }

    private static final String ADD_JOIN = "insert into author_book_xref(book_id,auth_id,abx_editor) values(?,?,?)";

    public void update(Book b) {
        log.debug("Updating " + b);

        jdbcTemplate.update(UPDATE,
            b.getTitle(),
            b.getType().getId(),
            b.getMedia().getId(),
            b.isAnthology() ? 'y' : 'n',
            b.getSeries(),
            b.getPublishedYear(),
            b.getIsbn(),
            b.getNumberOfPages(),
            b.getComments(),
            b.getId()
        );
    }

    private static final String UPDATE =
        "update book " +
        "set book_title=?,bot_id=?,med_id=?,book_anthology=?,book_series=?,"
        + "  book_published_year=?,book_isbn=?,book_number_of_pages=?,book_comments=?, "
        + "  book_modified_date=datetime(current_timestamp,'localtime') " +
        "where book_id=?";

    public void delete(Book b) {
        jdbcTemplate.update("delete from book where book_id=?", b.getId());
    }

    public void removeJoinRecords(Book b) {
        jdbcTemplate.update("delete from author_book_xref where book_id=?", b.getId());
    }

    public Long count() {
        return jdbcTemplate.queryForObject("select count(book_id) from book", Long.class);
    }

    public List<Book> search(Book b) {
        String sql = BASE_SEARCH;
        List<String> criteria = new ArrayList<>(2);

        if (b.getId() != null) {
            sql += " and book_id=?";
            criteria.add(b.getId().toString());
        }

        if (!NullSafe.isEmpty(b.getTitle())) {
            sql += " and lower(book_title) like lower(?)";
            criteria.add("%" + b.getTitle() + "%");
        }

        if (!NullSafe.isEmpty(b.getSeries())) {
            sql += " and lower(book_series) like lower(?)";
            criteria.add("%" + b.getSeries() + "%");
        }

        if (b.getMedia() != null) {
            sql += " and med_id=? ";
            criteria.add(String.valueOf(b.getMedia().getId()));
        }

        if (!b.getAuthors().isEmpty()) {
            sql += " and book_id in (select book_id from author_book_xref where auth_id in" +
                    " (select auth_id from author where auth_id is not null";

            Author a = b.getAuthors().getFirst();

            if (a.getId() != null) {
                sql += " and auth_id=?";
                criteria.add(a.getId().toString());
            }
            else {
                if (!NullSafe.isEmpty(a.getName())) {
                    sql += " and lower(auth_name) like lower(?)";
                    criteria.add("%" + a.getName() + "%");
                }
            }

            sql += "))";
        }

        sql += " order by book_title";

        return jdbcTemplate.query(sql, this::mapRow, criteria.toArray());
    }

    private static final String BASE_SEARCH = "select * from book where book_id is not null";

    private Book mapRow(ResultSet rs, int rowCount) throws SQLException {
        Book b = new Book();

        b.setId(rs.getLong("book_id"));
        b.setTitle(rs.getString("book_title"));
        b.setComments(rs.getString("book_comments"));
        b.setAnthology("y".equalsIgnoreCase(rs.getString("book_anthology")));
        b.setSeries(rs.getString("book_series"));
        b.setPublishedYear(rs.getString("book_published_year"));
        b.setIsbn(rs.getString("book_isbn"));
        b.setNumberOfPages(rs.getInt("book_number_of_pages"));
        b.setType(BookType.from(rs.getInt("bot_id")));
        b.setMedia(Media.from(rs.getInt("med_id")));
        b.setAddedTimestamp(rs.getString("book_created_date"));

        return b;
    }
}
