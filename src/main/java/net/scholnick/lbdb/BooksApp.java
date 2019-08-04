package net.scholnick.lbdb;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.awt.*;

@SpringBootApplication
public class BooksApp {
    public static void main(String[] args) {
        ApplicationContext context = new SpringApplicationBuilder(BooksApp.class).headless(false).run(args);
        EventQueue.invokeLater( () -> context.getBean(BooksDB.class).init() );
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource  = new BasicDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");

        String url = DEV_DB_LOCATION;
        if ("production".equalsIgnoreCase(System.getProperty("lbdb.database.type","dev"))) {
            url = PROD_DB_LOCATION;
        }

        dataSource.setUrl("jdbc:sqlite:" + url);
        return dataSource;
    }

    private static final String DEV_DB_LOCATION  = "/Users/steve/development/java/lbdb/sql/test.db";
    private static final String PROD_DB_LOCATION = "/Users/steve/Documents/lbdb.db";
}
