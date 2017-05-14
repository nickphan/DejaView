package tests;

import android.support.test.rule.ActivityTestRule;

import com.deja11.dejaphoto.GeoLocation;
import com.deja11.dejaphoto.MainActivity;
import com.deja11.dejaphoto.Photo;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by thefr on 5/12/2017.
 */

public class JUnitTest1 {

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);
    public ArrayList<Photo> photoList = new ArrayList<Photo>();

    @Test
    public void test1(){


    }
}
