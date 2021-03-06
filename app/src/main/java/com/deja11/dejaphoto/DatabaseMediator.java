package com.deja11.dejaphoto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import static com.deja11.dejaphoto.DatabaseHelper.COL_KARMA_8;
import static com.deja11.dejaphoto.DatabaseHelper.COL_LOC_NAME_11;
import static com.deja11.dejaphoto.DatabaseHelper.COL_TOTAL_KARMA_12;
import static com.deja11.dejaphoto.DatabaseHelper.TAGDATABASE;


/**
 * Created by Tee on 6/4/17.
 */

public class DatabaseMediator {
    private DatabaseHelper databaseHelper;
    private FirebaseHelper firebaseHelper;
    private Context context;

    public DatabaseMediator(Context context){
        databaseHelper = new DatabaseHelper(context);
        firebaseHelper = new FirebaseHelper(context);
        this.initDatabase(context);
        this.context = context;
        //databaseHelper.initialize(context);
    }

    public void initDatabase(Context context){
        // Ensure that app has permission to access the storage
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String username = sharedPreferences.getString("username", "unknown");


            if(username.equals("unknown")){
                return;
            }

            // Delegate to gatherPhotoInfo to get raw information of all photos in the camera album
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
            String defaultLocation = "default";

            while (cursor.moveToNext()) {
                absolutePath = cursor.getString(columnIndexPath); //path to the photo
                dateAdded = cursor.getString(columnIndexDate); //date in string format
                latitude = cursor.getDouble(columnIndexLat); // latitude
                longitude = cursor.getDouble(columnIndexLong); // longtitude

                String test = "deja";
                String test2 = "friends";
                // Make sure it is in the camera album

                boolean isInDejaPhoto = Pattern.compile(".*/DejaPhoto/.*").matcher(absolutePath).matches();
                boolean isInDejaPhotoCopied = Pattern.compile(".*/DejaPhotoCopied/.*").matcher(absolutePath).matches();
                boolean isInDejaPhotoFriends = Pattern.compile(".*/DejaPhotoFriends/.*").matcher(absolutePath).matches();


                if (isInDejaPhoto || isInDejaPhotoCopied || isInDejaPhotoFriends) {
                    String photoName = Uri.fromFile(new File(absolutePath)).getLastPathSegment();
                    GeoLocation tempLoc = new GeoLocation(latitude,longitude);
                    defaultLocation = tempLoc.getLocationName(context);
                    databaseHelper.tryToInsertData(absolutePath, latitude, longitude, dateAdded, 0, 0, 0,photoName, username, defaultLocation, 0);

                    if(!isInDejaPhotoFriends) {

                        firebaseHelper.tryToInsertFirebase(absolutePath, latitude, longitude, dateAdded, 0, 0, 0, photoName, username, defaultLocation, "0");

                    }
                }
            }
        }
    }

    public ArrayList<String> getFriendsPhoto(String owner){
        ArrayList<String> data = new ArrayList<>();
        // Delegate to gattherPhotoInfo to gett raw information of all photos in the camera album
        Cursor cursor = gatherPhotoInfo(context);


        // Get columns of MediaStore object to get photos' information
        int columnIndexPath = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        String absolutePath = null;
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            absolutePath = cursor.getString(columnIndexPath); //path to the photo

            boolean isFriendsPhoto = Pattern.compile(".*/"+ owner +"/.*").matcher(absolutePath).matches();

            // Make sure it is in the camera album
            if (isFriendsPhoto) {
                //Log.d("PHOTO FOUND", "This should be deleted: " + absolutePath);
                data.add(absolutePath);
            }
        }
        return data;

    }


    public void updatePoint(GeoLocation deviceLocation, Calendar deviceCalendar) {
        databaseHelper.updatePoint(deviceLocation, deviceCalendar);
    }

    public Photo getNextPhoto() {
        return databaseHelper.getNextPhoto();
    }

    public void updateKarma(String photoLocation, int totalKarma, String owner) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username = sharedPreferences.getString("username", "unknown");


        databaseHelper.updateKarma(photoLocation, totalKarma);
        //TODO FIREBASE

        if(username.equals(owner)) {
            firebaseHelper.updateFirebase(username, photoLocation, COL_KARMA_8, "1");
        }
        int karma = totalKarma+1;


        firebaseHelper.updateFirebase(owner, photoLocation, COL_TOTAL_KARMA_12, String.valueOf(karma));

        Log.d("Karma", "database mediator username to be karma "+username+ " // " +photoLocation);
    }

    public void updateRelease(String photoLocation, String owner) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username = sharedPreferences.getString("username", "unknown");
        databaseHelper.updateRelease(photoLocation);
        //firebaseHelper.updateFirebase(currentUserName, photoLocation ,COL_REL_7,"1");

        Log.i("Release", "databaseHelper not the problem");

        if(owner.equals(username)) {
            //firebaseHelper.updateFirebase(currentUserName, photoLocation ,COL_REL_7,"1");
            firebaseHelper.updateRelease(username, photoLocation);
        }
    }




    public void downloadFriendPhotos(Context context, String username) {

        firebaseHelper.downloadFriendPhotos(context, username);
    }

    public void deleteFriendPhotos(String username){
        databaseHelper.deletePhotos(username);
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


    public void addFriendFirebase(String user, String friend){
        firebaseHelper.addFriend(user, friend);
    }


    public void createUser(String username){
        firebaseHelper.createUser(username);
    }
    /**
     *      FIREBASE SETTERS
     *
     * */

    public void setSharing(String user, boolean value){
        firebaseHelper.setSharing(user, value);
    }

    /**
     *      FIREBASE GETTERS
     *
     * */

    public int getTotalKarma(String ownerName, String photoName){
        int dot = photoName.indexOf('.');
        String name;
        if(dot != -1){
            name = photoName.substring(0,dot) + photoName.substring(dot+1);
            return firebaseHelper.getTotalKarma(ownerName, name);
        }
        return  firebaseHelper.getTotalKarma(ownerName, photoName);
    }

    public boolean getSharing(String username){
        return firebaseHelper.getSharing(username);
    }


    public ArrayList<Pair<String, String>> getFriendsSharing(String username){
        return firebaseHelper.getFriendsSharing(username);
    }
    public String getUsername(String username){
        return  firebaseHelper.getUsername(username);
    }
    public ArrayList<String> testGetPhotoNamesFromFirebase(){
        return firebaseHelper.getPhotos();
    }
    public void setLocationName(String photoPath, String locationName) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username = sharedPreferences.getString("username", "unknown");


        Log.i("location", "mediator updating location name");

        databaseHelper.updateField(photoPath, DatabaseHelper.COL_LOC_NAME_11, locationName);
        firebaseHelper.updateFirebase(username,photoPath,COL_LOC_NAME_11,locationName);

    }







    /**
     *      Info that needs to get pulled from firebase for each photo
     *          total karma
     *          release value
     *              if anyone releases a photo, reflect that change in sql
     *              if an owner releases a photo, reflect that change in sql and firebase
     *          location name
     *              if anyone renames a photo, reflect that change in sql
     *              if the owner of the photo renames it, reflect that change in sql and firebase
     *
     *
     * */
}
