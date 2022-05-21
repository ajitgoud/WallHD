package com.android.blackgoku.wallhd.broadcast_receiver;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.android.blackgoku.wallhd.R;
import com.android.blackgoku.wallhd.activity.NotificationResultActivity;


import es.dmoral.toasty.Toasty;

import static com.android.blackgoku.wallhd.application.App.CHANNEL_ID_1;

public class AppBroadcastReceiver extends BroadcastReceiver {

    public static final int PENDING_INTENT_REQUEST_CODE = 1001;
    public static final String IMAGE_URI = "image_uri";
    private String fileName;

    private NotificationManagerCompat notificationManager;

    public AppBroadcastReceiver() {
    }

    public AppBroadcastReceiver(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {


        notificationManager = NotificationManagerCompat.from(context);

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {

            Intent resultIntent = new Intent(context, NotificationResultActivity.class);
            resultIntent.putExtra(IMAGE_URI, fileName);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(PENDING_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID_1)
                    .setSmallIcon(R.drawable.ic_round_done_24px)
                    .setContentTitle("Download completed")
                    .setContentText(fileName)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(1, notification);

        }

    }
}
