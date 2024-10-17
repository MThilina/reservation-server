package com.enactor.endpoint;

import com.enactor.configuration.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/health")
public class HealthServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HealthServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        boolean isDatabaseUp = checkDatabaseHealth();

        if (isDatabaseUp) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{ \"status\": \"UP\" }");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{ \"status\": \"DOWN\", \"details\": \"Database connection failed\" }");
        }
    }

    private boolean checkDatabaseHealth() {
        try (Connection connection = DatabaseConfig.getConnection().getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            logger.error(" SQL error occurred when try to establish connection {}",e.getMessage());
            return false;
        }
    }
}

