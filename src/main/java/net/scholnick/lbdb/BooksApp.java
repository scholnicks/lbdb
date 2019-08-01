package net.scholnick.lbdb;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.awt.*;

@SpringBootApplication
public class BooksApp {
    public static void main(String[] args) {
        var ctx = new SpringApplicationBuilder(BooksApp.class).headless(false).run(args);
        EventQueue.invokeLater(() -> {
            var ex = ctx.getBean(BooksDB.class);
            ex.init();
        });
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource  = new BasicDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:/Users/steve/development/java/lbdb/sql/test.db");
        return dataSource;
    }
}
