package com.deja11.dejaphoto;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class GeoLocation {

    // for isNearlocation()
    int CONSTANT_CONSTRAINT = 1000;

    private double longitude;
    private double latitude;
    private String locationName;

    public GeoLocation(double longitude, double latitude){
        this.latitude = latitude;
        this.longitude = longitude;

        // call the location API to get the location name
        // this.locationName = locationName;
    }

    /**
     * Check if the geolocation of the photo is within 1000 feet of where the user is currently at
     * @param longitude the current longitude of the user
     * @param latitude the current latitude of the user
     * @return true if the photo was taken nearby this current location
     */
    public boolean isNearCurrentLocation(double longitude, double latitude){
        return Math.sqrt((this.longitude - longitude) * (this.longitude - longitude) +
                         (this.latitude - latitude) * (this.latitude - latitude))
                         < CONSTANT_CONSTRAINT;
    }

    /**
     * Get the name of the location where the photo is taken
     *
     * @param context The context in which this method is being called
     * @return A string containing the location name where the photo is taken
     */
    public String getLocationName(Context context){
        Geocoder geocoder = new Geocoder(context);

        try {
            // extract the location name using the Address created from the latitude and longitude coordinates
            List<Address> addressName = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
            locationName = buildLocationName(addressName.listIterator().next());
        }
        catch (IOException e) {
            // set location name to null if there was an error
            locationName = null;
        }

        return locationName;
    }

    /**
     * Helper method to getLocationName() which builds the name of the location using information in the provided Address.
     *
     * @param address The Address object whose name should be extracted
     * @return A string with the name of the geographic feature, the street address, the locality, the administrative area,
     * and the country, in that order, if such information is included in the Address.
     */

    private String buildLocationName(Address address) {
        StringBuilder name = new StringBuilder();

        if (address.getFeatureName() != null) {
            name.append(address.getFeatureName());
        }

        if (address.getThoroughfare() != null) {
            // if something is already in the location name (i.e. a more specific part of the name), use a comma separator
            if (name.length() != 0) {
                name.append(", ");
            }

            name.append(address.getThoroughfare());
        }

        if (address.getLocality() != null) {
            // if something is already in the location name (i.e. a more specific part of the name), use a comma separator
            if (name.length() != 0) {
                name.append(", ");
            }

            name.append(address.getLocality());
        }

        if (address.getAdminArea() != null) {
            // if something is already in the location name (i.e. a more specific part of the name), use a comma separator
            if (name.length() != 0) {
                name.append(", ");
            }

            name.append(address.getAdminArea());
        }

        if (address.getCountryName() != null) {
            // if something is already in the location name (i.e. a more specific part of the name), use a comma separator
            if (name.length() != 0) {
                name.append(", ");
            }

            name.append(address.getCountryName());
        }

        return name.toString();
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
