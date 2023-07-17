package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Constructor;

public class NetworkReceiver extends BroadcastReceiver {
    protected boolean isConnected;

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //receive network status
        isConnected = networkInfo != null && networkInfo.isConnected();
        if (isConnected) {
            Toast.makeText(context, "Network is ON", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Network is OFF", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}


/////kdfmlksnslkadfnawgfrwnal

////fdgbmflkdabak


