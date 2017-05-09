/*
This DatabaseHelper class was based on the video series "Android SQLite Database Tutorial"
made by the youtube channel "ProgrammingKnowledge"

 */

package com.deja11.dejaphoto;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.deja11.dejaphoto.R.id.release;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "deja.db";
    public static final String TABLE_NAME = "photo_table";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "PHONELOCATION";
    public static final String COL_3 = "GEOLOCATIONLAT";
    public static final String COL_4 = "GEOLOCATIONLONG";
    public static final String COL_5 = "DATE";
    public static final String COL_6 = "DEJAPOINTS";
    public static final String COL_7 = "RELEASED";
    public static final String COL_8 = "KARMA";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "PHONELOCATION TEXT, GEOLOCATIONLAT DECIMAL(10, 8)," +
                " GEOLOCATIONLONG FLOAT DECIMAL(11, 8), DATE TEXT, DEJAPOINTS INTEGER," +
                " RELEASED BOOL, KARMA BOOL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*
        Insert a new photo into the database
     */
    public boolean insertData(String phoneLocation, double geoLat, double geoLong, String date, int dejapoints, int isReleased, int isKarma) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, phoneLocation);
        contentValues.put(COL_3, geoLat);
        contentValues.put(COL_4, geoLong);
        contentValues.put(COL_5, date);
        contentValues.put(COL_6, dejapoints);
        contentValues.put(COL_7, isReleased);
        contentValues.put(COL_8, isKarma);

        long result = db.insert(TABLE_NAME, null, contentValues); // return -1 if not successful
        if (result == -1)
            return false;
        else
            return true;
    }

    /*
        Return the path of a photo
     */
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    /*
        Add photos all the photo in the camera album to the database
     */
    public void initialize(Context context){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {

            Cursor cursor = gatherPhotoInfo(context);
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
                latitude = cursor.getDouble(columnIndexLat);
                longitude = cursor.getDouble(columnIndexLong);
                this.insertData(absolutePath,latitude,longitude, dateAdded,0,0,0);
            }
        }
    }


    /*
        This function was based on the website
        http://stackoverflow.com/questions/18590514/loading-all-the-images-from-gallery-into-the-application-in-android
     */
    public Cursor gatherPhotoInfo(Context context){
        Uri uri;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE

        };
        return context.getContentResolver().query(uri, projection, null, null, null);

    }


}



