package com.vish.foodventure;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeScreen extends MenuLoader implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, DataLoader {

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 104;
    private GoogleApiClient googleApiClient;
    private Location mLastLocation;
    private GoogleMap googleMap;
    private Marker marker = null;

    private LayoutInflater listInflator;

    private ArrayList<Restaurant> availableRestaurants = new ArrayList<Restaurant>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initializeUI();
    }


    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    private void initializeUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_menu_bar);
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listInflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("ConnectionFailure", connectionResult.getErrorMessage());
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }

        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LatLng currentLocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        this.marker = googleMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("Current Location")
                .draggable(true)
                .snippet("My Location")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        new RestaurantLoader(this,mLastLocation).execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void loadData(String jsonData) {
        try {
            JSONObject restaurantData = new JSONObject(jsonData);
            Log.d("Main",(String)restaurantData.get("status"));
            switch ((String)restaurantData.get("status")){
                case "OK":
                    loadRestaurants(restaurantData);
                    initializeList();
                    addMarkers();
                    break;
                case "ZERO_RESULTS":
                    Toast.makeText(HomeScreen.this,"Sorry, No restaurants nearby!",Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "Granted");
                    return;

                } else {
                    Toast.makeText(HomeScreen.this, "Location Service disabled!", Toast.LENGTH_LONG);
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private void loadRestaurants(JSONObject restaurantsData){
        try {
            JSONArray result = (JSONArray)restaurantsData.get("results");
            for(int i=0;i<result.length();i++){
                JSONObject tempRestaurantData = (JSONObject)result.get(i);
                Restaurant newRestaurant = new Restaurant();
                newRestaurant.setRestaurantName((String)tempRestaurantData.get("name"));
                if(tempRestaurantData.get("rating").getClass().getSimpleName().equals("Double"))
                    newRestaurant.setRating((Double)tempRestaurantData.get("rating"));
                else
                    newRestaurant.setRating((Integer)tempRestaurantData.get("rating"));
                newRestaurant.setAddress((String)tempRestaurantData.get("vicinity"));

                JSONObject geometry = (JSONObject)tempRestaurantData.get("geometry");
                JSONObject location = (JSONObject)geometry.get("location");
                newRestaurant.setLatitude((Double)location.get("lat"));
                newRestaurant.setLongitude((Double)location.get("lng"));
                if(tempRestaurantData.has("opening_hours")){
                    JSONObject status = (JSONObject)tempRestaurantData.get("opening_hours");
                    newRestaurant.setOpenNow((Boolean)status.get("open_now"));
                }
                else{
                    newRestaurant.setOpenNow(false);
                }
                this.availableRestaurants.add(newRestaurant);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeList(){
        ListView restaurantListView = (ListView)findViewById(R.id.list);
        restaurantListView.setAdapter(new RowIconAdapter(this, R.layout.restaurant_item, R.id.row_label, availableRestaurants));
    }

    class RowIconAdapter extends ArrayAdapter<Restaurant>
    {
        private ArrayList<Restaurant> restaurants;
        public RowIconAdapter(Context c, int rowResourceId, int textViewResourceId,
                              ArrayList<Restaurant> items)
        {
            super(c, rowResourceId, textViewResourceId, items);
            restaurants = items;
        }

        public View getView(int pos, View convertView, ViewGroup parent)
        {
            RestautantViewHolder viewHolder;
            if(convertView == null){
                convertView = listInflator.inflate(R.layout.restaurant_item, parent, false);
                viewHolder = new RestautantViewHolder();
                //find the row adapter elements
                ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
                TextView movieText = (TextView) convertView.findViewById(R.id.row_label);
                //Add the row adapter details to the holder for reuse.
                viewHolder.imageIcon = icon;
                viewHolder.restaurantName = movieText;

                //Add the holder object to the layout ConvertView
                convertView.setTag(viewHolder);

            }
            else
                viewHolder = (RestautantViewHolder) convertView.getTag();

            //Set data to the elements of row adapter
            Restaurant currentRestaurant = restaurants.get(pos);
            viewHolder.restaurantName.setText(currentRestaurant.getRestaurantName());
            Bitmap restuarantIcon = getRestaurantIcon(currentRestaurant.getRating());
            viewHolder.imageIcon.setImageBitmap(restuarantIcon);

            return convertView;
        }
    }

    /** Creates a unique movie icon based on name and rating */
    private Bitmap getRestaurantIcon(double restaurantRating)
    {
        int bgColor = Color.BLACK;
        if(restaurantRating >= 4.0){
            bgColor = Color.GREEN;
        }
        else if(restaurantRating >= 3.5 && restaurantRating < 4.0){
            bgColor = Color.CYAN;
        }
        else if(restaurantRating >= 3.0 && restaurantRating <3.5){
            bgColor = Color.YELLOW;
        }
        else if(restaurantRating >=2.0 && restaurantRating < 3.0){
            bgColor = Color.MAGENTA;
        }
        else if(restaurantRating < 2.0){
            bgColor = Color.RED;
        }
        Bitmap restaurantIcon = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
        restaurantIcon.eraseColor(bgColor); // fill bitmap with the color
        Canvas c = new Canvas(restaurantIcon);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        p.setTextSize(24.0f);
        c.drawText(String.valueOf(restaurantRating), 8, 32, p);
        return restaurantIcon;
    }

    static class RestautantViewHolder {
        ImageView imageIcon;
        TextView restaurantName;
    }

    private void addMarkers(){
        for(Restaurant restaurant : availableRestaurants){
            LatLng restaurantLocation = new LatLng(restaurant.getLatitude(),restaurant.getLongitude());
            this.googleMap.addMarker(new MarkerOptions()
            .position(restaurantLocation)
            .title(restaurant.getRestaurantName())
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        }
    }
}