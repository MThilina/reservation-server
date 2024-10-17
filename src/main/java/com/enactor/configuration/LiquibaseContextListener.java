package com.enactor.configuration;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * <p>
 *     This class loading the liquibase context and execute it throw the jdbc layer
 *     Since we are not using any kind of framework it should create the connection to database and provide execution manually
 * </p>
 */
@WebListener
public class LiquibaseContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(LiquibaseContextListener.class);
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection connection = DatabaseConfig.getConnection().getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts());

            logger.info("Liquibase migration executed successfully");
        } catch (SQLException | LiquibaseException e) {
            logger.error("Error occurred during data migration {}",e.getMessage());
            throw new RuntimeException("Failed to execute Liquibase migration", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Deregister JDBC drivers to prevent memory leaks
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                logger.info("Deregistered JDBC driver: {}", driver);
            } catch (SQLException e) {
                logger.error("Error unregistering driver: {}",e.getMessage());
            }

            // Shut down AbandonedConnectionCleanupThread
            AbandonedConnectionCleanupThread.checkedShutdown();

        }
    }
}