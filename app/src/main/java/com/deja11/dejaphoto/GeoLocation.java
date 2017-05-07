package com.deja11.dejaphoto;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class GeoLocation {

    // for isNearlocation()
    int CONSTANT_CONSTRAINT = 1000;

    private double longitude;
    private double latitude;
    private String locationName;

    public GeoLocation(double longitude, double latitude, String locationName){
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
    }

    /**
     * Check if the geolocation of the photo is within 1000 feet of where the user is currently at
     * @param currentLocation the current location where the user is currently located
     * @return true if the photo was taken nearby this current location
     */
    public boolean isNearLocation(GeoLocation currentLocation){
        return true;
    }

    /**
     *  Get the name of the location where the photo is taken
     * @return the location name where the photo is taken
     */
    public String getLocationName(){
        return locationName;
    }

    /**
     * Get the latitude of the location where the photo is taken
     * @return the latitude of the geolocation
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Get the longitude of the location where the photo is taken
     * @return the longitude of the geolocation
     */
    public double getLongitude() {
        return longitude;
    }

}
