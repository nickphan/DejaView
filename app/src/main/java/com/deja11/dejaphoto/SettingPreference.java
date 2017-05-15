package com.deja11.dejaphoto;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingPreference extends Activity {

    final int TIME_OFFSET = 5;
    final int MAX_TIME = 25;

    //For testing purpose
    private static SettingPreference instance;

    public static SettingPreference getInstance() {
        if (instance == null) {
            setInstance(instance);
        }
        return instance;
    }

    public static void setInstance(SettingPreference instance) {
        SettingPreference.instance = instance;
    }

    // For testing
    private TextView intervalText;

    private int currentInterval = TIME_OFFSET;
    private static int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_preference);

        // For Junit Test
        setInstance(this);

        // Initialize the seekbar
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(MAX_TIME);
        intervalText = (TextView) findViewById(R.id.seekbarvalue);
        currentInterval = getCurrentProgress();
        seekBar.setProgress(currentInterval);
        currentPosition = getCurrentLocation();
        setText(seekBar, intervalText, currentInterval, currentPosition);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int interval, boolean fromUser) {

                currentInterval = interval;
                int val = (interval * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                currentPosition = val;
                setText(seekBar, intervalText, currentInterval, val);
                saveProgressAndLocation();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * For Testing purpose
     *
     * @param seekBar  the seekbar object
     * @param interval the current time interval
     * @param fromUser true if the user updates the tme interval
     */
    public void onProgressChanged(SeekBar seekBar, int interval, boolean fromUser) {
        currentInterval = interval;
        int val = (interval * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
        currentPosition = val;
        saveProgressAndLocation();
    }

    private void setText(SeekBar seekBar, TextView progressText, int interval, int val) {
        progressText.setText(String.valueOf(interval + TIME_OFFSET) + " min");
        progressText.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);

    }

    private void saveProgressAndLocation() {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefs.edit().putInt("interval", currentInterval).apply();
        mSharedPrefs.edit().putInt("position", currentPosition).apply();
    }

    private int getCurrentProgress() {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            int interval = mSharedPrefs.getInt("interval", 0);
            return interval;
        } catch (Exception e) {
            return 0;
        }
    }

    private int getCurrentLocation() {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            int position = mSharedPrefs.getInt("position", 0);
            return position;
        } catch (Exception e) {
            return 0;
        }
    }
}
