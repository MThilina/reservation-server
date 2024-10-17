package com.enactor.endpoint;

import com.enactor.service.ReservationService;
import com.enactor.model.ReservationResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ReservationServletTest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private ReservationService mockReservationService;

    @Mock
    private Logger mockLogger;

    @InjectMocks
    private ReservationServlet reservationServlet;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        reservationServlet = new ReservationServlet();
        responseWriter = new StringWriter();

        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void doPost_ShouldReturnBadRequest_WhenMissingRequiredParameters() throws Exception {
        // Arrange
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader("{}")));

        // Act
        reservationServlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).setContentType("application/json");
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
    }

    @Test
    void doPost_ShouldReturnBadRequest_WhenOriginIsMissing() throws Exception {
        // Arrange
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("destination", "CityB");
        jsonObject.addProperty("passengerID", "P123");

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

        // Act
        reservationServlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).setContentType("application/json");
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
    }

    @Test
    void doPost_ShouldReturnBadRequest_WhenDestinationIsMissing() throws Exception {
        // Arrange
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("origin", "CityA");
        jsonObject.addProperty("passengerID", "passenger2");

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

        // Act
        reservationServlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).setContentType("application/json");
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
    }

    @Test
    void doPost_ShouldReturnBadRequest_WhenPassengerIDIsMissing() throws Exception {
        // Arrange
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("origin", "CityA");
        jsonObject.addProperty("destination", "CityB");

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

        // Act
        reservationServlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).setContentType("application/json");
        verify(mockResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
    }

    @Test
    void doPost_ShouldReturnReservationResponse_WhenReservationIsSuccessful() throws Exception {
        // Arrange
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("origin", "CityB");
        jsonObject.addProperty("destination", "CityC");
        jsonObject.addProperty("passengerID", "passenger2");

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));

        ReservationResponse reservationResponse = new ReservationResponse(UUID.randomUUID().toString(), "B1", "CityA", "CityB", new java.sql.Date(System.currentTimeMillis()), 50);
        when(mockReservationService.reserveSeat(anyString(), anyString(), anyString())).thenReturn(reservationResponse);

        // Act
        reservationServlet.doPost(mockRequest, mockResponse);

        // Assert

        // Parse the response JSON
        JsonObject responseJson = new Gson().fromJson(responseWriter.toString().trim(), JsonObject.class);
        verify(mockResponse).setContentType("application/json");

        if(responseJson.get("error").getAsString() != null){
            verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            assertEquals("Failed to reserve seat. Seat may not be available.", responseJson.get("error").getAsString());
        }else{
            verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
            assertEquals("A1", responseJson.get("seatNumber").getAsString());
            assertEquals("CityA", responseJson.get("origin").getAsString());
            assertEquals("CityB", responseJson.get("destination").getAsString());
        }

    }

    @Test
    void doPost_ShouldReturnBadRequest_WhenReservationFails() throws Exception {
        // Arrange
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("origin", "CityA");
        jsonObject.addProperty("destination", "CityB");
        jsonObject.addProperty("passengerID", "P123");

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(jsonObject.toString())));
        when(mockReservationService.reserveSeat(anyString(), anyString(), anyString())).thenReturn(null);

        // Act
        reservationServlet.doPost(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).setContentType("application/json");
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Failed to reserve seat. Seat may not be available."));
    }

}
