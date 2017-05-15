package com.deja11.dejaphoto;

import java.util.Date;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class Photo {

    private String phoneLocation;
    private GeoLocation geoLocation;
    private Date date;
    private boolean released;
    private boolean karma;


    public Photo(String phoneLocation, GeoLocation geoLocation, Date date, int dejaPoints, boolean released, boolean karma) {
        this.phoneLocation = phoneLocation;
        this.geoLocation = geoLocation;
        this.date = date;
        this.released = released;
        this.karma = karma;
    }

    /**
     * Check if the photo is karma-ed
     *
     * @return true if the photo is karma-ed
     */
    public boolean isKarma() {
        return karma;
    }

    /**
     * Set the photo karma-ed
     *
     * @param karma the condition whether the photo is about to be karma-ed
     */
    public void setKarma(boolean karma) {
        this.karma = karma;
    }

    /**
     * Check if the photo has been released
     *
     * @return true if the photo has been released
     */
    public boolean isReleased() {
        return released;
    }

    /**
     * Update the photo with either staying in the wallpaper cycle or not
     *
     * @param released true if the photo is about to be released; otherwise, false
     */
    public void setReleased(boolean released) {
        this.released = released;
    }

    /**
     * Get the location where the photo is stored in the phone
     *
     * @return the location of the photo in the phone
     */
    public String getPhotoLocation() {
        return phoneLocation;
    }

    /**
     * Get the location data where the photo is taken
     *
     * @return the geoLocation data
     */
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }


    /**
     * Used instead of operator overloading
     *
     * @return true if the photos share the same path, false otherwise
     */
    public boolean equals(Photo photo) {
        return this.phoneLocation.equals(photo.phoneLocation);
    }

}
