package com.enactor.endpoint;

import com.enactor.configuration.DatabaseConfig;

import com.enactor.model.ReservationResponse;
import com.enactor.service.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;



@WebServlet("/reservation")
public class ReservationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReservationServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        Gson gson = new Gson();

        // Parse JSON to Java Object
        JsonObject jsonObject = gson.fromJson(readRequestBody(request,gson), JsonObject.class);

        // Extract values from JSON
        String origin = jsonObject.get("origin") != null ? jsonObject.get("origin").getAsString() : null;
        String destination = jsonObject.get("destination") != null ? jsonObject.get("destination").getAsString() : null;
        String passengerID = jsonObject.get("passengerID") != null ? jsonObject.get("passengerID").getAsString() : null;


        if (origin == null || destination == null || passengerID == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
            return;
        }

        try (Connection connection = DatabaseConfig.getConnection().getConnection()) {
            ReservationService reservationService = new ReservationService(connection);

            ReservationResponse reservationResponse = reservationService.reserveSeat(origin, destination,passengerID);
            if (reservationResponse == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Failed to reserve seat. Seat may not be available.\"}");
            } else {
            response.setStatus(HttpServletResponse.SC_OK);
            String jsonResponse = new Gson().toJson(reservationResponse);
            response.getWriter().write(jsonResponse);
        }

        } catch (SQLException e) {
            logger.error("Reservation Failed ", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    private String readRequestBody(HttpServletRequest request,Gson gson){
        // Read request body
        StringBuilder jsonBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
            logger.info("Request Body : {}",jsonBody.toString());
        } catch (IOException e) {
            logger.error("Reading Request Body Failed : {}",e.getMessage());
            throw new RuntimeException(e);
        }
        return jsonBody.toString();
    }
}

