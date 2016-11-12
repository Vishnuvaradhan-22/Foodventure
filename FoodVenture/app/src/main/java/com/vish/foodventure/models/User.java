package com.vish.foodventure.models;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vish on 12/11/2016.
 */

public class User {
    String userId;
    HashMap<String,String> favrestaurants;
    public User(){
        userId = null;
        favrestaurants = new HashMap<String,String>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public HashMap<String, String> getFavrestaurants() {
        return favrestaurants;
    }

    public void setFavrestaurants(HashMap<String, String> favrestaurants) {
        this.favrestaurants = favrestaurants;
    }
}
