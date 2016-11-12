package com.vish.foodventure;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.vish.foodventure.utility.Restaurant;

public class DisplayRestaurant extends MenuLoader {

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
        updateUI(selectedRestaurant);
    }

    private void updateUI(Restaurant restaurant){
        TextView restaurantName = (TextView)findViewById(R.id.restaurantName);
        TextView restaurantDetails = (TextView)findViewById(R.id.restaurantDetails);

        restaurantName.setText(restaurant.getRestaurantName());
        StringBuilder details = new StringBuilder();

        details.append(restaurant.getRating());
        details.append("\n"+restaurant.getAddress());
        if(restaurant.isOpenNow())
            details.append("\nResturant serving now, Hurry Up!!!");
        else
            details.append("\nRestaurant closed!!!");

        restaurantDetails.setText(details);
    }
}
