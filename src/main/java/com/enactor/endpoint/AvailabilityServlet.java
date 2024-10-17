package com.enactor.endpoint;

import com.enactor.configuration.DatabaseConfig;
import com.enactor.model.SeatAvailability;
import com.enactor.service.ReservationService;
import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/availability")
public class AvailabilityServlet extends HttpServlet {


    private static final Logger logger = LoggerFactory.getLogger(AvailabilityServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String origin = request.getParameter("origin");
        String destination = request.getParameter("destination");

        if (origin == null || destination == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Please provide both origin and destination.\"}");
            return;
        }
        ReservationService reservationService = null;
        try (Connection connection = DatabaseConfig.getConnection().getConnection()) {
             reservationService = new ReservationService(connection);
        }catch (SQLException e){
            logger.error("Database connection issue {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }

        List<SeatAvailability> availableSeats = new ArrayList<>();
        if (reservationService != null) {
            availableSeats = reservationService
                    .getAvailableSeatsByOriginAndDestination(origin, destination);
        }

        if (availableSeats.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"No seats available for the specified route.\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            String jsonResponse = new Gson().toJson(availableSeats);
            response.getWriter().write(jsonResponse);
        }
    }
}