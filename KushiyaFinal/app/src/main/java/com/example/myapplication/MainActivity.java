package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver br = new NetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });
        // This function will call each time when MainActivity will create
        registerNetworkReceiver();
    }

    // 8 Broadcast receiver for internet connectivity.
    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(br, filter, Manifest.permission.ACCESS_NETWORK_STATE,null);
    }
}
