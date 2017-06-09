package com.deja11.dejaphoto;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import android.util.Pair;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import static com.deja11.dejaphoto.R.id.username;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class Controller implements Parcelable {

    public static final int INTERVAL_OFFSET = 5; // offset for the interval
    public static final String INTERVAL_KEY = "progress"; // the key for the interval in the shared preferences
    public static final int INTERVAL_DEFAULT = 0; // default value for the interval in the shared preferences
    public static final long MIN_TO_MS = 60000; // a conversion factor from minutes to milliseconds

    public static final int SYNC_INTERVAL = 10000;

    // request codes for each pending intent
    public static final int LEFT_PENDING_INTENT_RC = 0;
    public static final int RIGHT_PENDING_INTENT_RC = 1;
    public static final int KARMA_PENDING_INTENT_RC = 2;
    public static final int RELEASE_PENDING_INTENT_RC = 3;
    public static final int ALARM_PENDING_INTENT_RC = 4;
    public static final int SYNC_PENDING_INTENT_RC = 5;
    public static final int NOTIFICATION_ID = 123;
    public static final int PHOTO_PICKER_SINGLE_CODE = 5;
    public static final int PHOTO_PICKER_MULTIPLE_CODE = 6;

    // codes for identifying which action the service has to execute
    public static final String CODE_KEY = "Order";
    public static final int CODE_NEXT_PHOTO = 1;
    public static final int CODE_PREVIOUS_PHOTO = 2;
    public static final int CODE_KARMA = 3;
    public static final int CODE_RELEASE = 4;
    public static final int CODE_SYNC = 5;

    // string paths of the dejaFolders
    public static final String DEJAPHOTOPATH = Environment.getExternalStorageDirectory() + "/DejaPhoto";
    public static final String DEJAPHOTOCOPIEDPATH = Environment.getExternalStorageDirectory() + "/DejaPhotoCopied";
    public static final String DEJAPHOTOFRIENDSPATH = Environment.getExternalStorageDirectory() + "/DejaPhotoFriends";


    private DatabaseHelper databaseHelper;



    public DatabaseMediator databaseMediator;

    private Context context;
    private Photo currPhoto;
    private LinkedList<Photo> cache;
    private int mData = 0;
    private int screenw;
    private int screenh;

    private User user;
    private FirebaseDatabase database;
    private DatabaseReference myFirebaseRef;
    /**
     * Constructor with context to use for changing wallpaper
     */
    public Controller(Context context) {
        this.context = context;

        databaseMediator = new DatabaseMediator(context);
        cache = new LinkedList<Photo>();
        user = new User();

        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();

        initialize();

        int width= context.getResources().getDisplayMetrics().widthPixels;
        screenw = width;
        int height= context.getResources().getDisplayMetrics().heightPixels;
        screenh = height;

        //databaseMediator.createUser(user.getUsername());
    }

    /**
     * Get the next photo to display
     *
     * @return the next photo either from cache or from DatabaseHelper
     */
    public Photo getNextPhoto() {
        databaseMediator.updatePoint(getUserCurrentLocation(), getUserCalendar());
        Photo photo = databaseMediator.getNextPhoto();
        if (currPhoto == null) {
            return photo;

        } else {
            int currIndex = cache.indexOf(currPhoto);
            if (currIndex == -1) {
                if (photo.isReleased()) {
                    return getNextPhoto();
                } else {
                    return photo;
                }
            } else if (currIndex == cache.size() - 1) {
                if (photo.isReleased()) {
                    return getNextPhoto();
                } else {
                    return photo;
                }
            } else {
                return cache.get(currIndex + 1);
            }
        }
    }

    /**
     * Get the previous photo displayed
     *
     * @return the previous photo from the cache or null if necessary
     */
    public Photo getPreviousPhoto() {
        if (currPhoto == null) {
            Log.i("getPreviousPhoto", "currPhoto is null");
            return null;
        } else {
            int currIndex = cache.indexOf(currPhoto);
            if (currIndex == -1) {
                return cache.getLast();
            } else if (currIndex == 0) {
                Log.i("getPreviousPhoto", "beginning of cache");
                return null;
            } else {
                return cache.get(currIndex - 1);
            }
        }
    }

    /**
     * Get the current photo that is displayed as the wallpaper
     *
     * @return the current wallpaper as a photo
     */
    public Photo getCurrentWallpaper() {
        return currPhoto;
    }

    /**
     * Set current photo karma field to true
     */
    public boolean karmaPhoto() {
        Log.d("Karma", "Karma button clicked");
        Photo photo = getCurrentWallpaper();
        if (!photo.isKarma()) {
            photo.setKarma(true);
            photo.incrementKarma();
            databaseMediator.updateKarma(photo.getPhotoLocation(), photo.getTotalKarma());
            Log.d("Karma", "controller username to be karma "+username+ " // " +photo.getPhotoLocation());
            return true;
        } else {
            Log.i("karmaPhoto", "photo karma is true");
            return false;
        }
    }

    /**
     * Remove the current photo shown on the homepage from the cycle
     */
    void releasePhoto() {
        if (currPhoto != null) {
            currPhoto.setReleased(true);
            databaseMediator.updateRelease(currPhoto.getPhotoLocation(), currPhoto.getOwner());
            int currIndex = cache.indexOf(currPhoto);
            if (currIndex == -1) {
                currPhoto = cache.getLast();
                Photo photo = getNextPhoto();
                setWallpaper(photo);
            } else if (currIndex == cache.size() - 1) {
                cache.remove(cache.size() - 1);
                currPhoto = cache.getLast();
                Photo photo = getNextPhoto();
                setWallpaper(photo);
            } else {
                cache.remove(currIndex);
                currPhoto = null;
                setWallpaper(cache.get(currIndex));
            }
        }
        Log.i("releasePhoto", "currPhoto is null already");
    }

    /**
     * Set the desired photo to be the wallpaper
     *
     * @param photo the photo acquired from getNextPhoto()
     * @return true if the wallpaper was set. false otherwise
     */
    public boolean setWallpaper(Photo photo) {
        if (photo == null) {
            return false;
        }
        if (currPhoto == null) {
            int nextPhoto = cache.indexOf(photo);
            if (nextPhoto == -1) {
                currPhoto = photo;
                cache.add(photo);
                if (cache.size() > 10) {
                    cache.remove(0);
                }
            } else {
                currPhoto = photo;
            }
            return setWallpaper(photo.getPhotoLocation(), photo.getGeoLocation().getLocationName(context), String.valueOf(photo.getTotalKarma()));
        } else {
            int currIndex = cache.indexOf(currPhoto);
            if (currIndex == -1) {
                cache.add(currPhoto);
                if (cache.size() > 10) {
                    cache.remove(0);
                }
                currPhoto = photo;
                return setWallpaper(photo.getPhotoLocation(), photo.getGeoLocation().getLocationName(context), photo.getTotalKarma()+"");
            } else {
                currPhoto = photo;
                return setWallpaper(photo.getPhotoLocation(), photo.getGeoLocation().getLocationName(context), photo.getTotalKarma()+"");
            }
        }
    }

    /*PRIVATE HELPER METHODS*/

    /**
     * Set the desired photo without location to be the wallpaper (for testing purpose)
     *
     * @param photoPath the path of the photo as a string
     * @return true if the wallpaper was set. false otherwise
     */
    private boolean setWallpaper(String photoPath) {
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        if (photoPath == null) {
            try {
                myWallpaperManager.setResource(+R.drawable.default_image);
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            FileInputStream photoStream = new FileInputStream(new File(photoPath));
            myWallpaperManager.setStream(photoStream);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Set the desired photo to be the wallpaper
     *
     * @param photoPath   the path of the photo as a string
     * @param geoLocation the location of the photo to display
     * @return true if the wallpaper was set. false otherwise
     */
    private boolean setWallpaper(String photoPath, String geoLocation, String totalKarma) {
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        if (photoPath == null) {
            try {
                myWallpaperManager.setResource(+R.drawable.default_image);
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(new File(photoPath).getAbsolutePath());
            int height = getHeightFromString(photoPath);
            int width = getWidthFromString(photoPath);

            //create the bitmap that has the same size as the screen
            Log.i("SCREENW", String.valueOf(screenw));
            Log.i("SCREENH", String.valueOf(screenh));

            //
            Bitmap mutableBitmap = Bitmap.createBitmap(width, screenh, bitmap.getConfig());

            // inside the method, we need to adjust the photo size
            writeBitmapOnMutable(mutableBitmap, bitmap, width, height );

            writeTextOnWallpaper(mutableBitmap, geoLocation,totalKarma);

            myWallpaperManager.setBitmap(mutableBitmap);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Private helper method to create a canvas to write on
     *
     * @param mutableBitmap the container where the drawing is done
     * @param bitmap the photo
     */
    private void writeBitmapOnMutable(Bitmap mutableBitmap, Bitmap bitmap, int photoWidth, int photoHeight) {

        Canvas canvas = new Canvas(mutableBitmap);
        Matrix m = new Matrix();

        int x_difference = photoWidth - mutableBitmap.getWidth() ;
        int y_difference = photoHeight - mutableBitmap.getHeight() ;
        int width_after_resize;
        int height_after_resize;
       /* if(x_difference <= 0 && y_difference <= 0){
            m.setScale(1, 1);
        }
        else{
            if(x_difference > 0 && y_difference > 0){*/
                float ratio;
                if(x_difference > y_difference){
                    ratio = (float) mutableBitmap.getWidth() / photoWidth;
                }
                else{
                    ratio = (float) mutableBitmap.getHeight() / photoHeight;
                }
                Log.d("current Ratio:", ratio+"");
                width_after_resize = (int)ratio * photoWidth;
                height_after_resize = (int)ratio* photoHeight;
                bitmap = Bitmap.createScaledBitmap(bitmap, width_after_resize,
                        height_after_resize, true);
            //}
        //}
        int cx = (canvas.getWidth() - bitmap.getWidth()) >> 1;
        int cy = (canvas.getHeight() - bitmap.getHeight()) >> 1;
        canvas.drawBitmap(bitmap, cx,cy, new Paint());
    }

    /**
     * Private helper method to place text on the wallpaper
     *
     * @param mutableBitmap the bitmap of the image to be the wallpaper
     * @param locationText the text to be displayed
     */
    private void writeTextOnWallpaper(Bitmap mutableBitmap, String locationText, String karma) {

        String cutText = locationText;
        if(cutText.length() > 30){
            cutText = locationText.substring(0, 30);
            cutText = cutText.concat("...");
        }

        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(canvas.getHeight() / 40);
        canvas.drawText(cutText, canvas.getHeight() / 40, (int)(0.95 * screenh), paint);
        canvas.drawText(karma, canvas.getWidth()-3*canvas.getHeight()/40, (int)(0.95 * screenh),paint);
    }

    /**
     * Private helper method to get the width of the photo
     *
      * @param photoPath the string of the location of the photo
     * @return the width of the photo
     */
    private int getWidthFromString(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);
        return options.outWidth;
    }

    /**
     * Private helper method to get the height of the photo
     *
     * @param photoPath the string of the location of the photo
     * @return the height of the photo
     */
    private int getHeightFromString(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);
        return options.outHeight;
    }

    /**
     * Gets the user's current location
     *
     * @return The user's current location as a Location object, null if location permission not granted
     */
    public GeoLocation getUserCurrentLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        ControllerLocationListener locationListener = new ControllerLocationListener();
        try {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, Looper.getMainLooper());
        } catch (SecurityException e) {
            return new GeoLocation(new Location(LocationManager.GPS_PROVIDER));
        }

        return new GeoLocation(locationListener.getLastLocation());
    }

    public Calendar getUserCalendar() {
        Calendar currTime = Calendar.getInstance();
        return currTime;
    }

    /**
     * Private helper method to run when object created
     */
    private void initialize() {
        Photo photo = getNextPhoto();
        setWallpaper(photo);
    }

    /*For JUnit testing purposes*/
    public LinkedList getCache() {
        return cache;
    }

    /*NECESSARY METHODS TO IMPLEMENT PARCELABLE*/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(i);
    }

    public static final Parcelable.Creator<Controller> CREATOR
            = new Parcelable.Creator<Controller>() {
        public Controller createFromParcel(Parcel in) {
            return new Controller(in);
        }

        public Controller[] newArray(int size) {
            return new Controller[size];
        }
    };

    private Controller(Parcel in) {
        mData = in.readInt();
    }

    class ControllerLocationListener implements LocationListener {
        private Location lastLocation;

        private String locationProvider;

        public Location getLastLocation() {
            return lastLocation;
        }

        public void onLocationChanged(Location location) {
            lastLocation = location;
        }

        public void onProviderDisabled(String provider) {   }

        public void onProviderEnabled(String provider) {
            locationProvider = provider;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void copyPhotos(ArrayList<Uri> uriArrayList) {
        File storagePath = new File(Environment.getExternalStorageDirectory(), "/DejaPhotoCopied");
        Log.i("Folder Path", storagePath.getAbsolutePath());
        if (!storagePath.exists()) {
            boolean result = storagePath.mkdirs();
            Log.i("Directory made", result + " ");
        }

        for (Uri u : uriArrayList) {
            try {

                Uri uri;
                String selection;
                String[] id;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    selection = "_id=?";
                    id = new String[]{DocumentsContract.getDocumentId(u).split(":")[1]};
                }

                else {
                    uri = u;
                    selection = null;
                    id = null;
                }

                File source = new File(AlbumUtils.getPath(context, uri, selection, id));
                File destination = new File(storagePath, source.getName());
                AlbumUtils.copyPhoto(source, destination);
                Log.i("Photo Copy", "Successful");
            } catch (Exception e) {
                Toast.makeText(context,
                        "Error copying a photo", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /**
     *      USER METHODS
     *
     *
     * */
    public void updateUser(){
        user.setSharing(databaseMediator.getSharing(user.getUsername()));
        ArrayList<Pair<String, String>> friendsList = databaseMediator.getFriends(user.getUsername());
        for(int i = 0; i < friendsList.size(); i++){
            Pair<String, String> friend = friendsList.get(i);
            String name = friend.first;
            String val = friend.second;
            user.setFriend(name, val);
        }
    }

    public void updateLocationName(String photoPath, String locationName) {
        databaseMediator.setLocationName(photoPath, locationName);
    }

    public void createUser(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username = sharedPreferences.getString("username", "unknown");
        user.setUsername(username);
        databaseMediator.createUser(username);
    }

    public void addFriend(String friendName){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username = sharedPreferences.getString("username", "unknown");
        Log.i("AddFriendUser", username);
        Log.i("AddFriendFriend", friendName);
        databaseMediator.addFriendFirebase(username, friendName);
    }

    public void sync(){

        // adds all the photos in the folder into the gallery (so the database can scan it)
        for (String folderPath : new String[] {DEJAPHOTOPATH, DEJAPHOTOCOPIEDPATH, DEJAPHOTOFRIENDSPATH}) {
            File[] files = new File(folderPath).listFiles();
            String[] filePath = new String[files.length];

            for (int i = 0; i < files.length; i++) {
                filePath[i] = files[i].getAbsolutePath();
            }

            MediaScannerConnection.scanFile(context, filePath, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("Scan Completed", path);
                }
            });

        }
        databaseMediator.initDatabase(context);

        if(SettingPreference.viewFriendPhoto){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String username = sharedPreferences.getString("username", "unknown");
            ArrayList<Pair<String,String>> myFriends = databaseMediator.getFriends(username);

            for (Pair<String,String> currFriend : myFriends){

                Log.d("SHOWING FRIEND ", currFriend.first);
                if(currFriend.second.equals("true")){
                    databaseMediator.downloadFriendPhotos(context, currFriend.first);
                }
                else{
                    //delete
                }
            }

        }
    }
    //sync should also look for karma, release, and name






    /*delete this?*/
    /*public ArrayList<String> checkForRequests(){
        ArrayList<String> localFriends = user.getFriends();
        final ArrayList<String> firebaseFriends = new ArrayList<>();
        ArrayList<String> friended = new ArrayList<>();

        DatabaseReference databaseReference = myFirebaseRef.child(user.getUsername());
        Query query = databaseReference;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot friendSnapShot: dataSnapshot.child("friends").getChildren()){
                    String key = friendSnapShot.getKey();
                    String val = friendSnapShot.getValue().toString();
                    firebaseFriends.add(key);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        while(firebaseFriends.size() < localFriends.size()){
            try{
                Thread.sleep(500);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        for(int i = 0; i < firebaseFriends.size(); i++){
            String friend = firebaseFriends.get(i);
            if(!localFriends.contains(friend)){
                friended.add(friend);
            }
        }
        return friended;

    }
    public void updateUser(){
        user.setSharing(databaseMediator.getSharing(user.getUsername()));
        ArrayList<Pair<String, String>> friendsList = databaseMediator.getFriends(user.getUsername());
        for(int i = 0; i < friendsList.size(); i++){
            Pair<String, String> friend = friendsList.get(i);
            String name = friend.first;
            String val = friend.second;
            user.setFriend(name, val);
        }
    }

    public void updateLocationName(Uri photoUri, String locationName) {
        String directoryPath = photoUri.getPath();
        databaseMediator.setLocationName(locationName, directoryPath);
    }

    
    //sync should also look for karma, release, and name
    }*/

}
