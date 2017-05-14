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
    private static Controller controller;
    private static int i = 0;

    public SetWallpaperService() {
        super("WallpaperService");
        //controller = getController();
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("Service Started", "SetWallpaper Service called");

        int order = intent.getIntExtra("Order", 0);
        Log.i("i", Integer.toString(order));
        if(order == 1) {
            Photo nextPhoto = controller.getNextPhoto();
            controller.setWallpaper(nextPhoto);
        }else if(order == 2){
            Photo prevPhoto = controller.getPreviousPhoto();
            controller.setWallpaper(prevPhoto);
        }else if(order == 3){
            controller.karmaPhoto();
        }else if(order == 4){
            controller.releasePhoto();
        }else if(order == 0){
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
