package com.vish.foodventure.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.vish.foodventure.R;
import com.vish.foodventure.models.Restaurant;
import com.vish.foodventure.utility.NetworkManager;

public class FacebookShareContentBuilder extends MenuLoader {

    private Restaurant selectedRestaurant;
    private TextView restaurantName;
    private EditText description;
    private RadioButton hashtagCheck;
    private EditText hashtag;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        setContentView(R.layout.activity_facebook_share_content_builder);
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

    private void initializeUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_menu_bar);
        setSupportActionBar(toolbar);
        Bundle receivedBundle = getIntent().getBundleExtra("Restaurant");
        selectedRestaurant = (Restaurant)receivedBundle.getSerializable("Restaurant");
        restaurantName = (TextView)findViewById(R.id.fb_restaurant_name);
        restaurantName.setText(selectedRestaurant.getRestaurantName());

        hashtagCheck = (RadioButton)findViewById(R.id.hashtagRadioButton);
        description = (EditText)findViewById(R.id.fb_description);
        hashtag = (EditText)findViewById(R.id.fb_hashtag);
        if(hashtagCheck.isChecked())
            hashtag.setVisibility(View.VISIBLE);
        else
            hashtag.setVisibility(View.INVISIBLE);

        hashtagCheck.setOnClickListener(hashtagListener);

        Button shareButton = (Button)findViewById(R.id.fb_share_button);
        shareButton.setOnClickListener(shareButtonListener);
    }

    private View.OnClickListener hashtagListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(hashtagCheck.isChecked()){
                hashtag.setVisibility(View.VISIBLE);
                Toast.makeText(FacebookShareContentBuilder.this,"Add single Hashtag for post",Toast.LENGTH_LONG).show();
            }
            else{
                hashtag.setVisibility(View.INVISIBLE);
            }
        }
    };
    private View.OnClickListener shareButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean validation = validateData();
            if(validation && hashtagCheck.isChecked()) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentTitle(selectedRestaurant.getRestaurantName())
                            .setContentDescription(description.getText().toString())
                            .setContentUrl(Uri.parse("https://developers.facebook.com"))
                            .setShareHashtag(new ShareHashtag.Builder().setHashtag("#"+hashtag.getText().toString()).build())
                            .build();
                    shareDialog.show(content);
                }
            }
            else if(validation){
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentTitle(selectedRestaurant.getRestaurantName())
                                .setContentDescription(description.getText().toString())
                                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                                .build();
                    shareDialog.show(content);
                }
            }
        }
    };

    private boolean validateData(){
        boolean result = false;
        boolean state = hashtagCheck.isChecked();
        if(description.getText().toString().length() == 0){
            description.setError("Please add description for the post");
            return result;
        }
        else if(state && hashtag.getText().toString().length()==0 ){
            hashtag.setError("Please add some hashtag");
            return result;
        }
        else
            result = true;
        return result;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
