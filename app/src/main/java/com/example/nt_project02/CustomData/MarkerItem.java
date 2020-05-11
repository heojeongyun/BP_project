package com.example.nt_project02.CustomData;

public class MarkerItem {
    double lat;
    double lon;
    String price;
    public MarkerItem(double lat, double lon, String price) {
        this.lat = lat;
        this.lon = lon;
        this.price = price;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLon() { return lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price; }
}


