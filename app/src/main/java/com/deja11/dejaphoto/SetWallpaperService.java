package com.deja11.dejaphoto;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class SetWallpaperService extends IntentService {

    // codes for identifying which action the service has to execute
    private static String CODE_KEY = "Order";
    private static int CODE_NEXT_PHOTO = 1;
    private static int CODE_PREVIOUS_PHOTO = 2;
    private static int CODE_KARMA = 3;
    private static int CODE_RELEASE = 4;
    private static int CODE_DEFAULT_VALUE = 0;

    private static final int INTERVAL_OFFSET = 5; // offset for the interval
    private static final String INTERVAL_KEY = "progress"; // the key for the interval in the shared preferences
    private static final int INTERVAL_DEFAULT = 0; // default value for the interval in the shared preferences
    private static final long MIN_TO_MS = 60000; // a conversion factor from minutes to milliseconds

    private static Controller controller;
    private static long interval = 10000;

    public SetWallpaperService() {
        super("WallpaperService");
        //controller = getController();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("Service Started", "SetWallpaper Service called");

        int order = intent.getIntExtra(CODE_KEY, CODE_DEFAULT_VALUE);

        Log.d("Order Number", Integer.toString(order));

        if(order == CODE_NEXT_PHOTO) {
            Photo nextPhoto = controller.getNextPhoto();
            controller.setWallpaper(nextPhoto);
        }else if(order == CODE_PREVIOUS_PHOTO){
            Photo prevPhoto = controller.getPreviousPhoto();
            controller.setWallpaper(prevPhoto);
        }else if(order == CODE_KARMA){
            controller.karmaPhoto();
        }else if(order == CODE_RELEASE){
            controller.releasePhoto();
        }else if(order == CODE_DEFAULT_VALUE){
            /*SHOULD NEVER GET HERE*/
        }else{
            /*ESPECIALLY SHOULD NEVER GET HERE*/
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(controller == null){
            controller = new Controller(getApplicationContext());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public static void updateInterval(int minutes) {
        interval = minutes * MIN_TO_MS;
    }

    public static long getInterval() {
        return interval;
    }

    /*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(SetWallpaperService.this, "Started", Toast.LENGTH_SHORT);

        Thread thread = new Thread(new PhotoFileThread(startId));
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    final class PhotoFileThread implements Runnable {
        int startId;

        public PhotoFileThread(int startId) {
            this.startId = startId;
        }
        @Override
        public void run() {
            synchronized (this) {
                try {
                    Photo photo = controller.getNextPhoto();
                    boolean setWallpaper = controller.setWallpaper(photo);
                    Toast.makeText(SetWallpaperService.this, "Changed", Toast.LENGTH_SHORT);
                    wait(10000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    } */
}
