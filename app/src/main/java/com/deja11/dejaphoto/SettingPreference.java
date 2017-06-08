package com.deja11.dejaphoto;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingPreference extends Activity {

    static final int TIME_OFFSET = 5;
    final int MAX_TIME = 25;
    final String KEY_POSITION = "position";
    final String KEY_INTERVAL = "interval";
    private static int currentInterval = TIME_OFFSET;
    private static int currentPosition;

    //For testing purpose
    private static SettingPreference instance;
    public static SettingPreference getInstance() {
        if (instance == null) {setInstance(instance);}return instance;}
    public static void setInstance(SettingPreference instance) {SettingPreference.instance = instance;}
    private TextView intervalText;

    /*For toggles*/
    Switch switchPhoto;
    TextView switchStatus;
    private static boolean showSwitch = true;

    Switch switchLocation;
    TextView locationStatus;
    private static boolean showLocation = true;

    Switch switchMine;
    TextView myStatus;
    private static boolean viewMyPhoto = true;

    Switch switchFriends;
    TextView friendsStatus;
    private static boolean viewFriendPhoto = true;

    Switch switchSharing;
    TextView sharingStatus;
    private static boolean sharing = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_preference);

        // For Junit Test
        setInstance(this);

        /* For all the toggles*/
        switchPhoto = (Switch) findViewById(R.id.switch1);
        switchStatus = (TextView) findViewById(R.id.set1);
        showSwitch = getCurrentStatus("photoswitch");
        updateStatus(showSwitch, switchStatus, switchPhoto);
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        enableSlider(seekBar);
        if(!showSwitch){
            seekBar.setEnabled(false);
        }
        switchPhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateStatus(isChecked, switchStatus, switchPhoto);
                if(isChecked){
                    seekBar.setEnabled(true);
                }
                else{
                    seekBar.setEnabled(false);
                }
                saveStatus("photoswitch", isChecked);
            }
        });

        /* For all the toggles*/
        switchLocation = (Switch) findViewById(R.id.switch2);
        locationStatus = (TextView) findViewById(R.id.set2);
        showLocation = getCurrentStatus("location");
        updateStatus(showLocation,locationStatus,switchLocation);
        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateStatus(isChecked, locationStatus, switchLocation);
                saveStatus("location", isChecked);
            }
        });

        switchMine = (Switch) findViewById(R.id.switch3);
        myStatus = (TextView) findViewById(R.id.set3);
        viewMyPhoto = getCurrentStatus("viewMyPhotos");
        updateStatus(viewMyPhoto,myStatus,switchMine);
        switchMine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateStatus(isChecked, myStatus, switchMine);
                saveStatus("viewMyPhotos", isChecked);
            }
        });

        switchFriends = (Switch) findViewById(R.id.switch4);
        friendsStatus = (TextView) findViewById(R.id.set4);
        viewFriendPhoto = getCurrentStatus("viewFriendsPhotos");
        updateStatus(viewFriendPhoto,friendsStatus,switchFriends);
        switchFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateStatus(isChecked, friendsStatus, switchFriends);
                saveStatus("viewFriendsPhotos", isChecked);
            }
        });

        switchSharing = (Switch) findViewById(R.id.switch5);
        sharingStatus = (TextView) findViewById(R.id.set5);
        sharing = getCurrentStatus("sharing");
        updateStatus(sharing,sharingStatus,switchSharing);
        switchSharing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateStatus(isChecked, sharingStatus, switchSharing);
                saveStatus("sharing", isChecked);
            }
        });
    }

    private void saveStatus(String key, boolean status){
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefs.edit().putBoolean(key, status).apply();
    }

    private boolean getCurrentStatus(String key){
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            boolean status = mSharedPrefs.getBoolean(key, true);
            return status;
        } catch (Exception e) {
            return true;
        }
    }

    private void updateStatus(boolean isChecked, TextView view, Switch switchButton){
        if (isChecked) {
            view.setText("on");

        } else {
            view.setText("off");
        }
        switchButton.setChecked(isChecked);
    }

    private void enableSlider( SeekBar seekBar){
        seekBar.setMax(MAX_TIME);
        intervalText = (TextView) findViewById(R.id.seekbarvalue);

        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentInterval = mSharedPrefs.getInt(KEY_INTERVAL, 0);

        seekBar.setProgress(currentInterval);
        currentPosition = getCurrentPosition();
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

    private void setText(SeekBar seekBar, TextView progressText, int interval, int val) {
        progressText.setText(String.valueOf(interval + TIME_OFFSET) + " min");
        progressText.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);

    }

    private void saveProgressAndLocation() {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefs.edit().putInt(KEY_INTERVAL, currentInterval).apply();
        mSharedPrefs.edit().putInt(KEY_POSITION, currentPosition).apply();
    }

    private int getCurrentPosition() {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            int position = mSharedPrefs.getInt(KEY_POSITION, 0);
            return position;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getCurrentInterval(){return currentInterval;}

    public boolean ShowLocation(){return showLocation;}

    public boolean ViewMyPhoto(){return viewMyPhoto;}

    public boolean ViewFriendPhoto(){return viewFriendPhoto;}

    public boolean PhotoSharing(){return sharing;}

    /**
     * For Testing purpose
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


}
