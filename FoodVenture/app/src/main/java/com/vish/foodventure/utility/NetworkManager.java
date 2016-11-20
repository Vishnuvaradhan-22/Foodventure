package com.vish.foodventure.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Vish on 16/11/2016.
 */
public class NetworkManager extends Activity{
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    public NetworkManager() {
    }

    public boolean testConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean result = networkInfo !=null && networkInfo.isConnectedOrConnecting();
        return result;
    }
}
