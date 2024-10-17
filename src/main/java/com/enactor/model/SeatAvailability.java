package com.enactor.model;

import java.sql.Date;
import java.util.Objects;

public class SeatAvailability {
    private String seatNumber;
    private double price;
    private String journeyID;
    private Date journeyDate;

    public SeatAvailability(String seatNumber, double price, String journeyID, Date journeyDate) {
        this.seatNumber = seatNumber;
        this.price = price;
        this.journeyID = journeyID;
        this.journeyDate = journeyDate;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(String journeyID) {
        this.journeyID = journeyID;
    }

    public Date getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(Date journeyDate) {
        this.journeyDate = journeyDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatAvailability that = (SeatAvailability) o;
        return Double.compare(price, that.price) == 0 && Objects.equals(seatNumber, that.seatNumber) && Objects.equals(journeyID, that.journeyID) && Objects.equals(journeyDate, that.journeyDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatNumber, price, journeyID, journeyDate);
    }

    @Override
    public String toString() {
        return "SeatAvailability{" +
                "seatNumber='" + seatNumber + '\'' +
                ", price=" + price +
                ", journeyID='" + journeyID + '\'' +
                ", journeyDate=" + journeyDate +
                '}';
    }
}
