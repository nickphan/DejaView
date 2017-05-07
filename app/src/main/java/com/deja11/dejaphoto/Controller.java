package com.deja11.dejaphoto;

import android.os.CountDownTimer;

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
     */
    void setWallpaper(Photo photo){

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
