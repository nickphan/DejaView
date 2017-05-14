/**
 * This class is used as a gallery databases that store, access all the photos.
 * This class was based on
 *
 * @author : Sothyrak Tee Srey (Some methods are based on the video series "Android SQLite
 * Database Tutorial" made by the youtube channel "ProgrammingKnowledge"
 *
 * *Note* query is in the following format:
 * Cursor query (boolean distinct,
 *              String table,
 *              String[] columns,
 *              String selection,
 *              String[] selectionArgs,
 *              String groupBy,
 *              String having,
 *              String orderBy,
 *              String limit)
 *
 **/

package com.deja11.dejaphoto;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.R.attr.format;
import static android.R.attr.id;
import static android.R.attr.path;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "deja.db";
    public static final String TABLE_NAME = "photo_table";

    public static final String COL_ID_1 = "ID";
    public static final String COL_PATH_2 = "PHONELOCATION";
    public static final String COL_LAT_3 = "GEOLOCATIONLAT";
    public static final String COL_LONG_4 = "GEOLOCATIONLONG";
    public static final String COL_DATE_5 = "DATE";
    public static final String COL_DEJA_6 = "DEJAPOINTS";
    public static final String COL_REL_7 = "RELEASED";
    public static final String COL_KARMA_8 = "KARMA";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " PHONELOCATION TEXT, GEOLOCATIONLAT DECIMAL(10, 8)," +
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
        contentValues.put(COL_PATH_2, phoneLocation);
        contentValues.put(COL_LAT_3, geoLat);
        contentValues.put(COL_LONG_4, geoLong);
        contentValues.put(COL_DATE_5, date);
        contentValues.put(COL_DEJA_6, dejapoints);
        contentValues.put(COL_REL_7, isReleased);
        contentValues.put(COL_KARMA_8, isKarma);

        long result = db.insert(TABLE_NAME, null, contentValues); // return -1 if not successful
        if (result == -1)
            return false;
        else
            return true;
    }

    /*
        Update a field
     */
    public boolean updateField(int id, String column, String newValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_1,id);
        contentValues.put(column,newValue);
        db.update(TABLE_NAME,contentValues,"ID = ?", new String[]{Integer.toString(id)}); // update based on id

        return true;
    }
    public boolean updateField(int id, String column, int newValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_1,id);
        contentValues.put(column,newValue);
        db.update(TABLE_NAME,contentValues,"ID = ?", new String[]{Integer.toString(id)}); // update based on id

        return true;
    }

    public void updateKarma(String photoLocation){

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.query(true,TABLE_NAME,new String[]{COL_ID_1},COL_PATH_2+"='"+photoLocation+"'",null,null,null,null,null);
        res.moveToNext();
        updateField(res.getInt(0),COL_KARMA_8,1);
    }

    public void updateRelease(String photoLocation){

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.query(true,TABLE_NAME,new String[]{COL_ID_1},COL_PATH_2+"='"+photoLocation+"'",null,null,null,null,null);
        res.moveToNext();
        updateField(res.getInt(0),COL_REL_7,1);

//updateField(4,COL_KARMA_8,1);


    }


    public void test(Context context){

        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select * from " + TABLE_NAME , null);
        StringBuffer buffer = new StringBuffer();
        //buffer.append(res.getCount() + "");
        //chooseNextPath();

        //updateField(2,COL_KARMA_8,"1");
        //updateField(3,COL_DEJA_6,50);
        //updateField(2,COL_DEJA_6,25);
        //updateField(3,COL_KARMA_8,1);




        buffer.append(printAll(context));

        showMessage("Data",buffer.toString(),context);



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

            String str2 = "DCIM";

            while (cursor.moveToNext()) {
                absolutePath = cursor.getString(columnIndexPath); //path to the photo
                dateAdded = cursor.getString(columnIndexDate); //date in string format
                latitude = cursor.getDouble(columnIndexLat);
                longitude = cursor.getDouble(columnIndexLong);

                if (absolutePath.toLowerCase().contains(str2.toLowerCase())){
                    try{
                        Cursor res = db.rawQuery("SELECT id FROM photo_table WHERE phonelocation = '" + absolutePath + "'", null);
                        if(res.getCount() ==0) {
                            this.insertData(absolutePath, latitude, longitude, dateAdded, 0, 0, 0);
                            Toast.makeText(context,"Yass : "+absolutePath,Toast.LENGTH_SHORT).show();
                            Log.i("Database insertion", absolutePath+" is now in the table");
                        }}catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /*
        This method was based on the website
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
/*
    80% chances of displaying the top 10 photo with highest deja point
    20% chances of displaying a random photo

 */
    public String chooseNextPath(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;

        final int TOP10 = 3;
        final int TOP3 = 3;

        /*
        * Update score first before deciding what to choose
        * */

        //update all points


        int randomNumber;
        int randomPosition = 0;
        String pathToPhoto = null;
        Random rand = new Random();

        randomNumber = rand.nextInt(10)+1;

        // Random number gives number between 1 and 10
        // 1-6 pick top 5 highest deja point
        // 7-9 pick top 10 most recent
        // 10 choose random photo
        if(randomNumber >= 10){
            // Don't choose photos that have been released
            res = db.query(true, TABLE_NAME, new String[]{COL_PATH_2}, COL_REL_7+ "= 0", null, null, null, null, null);
            //pathToPhoto += "\n Random photo\n" + randomNumber;
        }
        else if(randomNumber >= 7){
            res = db.query(true, TABLE_NAME, new String[]{COL_PATH_2}, COL_REL_7+ "= 0", null, null, null, COL_DATE_5 + " DESC", String.valueOf(TOP3));
        }
        else {
            /* Note to self, make sure top 10 is not going over the database size*/
            // Select path from photo_table order by deja point descending Limit Top10
            res = db.query(true, TABLE_NAME, new String[]{COL_PATH_2}, COL_REL_7+ "= 0", null, null, null, COL_DEJA_6 + " DESC", String.valueOf(TOP10));
            //pathToPhoto += "\n Top 10\n" + randomNumber ;

        }



        randomPosition = rand.nextInt(res.getCount());
        res.moveToPosition(randomPosition);
        pathToPhoto = res.getString(0);

        return pathToPhoto;
    }

    public Photo getNextPhoto(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM photo_table WHERE phonelocation = '" + chooseNextPath() + "'", null);

        res.moveToNext();

        String photoLocation = res.getString(1);

        GeoLocation geoLocation = new GeoLocation(res.getDouble(2),res.getDouble(3));

        Date date = new Date(Long.parseLong(res.getString(4)));

        int dejapoint = res.getInt(5);

        boolean isReleased = res.getInt(6) > 0 ? true : false;
        boolean isKarma = res.getInt(7) > 0 ? true : false;

        return new Photo(photoLocation,geoLocation,date,dejapoint,isReleased,isKarma);

    }

    public void updatePoint(GeoLocation deviceLocation){
        // loop throught all rows
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME , null);
        //each row get the id then update that id

        int newPoint = 0;
        int id = 0;
        GeoLocation photoGeoLocation;
        Date date ;
        boolean isKarma;

        while (res.moveToNext()) {

            id = res.getInt(0);


            // if location is nearby, add 2 points
            photoGeoLocation = new GeoLocation(res.getDouble(2),res.getDouble(3));
            if (photoGeoLocation.isNearCurrentLocation(deviceLocation)){
                newPoint+=10;
            }

            // if date is same add 2 points
            date = new Date(Long.parseLong(res.getString(4)));
            Calendar photoTime = Calendar.getInstance();
            photoTime.setTime(date);
            Calendar currentTime = Calendar.getInstance();

            // 1 point for matching day of week
            if (currentTime.get(Calendar.DAY_OF_WEEK) == photoTime.get(Calendar.DAY_OF_WEEK)) {
                newPoint += 10;
            }

            int photoTimeOfDay = (photoTime.get(Calendar.HOUR_OF_DAY) * 60) + photoTime.get(Calendar.MINUTE);
            int currentTimeOfDay = (currentTime.get(Calendar.HOUR_OF_DAY) * 60) + currentTime.get(Calendar.MINUTE);
            int lowerTimeBound = (currentTimeOfDay - 120) % 1440, highTimeBound = (currentTimeOfDay + 120) % 1440;

            // 1 point for matching time of day
            if (photoTimeOfDay >= lowerTimeBound && photoTimeOfDay <= highTimeBound) {
                newPoint += 10;
            }

            // if it is karma add 1 point
            isKarma = res.getInt(7) > 0 ? true : false;
            if(isKarma) {newPoint+=5;}


            updateField(id,COL_DEJA_6,newPoint);
            newPoint = 0;
        }

        /*Loop through 10 latest photo*/
        res = db.query(true, TABLE_NAME, new String[]{COL_PATH_2}, COL_REL_7+ "= 0", null, null, null, COL_DATE_5 + " DESC", String.valueOf(10));

    }

    public String printAll(Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        StringBuffer buffer = new StringBuffer();


        res = db.rawQuery("select * from " + TABLE_NAME , null);
        //res = db.query(true, TABLE_NAME, new String[]{COL_PATH_2}, null, null, null, null, COL_DEJA_6 + " DESC", String.valueOf(3));
        //res = db.query(true, TABLE_NAME, null, COL_REL_7+ "= 0", null, null, null, COL_DATE_5 + " DESC", String.valueOf(3));
        //res = db.query(true, TABLE_NAME, new String[]{COL_ID_1,COL_PATH_2,COL_DEJA_6}, COL_REL_7+ "= 0", null, null, null, null, null);


        buffer.append(res.getCount() + "\n");

        while (res.moveToNext()) {

            //buffer.append(res.moveToPosition(4)+"\n");

            String format = "MM-dd-yyyy HH:mm:ss";
            SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);

                buffer.append("\n\nId :" + res.getString(0));
                buffer.append("\nphone location:" + res.getString(1));
                buffer.append("\ngeoLat :" + res.getString(2));
                buffer.append("\ngeoLong :" + res.getString(3));
                buffer.append("\ndate :" + formatter.format(new Date(Long.parseLong(res.getString(4)))));
                buffer.append("\ndejapoints:" + res.getString(5));
                buffer.append("\nrelease :" + res.getString(6));
                buffer.append("\nkarma :" + res.getString(7));


        }
        return buffer.toString();
    }

}



