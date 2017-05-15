package test_seekbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.deja11.dejaphoto.MainActivity;
import com.deja11.dejaphoto.R;
import com.deja11.dejaphoto.SettingPreference;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
        int progress = 0;
        SeekBar seekBar = (SeekBar)setting.getActivity().findViewById(R.id.seekBar);
        /* First enter*/
        SettingPreference.getInstance().onProgressChanged(seekBar,0,true);
        try{progress = mSharedPrefs.getInt("progress",0);} catch(Exception e){}
        assertTrue(0==progress);

        /*Change value*/
        SettingPreference.getInstance().onProgressChanged(seekBar,25,true);
        try{progress = mSharedPrefs.getInt("progress",0);} catch(Exception e){}
        assertTrue(0!=progress);
        assertTrue(25==progress);

        /*Change value*/
        SettingPreference.getInstance().onProgressChanged(seekBar,20,true);
        try{progress = mSharedPrefs.getInt("progress",0);} catch(Exception e){}
        assertTrue(0!=progress);
        assertTrue(20==progress);

        SettingPreference.getInstance().onProgressChanged(seekBar,25,true);
        try{progress = mSharedPrefs.getInt("progress",0);} catch(Exception e){}
        assertTrue(progress==25);

        SettingPreference.getInstance().onProgressChanged(seekBar,23,true);
        try{progress = mSharedPrefs.getInt("progress",0);} catch(Exception e){}
        assertTrue(23==progress);
    }
/*
    @Test
    public void test2(){
        MainActivity.getInstance().settingsClicked(main.getActivity().findViewById(R.id.setting));
        int progress = 0;

        //SeekBar seekBar = (SeekBar)setting.getActivity().findViewById(R.id.seekBar);

        SettingPreference.getInstance().onProgressChanged(seekBar,25,true);
        try{progress = mSharedPrefs.getInt("progress",0);} catch(Exception e){}
        assertTrue(progress==25);

        SettingPreference.getInstance().onProgressChanged(seekBar,23,true);
        try{progress = mSharedPrefs.getInt("progress",0);} catch(Exception e){}
        assertTrue(23==progress);
    }
    */
}
