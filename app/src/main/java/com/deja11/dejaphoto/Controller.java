package com.deja11.dejaphoto;

import android.os.CountDownTimer;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class Controller {

    // replace the string with the real location as the deault value
    String albumLocation = "cameraAlbum";

    // database that contains the photo locations on the phone

    CountDownTimer countDown;

    /**
     * Store the next 10 photos to display
     */
    public void cache(){

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
     */
    void setWallPaper(Photo photo){

    }

    /**
     * Give the desired photo priority of appearance
     * @param photo the photo to be boosted
     */
    void karmaPhoto(Photo photo){

    }

    /**
     * Remove the current photo shown on the homepage from the cycle
     * @param photo the photo shown on the screen
     */
    void releasePhoto(Photo photo){

    }
}
