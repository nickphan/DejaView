package com.deja11.dejaphoto;

import java.util.Calendar;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class Photo {

    String phoneLocation;
    GeoLocation geoLocation;
    Date date;
    int dejaPoints;
    boolean released;
    boolean karma;

    public Photo(String phoneLocation){
        this.phoneLocation = phoneLocation;
        this.dejaPoints = 0;
        this.karma = false;
        this.released = false;
    }

    public Photo(String phoneLocation, GeoLocation geoLocation, Date date, int dejaPoints, boolean released, boolean karma) {
        this.phoneLocation = phoneLocation;
        this.geoLocation = geoLocation;
        this.date = date;
        this.dejaPoints = dejaPoints;
        this.released = released;
        this.karma = karma;
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
     * Get the day of the week the photo was taken
     * @return Integer corresponding to day of week, starting from 1 for Sunday, 2 for Monday ... 7 for Saturday.
     */
    public int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Determine if the photo was taken on the same day of the week as the given day of the week
     * @param dayOfWeek The day of the week to compare to the photo's day of the week
     * @return Whether or not the photo's day of the week is the same
     */
    public boolean isSameDayOfWeek(int dayOfWeek) {
        return getDayOfWeek() == dayOfWeek;
    }

    /**
     * Get the time of day the photo was taken
     * @return The time of day the photo was taken, in the format of minutes after 12:00 midnight
     */
    public int getTimeOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Return time of day in the form of minutes after 12:00 midnight
        return (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
    }

    /**
     * Determine if the photo was taken around the same time of day as a given time of day
     * @param timeOfDay The time of day to compare the photo's time of day, in minutes after 12:00 midnight
     * @return Whether or not the photo's time of day is within 2 hours before or after the given time of day.
     */
    public boolean isSameTimeOfDay(int timeOfDay) {
        // Get photo's time of day and create bounds for "same time of day"
        int thisTimeOfDay = getTimeOfDay();
        int lowTimeBound = (thisTimeOfDay - 120) % 1440, highTimeBound = (thisTimeOfDay + 120) % 1440;

        if (timeOfDay >= lowTimeBound || timeOfDay <= highTimeBound) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Get the dejaPoints of the current photo
     * @return the dejaPoints of the current phone
     */
    public int getDejaPoints() {
        return dejaPoints;
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


    public boolean equals(Photo photo){
        if(this.phoneLocation == photo.phoneLocation){
            return true;
        }else{
            return false;
        }
    }

    /*private ArrayList<String> gatherPhotos(Context context) {
        Uri uri;
        Cursor cursor;
        int columnIndexData;
        int columnIndexFolder;

        ArrayList<String> imageList = new ArrayList<String>();
        String absolutePath = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        cursor = context.getContentResolver().query(uri, projection, null, null, null);

        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        columnIndexFolder = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while (cursor.moveToNext()) {
            absolutePath = cursor.getString(columnIndexData);
            imageList.add(absolutePath);
        }

        return imageList;
    }*/
}
