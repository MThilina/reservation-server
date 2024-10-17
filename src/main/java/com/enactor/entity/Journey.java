package com.enactor.entity;

import java.sql.Date;
import java.util.Objects;

public class Journey {
    private String journeyID;
    private String origin;
    private String destination;
    private Date date;
    private double price;

    public Journey(String journeyID, String origin, String destination, Date date, double price) {
        this.journeyID = journeyID;
        this.origin = origin;
        this.destination = destination;
        this.date = date;
        this.price = price;
    }

    // Getters
    public String getJourneyID() {
        return journeyID;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public Date getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Journey journey = (Journey) o;
        return Double.compare(price, journey.price) == 0 && Objects.equals(journeyID, journey.journeyID) && Objects.equals(origin, journey.origin) && Objects.equals(destination, journey.destination) && Objects.equals(date, journey.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(journeyID, origin, destination, date, price);
    }
}
