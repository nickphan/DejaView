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

    // Testing value
    String testPath = "GenericPath";
    double testLat = 2.5;
    double testLong = -6.8;
    String testDate = "10000";
    int testPoint = 10;
    int testKarma = 0;
    int testRelease = 0;


    Cursor res;         // Result from the query

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

        //Create database
        testDb = new DatabaseHelper(mainActivity.getActivity());
        SQLiteDatabase db = testDb.getWritableDatabase();

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

        assertEquals(res.getCount(), 2);


        db.delete(TABLE_NAME, null, null);
        testDb.initialize(mainActivity.getActivity());


        testDb.initialize(mainActivity.getActivity());
    }

    @Test
    public void TestUpdateDatabase(){
        //Create database
        testDb = new DatabaseHelper(mainActivity.getActivity());

        SQLiteDatabase db = testDb.getWritableDatabase();

        // Insert data
        db.delete(TABLE_NAME, null, null);
        testDb.insertData(testPath, testLat, testLong, testDate, 25, testRelease, testKarma);

        // Select ID from photo_table where phonelocation = testpath
        res = db.query(true,TABLE_NAME,new String[]{COL_ID_1},COL_PATH_2+"='"+testPath+"'",null,null,null,null,null);


        res.moveToNext();
        assertEquals(testDb.updateField(res.getInt(0),COL_DEJA_6, testPoint ), true);

        // Check if updated properly, gives correct size, data is correct
        res = db.query(true, TABLE_NAME, null, null, null, null, null, null, null);

        assertEquals(res.getCount(),1);

        // Check if updated properly, gives correct size, data is correct
        res = db.query(true, TABLE_NAME, null, null, null, null, null, null, null);
        assertEquals(res.getCount(), 1);
        res.moveToNext();
        assertEquals(res.getString(1), testPath);
        assertEquals(res.getDouble(2), testLat, 1e-15);
        assertEquals(res.getDouble(3), testLong, 1e-15);
        assertEquals(res.getString(4), testDate);
        assertEquals(res.getInt(5), testPoint);
        assertEquals(res.getInt(6), testRelease);
        assertEquals(res.getInt(7), testKarma);

    }

    @Test
    public void TestUpdateKarma(){

        //Create database
        testDb = new DatabaseHelper(mainActivity.getActivity());

        SQLiteDatabase db = testDb.getWritableDatabase();

        // Insert data
        db.delete(TABLE_NAME, null, null);
        testDb.insertData(testPath, testLat, testLong, testDate, testPoint, testRelease, testKarma);

        testDb.updateKarma(testPath);


        // Check if updated properly, karma is now set to 1
        res = db.query(true, TABLE_NAME, null, null, null, null, null, null, null);
        assertEquals(res.getCount(), 1);
        res.moveToNext();
        assertEquals(res.getString(1), testPath);
        assertEquals(res.getDouble(2), testLat, 1e-15);
        assertEquals(res.getDouble(3), testLong, 1e-15);
        assertEquals(res.getString(4), testDate);
        assertEquals(res.getInt(5), testPoint);
        assertEquals(res.getInt(6), testRelease);
        assertEquals(res.getInt(7), 1);
    }

    @Test
    public void TestUpdateRelease(){

        //Create database
        testDb = new DatabaseHelper(mainActivity.getActivity());

        SQLiteDatabase db = testDb.getWritableDatabase();

        // Insert data
        db.delete(TABLE_NAME, null, null);
        testDb.insertData(testPath, testLat, testLong, testDate, testPoint, testRelease, testKarma);

        testDb.updateRelease(testPath);


        // Check if updated properly, karma is now set to 1
        res = db.query(true, TABLE_NAME, null, null, null, null, null, null, null);
        assertEquals(res.getCount(), 1);
        res.moveToNext();
        assertEquals(res.getString(1), testPath);
        assertEquals(res.getDouble(2), testLat, 1e-15);
        assertEquals(res.getDouble(3), testLong, 1e-15);
        assertEquals(res.getString(4), testDate);
        assertEquals(res.getInt(5), testPoint);
        assertEquals(res.getInt(6), 1);
        assertEquals(res.getInt(7), testKarma);
    }

    @Test
    public void TestGettingRandom(){

        //Create database
        testDb = new DatabaseHelper(mainActivity.getActivity());

        testDb.initialize(mainActivity.getActivity());
        SQLiteDatabase db = testDb.getWritableDatabase();

        String path1 = testDb.chooseNextPath();
        String path2 = testDb.chooseNextPath();
        String path3 = testDb.chooseNextPath();
        String path4 = testDb.chooseNextPath();
        assertFalse(path1 == path2 && path2 == path3 && path3 == path4);
    }

}
