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
import android.content.DialogInterface;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {

    Controller controller;
    DatabaseHelper myDb;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myFirebaseRef = database.getReference();
    Context myContext;


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

    //login email
    String email;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //setContentView(R.layout.test_photo_picker);

        // For Junit test
        setInstance(this);
        controller = new Controller(this);

        // request for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        // create folders for the app if they do not exist
        File dejaPhotoFolder = new File(Controller.DEJAPHOTOPATH);
        File dejaPhotoCopiedFolder = new File(Controller.DEJAPHOTOCOPIEDPATH);
        File dejaPhotoFriendsFolder = new File(Controller.DEJAPHOTOFRIENDSPATH);

        if (!dejaPhotoFolder.exists()) dejaPhotoFolder.mkdirs();
        if (!dejaPhotoCopiedFolder.exists()) dejaPhotoCopiedFolder.mkdirs();
        if (!dejaPhotoFriendsFolder.exists()) dejaPhotoFriendsFolder.mkdirs();

        final SharedPreferences mSharedPrefcheck = PreferenceManager.getDefaultSharedPreferences(this);
        email = mSharedPrefcheck.getString("username", "unknown");
        if(email.equals("unknown")){
            View v = getLayoutInflater().from(MainActivity.this).inflate(R.layout.user_input_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final EditText mEmail = (EditText) v.findViewById(R.id.username);
            builder.setView(v).setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    email = mEmail.getText().toString();
                    Log.e("the input username: ", email);
                    Log.d("To SharedPreference: ", email);
                    /*
                    int dot = email.indexOf('.');
                    String username = email.substring(0,dot) + email.substring(dot+1);
*/
                    mSharedPrefcheck.edit().putString("username",email).apply();
                    controller.createUser();
                }
            });
            //pop out the window
            builder.create().show();
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

        // create the view for the notification
        RemoteViews notificationView = new RemoteViews(getBaseContext().getPackageName(),
                R.layout.notification);

        // register the intents to each of the buttons in the notification bar
        Intent leftButtonIntent = new Intent("left_button_receiver");
        PendingIntent leftButtonPIntent = PendingIntent.getBroadcast(this,
                Controller.LEFT_PENDING_INTENT_RC, leftButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.previous, leftButtonPIntent);

        Intent rightButtonIntent = new Intent("right_button_receiver");
        PendingIntent rightButtonPIntent = PendingIntent.getBroadcast(this,
                Controller.RIGHT_PENDING_INTENT_RC, rightButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.next, rightButtonPIntent);

        Intent karmaButtonIntent = new Intent("karma_button_receiver");
        PendingIntent karmaButtonPIntent = PendingIntent.getBroadcast(this,
                Controller.KARMA_PENDING_INTENT_RC, karmaButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.karma, karmaButtonPIntent);

        Intent releaseButtonIntent = new Intent("release_button_receiver");
        PendingIntent releaseButtonPIntent = PendingIntent.getBroadcast(this,
                Controller.RELEASE_PENDING_INTENT_RC, releaseButtonIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.release, releaseButtonPIntent);

        // set the icon and time and build the notification of deja photo
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_wallpaper)
                .setWhen(System.currentTimeMillis())
                .setContent(notificationView)
                .build();

        // call the notification manager to show the notification in the status bar
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(Controller.NOTIFICATION_ID, notification);

        // setting up the alarms for changing wallpaper and syncing the database
        Intent alarmIntent = new Intent("alarm_receiver");
        PendingIntent alarmPIntent = PendingIntent.getBroadcast(this,
                Controller.ALARM_PENDING_INTENT_RC, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent syncIntent = new Intent("sync_receiver");
        PendingIntent syncPIntent = PendingIntent.getBroadcast(this,
                Controller.SYNC_PENDING_INTENT_RC, syncIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), alarmPIntent);
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), syncPIntent);
        } else {
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), SetWallpaperService.interval, alarmPIntent);
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), Controller.SYNC_INTERVAL, syncPIntent);
        }
    }

    @Override
    protected void onDestroy() {

       super.onDestroy();
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

    public void addFriends(View view){
        //final SharedPreferences mSharedPrefcheck = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i("ADDFRIEND", "START");
        View v = getLayoutInflater().from(MainActivity.this).inflate(R.layout.add_friend_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText mEmail = (EditText) v.findViewById(R.id.username);
        builder.setView(v).setPositiveButton("add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                email = mEmail.getText().toString();
                /*
                Log.e("the input username: ", email);
                Log.d("To SharedPreference: ", email);
                mSharedPrefcheck.edit().putString("friendEmail",email).apply();

                //access to the firebase and then add the name
                */
                controller.addFriend(email);
                Log.i("ADDFRIEND", "WHOA");

                Toast.makeText(myContext, "request has been sent to "+email, Toast.LENGTH_SHORT).show();
            }
        });
        Log.i("ADDFRIEND", "END");
        //pop out the window
        builder.create().show();

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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, final Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == Controller.PHOTO_PICKER_SINGLE_CODE){
                View v = getLayoutInflater().from(MainActivity.this).inflate(R.layout.rename_location_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final EditText location = (EditText) v.findViewById(R.id.locationname);
                builder.setView(v).setPositiveButton("update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newLocation = location.getText().toString();
                        Uri imageData = data.getData();
                        String path = imageData.getPath();
                        controller.updateLocationName(path,newLocation);

                    }
                });

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
                        }

                        controller.copyPhotos(uriArrayList);

                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void launchCamera(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File folderPath = new File(Controller.DEJAPHOTOPATH);
            if (!folderPath.exists()) folderPath.mkdirs();

            String timeStamp = new SimpleDateFormat("ddMMMyyyy_hh:mm:ss").format(new Date());
            File imageFile = new File(folderPath, "DejaPhoto" + timeStamp + ".jpg");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));

            startActivity(cameraIntent);
        }
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
        DatabaseReference databaseReference = myFirebaseRef.child("user");

        Query queryRef = databaseReference.orderByChild("username").equalTo(username);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot == null || dataSnapshot.getValue() == null){
                    /*User doesn't exist*/
                }else{
                    /*User exists*/
                    //POINT 1
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //POINT 2


        return false;
    }



    public void register(String username){
        SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPref.edit().putString("username", username).apply();

        //
        //mSharedPref.getString("username", "none");
    }

}
