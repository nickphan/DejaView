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
    private boolean mykarma;
    private int totalKarma;

    // newly added field to accomodate for the updated column
    private String dateString;
    private String fileName;
    private String owner;
    private String locationName;

    /*public Photo(String phoneLocation, GeoLocation geoLocation, Date date, int dejaPoints, boolean released, boolean karma) {
        this.phoneLocation = phoneLocation;
        this.geoLocation = geoLocation;
        this.date = date;
        this.released = released;
        this.mykarma = karma;
        this.totalKarma = 0;
    }

    public Photo(String phoneLocation, GeoLocation geoLocation, Date date, int dejaPoints, boolean released, boolean karma, int totalKarma){
        this.phoneLocation = phoneLocation;
        this.geoLocation = geoLocation;
        this.date = date;
        this.released = released;
        this.mykarma = karma;
        this.totalKarma = totalKarma;
    }*/

    public Photo(String phoneLocation, GeoLocation geoLocation, Date date, int dejaPoints, boolean released, boolean karma, int totalKarma, String dateString, String fileName, String owner, String locationName ){
        this.phoneLocation = phoneLocation;
        this.geoLocation = geoLocation;
        this.date = date;
        this.released = released;
        this.mykarma = karma;
        this.totalKarma = totalKarma;
        this.dateString = dateString;
        this.fileName = fileName;
        this.owner = owner;
        this.locationName = locationName;
    }



    /**
     * Check if the photo is karma-ed
     *
     * @return true if the photo is karma-ed
     */
    public boolean isKarma() {
        return mykarma;
    }

    /**
     * Set the photo karma-ed
     *
     * @param karma the condition whether the photo is about to be karma-ed
     */
    public void setKarma(boolean karma) {
        this.mykarma = karma;
    }


    public int getTotalKarma(){return totalKarma;}

    public void incrementKarma(){totalKarma++;}
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


    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setPhoneLocation(String phoneLocation) {
        this.phoneLocation = phoneLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMykarma(boolean mykarma) {
        this.mykarma = mykarma;
    }

    public void setTotalKarma(int totalKarma) {
        this.totalKarma = totalKarma;
    }
}
