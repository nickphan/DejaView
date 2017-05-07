package com.deja11.dejaphoto;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.CountDownTimer;

import java.util.LinkedList;
import java.io.InputStream;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class Controller {

    // replace the string with the real location as the default value
    String albumLocation = "cameraAlbum";

    // database that contains the photo locations on the phone
    // someDataBase photoDatabase;

    CountDownTimer countDown;

    // cache that stores the previous 10 photos
    LinkedList<Photo> cache = new LinkedList<Photo>();


    public Controller(){

    }

    /**
     * Get the next photo to display
     * @return the next photo
     */
    public Photo getNextPhoto(){

<<<<<<< HEAD
=======
        return null;
    }

    /**
     * Get the previous photo displayed
     * @return the previous photo
     */
    public Photo getPreviousPhoto() {
        return cache.removeLast();
    }

    /**
     * Get the current photo that is displayed as the wallpaper
     * @return the current wallpaper as a photo
     */
    public Photo getCurrentWallpaper() {
>>>>>>> 8346ea1ee6af00300a3829d9c5eaf75e33b0861a
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
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        if(photo == null){
            try{
                myWallpaperManager.setResource(+R.drawable.default_image);
                return false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        Uri data = Uri.parse(photo.getPhotoLocation());

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
     * Give the current photo priority of appearance
     */
    void karmaPhoto(){
        // get current wallpaper
        // delegate to photo's setKarma()
    }

    /**
     * Remove the current photo shown on the homepage from the cycle
     */
    void releasePhoto(){
        // get current wallpaper
        // delegate to photo's setReleased()
    }
}
