package com.enactor.entity;

import java.util.Objects;

public class BusRoute {
    private String routeID;
    private String origin;
    private String destination;
    private double price;

    public BusRoute(String routeID, String origin, String destination, double price) {
        this.routeID = routeID;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
    }

    // Getters and setters
    public String getRouteID() {
        return routeID;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusRoute busRoute = (BusRoute) o;
        return Double.compare(price, busRoute.price) == 0 && Objects.equals(routeID, busRoute.routeID) && Objects.equals(origin, busRoute.origin) && Objects.equals(destination, busRoute.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeID, origin, destination, price);
    }
}
