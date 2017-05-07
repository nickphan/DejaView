package com.deja11.dejaphoto;

import android.os.CountDownTimer;

import java.util.LinkedList;

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

    /**
     * Get the next photo to display
     * @return the next photo
     */
    public Photo getNextPhoto(){

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
        return null;
    }

    /**
     * Set the desired photo to be the wallpaper
     * @param photo the photo acquired from getNextPhoto()
     */
    void setWallpaper(Photo photo){
        // get current wallpaper
        // if cache.size() == 10, cache.removeFirst()
        // add wallpaper to cache using cache.addLast(photo)
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
