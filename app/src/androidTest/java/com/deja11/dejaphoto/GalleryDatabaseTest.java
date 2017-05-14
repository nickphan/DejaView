package com.deja11.dejaphoto;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.rule.ActivityTestRule;

import static android.R.attr.id;
import static com.deja11.dejaphoto.DatabaseHelper.COL_ID_1;
import static com.deja11.dejaphoto.DatabaseHelper.COL_PATH_2;
import static com.deja11.dejaphoto.DatabaseHelper.COL_LAT_3;
import static com.deja11.dejaphoto.DatabaseHelper.COL_LONG_4;
import static com.deja11.dejaphoto.DatabaseHelper.COL_DATE_5;
import static com.deja11.dejaphoto.DatabaseHelper.COL_DEJA_6;
import static com.deja11.dejaphoto.DatabaseHelper.COL_KARMA_8;
import static com.deja11.dejaphoto.DatabaseHelper.COL_REL_7;

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


        //Create database
        testDb = new DatabaseHelper(mainActivity.getActivity());
        assertTrue("Database created", true);


    }

    @Test
    public void TestInsertDatabase(){
        Cursor res;

        SQLiteDatabase db = testDb.getWritableDatabase();

        String testPath = "GenericPath";
        double testLat = 2.5;
        double testLong = -6.8;
        String testDate = "10000";
        int testPoint = 10;
        int testKarma = 0;
        int testRelease = 1;

        // Insert data
        db.delete(TABLE_NAME, null, null);
        assertEquals(testDb.insertData(testPath, testLat, testLong, testDate, testPoint, testRelease, testKarma), true);
        assertEquals(testDb.insertData("GenericPath2", 2.5454545, -6.822234, "99887766", 100, 1, 0), true);

        // Check if inserted properly, gives correct size, data is correct
        res = db.query(true, TABLE_NAME, null, null, null, null, null, null, null);
        assertEquals(res.getCount(), 2);
        res.moveToNext();
        assertEquals(res.getString(1), testPath);
        assertEquals(res.getDouble(2), testLat, 1e-15);
        assertEquals(res.getDouble(3), testLong, 1e-15);
        assertEquals(res.getString(4), testDate);
        assertEquals(res.getInt(5), testPoint);
        assertEquals(res.getInt(6), testRelease);
        assertEquals(res.getInt(7), testKarma);

        res.moveToNext();
        assertEquals(res.getString(1), "GenericPath2");
        assertEquals(res.getDouble(2), 2.5454545, 1e-15);
        assertEquals(res.getDouble(3), -6.822234, 1e-15);
        assertEquals(res.getString(4), "99887766");
        assertEquals(res.getInt(5), 100);
        assertEquals(res.getInt(6), 1);
        assertEquals(res.getInt(7), 0);


        db.delete(TABLE_NAME, null, null);
        testDb.initialize(mainActivity.getActivity());
        res = db.query(true, TABLE_NAME, null, null, null, null, null, null, null);
        assertEquals(res.getCount(), 6);

        testDb.initialize(mainActivity.getActivity());
    }


}
