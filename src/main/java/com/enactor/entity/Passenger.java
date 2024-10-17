package com.enactor.entity;

import java.util.Objects;

public class Passenger {
    private String passengerID;
    private String name;
    private String contact;

    public Passenger(String passengerID, String name, String contact) {
        this.passengerID = passengerID;
        this.name = name;
        this.contact = contact;
    }

    // Getters and setters
    public String getPassengerID() {
        return passengerID;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(passengerID, passenger.passengerID) && Objects.equals(name, passenger.name) && Objects.equals(contact, passenger.contact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passengerID, name, contact);
    }
}
