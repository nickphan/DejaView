package com.deja11.dejaphoto;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.rule.ActivityTestRule;
import android.widget.SeekBar;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Created by shuai9532 on 5/14/17.
 */

public class seekBarTest {

    @Rule
    public ActivityTestRule<SettingPreference> setting =
            new ActivityTestRule<SettingPreference>(SettingPreference.class);
    @Rule
    public ActivityTestRule<MainActivity>main=
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void test1() {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(setting.getActivity());
        MainActivity.getInstance().settingsClicked(main.getActivity().findViewById(R.id.setting));
        int interval = 0;

        SeekBar seekBar = (SeekBar)setting.getActivity().findViewById(R.id.seekBar);
        /* First enter*/
        SettingPreference.getInstance().onProgressChanged(seekBar,0,true);
        interval = mSharedPrefs.getInt("interval",0);
        assertTrue(0==interval);

        /*Change value*/
        SettingPreference.getInstance().onProgressChanged(seekBar,25,true);
        interval = mSharedPrefs.getInt("interval",0);
        assertTrue(0!=interval);
        assertTrue(25==interval);

        /*Change value*/
        SettingPreference.getInstance().onProgressChanged(seekBar,20,true);
        interval = mSharedPrefs.getInt("interval",0);
        assertTrue(0!=interval);
        assertTrue(20==interval);

        /*Change value*/
        SettingPreference.getInstance().onProgressChanged(seekBar,25,true);
        interval = mSharedPrefs.getInt("interval",0);
        assertTrue(interval==25);

        /*change value*/
        SettingPreference.getInstance().onProgressChanged(seekBar,23,true);
        interval = mSharedPrefs.getInt("interval",0);
        assertTrue(23==interval);
    }
}
