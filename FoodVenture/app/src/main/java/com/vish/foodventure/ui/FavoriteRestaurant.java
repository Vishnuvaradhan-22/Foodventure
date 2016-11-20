package com.vish.foodventure.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vish.foodventure.R;
import com.vish.foodventure.models.Restaurant;
import com.vish.foodventure.utility.NetworkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FavoriteRestaurant extends MenuLoader {

    private DatabaseReference databaseReference;
    private ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();
    private LayoutInflater layoutInflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_restaurant);
        NetworkManager networkManager = new NetworkManager();
        boolean connectionResult = networkManager.testConnection();
        if(connectionResult)
            initializeUI();
        else{
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),LaunchScreenActivity.class);
            startActivity(intent);
        }
    }

    private void  initializeUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_menu_bar);
        setSupportActionBar(toolbar);

        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadFavoriteRestaurant();

    }

    private void loadFavoriteRestaurant(){
        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnap : dataSnapshot.getChildren()){
                            HashMap<String,Object> resultMap = (HashMap<String, Object>) dataSnap.getValue();
                            for(HashMap.Entry<String,Object> entry : resultMap.entrySet()){
                                HashMap<String,Object> restaurantMap = (HashMap)entry.getValue();
                                Restaurant favoriteRestaurant = new Restaurant();
                                favoriteRestaurant.getRestaurantFromMap(restaurantMap);
                                addToList(favoriteRestaurant);
                            }
                        }
                        initializeList();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addToList(Restaurant restaurant){
        this.restaurantList.add(restaurant);
    }
    private void initializeList(){
        if(restaurantList.isEmpty()){
            Toast.makeText(this,"No restaurant in favorite list. Please add restaurant to favorites!",Toast.LENGTH_LONG).show();
        }
        else{
            ListView restaurantListView = (ListView)findViewById(R.id.fav_res_list);
            restaurantListView.setAdapter(new RestaurantRowAdapter(this,R.layout.favorite_restaurant_list,R.id.favRestaurantName,this.restaurantList));
        }
    }

    class RestaurantRowAdapter extends ArrayAdapter<Restaurant>{
        private ArrayList<Restaurant> restaurants;
        public RestaurantRowAdapter(Context c, int rowResourceId, int textViewResourceId,
                              ArrayList<Restaurant> items)
        {
            super(c, rowResourceId, textViewResourceId, items);
            restaurants = items;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            FavoriteRestaurantViewHolder viewHolder;
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.favorite_restaurant_list,parent,false);

                viewHolder = new FavoriteRestaurantViewHolder();
                viewHolder.favRestaurantName = (TextView)convertView.findViewById(R.id.favRestaurantName);
                viewHolder.favRestaurantAddress = (TextView)convertView.findViewById(R.id.favRestaurantAddress);

                convertView.setTag(viewHolder);
            }
            else
                viewHolder = (FavoriteRestaurantViewHolder)convertView.getTag();

            Restaurant favRestaurantFromList = restaurants.get(position);
            viewHolder.favRestaurantName.setText(favRestaurantFromList.getRestaurantName());
            viewHolder.favRestaurantAddress.setText(favRestaurantFromList.getAddress());
            return convertView;
        }

    }

    static class FavoriteRestaurantViewHolder{
        TextView favRestaurantName;
        TextView favRestaurantAddress;
    }
}
