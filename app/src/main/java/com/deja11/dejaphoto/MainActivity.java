package com.deja11.dejaphoto;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MainActivity extends Activity {

    DatabaseHelper myDb;
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);
        boolean isInserted = myDb.insertData("expectedlocatikkon",10.01,10.02,999,10,1,1);
        if (isInserted)
            Toast.makeText(MainActivity.this, "Data Insert", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(MainActivity.this, "Data not Insert", Toast.LENGTH_LONG).show();

        //set the icon and time and build the notification of deja photo
        int icon = R.drawable.ic_wallpaper;
        long when = System.currentTimeMillis();
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(icon)
                .setWhen(when)
                .build();
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // get the content view of the notification
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        notification.contentView = contentView;

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound
        mNotificationManager.notify(1, notification);



    }
}
