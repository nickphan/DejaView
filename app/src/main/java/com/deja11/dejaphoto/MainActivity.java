package com.deja11.dejaphoto;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    Controller controller;
    DatabaseHelper myDb;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myFirebaseRef = database.getReference();


    // For testing purpose
    private static MainActivity instance;
    public static MainActivity getInstance() {
        if (instance == null) {
            setInstance(instance);
        }
        return instance;
    }
    public static void setInstance(MainActivity instance) {
        MainActivity.instance = instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.test_photo_picker);

        controller = new Controller(this);

        int hasPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionGranted = PackageManager.PERMISSION_GRANTED;

        Log.i("Has Permission", hasPermission + "");
        Log.i("Permission Granted", permissionGranted + "");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        myFirebaseRef = database.getReference().child("name").child("123");
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);

                Toast.makeText(getBaseContext(), data.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/*
        // For Junit test
        setInstance(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // initialize the value of the interval using shared preferences, if applicable
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SetWallpaperService.updateInterval(mSharedPref.getInt(INTERVAL_KEY, INTERVAL_DEFAULT)
                + INTERVAL_OFFSET);

        // register the mainActivity to detect any changes in preferences
        mSharedPref.registerOnSharedPreferenceChangeListener(this);

        // create the view for the notification
        RemoteViews notificationView = new RemoteViews(getBaseContext().getPackageName(),
                R.layout.notification);

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

        // Setting up the alarm
        int timer;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            timer = 300000 + sharedPreferences.getInt("Progress", 0);
        } catch (Exception e) {
            e.printStackTrace();
            timer = 300000;
        }
        Log.i("TIMER", Integer.toString(timer));
        Intent alarmIntent = new Intent("alarm_receiver");
        PendingIntent alarmPIntent = PendingIntent.getBroadcast(this,
                ALARM_PENDING_INTENT_RC, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), alarmPIntent);
        } else {
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

        // create a new thread for syncing
       /* interacting with front end
        new Thread(){
            public void run(){
                while(true){
                    try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
                    runOnUiThread (new Runnable() {
                        @Override
                        public void run() {
                            sync();
                            Toast.makeText(MainActivity.this, "thread running", Toast.LENGTH_SHORT).show();}
                    });

                }
            }
        }.start();
        */
        //new Thread(new Sync()).start();
    }
    class Sync implements Runnable{
        @Override
        public void run() {
            while(true){
                try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
                //downloadPhotos();
            }
        }
    }

    @Override
    protected void onDestroy() {
       /*
        if(isBound){
            unbindService(serviceConnection);
            isBound = false;
        }
*/
       super.onDestroy();
        // unregister the MainActivity as a listener for preference changes
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPref.unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // the interval settings is changed
        if (key.equals(Controller.INTERVAL_KEY)) {
            int minutes = sharedPreferences.getInt(Controller.INTERVAL_KEY, Controller.INTERVAL_DEFAULT)
                    + Controller.INTERVAL_OFFSET;
            SetWallpaperService.updateInterval(minutes);
            Log.d("Preference Changed", "Updated interval to " + minutes + " minutes");
        }
    }

    /**
     * For testing purpose
     *
     * @param view
     */
    public void settingsClicked(View view) {
        Intent intent = new Intent(MainActivity.this, SettingPreference.class);
        startActivity(intent);
    }

    /**
     * Launches the gallery for a single photo
     * @param view, the view that calls it
     * */
    public void getSingleImageFromGallery(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, Controller.PHOTO_PICKER_SINGLE_CODE);
    }
    /**
     * Launches the gallery and lets you select photos
     * @param view, the view that calls this method
     * */
    public void getMultipleImagesFromGallery(View view){
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), Controller.PHOTO_PICKER_MULTIPLE_CODE);
    }

    /**
     * Handler for whenever an activity is returned
     * @param requestCode, the code for whatever activity was started
     * @param resultCode the code for how things went
     * @param data the returned intent with the data we want
     * */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == Controller.PHOTO_PICKER_SINGLE_CODE){
                Uri imageData = data.getData();
                /* IDK DO SOMETHING WITH THE SINGLE PHOTO */
            }
            if(requestCode == Controller.PHOTO_PICKER_MULTIPLE_CODE){
                /* SINGLE RETURNED. SHOULD NEVER COME HERE */
                if(data.getData() != null){
                    Uri imageData = data.getData();

                }
                /* MULTIPLE RETURNED. SHOULD ONLY EVER COME HERE */
                else{
                    if(data.getClipData() != null){
                        ClipData clipData = data.getClipData();
                        ArrayList<Uri> uriArrayList = new ArrayList<Uri>();
                        for(int i = 0; i < clipData.getItemCount(); i++){
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            uriArrayList.add(uri);
                            Log.i("Image URI", uri.getPath());
                            Log.i("Image Authority", uri.getAuthority());
                            Log.i("Scheme", uri.getScheme());
                            Log.i("Id", DocumentsContract.getDocumentId(uri));
                        }

                        controller.copyPhotos(uriArrayList);

                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *  -On first user, user registers a name
        -From then on, that name is in the Firebase and is linked to that device
            -sharing
            -username
            -friendsList with true/false

     -No password is necessary
     -No user class is necessary
     -Just get user's information from firebase
     *
     * */

    public boolean checkUserExists(String username){
        final boolean[] check = new boolean[1];
        DatabaseReference databaseReference = myFirebaseRef.child("user");
        Query queryRef = databaseReference.orderByChild("username").equalTo(username);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot == null || dataSnapshot.getValue() == null){
                    /*User doesn't exist*/
                    check[0] = false;
                }else{
                    /*User exists*/
                    check[0] = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return check[0];
    }

    public void register(String username){
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPref.edit().putString("username", username).apply();

        User firstUser = new User();
        myFirebaseRef.child("user").setValue(firstUser);// ???maybe???
    }

}
