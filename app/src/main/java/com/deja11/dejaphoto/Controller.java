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
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;

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
    }

    /**
     * Get the next photo to display
     * @return the next photo
     * */
    public Photo getNextPhoto(){
        int currIndex = cache.indexOf(currPhoto);
<<<<<<< HEAD
        Photo photo;
        if(currIndex == -1){
            photo = databaseHelper.chooseNextPhoto();
        }else{
            photo = cache.get(currIndex+1);
        }
        if(photo.isReleased()){
            return getNextPhoto();
=======
        if(currIndex == cache.size()-1){
            return databaseHelper.getNextPhoto();
>>>>>>> 82e0a9b71c18491972f83a0cf132f454c5033f0b
        }else{
            return photo;
        }
    }

    /**
     * Get the previous photo displayed
     * @return the previous photo
     * */
    public Photo getPreviousPhoto() {
        int currIndex = cache.indexOf(currPhoto);
        if(currIndex == 0){
            /*SOME ERROR MESSAGE*/
            return (Photo)null;
        }if(currIndex == -1){
            return cache.get(cache.size()-1);
        }else{
            return cache.get(currIndex-1);
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
        Photo photo = getCurrentWallpaper();
        int currIndex = cache.indexOf(photo);
        if(currIndex == -1){
            Photo nextPhoto = getNextPhoto();
            currPhoto = null;
            setWallpaper(nextPhoto);
        }else{
            cache.remove(currIndex);
            currPhoto = null;
            setWallpaper(cache.get(currIndex));
        }
        photo.setReleased(true);
    }

    /**
     * Set the desired photo to be the wallpaper
     * @param photo the photo acquired from getNextPhoto()
     *        context the Activity context
     *        contentResolver ...
     * @return true if the wallpaper was set. false otherwise
     */
    boolean setWallpaper(Photo photo){
        int nextIndex = cache.indexOf(photo);
        int currIndex = cache.indexOf(currPhoto);

        if(currPhoto == null){
            currPhoto = photo;
        }if(nextIndex == -1 || currIndex == -1){
            cache.add(currPhoto);
            currPhoto = photo;
        }else{
            currPhoto = cache.get(nextIndex);
        }
        return setWallpaper(photo.phoneLocation,photo.geoLocation.getLocationName());
    }
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
            FileInputStream photoStream = new FileInputStream(new File(photoPath));
            myWallpaperManager.setStream(photoStream);

            InputStream inputStream = context.getContentResolver().openInputStream(data);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            BitmapDrawable drawble = writeTextOnWallpaper(bitmap,geoLocation);

            //myWallpaperManager.setBitmap(bitmap);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private BitmapDrawable writeTextOnWallpaper(Bitmap bitmap, String text){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(15);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, 0, 0, paint);
        return new BitmapDrawable(context.getResources(),bitmap);
    }
}
