package tests;

import android.support.test.rule.ActivityTestRule;

import com.deja11.dejaphoto.MainActivity;

import org.junit.Rule;
import org.junit.Test;

/**
 * Created by thefr on 5/12/2017.
 */

public class JUnitTest1 {

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void test1(){

    }
}
