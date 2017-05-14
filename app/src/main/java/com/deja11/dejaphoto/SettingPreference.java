package com.deja11.dejaphoto;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.TextView;


public class SettingPreference extends Activity {

    /**
     * For testing purpose
     */
    private static SettingPreference instance;
    public static SettingPreference getInstance() {
        if(instance==null){
            setInstance(instance);
        }
        return instance;
    }
    public static void setInstance(SettingPreference instance) {
        SettingPreference.instance = instance;
    }

    private int currentProgress = 5;
    private static int currentLocation;

    // For testing
    private TextView progressText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_preference);
        setInstance(this);
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setMax(25);
        progressText = (TextView)findViewById(R.id.seekbarvalue);
        //default
        currentProgress = getCurrentProgress();
        seekBar.setProgress(currentProgress);
        currentLocation=getCurrentLocation();
        setText(seekBar,progressText,currentProgress,currentLocation);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                currentProgress= progress;
                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                currentLocation= val;
                setText(seekBar, progressText, currentProgress, val);
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
     *  For Testing purpose
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        currentProgress= progress;
        int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
        currentLocation= val;
        saveProgressAndLocation();
    }


    private void setText(SeekBar seekBar, TextView progressText, int progress, int val){
        progressText.setText(String.valueOf(progress+5) + " min");
        progressText.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);

    }

    private void saveProgressAndLocation(){
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefs.edit().putInt("progress",currentProgress).apply();
        mSharedPrefs.edit().putInt("location",currentLocation).apply();
    }

    private int getCurrentProgress(){
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        try{
            int progress = mSharedPrefs.getInt("progress",0);
            return progress;
        }
        catch(Exception e){
            return 0;
        }
    }
    private int getCurrentLocation(){
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        try{
            int location = mSharedPrefs.getInt("location",0);
            return location;
        }
        catch(Exception e){
            return 0;
        }
    }

}
