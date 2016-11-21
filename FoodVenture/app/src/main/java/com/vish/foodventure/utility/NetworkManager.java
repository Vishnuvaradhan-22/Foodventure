package com.vish.foodventure.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Vish on 16/11/2016.
 */
public class NetworkManager{
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private Context mContext;
    public NetworkManager(Context context) {
        mContext = context;
    }

    public boolean testConnection(){

        connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean result = networkInfo !=null && networkInfo.isConnectedOrConnecting();
        return result;
    }
}
