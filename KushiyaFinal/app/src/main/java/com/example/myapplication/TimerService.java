package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class TimerService extends Service {

    private static final String CHANNEL_ID = "TimerServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        pendingIntentFlags |= PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingIntentFlags);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Timer Service")
                .setContentText("This is a timer running in background.")
                .setSmallIcon(R.drawable.main_icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //your code for what service will do
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        if(manager != null) {
            manager.createNotificationChannel(serviceChannel);
        }
    }
}