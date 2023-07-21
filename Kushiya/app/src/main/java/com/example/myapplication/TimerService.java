package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class TimerService extends Service {

    private boolean timerRunning = false;
    private Chronometer chronometer;

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_TITLE = "Foreground Service";
    private static final String NOTIFICATION_CONTENT = "Timer is running...";


    @Override
    public void onCreate() {
        super.onCreate();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel();
//        }
//        chronometer = new Chronometer(this);
//        chronometer.setBase(SystemClock.elapsedRealtime());
//        chronometer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        startForeground(NOTIFICATION_ID, buildNotification());
//        startTimer();
//        return START_STICKY;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Log.e("servicee", "Service is running");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
        ).start();
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentText(NOTIFICATION_CONTENT)
                .setContentTitle(NOTIFICATION_TITLE)
                .setSmallIcon(R.drawable.ic_launcher_background);
        startForeground(NOTIFICATION_ID,notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //stopTimer();
        super.onDestroy();
    }
//    }
//    private void startTimer() {
//        if (!timerRunning) {
//            chronometer.setBase(SystemClock.elapsedRealtime());
//            chronometer.start();
//        }
//        timerRunning = true;
//    }
//
//    private void stopTimer() {
//        if (timerRunning) {
//            chronometer.stop();
//            elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
//        }
//            timerRunning = false;
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
