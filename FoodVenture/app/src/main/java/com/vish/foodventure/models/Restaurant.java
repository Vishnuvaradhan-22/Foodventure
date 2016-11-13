package com.vish.foodventure.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vish on 10/11/2016.
 */
public class Restaurant implements Serializable {
    String restaurantName;
    String address;
    double rating;
    double latitude;
    double longitude;
    boolean openNow;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public Map<String,Object> objectMapper(){
        Map<String,Object> mappedObject = new HashMap<String,Object>();
        mappedObject.put("restaurantName",this.restaurantName);
        mappedObject.put("address",this.address);
        return mappedObject;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantName='" + restaurantName + '\'' +
                ", address='" + address + '\'' +
                ", rating=" + rating +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", openNow=" + openNow +
                '}';
    }
}
