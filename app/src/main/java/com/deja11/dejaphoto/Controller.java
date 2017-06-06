package com.deja11.dejaphoto;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class Controller implements Parcelable {



    private DatabaseMediator databaseMediator;
    private Context context;
    private Photo currPhoto;
    private LinkedList<Photo> cache;
    private int mData = 0;

    private User user;
    private FirebaseDatabase database;
    private DatabaseReference myFirebaseRef;
    /**
     * Constructor with context to use for changing wallpaper
     */
    public Controller(Context context) {
        this.context = context;

        databaseMediator = new DatabaseMediator(context);
        //databaseMediator.init(this.context);

        cache = new LinkedList<Photo>();
        user = new User();

        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();

        initialize();

        //TODO
        //databaseMediator.downloadFriendPhotos(context);
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
            if (photo.isReleased()) {
                return getNextPhoto();
            } else {
                return photo;
            }
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
        Photo photo = getCurrentWallpaper();
        if (!photo.isKarma()) {
            photo.setKarma(true);
            photo.incrementKarma();
            databaseMediator.updateKarma(photo.getPhotoLocation());
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
            databaseMediator.updateRelease(currPhoto.getPhotoLocation());
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
                return setWallpaper(photo.getPhotoLocation(), photo.getGeoLocation().getLocationName(context));
            } else {
                currPhoto = photo;
                return setWallpaper(photo.getPhotoLocation(), photo.getGeoLocation().getLocationName(context));
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
    private boolean setWallpaper(String photoPath, String geoLocation) {
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
            Bitmap mutableBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
            writeBitmapOnMutable(mutableBitmap, bitmap);
            writeTextOnWallpaper(mutableBitmap, geoLocation, height, "");
            myWallpaperManager.setBitmap(mutableBitmap);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean setWallpaper(String photoPath, String geoLocation, String totalKarma){
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
            Bitmap mutableBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
            writeBitmapOnMutable(mutableBitmap, bitmap);
            writeTextOnWallpaper(mutableBitmap, geoLocation, height, totalKarma);
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
    private void writeBitmapOnMutable(Bitmap mutableBitmap, Bitmap bitmap) {
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    /**
     * Private helper method to place text on the wallpaper
     *
     * @param mutableBitmap the bitmap of the image to be the wallpaper
     * @param locationText the text to be displayed
     */
    private void writeTextOnWallpaper(Bitmap mutableBitmap, String locationText, int height, String karmaText) {
        String cutText = locationText;
        if(cutText.length() > 30){
            cutText = locationText.substring(0, 30);
            cutText = cutText.concat("...");
        }

        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(canvas.getHeight() / 50);
        canvas.drawText(cutText, (float) (0.35 * canvas.getWidth()), (float) (0.95 * canvas.getHeight()), paint);
        canvas.drawText(karmaText, (float)(0.95 * canvas.getWidth()), (float)(0.95 * canvas.getHeight()), paint);
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

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {
            locationProvider = provider;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }


    /**
     *      USER METHODS
     *
     *
     * */
    public ArrayList<String> checkForRequests(){
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

}
