package com.deja11.dejaphoto;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.InputStream;
import java.util.Stack;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class Controller implements Serializable{


    /**
     * TODO
     *  - replace arraylists with databasehelper
     *  - TEST THE SETWALLPAPER METHOD
     * */


    DatabaseHelper databaseHelper;
    Context context;
    Photo currPhoto;

    //For prev
    LinkedList<Photo> cache;

    /**
     * Constructor with context
     * */
    public Controller(Context context){
        this.context = context;
        databaseHelper = new DatabaseHelper(this.context);
        databaseHelper.initialize(this.context);
        cache = new LinkedList<Photo>();
    }

    /**
     * Get the next photo to display
     * @return the next photo
     * */
    public Photo getNextPhoto(){
        if(currPhoto == null){
            return databaseHelper.getNextPhoto();
        }else{
            int currIndex = cache.indexOf(currPhoto);
            if(currIndex == -1){
                return databaseHelper.getNextPhoto();
            }else if(currIndex == cache.size()-1){
                return databaseHelper.getNextPhoto();
            }else{
                return cache.get(currIndex+1);
            }
        }
    }

    /**
     * Get the previous photo displayed
     * @return the previous photo
     * */
    public Photo getPreviousPhoto() {
        if(currPhoto == null){
            /*SOME ERROR MESSAGE*/
            return null;
        }else{
            int currIndex = cache.indexOf(currPhoto);
            if(currIndex == -1){
                return cache.getLast();
            }else if(currIndex == 0){
                return null;
            }else{
                return cache.get(currIndex-1);
            }
        }
    }

    /**
     * Get the current photo that is displayed as the wallpaper
     * @return the current wallpaper as a photo
     */
    public Photo getCurrentWallpaper() {
        return currPhoto;
    }

    /**
     * Set current photo karma field to true
     */
    void karmaPhoto(){
        Photo photo = getCurrentWallpaper();
        if(!photo.isKarma()){
            photo.setKarma(true);
        }
    }

    /**
     * Remove the current photo shown on the homepage from the cycle
     */
    void releasePhoto(){
        currPhoto.setReleased(true);
        if(currPhoto != null){
            int currIndex = cache.indexOf(currPhoto);
            if(currIndex == -1){
                currPhoto = cache.getLast();
                Photo photo = getNextPhoto();
                setWallpaper(photo);
            }else if(currIndex == cache.size()-1){
                cache.remove(cache.size()-1);
                currPhoto = cache.getLast();
                Photo photo = getNextPhoto();
                setWallpaper(photo);
            }else{
                cache.remove(currIndex);
                currPhoto = null;
                setWallpaper(cache.get(currIndex));
            }
        }
    }

    /**
     * Set the desired photo to be the wallpaper
     * @param photo the photo acquired from getNextPhoto()
     *        context the Activity context
     *        contentResolver ...
     * @return true if the wallpaper was set. false otherwise
     */
    boolean setWallpaper(Photo photo){
        if(currPhoto == null){
            int nextPhoto = cache.indexOf(photo);
            if(nextPhoto == -1) {
                currPhoto = photo;
                cache.add(photo);
            }else{
                currPhoto = photo;
            }
            return setWallpaper(photo.phoneLocation, photo.geoLocation.getLocationName());
        }
        else{
            int currIndex = cache.indexOf(currPhoto);
            if(currIndex == -1){
                cache.add(currPhoto);
                currPhoto = photo;
                return setWallpaper(photo.phoneLocation, photo.geoLocation.getLocationName());
            }else{
                currPhoto = photo;
                return setWallpaper(photo.phoneLocation, photo.geoLocation.getLocationName());
            }
        }
    }

    //set the wallpaper without a location displayed
    boolean setWallpaper(String photoPath){
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        if(photoPath == null){
            try{
                myWallpaperManager.setResource(+R.drawable.default_image);
                return false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        //Uri data = Uri.parse(photoPath);
        try {
            FileInputStream photoStream = new FileInputStream(new File(photoPath));
            myWallpaperManager.setStream(photoStream);

            //InputStream inputStream = context.getContentResolver().openInputStream(data);
            //Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            //BitmapDrawable drawble = writeTextOnWallpaper(bitmap,geoLocation);
            //myWallpaperManager.setBitmap(bitmap);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //setting the wallpaper with the lcoation displayed
    boolean setWallpaper(String photoPath, String geoLocation){
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        if(photoPath == null){
            try{
                myWallpaperManager.setResource(+R.drawable.default_image);
                return false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        Uri data = Uri.parse(photoPath);
        try {

            InputStream inputStream = context.getContentResolver().openInputStream(data);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            //create a new bitmap with the same configuration of the original
            //Bitmap mutableBitmap= Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
            Bitmap mutableBitmap= Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());

            //writeTextOnWallpaper(mutableBitmap, geoLocation);
            writeTextOnWallpaper(mutableBitmap, bitmap, geoLocation);

            //myWallpaperManager.setBitmap(bitmap);
            myWallpaperManager.setBitmap(mutableBitmap);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
/*
    private void writeTextOnWallpaper(Bitmap mutableBitmap, String text){
        Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(10);
        canvas.drawText("hello", 0, mutableBitmap.getHeight()/2, paint);

    }
*/
    private void writeTextOnWallpaper(Bitmap mutableBitmap, Bitmap bitmap, String text){
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(bitmap,0,0,null);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(10);
        canvas.drawText("hello", 50, bitmap.getHeight()/2, paint);
    }
}
