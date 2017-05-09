package com.deja11.dejaphoto;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    DatabaseHelper myDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create database object
        myDb = new DatabaseHelper(this);
        myDb.initialize(this);

        // create the view for the notification
        RemoteViews notificationView = new RemoteViews(getBaseContext().getPackageName(),
                R.layout.notification);

        // add onClickListeners
        // 1. create class that extends BroadcastReceiver (and add it to the manifest)
        // 2. create an intent that calls the class
        // 3. create a pending intent that contains this intent
        // 4. call setOnClickPendingIntent of the view with the appropriate button and pending intent

        Intent leftButtonIntent = new Intent("left_button_receiver");
        PendingIntent leftButtonPIntent = PendingIntent.getBroadcast(this, 1, leftButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.previous, leftButtonPIntent);

        Intent rightButtonIntent = new Intent("right_button_receiver");
        PendingIntent rightButtonPIntent = PendingIntent.getBroadcast(this, 2, rightButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.next, rightButtonPIntent);

        Intent karmaButtonIntent = new Intent("karma_button_receiver");
        PendingIntent karmaButtonPIntent = PendingIntent.getBroadcast(this, 3, karmaButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.karma, karmaButtonPIntent);

        Intent releaseButtonIntent = new Intent("release_button_receiver");
        PendingIntent releaseButtonPIntent = PendingIntent.getBroadcast(this, 4, releaseButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.release, releaseButtonPIntent);

        //set the icon and time and build the notification of deja photo
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_wallpaper)
                .setWhen(System.currentTimeMillis())
                .setContent(notificationView)
                .build();

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // TODO: Do we need this?
        /*notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound */

        mNotificationManager.notify(5, notification);
    }

    public static class LeftReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Previous Button Clicked", Toast.LENGTH_SHORT).show();
        }
    }

    public static class RightReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Next Button Clicked", Toast.LENGTH_SHORT).show();
        }
    }

    public static class KarmaReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Karma Button Clicked", Toast.LENGTH_SHORT).show();
        }
    }

    public static class ReleaseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Release Button Clicked", Toast.LENGTH_SHORT).show();
        }
    }
}
