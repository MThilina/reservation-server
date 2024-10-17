package com.enactor.entity;

import java.util.List;
import java.util.Objects;

public class Reservation {
    private String reservationID;
    private List<Seat> reservedSeats;
    private double totalPrice;
    private Passenger passenger;

    public Reservation(String reservationID, List<Seat> reservedSeats, double totalPrice, Passenger passenger) {
        this.reservationID = reservationID;
        this.reservedSeats = reservedSeats;
        this.totalPrice = totalPrice;
        this.passenger = passenger;
    }

    // Getters
    public String getReservationID() {
        return reservationID;
    }

    public List<Seat> getReservedSeats() {
        return reservedSeats;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Double.compare(totalPrice, that.totalPrice) == 0 && Objects.equals(reservationID, that.reservationID) && Objects.equals(reservedSeats, that.reservedSeats) && Objects.equals(passenger, that.passenger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationID, reservedSeats, totalPrice, passenger);
    }
}