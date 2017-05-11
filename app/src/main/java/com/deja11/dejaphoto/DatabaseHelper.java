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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.R.attr.format;
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
    public boolean updateField(String id, String point){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_1,id);
        contentValues.put(COL_DEJA_6,point);
        db.update(TABLE_NAME,contentValues,"ID = ?", new String[]{id}); // update based on id

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
        //Cursor res = db.rawQuery("select * from " + TABLE_NAME , null);
        StringBuffer buffer = new StringBuffer();

        //Cursor res = db.query(true, TABLE_NAME, null, COL_ID_1 +" = "+2, null, null, null, null, null);
        //Cursor res = db.query(true, TABLE_NAME, new String[] {COL_ID_1, COL_PATH_2,COL_DEJA_6}, null, null, null, null, COL_DEJA_6+" DESC", String.valueOf(3));
        //Cursor res = db.rawQuery("SELECT * FROM photo_table WHERE phonelocation = '" + chooseNextPath() + "'", null);
        //while (res.moveToNext()) {
    //res.moveToNext();
                String format = "MM-dd-yyyy HH:mm:ss";
                SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);



        Photo p = getNextPhoto();




                //String dateTime = formatter.format(date);

        buffer.append("\nphone location:" + p.getPhotoLocation());
        buffer.append("\ngeoLat :" + p.getGeoLocation().getLatitude());
        buffer.append("\ngeoLong :" + p.getGeoLocation().getLongitude());
        buffer.append("\n\ndate :" + formatter.format(p.getDate()));
        buffer.append("\ndejapoints:" + p.getDejaPoints());
        buffer.append("\nrelease :" + p.isReleased());
        buffer.append("\nkarma :" + p.isKarma());
        //}
        //res = db.query(true, TABLE_NAME, new String[]{COL_PATH_2}, null, null, null, null, COL_DEJA_6 + " DESC", String.valueOf(3));



        //updateField("2","25");
        //updateField("3","50");

        //buffer.append("\n\n");


        //buffer.append(chooseNextPath());
        //buffer.append(res.getCount());

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
/*
    80% chances of displaying the top 10 deja point
    20% chances of displaying a random photo

 */
    public String chooseNextPath(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;

        final int TOP10 = 3;

        /*
        * Update score first before deciding what to choose
        * */

        //update all points


        int  randomNumber;
        String pathToPhoto = null;
        Random rand = new Random();

        randomNumber = rand.nextInt(10)+1;

        pathToPhoto= randomNumber + "\n";
        if(randomNumber >7){
            res = db.rawQuery("SELECT * FROM photo_table", null);

            // Do loop to make sure that the data actually exists
            do{
                randomNumber= rand.nextInt(res.getCount()) + 1;
            }while (db.rawQuery("SELECT id FROM photo_table WHERE id = '" + randomNumber + "'", null).getCount()==0);

            res = db.rawQuery("SELECT phonelocation FROM photo_table WHERE id = " + randomNumber, null);
            while (res.moveToNext()) {
                pathToPhoto = res.getString(0);
            }
            //pathToPhoto += "\n Random photo\n" + randomNumber;
        }
        else {
            /* Note to self, make sure top 10 is not going over the database size*/

            // Select path from photo_table order by deja point descending Limit Top10


            // Do loop to make sure that the data actually exists
            do{
                randomNumber= rand.nextInt(TOP10) + 1;
                res = db.query(true, TABLE_NAME, new String[] {COL_PATH_2}, null, null, null, null, COL_DEJA_6+" DESC", String.valueOf(TOP10));
            }while (res.getCount()==0);

            for (int i = 0 ; i< randomNumber;i++) {
                if (!res.moveToNext()) {
                    break;
                }
                pathToPhoto = res.getString(0);
            }
            //pathToPhoto += "\n Top 10\n" + randomNumber ;

        }
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
}



