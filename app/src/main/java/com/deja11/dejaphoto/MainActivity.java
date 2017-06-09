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
import android.support.v4.content.FileProvider;
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
    Context myContext;
    String username;

    // For testing purpose
    private static MainActivity instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //setContentView(R.layout.test_photo_picker);

        // For Junit test
        initTesting();

        // request for permissions
        requestPermissions();

        // get the username from shared preferences and prompt the user to log in (if needed)
        SharedPreferences mSharedPrefcheck = PreferenceManager.getDefaultSharedPreferences(this);
        username = mSharedPrefcheck.getString("username", "unknown");

        if(username.equals("unknown")){
            promptUserLogin(mSharedPrefcheck);
        }

        else {
            initApp();
        }
    }


    /***********************************************************************************************
     *                            FUNCTIONS FOR INITIALIZING APP                                   *
     **********************************************************************************************/

    /**
     * Requests permission from the user
     */
    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 2);
        }
    }

    /**
     * Creates a pop-up dialog that prompts the user to log in
     */
    private void promptUserLogin(final SharedPreferences mSharedPrefcheck) {
        View v = getLayoutInflater().from(MainActivity.this).inflate(R.layout.user_input_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText mEmail = (EditText) v.findViewById(R.id.username);
        builder.setView(v).setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                username = mEmail.getText().toString();
                Log.e("the input username: ", username);
                Log.d("To SharedPreference: ", username);
                //int dot = email.indexOf('.');
                //String username = email.substring(0,dot) + email.substring(dot+1);

                mSharedPrefcheck.edit().putString("username", username).apply();
                //controller.createUser();
              
                
                initApp();
              
            }
        });
        //pop out the window
        builder.create().show();
    }

    /**
     * This is the main function that initializes the app.
     * It calls several smaller functions that initializes different parts of the app.
     */
    private void initApp() {
        createFolders();
        initNotificationBar();
        initAlarms();

        controller = new Controller(this);
        //controller.databaseMediator.initDatabase(MainActivity.this);
    }

    /**
     * Creates folders for the app if they do not exist
     */
    private void createFolders() {
        File dejaPhotoFolder = new File(Controller.DEJAPHOTOPATH);
        File dejaPhotoCopiedFolder = new File(Controller.DEJAPHOTOCOPIEDPATH);
        File dejaPhotoFriendsFolder = new File(Controller.DEJAPHOTOFRIENDSPATH);

        if (!dejaPhotoFolder.exists()) dejaPhotoFolder.mkdirs();
        if (!dejaPhotoCopiedFolder.exists()) dejaPhotoCopiedFolder.mkdirs();
        if (!dejaPhotoFriendsFolder.exists()) dejaPhotoFriendsFolder.mkdirs();
    }

    /**
     * Creates the notification bar view and sets the listeners for the buttons in it
     */
    private void initNotificationBar() {

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
    }

    /**
     * Sets the alarms for:
     *   1. changing the wallpaper
     *   2. syncing with the online database
     */
    private void initAlarms() {
        // create intents for the alarms
        Intent alarmIntent = new Intent("alarm_receiver");
        PendingIntent alarmPIntent = PendingIntent.getBroadcast(this,
                Controller.ALARM_PENDING_INTENT_RC, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent syncIntent = new Intent("sync_receiver");
        PendingIntent syncPIntent = PendingIntent.getBroadcast(this,
                Controller.SYNC_PENDING_INTENT_RC, syncIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // schedule the alarms
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


    /***********************************************************************************************
     *                               BUTTON ONCLICK FUNCTIONS                                      *
     **********************************************************************************************/

    /**
     * OnClick function for the Settings button.
     * Launches the settings activity.
     *
     * @param view
     */
    public void settingsClicked(View view) {
        Intent intent = new Intent(MainActivity.this, SettingPreference.class);
        startActivity(intent);
    }

    /**
     * OnClick function for the Add Friends button.
     * Displays a pop-up that prompts the user to enter the username of the friend.
     *
     * @param view
     */
    public void addFriends(View view){
        //final SharedPreferences mSharedPrefcheck = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i("ADDFRIEND", "START");
        View v = getLayoutInflater().from(MainActivity.this).inflate(R.layout.add_friend_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText mEmail = (EditText) v.findViewById(R.id.username);
        builder.setView(v).setPositiveButton("add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = mEmail.getText().toString();
                /*
                Log.e("the input username: ", email);
                Log.d("To SharedPreference: ", email);
                mSharedPrefcheck.edit().putString("friendEmail",email).apply();

                //access to the firebase and then add the name
                */
                controller.addFriend(email);
                Log.i("ADDFRIEND", "WHOA");

                //Toast.makeText(myContext, "request has been sent to "+email, Toast.LENGTH_SHORT).show();
            }
        });
        Log.i("ADDFRIEND", "END");
        //pop out the window
        builder.create().show();

    }

    /**
     * Launches the gallery for a single photo
     * @param view, the view that calls it
     */
    public void getSingleImageFromGallery(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, Controller.PHOTO_PICKER_SINGLE_CODE);
    }

    /**
     * OnClick function for the Import Photos button
     * Launches the gallery and lets the user select photos.
     *
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

                        //controller.copyPhotos(uriArrayList);

                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * OnClick function for the Camera button.
     * Launches the camera app to take a single photo and saves that photo into the DejaPhoto album.
     *
     * Code credits: https://developer.android.com/training/camera/photobasics.html
     *
     * @param view - the view that was clicked; required parameter for an onClick function
     */
    public void launchCamera(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // try to launch the camera if there is an app in the phone that can take a picture
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // get the path to the DejaPhoto folder (or create it if it doesn't exist)
            File folderPath = new File(Controller.DEJAPHOTOPATH);
            if (!folderPath.exists()) folderPath.mkdirs();

            try {
                // create a file with a unique name (via date and time)
                String timeStamp = new SimpleDateFormat("ddMMMyyyy_hh:mm:ss").format(new Date());
                File imageFile = File.createTempFile("DejaPhoto" + timeStamp, ".jpg", folderPath);
                Uri photoUri = FileProvider.getUriForFile(this, "com.DejaPhoto.fileprovider", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                Log.i("Camera", "Starting Camera Intent");
                startActivity(cameraIntent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Camera", "Error Creating Photo");
                Toast.makeText(this, "Cannot open camera", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /***********************************************************************************************
     *                                    TESTING FUNCTIONS                                        *
     **********************************************************************************************/

    public static MainActivity getInstance() {
        if (instance == null) {
            setInstance(instance);
        }
        return instance;
    }
    public static void setInstance(MainActivity instance) {
        MainActivity.instance = instance;
    }

    public void initTesting() {
        setInstance(this);
    }
}
