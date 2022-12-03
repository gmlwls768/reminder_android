package com.example.reminder_project;

import java.io.Serializable;

public class PlaceSearchItem implements Serializable{
    private String name;
    private String category;
    private String address;
    private double lat;
    private double lng;

    public PlaceSearchItem(String name, String category, String address, double lat, double lng){
        this.name = name;
        this.category = category;
        this.address = address;
        this.lat = lat;
        this.lng = lng;

    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
