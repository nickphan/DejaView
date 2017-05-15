package com.deja11.dejaphoto;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;



public class SetWallpaperService extends IntentService {

    // codes for identifying which action the service has to execute
    private static String CODE_KEY = "Order";
    private static int CODE_NEXT_PHOTO = 1;
    private static int CODE_PREVIOUS_PHOTO = 2;
    private static int CODE_KARMA = 3;
    private static int CODE_RELEASE = 4;
    private static int CODE_DEFAULT_VALUE = 0;

    public static long interval = 10000; // interval between Wallpaper changes (5 minutes default)
    private static final long MIN_TO_MS = 60000; // a conversion factor from minutes to milliseconds

    private static Controller controller;

    public SetWallpaperService() {
        super("WallpaperService");
    }

    /**
     * Use intent to figure out which button was clicked and respond accordingly
     * */
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
            Log.i("Order", Integer.toString(0));
        }else{
            /*ESPECIALLY SHOULD NEVER GET HERE*/
            Log.i("Error Order", "Service was started with an intent of " + Integer.toString(order));
        }
    }

    /**
     * Override to initialize a static controller object on the first service started*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(controller == null){
            controller = new Controller(getApplicationContext());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /* Call parent onDestroy*/
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public static void updateInterval(int minutes) {
        interval = minutes * MIN_TO_MS;
        Log.d("Interval updated to", Long.toString(interval));
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
