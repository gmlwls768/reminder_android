package com.example.reminder_project;

public class PlaceSearchItem {
    private String name;
    private String category;
    private String address;

    public PlaceSearchItem(String name, String category, String address){
        this.name = name;
        this.category = category;
        this.address = address;
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

}
