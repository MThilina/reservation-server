package com.enactor.service;

import com.enactor.configuration.DatabaseConfig;
import com.enactor.entity.Passenger;
import com.enactor.entity.Reservation;
import com.enactor.entity.Seat;
import com.enactor.model.ReservationResponse;
import com.enactor.model.SeatAvailability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 *     This service layer contains the business logic which is related to check the availability and the reserve seats
 *     This service is know as Reservation Service and directly provide as a contract to the API to consume and provide necessary operations
 * </p>
 */
public class ReservationService {
    private final Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    public ReservationService(Connection connection) {
        this.connection = connection;
    }

    /**
     * <p>
     *     This service method provide the avilablility of the bus tickets from origin to the destination.Out put is provide as a List
     * </p>
     * @param origin
     * @param destination
     * @return List<SeatAvailability>
     */
    public List<SeatAvailability> getAvailableSeatsByOriginAndDestination(String origin, String destination) {
        String query = "SELECT s.seatNumber, j.price, j.journeyID, j.date FROM Journey j " +
                "JOIN Seat s ON j.journeyID = s.journeyID " +
                "WHERE j.origin = ? AND j.destination = ? AND s.isAvailable = true";
        List<SeatAvailability> availableSeats = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            connection.setAutoCommit(false); // Start transaction

            preparedStatement.setString(1, origin);
            preparedStatement.setString(2, destination);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String seatNumber = resultSet.getString("seatNumber");
                    double price = resultSet.getDouble("price");
                    String journeyID = resultSet.getString("journeyID");
                    Date journeyDate = resultSet.getDate("date");
                    if (seatNumber != null && price != 0) {
                        availableSeats.add(new SeatAvailability(seatNumber, price, journeyID, journeyDate));
                    }
                }
            }

            connection.commit(); // Commit transaction

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Failed to rollback transaction: {}", ex.getMessage(), ex);
            }
            logger.error("Error checking seat availability: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to find available seats by origin and destination", e);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(true); // Reset auto-commit
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("Failed to close database connection: {}", e.getMessage(), e);
            }
        }

        return availableSeats;
    }
    /***
     * <p>
     *     This methods reserve tickets which are available
     *     assumptions only one ticket can be applied at a given time API call and that ticket will be assign to a particlular seat and a passenger
     *     Since the passenger is also tracking one purchase can be done and in the requirement it's mentioned only use to @param origin and @param destination
     *     even it gives multiple records all cannot be purchased without mentioned how many needed. So This will work as 1-1
     * </p>
     * @param origin
     * @param destination
     * @param passengerID
     * @return ReservationResponse
     */
    public ReservationResponse reserveSeat(String origin, String destination, String passengerID) {
        String findJourneyQuery = "SELECT s.seatID, s.seatNumber, j.journeyID, j.origin, j.destination, j.price, j.date FROM Journey j " +
                "JOIN Seat s ON j.journeyID = s.journeyID " +
                "WHERE j.origin = ? AND j.destination = ? AND s.isAvailable = true LIMIT 1";
        String updateSeatQuery = "UPDATE Seat SET isAvailable = false WHERE seatID = ? AND isAvailable = true";
        String insertReservationQuery = "INSERT INTO Reservation (reservationID, journeyID, passengerID, totalPrice) VALUES (?, ?, ?, ?)";
        String insertReservationSeatQuery = "INSERT INTO Reservation_Seat (reservationID, seatID) VALUES (?, ?)";

        try (PreparedStatement findJourneyStmt = connection.prepareStatement(findJourneyQuery);
             PreparedStatement updateSeatStmt = connection.prepareStatement(updateSeatQuery);
             PreparedStatement insertReservationStmt = connection.prepareStatement(insertReservationQuery);
             PreparedStatement insertReservationSeatStmt = connection.prepareStatement(insertReservationSeatQuery)) {

            connection.setAutoCommit(false); // Start transaction

            // Find journey and seat details
            findJourneyStmt.setString(1, origin);
            findJourneyStmt.setString(2, destination);

            try (ResultSet resultSet = findJourneyStmt.executeQuery()) {
                if (resultSet.next()) {
                    String journeyID = resultSet.getString("journeyID");
                    String journeyOrigin = resultSet.getString("origin");
                    String journeyDestination = resultSet.getString("destination");
                    double price = resultSet.getDouble("price");
                    Date date = resultSet.getDate("date");
                    String seatID = resultSet.getString("seatID");
                    String seatNumber = resultSet.getString("seatNumber");

                    // Update seat availability
                    updateSeatStmt.setString(1, seatID);
                    int updatedRows = updateSeatStmt.executeUpdate();
                    if (updatedRows == 0) {
                        logger.warn("Seat {} is no longer available", seatID);
                        connection.rollback();
                        return reserveSeat(origin, destination, passengerID); // Retry to find another available seat
                    }

                    // Insert reservation
                    String reservationID = UUID.randomUUID().toString();
                    insertReservationStmt.setString(1, reservationID);
                    insertReservationStmt.setString(2, journeyID);
                    insertReservationStmt.setString(3, passengerID);
                    insertReservationStmt.setDouble(4, price);
                    insertReservationStmt.executeUpdate();

                    // Insert reservation-seat mapping
                    insertReservationSeatStmt.setString(1, reservationID);
                    insertReservationSeatStmt.setString(2, seatID);
                    insertReservationSeatStmt.executeUpdate();

                    connection.commit(); // Commit transaction

                    // Return reservation response
                    return new ReservationResponse(reservationID, seatNumber, journeyOrigin, journeyDestination, date, price);
                } else {
                    logger.warn("No available seats found for journey from {} to {}", origin, destination);
                    return null;
                }
            }

        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback transaction on error
            } catch (SQLException rollbackEx) {
                logger.error("Failed to rollback transaction: {}", rollbackEx.getMessage(), rollbackEx);
            }
            logger.error("Failed to reserve seat: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to reserve seat", e);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(true); // Reset auto-commit
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("Failed to close database connection: {}", e.getMessage(), e);
            }
        }
    }

}

