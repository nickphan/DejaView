package com.deja11.dejaphoto;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class SetWallpaperService extends IntentService {
    private static Controller controller;

    public SetWallpaperService() {
        super("WallpaperService");
        //controller = getController();
    }

    /**
     * Use intent to figure out which button was clicked and respond accordingly
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("Service Started", "SetWallpaper Service called");

        int order = intent.getIntExtra("Order", 0);
        Log.i("i", Integer.toString(order));
        if (order == 1) {
            Photo nextPhoto = controller.getNextPhoto();
            controller.setWallpaper(nextPhoto);
        } else if (order == 2) {
            Photo prevPhoto = controller.getPreviousPhoto();
            controller.setWallpaper(prevPhoto);
        } else if (order == 3) {
            controller.karmaPhoto();
        } else if (order == 4) {
            controller.releasePhoto();
        } else if (order == 0) {
            /*SHOULD NEVER GET HERE*/
            Log.i("Order", Integer.toString(0));
        } else {
            /*ESPECIALLY SHOULD NEVER GET HERE*/
            Log.i("Error Order", "Service was started with an intent of " + Integer.toString(order));
        }
    }

    /**
     * Override to initialize a static controller object on the first service started
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (controller == null) {
            controller = new Controller(getApplicationContext());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /* Call parent onDestroy*/
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
