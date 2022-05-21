package com.android.blackgoku.wallhd.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;

import com.android.blackgoku.wallhd.broadcast_receiver.AppBroadcastReceiver;

public class App extends Application {

    public static final String CHANNEL_ID_1 = "channel_1";
    public static final String CHANNEL_ID_2 = "channel_2";
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

    }

    private void createNotificationChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel_1 = new NotificationChannel(
                    CHANNEL_ID_1,
                    "Main channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel_1.setDescription("This is main channel");

            NotificationChannel channel_2 = new NotificationChannel(
                    CHANNEL_ID_2,
                    "Secondary channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            channel_1.setDescription("This is secondary channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel_1);
            manager.createNotificationChannel(channel_2);

        }

    }
}
