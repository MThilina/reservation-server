package com.enactor.model;

import java.sql.Date;
import java.util.Objects;

public class ReservationResponse {
    private String reservationID;
    private String seatNumber;
    private String origin;
    private String destination;
    private Date journeyDate;
    private double totalPrice;

    public ReservationResponse(String reservationID, String seatNumber, String origin, String destination, Date journeyDate, double totalPrice) {
        this.reservationID = reservationID;
        this.seatNumber = seatNumber;
        this.origin = origin;
        this.destination = destination;
        this.journeyDate = journeyDate;
        this.totalPrice = totalPrice;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(Date journeyDate) {
        this.journeyDate = journeyDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationResponse that = (ReservationResponse) o;
        return Double.compare(totalPrice, that.totalPrice) == 0 && Objects.equals(reservationID, that.reservationID) && Objects.equals(seatNumber, that.seatNumber) && Objects.equals(origin, that.origin) && Objects.equals(destination, that.destination) && Objects.equals(journeyDate, that.journeyDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationID, seatNumber, origin, destination, journeyDate, totalPrice);
    }

    @Override
    public String toString() {
        return "ReservationResponse{" +
                "reservationID='" + reservationID + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", journeyDate='" + journeyDate + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
