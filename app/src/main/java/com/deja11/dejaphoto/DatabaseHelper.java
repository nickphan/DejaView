/**
 * This class is used as a gallery databases that store, access all the photos.
 * This class was based on
 *
 * @author Sothyrak Tee Srey (Some methods are based on the video series "Android SQLite
 * Database Tutorial" made by the youtube channel "ProgrammingKnowledge"
 * <p>
 * *Note* query is in the following format:
 * Cursor query (boolean distinct,
 * String table,
 * String[] columns,
 * String selection,
 * String[] selectionArgs,
 * String groupBy,
 * String having,
 * String orderBy,
 * String limit)
 **/

package com.deja11.dejaphoto;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAGDATABASE = "DatabaseHelper";

    public static final int TOP5 = 5;

    public static final String DATABASE_NAME = "deja.db";
    public static final String TABLE_NAME = "photo_table";
    public static final String ALBUMPREFIX = "Deja";

    public static final String COL_ID_1 = "ID";
    public static final String COL_PATH_2 = "PHONELOCATION";
    public static final String COL_LAT_3 = "GEOLOCATIONLAT";
    public static final String COL_LONG_4 = "GEOLOCATIONLONG";
    public static final String COL_DATE_5 = "DATE";
    public static final String COL_DEJA_6 = "DEJAPOINTS";
    public static final String COL_REL_7 = "RELEASED";
    public static final String COL_KARMA_8 = "KARMA";
    public static final String COL_FILE_NAME_9 = "FILENAME";
    public static final String COL_OWNER_10 = "OWNER";
    public static final String COL_LOC_NAME_11 = "LOCATIONNAME";
    public static final String COL_TOTAL_KARMA_12 = "TOTALKARMA";

    //public static final String currentUserName = "Teehee@heeheecom";
    //public static final String currentUserName = "yoohoohoo@heeheecom";
    //public static final String currentUserName = "physicalDevice@teesphonecom";

    Context context;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        Log.i(TAGDATABASE, "Database constructor called");
        this.context = context;
    }


    /**
     * Create a table in the database to store all the photo
     * ID : primary key and auto increment
     * PHONELOCATION : string containing path of the photo in the device
     * GEOLOCATIONLAT/GEOLOCATIONLONG : latitiude and longitude of the location of photo
     * DATE : the timestamp of the photo, milliseconds in string format
     * DEJAPOINTS : points assigned to each photo
     * RELEASED, KARMA : value 0 or 1 determined whether a photo is released or karma'd
     *
     * @param db database to be created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PATH_2 + " TEXT, " +
                COL_LAT_3 + " DECIMAL(10, 8), " +
                COL_LONG_4 + " DECIMAL(11, 8), " +
                COL_DATE_5 + " TEXT, " +
                COL_DEJA_6 + " INTEGER, " +
                COL_REL_7 + " INTEGER, " +
                COL_KARMA_8 + " INTEGER , " +
                COL_FILE_NAME_9 + " TEXT, " +
                COL_OWNER_10 + " TEXT, " +
                COL_LOC_NAME_11 + " TEXT, " +
                COL_TOTAL_KARMA_12 + " INTEGER   )");

        Log.i(TAGDATABASE, "Table created");

    }

    /**
     * Required to have onUpgrade in the event that database version changed
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        Log.d(TAGDATABASE, "Table recreated");
    }

    /**
     * Insert a new photo into the database
     *
     * @param phoneLocation path to the photo
     * @param geoLat        Latitude of the location photo was taken
     * @param geoLong       Longtitude of the location photo was taken
     * @param date          Date and time photo was taken
     * @param dejapoints    point assigned to the photo
     * @param isReleased    whether or not the photo is released
     * @param isKarma       whether or not the photo is karma'd
     * @return true if insertion is successful, otherwise false
     */
    public boolean insertData(String phoneLocation, double geoLat, double geoLong, String date, int dejapoints, int isReleased, int isKarma, String photoName, String owner, String locationName, int totalKarma) {
        SQLiteDatabase db = this.getWritableDatabase();


        // Put all data in a container
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PATH_2, phoneLocation);
        contentValues.put(COL_LAT_3, geoLat);
        contentValues.put(COL_LONG_4, geoLong);
        contentValues.put(COL_DATE_5, date);
        contentValues.put(COL_DEJA_6, dejapoints);
        contentValues.put(COL_REL_7, isReleased);
        contentValues.put(COL_KARMA_8, isKarma);
        contentValues.put(COL_FILE_NAME_9, photoName);
        contentValues.put(COL_OWNER_10, owner);
        contentValues.put(COL_LOC_NAME_11, locationName);
        contentValues.put(COL_TOTAL_KARMA_12, totalKarma);


        Log.i(TAGDATABASE, "Data inserted correctly");
        boolean status = db.insert(TABLE_NAME, null, contentValues) != -1;

        return status;
    }

    /**
     * Update a field in the database
     *
     * @param id       id of the row to be updated
     * @param column   the column of to be updated
     * @param newValue replacing the old value data. int because only dejapoint, karma and released
     *                 are updated.
     * @return true if more than 1 row is updated, false otherwise
     */
    public boolean updateField(int id, String column, int newValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Put data id and new data in a container
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_1, id);
        contentValues.put(column, newValue);

        Log.i(TAGDATABASE, "Data updated correctly");

        boolean status = db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{Integer.toString(id)}) > 0;

        return status;

    }

    /**
     *
     * Update a particular field in the database
     *
     * @param photoLocation
     * @param column
     * @param newValue
     */
    public void updateField(String photoLocation, String column, String newValue) {
        int id = findIdByColumn(COL_PATH_2, photoLocation);

        SQLiteDatabase db = this.getWritableDatabase();

        // Put data id and new data in a container
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_1, id);
        contentValues.put(column, newValue);

        Log.i(TAGDATABASE, "Data updated correctly");
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{Integer.toString(id)});


    }


    /**
     *  Delete all the photos of a particular owner
     * @param owner
     */
    public void deletePhotos(String owner) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DELETING FROM SQL", "delete owner " + owner);
        try {
            db.delete(TABLE_NAME, COL_OWNER_10 + "= '" + owner + "'", null);

        } catch (Exception e) {
            Log.e("DELETING FROM SQL", "ERROR Already deleted");
        }
    }

    /**
     * Update the value of the karma to 1
     *
     * @param photoLocation location of the photo that has been karma'd
     */
    public void updateKarma(String photoLocation, int totalKarma) {

        int id = findIdByColumn(COL_PATH_2, photoLocation);
        //Delegate to updated field
        updateField(id, COL_KARMA_8, 1);
        updateField(id, COL_TOTAL_KARMA_12, totalKarma + 1);


        Log.i(TAGDATABASE, photoLocation + " set to karma'd");
    }

    /**
     * Update the value of the release to 1
     *
     * @param photoLocation location of the photo that has been released
     */
    public void updateRelease(String photoLocation) {

        int id = findIdByColumn(COL_PATH_2, photoLocation);

        //Delegate to updated field
        updateField(id, COL_REL_7, 1);

        Log.i(TAGDATABASE, photoLocation + " set to released");
    }

    /**
     * Find the id in the database
     *
     * @param column Column name
     * @param value  Value in that column
     * @return id the id that the column = value
     */
    public int findIdByColumn(String column, String value) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Select id from photo_table where column = value
        Cursor res = db.query(true, TABLE_NAME, new String[]{COL_ID_1}, column + "='" + value + "'", null, null, null, null, null);

        if (res.getCount() >= 0) {
            res.moveToNext();
            Log.i(TAGDATABASE, value + " is found at id = " + res.getInt(0));
            return res.getInt(0);
        } else {
            Log.d(TAGDATABASE, value + " is not found");
            return -1;
        }
    }


    /**
     * Check if data already exists before inserting
     * @param absolutePath
     * @param geoLat
     * @param geoLong
     * @param date
     * @param dejapoints
     * @param isReleased
     * @param isKarma
     * @param photoName
     * @param owner
     * @param locationName
     * @param totalKarma
     */
    public void tryToInsertData(String absolutePath, double geoLat, double geoLong, String date, int dejapoints, int isReleased, int isKarma, String photoName, String owner, String locationName, int totalKarma) {

        SQLiteDatabase db = this.getWritableDatabase();
        // Check if it already exist before inserting to avoid duplicated
        try {
            Cursor res = db.query(true, TABLE_NAME, new String[]{COL_ID_1}, COL_PATH_2 + "='" + absolutePath + "'", null, null, null, null, null);

            if (res.getCount() == 0) {
                this.insertData(absolutePath, geoLat, geoLong, date, dejapoints, isReleased, isKarma, photoName, owner, locationName, totalKarma);
                Log.i("Database insertion", absolutePath + " is now in the table");
            }

        } catch (Exception e) {
            Log.d(TAGDATABASE, absolutePath + "Already");
            e.printStackTrace();
        }
    }

    /**
     * Choose the next path from the database.
     * 60% chance of choosing the top 5 photos with the highest deja point
     * 30% chance of choosing the top 5 most recent
     * 10% chance of choosing a random photo in the database
     *
     * @return
     */
    public String chooseNextPath() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;

        int randomNumber;
        int randomPosition = 0;
        String pathToPhoto = null;
        Random rand = new Random();

        String selection;


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username = sharedPreferences.getString("username", "unknown");
        boolean viewMyPhoto = SettingPreference.viewMyPhoto;
        if (viewMyPhoto) {
            Log.d("Setting:", "YOU CAN VIEW UR OWN PHOTOS");
        } else {
            Log.d("Setting:", "YOU CANNOT VIEW UR OWN PHOTOS");
        }
        boolean viewFriendPhoto = SettingPreference.viewFriendPhoto;

        if (viewFriendPhoto) {
            Log.d("Setting:", "YOU CAN VIEW UR FRIENDS' PHOTOS");
        } else {
            Log.d("Setting:", "YOU CANNOT VIEW UR FRIENDS' PHOTOS");
        }

        selection = COL_REL_7 + "= 0";


        // Selecting from the database based on the settings
        if (viewMyPhoto && !viewFriendPhoto) {
            selection += " AND ";
            selection += COL_OWNER_10 + " == '" + username + "'";
            Log.d("View Status", "OWN is On, Friend is OFF");
        } else if (!viewMyPhoto && viewFriendPhoto) {
            selection += " AND ";
            selection += COL_OWNER_10 + " != '" + username + "'";

            Log.d("View Status", "OWN is OFF, Friend is ON");

        } else if (!viewMyPhoto && !viewFriendPhoto) {

            Log.d("View Status", "OWN is OFF, Friend is OFF");
            //return "empty";
        } else {

            Log.d("View Status", "OWN is ON, Friend is ON");
        }


        // Generate a random number
        randomNumber = rand.nextInt(10) + 1;

        // Random number gives number between 1 and 10
        // 1-6 pick top 5 highest deja point
        // 7-9 pick top 5 most recent
        // 10 choose random photo
        if (randomNumber >= 10) {
            // Select photolocation from photo_table where Released = 0
            res = db.query(true, TABLE_NAME, new String[]{COL_PATH_2}, selection, null, null, null, null, null);
        } else if (randomNumber >= 7) {

            // Select phonelocation from photo_table where Released = 0 AND (emailSenderID=fID OR emailSenderID=myID) order by date desc limit 5
            res = db.query(true, TABLE_NAME, new String[]{COL_PATH_2}, selection, null, null, null, COL_DATE_5 + " DESC", String.valueOf(TOP5));
        } else {
            // Select phonelocation from photo_table where Released = 0 order by dejapoint desc limit 5
            res = db.query(true, TABLE_NAME, new String[]{COL_PATH_2}, selection, null, null, null, COL_DEJA_6 + " DESC", String.valueOf(TOP5));

        }

        if (res.getCount() == 0) {

            Log.d("CHOOSING PHOTO", "database is empty");
            return "empty";
        }

        // Choose randomly among those top 5 or random data
        randomPosition = rand.nextInt(res.getCount());
        res.moveToPosition(randomPosition);

        // Get the path of that photo
        pathToPhoto = res.getString(0);

        Log.i("Selecting photo", "Getting a random path " + pathToPhoto);
        return pathToPhoto;
    }


    /**
     * Return the next photo object
     *
     * @return photo object
     */
    public Photo getNextPhoto() {
        SQLiteDatabase db = this.getWritableDatabase();

        String nextphotoLocation = chooseNextPath();

        if (nextphotoLocation.equals("empty")) {

            Log.d("CHOOSING PHOTO", "next photo returned is also empty");
            return null;
        }


        // get a random photo an then get all their information
        // Select * from photo_table where phonelocation = chooseNextPath()


        Cursor res = db.query(true, TABLE_NAME, null, COL_PATH_2 + "='" + nextphotoLocation + "'", null, null, null, null, null);

        res.moveToNext();

        String photoLocation = res.getString(1);

        GeoLocation geoLocation = new GeoLocation(res.getDouble(2), res.getDouble(3));

        Date date = new Date(Long.parseLong(res.getString(4)));

        int dejapoint = res.getInt(5);

        boolean isReleased = res.getInt(6) > 0 ? true : false;
        boolean isKarma = res.getInt(7) > 0 ? true : false;
        int totalKarma = res.getInt(11);


        String dateString = res.getString(4);
        String fileName = res.getString(8);
        String owner = res.getString(9);
        String locName = res.getString(10);

        /*code to get total karma as int*/

        Log.i("CHOOSING PHOTO", "Next photo object returned");
        return new Photo(photoLocation, geoLocation, date, dejapoint, isReleased, isKarma, totalKarma, dateString, fileName, owner, locName);
    }

    /**
     * Update deja point of all the photos in the database
     *
     * @param deviceLocation
     */

    public void updatePoint(GeoLocation deviceLocation, Calendar deviceCalendar) {
        // loop throught all rows
        SQLiteDatabase db = this.getWritableDatabase();
        //select * from photo_table
        Cursor res = db.query(true, TABLE_NAME, null, null, null, null, null, null, null);

        int newPoint = 0;
        int id = 0;
        GeoLocation photoGeoLocation;
        Date date;
        boolean isKarma;

        //Each row get the id then update that id
        while (res.moveToNext()) {

            id = res.getInt(0);

            // Get location of the photo
            photoGeoLocation = new GeoLocation(res.getDouble(2), res.getDouble(3));

            // If location is nearby, add 10 points
            if (photoGeoLocation.isNearCurrentLocation(deviceLocation)) {
                newPoint += 10;
            }

            // Get date and time of the photo
            date = new Date(Long.parseLong(res.getString(4)));

            //Convert to calendar object
            Calendar photoTime = Calendar.getInstance();
            photoTime.setTime(date);

            // If same day of the week, add 10 points
            if (deviceCalendar.get(Calendar.DAY_OF_WEEK) == photoTime.get(Calendar.DAY_OF_WEEK)) {
                newPoint += 10;
            }

            int photoTimeOfDay = (photoTime.get(Calendar.HOUR_OF_DAY) * 60) + photoTime.get(Calendar.MINUTE);
            int currentTimeOfDay = (deviceCalendar.get(Calendar.HOUR_OF_DAY) * 60) + deviceCalendar.get(Calendar.MINUTE);
            int lowerTimeBound = (currentTimeOfDay - 120) % 1440, highTimeBound = (currentTimeOfDay + 120) % 1440;

            // If same day of the week, add 10 points
            if (photoTimeOfDay >= lowerTimeBound && photoTimeOfDay <= highTimeBound) {
                newPoint += 10;
            }

            // if it is karma, add 5 points
            isKarma = res.getInt(7) > 0 ? true : false;
            if (isKarma) {
                newPoint += 5;
            }

            // update deja points
            updateField(id, COL_DEJA_6, newPoint);

            newPoint = 0;
        }
        Log.d(TAGDATABASE, "Points updated");
    }


}



