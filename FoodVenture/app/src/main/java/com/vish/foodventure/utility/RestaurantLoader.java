package com.vish.foodventure.utility;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


/**
 * Created by Vish on 10/11/2016.
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class RestaurantLoader extends AsyncTask<Void,Void,String> {

    private  String googleUrl;
    private DataLoader dataLoader;
    private double latitude;
    private double longitude;
    private String jsonData;
    private int radius;
    private String authKey;
    private String searchType;

    public RestaurantLoader(Context context, Location currentLocation){
        dataLoader = (DataLoader)context;
        this.radius = 1000;
        this.authKey = "AIzaSyD9dzNe6WS1wzZIaACsxV3mJEBrxErKq9Q";
        this.searchType = "restaurant";
        this.latitude = currentLocation.getLatitude();
        this.longitude = currentLocation.getLongitude();
        this.googleUrl= "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+this.latitude+","+this.longitude+"&radius="+this.radius+"&type="+this.searchType+"&key="+this.authKey;
    }
    @Override
    protected String doInBackground(Void... voids) {
        HttpClient getClient = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(this.googleUrl);

        HttpResponse response;
        Log.d("URL",this.googleUrl);
        try {
            response = getClient.execute(getRequest);
            jsonData = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dataLoader.loadData(s);
    }
}