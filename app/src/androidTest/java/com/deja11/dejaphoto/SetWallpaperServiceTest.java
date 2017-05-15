package com.deja11.dejaphoto;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Tee on 5/14/17.
 */

public class SetWallpaperServiceTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void TestCreateDatabase() {
        SetWallpaperService.updateInterval(20);
        assertEquals(SetWallpaperService.interval,20*60000);
    }

}
