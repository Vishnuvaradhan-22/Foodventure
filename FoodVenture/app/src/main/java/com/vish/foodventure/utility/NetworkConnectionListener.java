package com.vish.foodventure.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Vish on 21/11/2016.
 */
public class NetworkConnectionListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int connectionStatus = NetworkManager.getConnectivityStatusString(context);
        if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if(connectionStatus==NetworkManager.NETWORK_STATUS_NOT_CONNECTED){
                Toast.makeText(context,"Network Error! Please connect to internet!",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,"Device Connected to internet!",Toast.LENGTH_LONG).show();
            }

        }
    }
}
