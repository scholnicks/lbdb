package net.scholnick.lbdb;

import com.zaxxer.hikari.*;
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
import java.util.*;

@SpringBootApplication
public class BooksApp {
    public static void main(String... args) {
        System.setProperty("apple.awt.application.appearance","system");
        System.setProperty("apple.laf.useScreenMenuBar","true");
        System.setProperty("com.apple.mrj.application.live-resize","true");

        ApplicationContext context = new SpringApplicationBuilder(BooksApp.class).headless(false).run(args);

        if (args != null && Arrays.asList(args).contains("--export")) {
            context.getBean(ExportService.class).export();
            System.exit(0);
        }

        // start the GUI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.exit(-1);
        }
        EventQueue.invokeLater(() -> context.getBean(BooksDB.class).init());
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean(destroyMethod="close")
    public DataSource dataSource() {
        Properties properties = new Properties();
        properties.put("autoCommit","false");
//        properties.put("driverClassName","org.sqlite.JDBC");
        properties.put("jdbcUrl","production".equalsIgnoreCase(System.getProperty("lbdb.environment","dev")) ? PROD_DB : DEV_DB);

        return new HikariDataSource(new HikariConfig(properties));
    }

    private static final String DEV_DB  = "jdbc:sqlite:/Users/steve/development/java/lbdb/sql/test.db";
    private static final String PROD_DB = "jdbc:sqlite:/Users/steve/data/lbdb.db";
}
