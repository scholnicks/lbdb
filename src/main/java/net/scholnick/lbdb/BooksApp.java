package net.scholnick.lbdb;

import com.zaxxer.hikari.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import net.scholnick.lbdb.util.*;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.*;

import javax.sql.DataSource;
import java.awt.*;
import java.util.Properties;

/**
 * BooksApp is the main entry point for the application.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@SpringBootApplication
public class BooksApp {
    private static final Logger log = LoggerFactory.getLogger(BooksApp.class);

    static void main(String... args) {
        try {
            System.setProperty("apple.awt.application.appearance","system");
            System.setProperty("apple.laf.useScreenMenuBar","true");
            System.setProperty("com.apple.mrj.application.live-resize","true");

            ApplicationContext context = new SpringApplicationBuilder(BooksApp.class).headless(false).run(args);

            GUIUtilities.setLookAndFeel();
            IconFontSwing.register(FontAwesome.getIconFont());
            EventQueue.invokeLater(() -> context.getBean(BooksDB.class).init());
        }
        catch (ApplicationException | BeansException e) {
            log.error("Unable to start lbdb",e);
            System.exit(-1);
        }
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(10_000);
        rf.setReadTimeout(15_000);

        return RestClient.builder().requestFactory(rf).build();
    }

    /** Configure the DataSource based on the environment. */
    @Bean(destroyMethod="close")
    public DataSource dataSource() {
        Properties properties = new Properties();
        properties.put("autoCommit","false");
        String database = "production".equalsIgnoreCase(System.getProperty("lbdb.environment", "dev")) ? PROD_DB : DEV_DB;
        properties.put("jdbcUrl", database);
        log.info("Connecting to {}",database);

        return new HikariDataSource(new HikariConfig(properties));
    }

    private static final String DEV_DB  = "jdbc:sqlite:/Users/steve/development/java/lbdb/sql/test.db";
    private static final String PROD_DB = "jdbc:sqlite:/Users/steve/data/lbdb.db";
}
