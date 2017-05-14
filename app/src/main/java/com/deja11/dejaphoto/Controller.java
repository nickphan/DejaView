package com.deja11.dejaphoto;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;

import java.io.InputStream;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class Controller {

    // replace the string with the real location as the deault value
    String albumLocation = "cameraAlbum";

    // database that contains the photo locations on the phone
    // someDataBase photoDatabase;

    CountDownTimer countDown;

    // cache that stores the previous 10 photos
    // someDataStructure cache;




    public Controller(){

    }

    /**
     * Get the next photo in the cache
     * @return the next photo in the cache
     */
    public Photo getNextPhoto(){
        return null;
    }

    /**
     * Set the desired photo to be the wallpaper
     * @param photo the photo acquired from getNextPhoto()
     *        context the Activity context
     *        contentResolver ...
     * @return true if the wallpaper was set. false otherwise
     */
    boolean setWallpaper(Photo photo, Context context, ContentResolver contentResolver){
        Uri data = Uri.parse(photo.getPhotoLocation());
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        try {
            InputStream inputStream = contentResolver.openInputStream(data);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            myWallpaperManager.setBitmap(bitmap);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the user's current location
     * @param context The Context from which this method is being called
     * @return The user's current location as a Location object, null if location permission not granted
     */
    public Location getUserCurrentLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        ControllerLocationListener locationListener = new ControllerLocationListener();

        try {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, Looper.getMainLooper());
        }
        catch (SecurityException e) {
            return new Location(LocationManager.GPS_PROVIDER);
        }

        return locationListener.getLastLocation();
    }

    /**
     * Give the current photo priority of appearance
     */
    void karmaPhoto(){

    }

    /**
     * Remove the current photo shown on the homepage from the cycle
     */
    void releasePhoto(){

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

        public void onProviderEnabled (String provider) {
            locationProvider = provider;
        }

        public void onStatusChanged (String provider, int status, Bundle extras) {

        }
    }
}
