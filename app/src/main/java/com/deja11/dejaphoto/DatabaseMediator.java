package com.deja11.dejaphoto;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;

import static com.deja11.dejaphoto.DatabaseHelper.ALBUMPREFIX;
import static com.deja11.dejaphoto.DatabaseHelper.COL_ID_1;
import static com.deja11.dejaphoto.DatabaseHelper.COL_KARMA_8;
import static com.deja11.dejaphoto.DatabaseHelper.COL_PATH_2;
import static com.deja11.dejaphoto.DatabaseHelper.COL_REL_7;
import static com.deja11.dejaphoto.DatabaseHelper.TABLE_NAME;
import static com.deja11.dejaphoto.DatabaseHelper.TAGDATABASE;
import static com.deja11.dejaphoto.DatabaseHelper.currentUserName;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.firebase.database.Query;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;


/**
 * Created by Tee on 6/4/17.
 */

public class DatabaseMediator {
    private DatabaseHelper databaseHelper;
    private FirebaseHelper firebaseHelper;

    public DatabaseMediator(Context context){
        databaseHelper = new DatabaseHelper(context);
        firebaseHelper = new FirebaseHelper(context);
        this.initDatabase(context);
        //databaseHelper.initialize(context);
    }

    public void initDatabase(Context context){
        // Ensure that app has permission to access the storage
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            // Delegate to gattherPhotoInfo to gett raw information of all photos in the camera album
            Cursor cursor = gatherPhotoInfo(context);

            // Get columns of MediaStore object to get photos' information
            int columnIndexPath = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            int columnIndexDate = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int columnIndexLat = cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE);
            int columnIndexLong = cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE);

            String absolutePath = null;
            String dateAdded = null;
            double latitude = 0.0;
            double longitude = 0.0;

            while (cursor.moveToNext()) {
                absolutePath = cursor.getString(columnIndexPath); //path to the photo
                dateAdded = cursor.getString(columnIndexDate); //date in string format
                latitude = cursor.getDouble(columnIndexLat); // latitude
                longitude = cursor.getDouble(columnIndexLong); // longtitude

                // Make sure it is in the camera album
                if (absolutePath.toLowerCase().contains(ALBUMPREFIX.toLowerCase())) {
                    String photoName = Uri.fromFile(new File(absolutePath)).getLastPathSegment();
                    databaseHelper.tryToInsertData(absolutePath, latitude, longitude, dateAdded, 0, 0, 0,photoName);

                    firebaseHelper.tryToInsertFirebase(absolutePath, latitude, longitude, dateAdded, 0, 0, 0,photoName);


                }
                }
        }
    }
















    public void updatePoint(GeoLocation deviceLocation, Calendar deviceCalendar) {
        databaseHelper.updatePoint(deviceLocation, deviceCalendar);
    }

    public Photo getNextPhoto() {
        return databaseHelper.getNextPhoto();
    }

    public void updateKarma(String photoLocation) {

        databaseHelper.updateKarma(photoLocation);
        //TODO FIREBASE

        firebaseHelper.updateFirebase(currentUserName, photoLocation ,COL_KARMA_8,"1");
    }

    public void updateRelease(String photoLocation) {
        databaseHelper.updateRelease(photoLocation);
        firebaseHelper.updateFirebase(currentUserName, photoLocation ,COL_REL_7,"1");
    }



    public void downloadFriendPhotos(Context context) {
        firebaseHelper.downloadFriendPhotos(context);
    }

    public void insertFirebase(String path, Objects object){

    }

    /**
     * Gather all the information in the photo.
     * This method was based on the website
     * http://stackoverflow.com/questions/18590514/loading-all-the-images-from-gallery-into-the-application-in-android
     *
     * @param context the context to of the activity
     * @return cursor containing information of photo
     */
    public Cursor gatherPhotoInfo(Context context) {
        Uri uri;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE

        };
        Log.i(TAGDATABASE, "Successfully access storage");
        return context.getContentResolver().query(uri, projection, null, null, null);

    }
}
