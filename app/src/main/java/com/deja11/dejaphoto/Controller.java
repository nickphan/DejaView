package com.deja11.dejaphoto;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.CountDownTimer;

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

    }

    /**
     * Remove the current photo shown on the homepage from the cycle
     */
    void releasePhoto(){

    }
}
