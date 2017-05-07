package com.deja11.dejaphoto;

import java.util.Date;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class Photo {

    private String phoneLocation;
    private GeoLocation geoLocation;
    private Date date;
    private int dejaPoint;
    private boolean released;
    private boolean karma;

    public Photo(String phoneLocation, GeoLocation geoLocation, Date date){
        this.phoneLocation = phoneLocation;
        this.geoLocation = geoLocation;
        this.date = date;
        this.dejaPoint = 0;
        this.karma = false;
        this.released = false;
    }

    /**
     * Check if the photo is karma-ed
     * @return true if the photo is karma-ed
     */
    public boolean isKarma() {
        return karma;
    }

    /**
     * Set the photo karma-ed
     * @param karma the condition whether the photo is about to be karma-ed
     */
    public void setKarma(boolean karma) {
        this.karma = karma;
    }

    /**
     * Check if the photo has been released
     * @return true if the photo has been released
     */
    public boolean isReleased() {
        return released;
    }

    /**
     * Update the photo with either staying in the wallpaper cycle or not
     * @param released true if the photo is about to be released; otherwise, false
     */
    public void setReleased(boolean released) {
        this.released = released;
    }

    /**
     * Get the date when the photo was taken
     * @return the photo taken time
     */
    public Date getDate() {
        return date;
    }

    /**
     * Get the dejaPoints of the current photo
     * @return the dejaPoints of the current phone
     */
    public int getDejaPoint() {
        return dejaPoint;
    }

    /**
     * Get the location where the photo is stored in the phone
     * @return the location of the photo in the phone
     */
    public String getPhotoLocation() {
        return phoneLocation;
    }

    /**
     * Get the location data where the photo is taken
     * @return the geoLocation data
     */
    public GeoLocation getGeoLocation(){
        return geoLocation;
    }

    /**
     * Update the dejaPoint of the photo
     */
    public void updateDejaPoint(){

    }

}
