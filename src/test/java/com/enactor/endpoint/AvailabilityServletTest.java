package com.enactor.endpoint;

import com.enactor.model.SeatAvailability;
import com.enactor.service.ReservationService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AvailabilityServletTest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private Connection mockConnection;

    @Mock
    private ReservationService mockReservationService;

    @Mock
    private Logger mockLogger;

    @InjectMocks
    private AvailabilityServlet availabilityServlet;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        availabilityServlet = new AvailabilityServlet();
        responseWriter = new StringWriter();

        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void doGet_ShouldReturnBadRequest_WhenOriginOrDestinationMissing() throws Exception {
        // Arrange
        when(mockRequest.getParameter("origin")).thenReturn(null);
        when(mockRequest.getParameter("destination")).thenReturn(null);

        // Act
        availabilityServlet.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertEquals("{\"error\": \"Please provide both origin and destination.\"}", responseWriter.toString().trim());
    }



    @Test
    void doGet_ShouldReturnNoSeatsMessage_WhenNoSeatsAreAvailable() throws Exception {
        // Arrange
        when(mockRequest.getParameter("origin")).thenReturn("CityA");
        when(mockRequest.getParameter("destination")).thenReturn("CityD");
        when(mockReservationService.getAvailableSeatsByOriginAndDestination(anyString(), anyString())).thenReturn(new ArrayList<>());

        // Act
        availabilityServlet.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        assertEquals("{\"message\": \"No seats available for the specified route.\"}", responseWriter.toString().trim());
    }

    @Test
    void doGet_ShouldReturnAvailableSeats_WhenSeatsAreAvailable() throws Exception {
        // Arrange
        when(mockRequest.getParameter("origin")).thenReturn("CityA");
        when(mockRequest.getParameter("destination")).thenReturn("CityB");

        List<SeatAvailability> availableSeats = new ArrayList<>();
        availableSeats.add(new SeatAvailability("A1", 50, "journey1", new java.sql.Date(System.currentTimeMillis())));

        when(mockReservationService.getAvailableSeatsByOriginAndDestination(anyString(), anyString())).thenReturn(availableSeats);

        // Act
        availabilityServlet.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        String expectedJsonResponse = new Gson().toJson(availableSeats);
        assertTrue(expectedJsonResponse.contains("A1"));
        assertTrue(expectedJsonResponse.contains("50"));
        assertTrue(expectedJsonResponse.contains("journey1"));
    }



    @Test
    void doGet_ShouldHandleEmptyRequestParameters_WhenOnlyOriginIsProvided() throws Exception {
        // Arrange
        when(mockRequest.getParameter("origin")).thenReturn("CityA");
        when(mockRequest.getParameter("destination")).thenReturn(null);

        // Act
        availabilityServlet.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertEquals("{\"error\": \"Please provide both origin and destination.\"}", responseWriter.toString().trim());
    }

    @Test
    void doGet_ShouldHandleEmptyRequestParameters_WhenOnlyDestinationIsProvided() throws Exception {
        // Arrange
        when(mockRequest.getParameter("origin")).thenReturn(null);
        when(mockRequest.getParameter("destination")).thenReturn("CityB");

        // Act
        availabilityServlet.doGet(mockRequest, mockResponse);

        // Assert
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertEquals("{\"error\": \"Please provide both origin and destination.\"}", responseWriter.toString().trim());
    }
}
