package net.scholnick.lbdb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.scholnick.lbdb.service.ExportService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.util.Properties;

@SpringBootApplication
public class BooksApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.exit(-1);
        }

        ApplicationContext context = new SpringApplicationBuilder(BooksApp.class).headless(false).run(args);
        EventQueue.invokeLater(() -> context.getBean(BooksDB.class).init());
        context.getBean(ExportService.class).export();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

//    @Bean
//    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }

    @Bean(destroyMethod="close")
    public DataSource dataSource() {
        // https://www.baeldung.com/hikaricp
        // https://stackoverflow.com/questions/26490967/how-do-i-configure-hikaricp-in-my-spring-boot-app-in-my-application-properties-f
        // implementation group: 'com.zaxxer', name: 'HikariCP', version: '3.4.5'

        Properties properties = new Properties();
        properties.put("autoCommit","false");
//        properties.put("driverClassName","org.sqlite.JDBC");
        properties.put("jdbcUrl","production".equalsIgnoreCase(System.getProperty("lbdb.database.type","dev")) ? PROD_DB_LOCATION : DEV_DB_LOCATION);

        return new HikariDataSource(new HikariConfig(properties));
    }

    private static final String DEV_DB_LOCATION  = "jdbc:sqlite:/Users/steve/development/java/lbdb/sql/test.db";
    private static final String PROD_DB_LOCATION = "jdbc:sqlite:/Users/steve/Documents/lbdb.db";
}
