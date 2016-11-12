package com.vish.foodventure.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vish.foodventure.R;
import com.vish.foodventure.utility.Restaurant;

public class DisplayRestaurant extends MenuLoader {

    TextView restaurantName;
    TextView restaurantDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_restaurant);
        initializeUI();
    }

    private void initializeUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_menu_bar);
        setSupportActionBar(toolbar);
        Bundle receivedBundle = getIntent().getBundleExtra("data");
        Restaurant selectedRestaurant = (Restaurant)receivedBundle.getSerializable("Restaurant");

        restaurantName = (TextView)findViewById(R.id.restaurantName);
        restaurantDetails = (TextView)findViewById(R.id.restaurantDetails);

        updateUI(selectedRestaurant);

        Button facebook = (Button)findViewById(R.id.facebook);
        Button message = (Button)findViewById(R.id.message);
        Button favorites = (Button)findViewById(R.id.favorites);

        facebook.setOnClickListener(facebookListener);
        message.setOnClickListener(messageListener);
        favorites.setOnClickListener(favoritesListner);
    }

    private View.OnClickListener facebookListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("FoodVenture","Facebook listener");
        }
    };

    private View.OnClickListener messageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sendMessage();
        }
    };

    private View.OnClickListener favoritesListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("FoodVenture","Add to Favorites");
        }
    };

    private void updateUI(Restaurant restaurant){

        restaurantName.setText(restaurant.getRestaurantName());
        StringBuilder details = new StringBuilder();

        details.append(restaurant.getRating());
        details.append("\nRating:"+restaurant.getAddress());
        if(restaurant.isOpenNow())
            details.append("\nResturant serving now, Hurry Up!!!");
        else
            details.append("\nRestaurant closed!!!");

        restaurantDetails.setText(details);
    }

    private void sendMessage(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"FoodVenture");

        StringBuilder textMessage =  new StringBuilder();
        textMessage.append(restaurantName.getText().toString());
        textMessage.append("\n"+restaurantDetails.getText().toString());

        shareIntent.putExtra(Intent.EXTRA_TEXT,textMessage.toString());

        startActivity(Intent.createChooser(shareIntent,"Share via"));
    }
}
