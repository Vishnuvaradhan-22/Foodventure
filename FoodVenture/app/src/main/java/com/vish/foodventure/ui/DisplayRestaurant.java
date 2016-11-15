package com.vish.foodventure.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vish.foodventure.R;
import com.vish.foodventure.models.Restaurant;

import java.util.HashMap;
import java.util.Map;

public class DisplayRestaurant extends MenuLoader {

    TextView restaurantName;
    TextView restaurantDetails;
    Restaurant selectedRestaurant;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize Facebook SDK

        setContentView(R.layout.activity_display_restaurant);
        AppEventsLogger.activateApp(this);

        initializeUI();
    }

    private void initializeUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_menu_bar);
        setSupportActionBar(toolbar);
        Bundle receivedBundle = getIntent().getBundleExtra("data");
        selectedRestaurant = (Restaurant)receivedBundle.getSerializable("Restaurant");

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

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("Restaurant",selectedRestaurant);
            intent.putExtra("Restaurant",bundle);
            intent.setClass(getApplicationContext(),FacebookShareContentBuilder.class);
            startActivity(intent);

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

            databaseReference = FirebaseDatabase.getInstance().getReference();
            String key = databaseReference.child("restaurants").push().getKey();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Map<String,Object> restaurant = selectedRestaurant.objectMapper();

            Map<String,Object> childUpdate = new HashMap<>();
            childUpdate.put("/users/"+userId+"/favorites/"+key,restaurant);

            databaseReference.updateChildren(childUpdate);
            Toast.makeText(DisplayRestaurant.this,"Added to Favorites",Toast.LENGTH_LONG).show();

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
