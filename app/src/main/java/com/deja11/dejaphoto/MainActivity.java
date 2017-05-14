package com.deja11.dejaphoto;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


public class MainActivity extends Activity {

    DatabaseHelper myDb;
    Controller controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }






        // Create database object
        //myDb = new DatabaseHelper(this);
        //myDb.initialize(this);
        //myDb.test(this);

        // Create controller object
        //controller = new Controller(MainActivity.this);


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
        //Bundle b = new Bundle();
        //b.putParcelable("controller", controller);
        //rightButtonIntent.putExtra("controller", controller);
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

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // TODO: Do we need this?
        /*notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound */

        mNotificationManager.notify(5, notification);

        //Button startButton = (Button)findViewById(R.id.startButton);
        //mNotificationManager.notify(5, notification);

        // Setting up the alarm

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmPIntent = PendingIntent.getBroadcast(this, 6, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int defaultTimer = 60000;
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), defaultTimer, alarmPIntent);

        ImageButton setting = (ImageButton) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingPreference.class));
            }
        });






        /*Nick's shitty way of testing*/
        /*
        controller = new Controller(this);
        Button button = (Button)findViewById(R.id.nextButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Photo photo = controller.getNextPhoto();
                Toast.makeText(getApplicationContext(), photo.phoneLocation, Toast.LENGTH_SHORT).show();
                controller.setWallpaper(photo);
            }
        });

        Button prevButton = (Button)findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Photo photo = controller.getPreviousPhoto();
                Toast.makeText(getApplicationContext(), photo.phoneLocation, Toast.LENGTH_SHORT).show();
                controller.setWallpaper(photo);
            }
        });
        */
    }

    public static class LeftReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "Previous Button Clicked", Toast.LENGTH_SHORT).show();
            Intent prevButtonIntent = new Intent(context, SetWallpaperService.class);
            prevButtonIntent.putExtra("Order", 2);
            context.startService(prevButtonIntent);
        }
    }

    public static class RightReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "Next Button Clicked", Toast.LENGTH_SHORT).show();
            Intent nextButtonIntent = new Intent(context, SetWallpaperService.class);
            nextButtonIntent.putExtra("Order", 1);
            context.startService(nextButtonIntent);
        }
    }

    public static class KarmaReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Photo is Karma'd", Toast.LENGTH_SHORT).show();
            Intent karmaButtonIntent = new Intent(context, SetWallpaperService.class);
            karmaButtonIntent.putExtra("Order", 3);
            context.startService(karmaButtonIntent);
        }
    }

    public static class ReleaseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Photo Released", Toast.LENGTH_SHORT).show();
            Intent releaseButtonIntent = new Intent(context, SetWallpaperService.class);
            releaseButtonIntent.putExtra("Order", 4);
            context.startService(releaseButtonIntent);
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Intent serviceIntent = new Intent(context, SetWallpaperService.class);
            //serviceIntent.putExtra("Order", 1);
            //context.startService(serviceIntent);
        }
    }
}
