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
    public NetworkManager(){
    }
    public NetworkManager(Context context) {
        mContext = context;
    }

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_NOT_CONNECTED=0;
    public static final int NETWORK_STATUS_WIFI=1;
    public static final int NETWORK_STATUS_MOBILE_DATA=2;

    public boolean testConnection(){

        connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean result = networkInfo !=null && networkInfo.isConnectedOrConnecting();
        return result;
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = NetworkManager.getConnectivityStatus(context);
        int status = 0;
        if (conn == NetworkManager.TYPE_WIFI) {
            status = NETWORK_STATUS_WIFI;
        } else if (conn == NetworkManager.TYPE_MOBILE) {
            status =NETWORK_STATUS_MOBILE_DATA;
        } else if (conn == NetworkManager.TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }
}
