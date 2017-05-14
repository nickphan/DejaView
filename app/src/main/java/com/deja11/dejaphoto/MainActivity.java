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
import android.content.SharedPreferences;
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
import android.util.Log;
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
import java.util.Set;

public class MainActivity extends Activity implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final int INTERVAL_OFFSET = 5; // offset for the interval
    private static final String INTERVAL_KEY = "progress"; // the key for the interval in the shared preferences
    private static final int INTERVAL_DEFAULT = 0; // default value for the interval in the shared preferences

    // request codes for each pending intent
    private static final int LEFT_PENDING_INTENT_RC = 0;
    private static final int RIGHT_PENDING_INTENT_RC = 1;
    private static final int KARMA_PENDING_INTENT_RC = 2;
    private static final int RELEASE_PENDING_INTENT_RC = 3;
    private static final int ALARM_PENDING_INTENT_RC = 4;

    private static final int NOTIFICATION_ID = 123;

    // codes for identifying which action the service has to execute
    private static final String CODE_KEY = "Order";
    private static final int CODE_NEXT_PHOTO = 1;
    private static final int CODE_PREVIOUS_PHOTO = 2;
    private static final int CODE_KARMA = 3;
    private static final int CODE_RELEASE = 4;

    DatabaseHelper myDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        // load the preferences
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SetWallpaperService.updateInterval(mSharedPref.getInt(INTERVAL_KEY, INTERVAL_DEFAULT)
                + INTERVAL_OFFSET);

        // register the mainActivity to detect any changes in preferences
        mSharedPref.registerOnSharedPreferenceChangeListener(this);

        // Create database object
        //myDb = new DatabaseHelper(this);
        //myDb.initialize(this);
        //myDb.test(this);

        // create the view for the notification
        RemoteViews notificationView = new RemoteViews(getBaseContext().getPackageName(),
                R.layout.notification);

        // add onClickListeners
        // 1. create class that extends BroadcastReceiver (and add it to the manifest)
        // 2. create an intent that calls the class
        // 3. create a pending intent that contains this intent
        // 4. call setOnClickPendingIntent of the view with the appropriate button and pending intent

        Intent leftButtonIntent = new Intent("left_button_receiver");
        PendingIntent leftButtonPIntent = PendingIntent.getBroadcast(this,
                LEFT_PENDING_INTENT_RC, leftButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.previous, leftButtonPIntent);

        Intent rightButtonIntent = new Intent("right_button_receiver");
        PendingIntent rightButtonPIntent = PendingIntent.getBroadcast(this,
                RIGHT_PENDING_INTENT_RC, rightButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.next, rightButtonPIntent);

        Intent karmaButtonIntent = new Intent("karma_button_receiver");
        PendingIntent karmaButtonPIntent = PendingIntent.getBroadcast(this,
                KARMA_PENDING_INTENT_RC, karmaButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.karma, karmaButtonPIntent);

        Intent releaseButtonIntent = new Intent("release_button_receiver");
        PendingIntent releaseButtonPIntent = PendingIntent.getBroadcast(this,
                RELEASE_PENDING_INTENT_RC, releaseButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.release, releaseButtonPIntent);

        //set the icon and time and build the notification of deja photo
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_wallpaper)
                .setWhen(System.currentTimeMillis())
                .setContent(notificationView)
                .build();

        // call the notification manager to show the notification in the status bar
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);

        //Button startButton = (Button)findViewById(R.id.startButton);
        //mNotificationManager.notify(5, notification);

        // Setting up the alarm to change the photo every x minutes

        Intent alarmIntent = new Intent("alarm_receiver");
        PendingIntent alarmPIntent = PendingIntent.getBroadcast(this,
                ALARM_PENDING_INTENT_RC, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), alarmPIntent);
        }

        else {
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), SetWallpaperService.interval, alarmPIntent);
        }

        // add a listener for the settings button
        ImageButton setting = (ImageButton) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingPreference.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister the MainActivity as a listener for preference changes
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPref.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // the interval settings is changed
        if (key.equals(INTERVAL_KEY)) {
            int minutes = sharedPreferences.getInt(INTERVAL_KEY, INTERVAL_DEFAULT)
                    + INTERVAL_OFFSET;
            SetWallpaperService.updateInterval(minutes);
            Log.d("Preference Changed", "Updated interval to " + minutes + " minutes");
        }
    }

    public static class LeftReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Button Clicked", "Left Button / Previous Photo Button");

            // reset the alarm by cancelling then rescheduling
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent alarmPIntent = PendingIntent.getBroadcast(context,
                    ALARM_PENDING_INTENT_RC, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            mAlarmManager.cancel(alarmPIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + SetWallpaperService.interval, alarmPIntent);
            }

            else {
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + SetWallpaperService.interval,
                        SetWallpaperService.interval, alarmPIntent);
            }

            // create an intent to call the SetWallpaperServices with the appropriate code
            Intent prevButtonIntent = new Intent(context, SetWallpaperService.class);
            prevButtonIntent.putExtra(CODE_KEY, CODE_PREVIOUS_PHOTO);
            context.startService(prevButtonIntent);
        }
    }

    public static class RightReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Button Clicked", "Right Button / Next Photo Button");

            // reset the alarm by cancelling then rescheduling
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent alarmPIntent = PendingIntent.getBroadcast(context,
                    ALARM_PENDING_INTENT_RC, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            mAlarmManager.cancel(alarmPIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + SetWallpaperService.interval, alarmPIntent);
            }

            else {
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + SetWallpaperService.interval,
                        SetWallpaperService.interval, alarmPIntent);
            }

            // create an intent to call the SetWallpaperServices with the appropriate code
            Intent nextButtonIntent = new Intent(context, SetWallpaperService.class);
            nextButtonIntent.putExtra(CODE_KEY, CODE_NEXT_PHOTO);
            context.startService(nextButtonIntent);
        }
    }

    public static class KarmaReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Photo karma'ed", Toast.LENGTH_SHORT).show();

            // create an intent to call the SetWallpaperServices with the appropriate code
            Intent karmaButtonIntent = new Intent(context, SetWallpaperService.class);
            karmaButtonIntent.putExtra(CODE_KEY, CODE_KARMA);
            context.startService(karmaButtonIntent);
        }
    }

    public static class ReleaseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Photo released", Toast.LENGTH_SHORT).show();

            // reset the alarm by cancelling then rescheduling
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent alarmPIntent = PendingIntent.getBroadcast(context,
                    ALARM_PENDING_INTENT_RC, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            mAlarmManager.cancel(alarmPIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + SetWallpaperService.interval, alarmPIntent);
            }

            else {
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + SetWallpaperService.interval,
                        SetWallpaperService.interval, alarmPIntent);
            }

            // create an intent to call the SetWallpaperServices with the appropriate code
            Intent releaseButtonIntent = new Intent(context, SetWallpaperService.class);
            releaseButtonIntent.putExtra(CODE_KEY, CODE_RELEASE);
            context.startService(releaseButtonIntent);
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Alarm", "Scheduled alarm fired");

            // for SDKs higher than 19 (KITKAT), we have to reschedule the alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent alarmPIntent = PendingIntent.getBroadcast(context,
                        ALARM_PENDING_INTENT_RC, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + SetWallpaperService.interval, alarmPIntent);

            }

            // create an intent to call the SetWallpaperServices with the appropriate code
            Intent serviceIntent = new Intent(context, SetWallpaperService.class);
            serviceIntent.putExtra(CODE_KEY, CODE_NEXT_PHOTO);
            context.startService(serviceIntent);
        }
    }
}
