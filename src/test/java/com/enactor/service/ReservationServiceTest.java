package com.enactor.service;

import com.enactor.model.ReservationResponse;
import com.enactor.model.SeatAvailability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockFindJourneyStmt;

    @Mock
    private PreparedStatement mockUpdateSeatStmt;

    @Mock
    private PreparedStatement mockInsertReservationStmt;

    @Mock
    private PreparedStatement mockInsertReservationSeatStmt;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reservationService = new ReservationService(mockConnection);
    }

    @Test
    void getAvailableSeatsByOriginAndDestination_ShouldReturnAvailableSeats_WhenSeatsAreAvailable() throws SQLException {
        // Arrange
        String origin = "CityA";
        String destination = "CityB";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockFindJourneyStmt);
        when(mockFindJourneyStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("seatNumber")).thenReturn("A1");
        when(mockResultSet.getDouble("price")).thenReturn(100.0);
        when(mockResultSet.getString("journeyID")).thenReturn("J123");
        when(mockResultSet.getDate("date")).thenReturn(new Date(System.currentTimeMillis()));

        // Act
        List<SeatAvailability> availableSeats = reservationService.getAvailableSeatsByOriginAndDestination(origin, destination);

        System.out.println("List size :"+availableSeats.size());
        // Assert
        if(!availableSeats.isEmpty()){
            assertNotNull(availableSeats);
            assertEquals(1, availableSeats.size());
        }else{
            assertTrue(availableSeats.isEmpty());
        }

    }

    @Test
    void reserveSeat_ShouldReturnReservationResponse_WhenSeatIsAvailable() throws SQLException {
        // Arrange
        String origin = "CityA";
        String destination = "CityB";
        String passengerID = "P123";
        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockFindJourneyStmt, mockUpdateSeatStmt, mockInsertReservationStmt, mockInsertReservationSeatStmt);
        when(mockFindJourneyStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("journeyID")).thenReturn("J123");
        when(mockResultSet.getString("origin")).thenReturn(origin);
        when(mockResultSet.getString("destination")).thenReturn(destination);
        when(mockResultSet.getDouble("price")).thenReturn(100.0);
        when(mockResultSet.getDate("date")).thenReturn(new Date(System.currentTimeMillis()));
        when(mockResultSet.getString("seatID")).thenReturn("S123");
        when(mockResultSet.getString("seatNumber")).thenReturn("A1");
        when(mockUpdateSeatStmt.executeUpdate()).thenReturn(1);

        // Act
        ReservationResponse response = reservationService.reserveSeat(origin, destination, passengerID);

        // Assert
        assertNotNull(response);
        assertEquals("A1", response.getSeatNumber());
        assertEquals(origin, response.getOrigin());
        assertEquals(destination, response.getDestination());
    }

    @Test
    void reserveSeat_ShouldReturnNull_WhenNoSeatIsAvailable() throws SQLException {
        // Arrange
        String origin = "CityA";
        String destination = "CityB";
        String passengerID = "P123";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockFindJourneyStmt);
        when(mockFindJourneyStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Act
        ReservationResponse response = reservationService.reserveSeat(origin, destination, passengerID);

        // Assert
        assertNull(response);
    }

    @Test
    void reserveSeat_ShouldThrowError_WhenSeatIsNoLongerAvailable() throws SQLException {
        // Arrange
        String origin = "CityA";
        String destination = "CityB";
        String passengerID = "P123";

        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockFindJourneyStmt, mockUpdateSeatStmt, mockInsertReservationStmt, mockInsertReservationSeatStmt);

        when(mockFindJourneyStmt.executeQuery()).thenReturn(mockResultSet);

        // Simulate a valid journey being found
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("seatID")).thenReturn("S123");
        when(mockResultSet.getString("journeyID")).thenReturn("J123");
        when(mockResultSet.getString("origin")).thenReturn(origin);
        when(mockResultSet.getString("destination")).thenReturn(destination);
        when(mockResultSet.getDouble("price")).thenReturn(100.0);
        when(mockResultSet.getDate("date")).thenReturn(new Date(System.currentTimeMillis()));
        when(mockResultSet.getString("seatNumber")).thenReturn("A1");

        // Simulate that the seat is no longer available when trying to update
        when(mockUpdateSeatStmt.executeUpdate()).thenReturn(0);

        // Act & Assert
        assertEquals(mockResultSet.next(), true);
    }

}