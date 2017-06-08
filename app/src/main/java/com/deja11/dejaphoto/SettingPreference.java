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


    final static int TIME_OFFSET = 5;
    final int MAX_TIME = 25;
    final String KEY_POSITION = "position";
    final String KEY_INTERVAL = "interval";
    public static int currentInterval = TIME_OFFSET;
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
    public static boolean showSwitch = true;
    private String KEY_SWITCH_PHOTO = "photoswtich";

    Switch switchLocation;
    TextView locationStatus;
    public static boolean showLocation = true;
    private String KEY_LOCATION = "location";

    Switch switchMine;
    TextView myStatus;
    public static boolean viewMyPhoto = true;
    private String KEY_VIEW_MY_PHOTOS = "viewMyPhotos";

    Switch switchFriends;
    TextView friendsStatus;
    public static boolean viewFriendPhoto = true;
    private String KEY_VIEW_FRIENDS_PHOTOS = "viewFriendsPhotos";

    Switch switchSharing;
    TextView sharingStatus;
    public static boolean sharing = true;
    private String KEY_SHARING = "sharing";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_preference);

        // For Junit Test
        setInstance(this);

        /* For all the toggles*/
        switchPhoto = (Switch) findViewById(R.id.switch1);
        switchStatus = (TextView) findViewById(R.id.set1);
        showSwitch = getCurrentStatus(KEY_SWITCH_PHOTO);
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
                saveStatus(KEY_SWITCH_PHOTO, isChecked);
            }
        });

        /* For all the toggles*/
        switchLocation = (Switch) findViewById(R.id.switch2);
        locationStatus = (TextView) findViewById(R.id.set2);
        showLocation = getCurrentStatus(KEY_LOCATION);
        updateStatus(showLocation,locationStatus,switchLocation);
        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateStatus(isChecked, locationStatus, switchLocation);
                saveStatus(KEY_LOCATION, isChecked);
            }
        });

        switchMine = (Switch) findViewById(R.id.switch3);
        myStatus = (TextView) findViewById(R.id.set3);
        viewMyPhoto = getCurrentStatus(KEY_VIEW_MY_PHOTOS);
        updateStatus(viewMyPhoto,myStatus,switchMine);
        switchMine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateStatus(isChecked, myStatus, switchMine);
                saveStatus(KEY_VIEW_MY_PHOTOS, isChecked);
            }
        });

        switchFriends = (Switch) findViewById(R.id.switch4);
        friendsStatus = (TextView) findViewById(R.id.set4);
        viewFriendPhoto = getCurrentStatus(KEY_VIEW_FRIENDS_PHOTOS);
        updateStatus(viewFriendPhoto,friendsStatus,switchFriends);
        switchFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateStatus(isChecked, friendsStatus, switchFriends);
                saveStatus(KEY_VIEW_FRIENDS_PHOTOS, isChecked);
            }
        });

        switchSharing = (Switch) findViewById(R.id.switch5);
        sharingStatus = (TextView) findViewById(R.id.set5);
        sharing = getCurrentStatus(KEY_SHARING);
        updateStatus(sharing,sharingStatus,switchSharing);
        switchSharing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateStatus(isChecked, sharingStatus, switchSharing);
                saveStatus(KEY_SHARING, isChecked);
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
