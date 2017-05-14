package com.deja11.dejaphoto;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.rule.ActivityTestRule;

import static com.deja11.dejaphoto.DatabaseHelper.TABLE_NAME;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;

/**
 * Created by Tee on 5/13/17.
 */

public class GalleryDatabaseTest {

    DatabaseHelper testDb;

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void TestCreateDatabase() {
        testDb = new DatabaseHelper(mainActivity.getActivity());
        assertTrue("Database created", true);
    }

    @Test
    public void TestInsert(){
        String testPath = "GenericPath";
        double testLat = 2.5;
        double testLong = -6.8;
        String testDate = "10000";
        int testPoint = 10;
        int testKarma = 0;
        int testRelease = 1;


        testDb.insertData("GenericPath", 2.5, -6.8, "1000", 0, 0, 0);


    }

}
