package com.enactor.entity;

import java.util.Objects;

public class Seat {
    private String seatID;
    private String seatNumber;
    private boolean isAvailable;

    public Seat(String seatID, String seatNumber,boolean isAvailable) {
        this.seatID = seatID;
        this.seatNumber = seatNumber;
        this.isAvailable = isAvailable;
    }

    // Getters and setters
    public String getSeatID() {
        return seatID;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return isAvailable == seat.isAvailable && Objects.equals(seatID, seat.seatID) && Objects.equals(seatNumber, seat.seatNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatID, seatNumber, isAvailable);
    }
}
