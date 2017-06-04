package com.deja11.dejaphoto;

import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Chris on 5/14/2017.
 */

public class GeoLocationTest {
    GeoLocation geiselLibrary;

    @Before
    public void setup() {
        geiselLibrary = new GeoLocation(32.881151, -117.237450);
    }

    @Test
    public void testIsNearCurrentLocation() {
        // A location within 1000 ft of Geisel
        GeoLocation snakePath = new GeoLocation(32.881065, -117.236620);
        assertTrue("The Snake Path outside Geisel is within 1000 ft of Geisel", geiselLibrary.isNearCurrentLocation(snakePath));

        // A location definitely not within 1000 ft of Geisel
        GeoLocation spaceNeedle = new GeoLocation(47.620506, -122.349277);
        assertFalse("The Space Needle is not within 1000 ft of Geisel", geiselLibrary.isNearCurrentLocation(spaceNeedle));

        // A location near Geisel, but not within 1000 ft of it
        GeoLocation galbraithHall = new GeoLocation(32.873710, -117.240972);
        assertFalse("Galbraith is somewhat near Geisel, but not within 1000 ft", geiselLibrary.isNearCurrentLocation(galbraithHall));
    }

    @Test
    public void testGetLocationName() {
        assertEquals("Library Walk, Library Walk, San Diego, California, United States",
                        geiselLibrary.getLocationName(InstrumentationRegistry.getTargetContext()));

        GeoLocation spaceNeedle = new GeoLocation(47.620506, -122.349277);
        assertEquals("400, Broad Street, Seattle, Washington, United States",
                        spaceNeedle.getLocationName(InstrumentationRegistry.getTargetContext()));

    }

    @Test
    public void testGetLatitudeAndLongitude() {
        assertEquals(32.881151, geiselLibrary.getLatitude(), 0);
        assertEquals(-117.237450, geiselLibrary.getLongitude(), 0);

        // Geisel's reported lat-long should not be equal to the lat-long of the Space Needle
        assertNotEquals(47.620506, geiselLibrary.getLatitude(), 0);
        assertNotEquals(-122.349277, geiselLibrary.getLongitude(), 0);
    }

    @Test
    public void testCustomLocationName() {
        // Name is correctly updated in the GeoLocation object
        geiselLibrary.setCustomLocationName("Geisel Library, San Diego, California, United States");
        assertEquals("Geisel Library, San Diego, California, United States",
                        geiselLibrary.getLocationName(InstrumentationRegistry.getTargetContext()));

        // Name is correctly reset to normal auto-generated name after custom name is cleared
        geiselLibrary.clearCustomLocationName();
        assertEquals("Library Walk, Library Walk, San Diego, California, United States",
                geiselLibrary.getLocationName(InstrumentationRegistry.getTargetContext()));

        // Correctly recognizes empty string as a valid custom name
        geiselLibrary.setCustomLocationName("");
        assertEquals("", geiselLibrary.getLocationName(InstrumentationRegistry.getTargetContext()));
    }
}
