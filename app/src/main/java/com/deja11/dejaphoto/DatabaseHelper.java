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
import java.util.Random;

import static android.R.attr.format;
import static android.R.attr.name;
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
                " RELEASED INTEGER, KARMA INTEGER)");
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

    public void test(Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        String query = "SELECT * FROM photo_table WHERE id not in (7,10)";
        StringBuffer buffer = new StringBuffer();
        for(int i = 0 ; i< 1 ; i++){
            //res = db.rawQuery("SELECT * FROM photo_table WHERE karma = 0 '"+i+"'", null);
            if(res.getCount()==0)
            buffer.append(i+"\n");
        }
        //Cursor res = myDb.getAllData();


        String pathToPhoto = "/storage/emulated/0/DCIM/Camera/corgi2.jpg";
        //res = db.rawQuery("SELECT id FROM photo_table WHERE phonelocation = '" + pathToPhoto + "'", null);

        res = db.rawQuery("SELECT * FROM photo_table", null);


        //buffer.append(res.getCount() + " " + res.getColumnCount());
        Random rand = new Random();

        int  n;
        do{
            n= rand.nextInt(res.getCount()) + 1;
        }while (db.rawQuery("SELECT id FROM photo_table WHERE id = '" + n + "'", null).getCount()==0);

        res = db.rawQuery("SELECT phonelocation FROM photo_table WHERE id = " + n, null);
        while (res.moveToNext()) {
            buffer.append(res.getString(0)+" ");}




/*
        while (res.moveToNext()) {

                String format = "MM-dd-yyyy HH:mm:ss";
                SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);

                String dateTime = formatter.format(new Date(Long.parseLong(res.getString(4))));

                buffer.append("\n\nId :" + res.getString(0));
                buffer.append("\nphone location:" + res.getString(1));
                buffer.append("\ngeoLat :" + res.getString(2));
                buffer.append("\ngeoLong :" + res.getString(3));
                buffer.append("\n\ndate :" + dateTime);
                buffer.append("\ndejapoints:" + res.getString(5));
                buffer.append("\nrelease :" + res.getString(6));
                buffer.append("\nkarma :" + res.getString(7));
        }
*/


        // show all data
        showMessage("Data", buffer.toString(),context);
    }

    /*
        Add photos all the photo in the camera album to the database
     */
    public void initialize(Context context){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {

            SQLiteDatabase db = this.getWritableDatabase();
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

                Cursor res = db.rawQuery("SELECT id FROM photo_table WHERE phonelocation = '" + absolutePath + "'", null);
                if(res.getCount() ==0) {
                    this.insertData(absolutePath, latitude, longitude, dateAdded, 0, 0, 0);
                }
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

    // for displaying a message board
    public void showMessage(String title, String message,Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}



