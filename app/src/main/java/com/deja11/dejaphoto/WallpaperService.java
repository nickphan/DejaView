package com.deja11.dejaphoto;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class WallpaperService extends Service {
    public WallpaperService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(WallpaperService.this, "Wallpaper will begin to change", Toast.LENGTH_SHORT).show();
        Thread thread = new Thread(new myThread(startId));
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }


    public void onDestroy() {
        super.onDestroy();
    }


    final class myThread implements Runnable {
        boolean running;
        int startId;
        Controller controller;

        public myThread(int passedId) {
            this.startId = passedId;
            controller = new Controller();
            running = true;
        }

        /**
         * Change background every 5 min
         */
        @Override
        public void run() {
            try {
                //IF ANYONE HAS A BETTER SOLUTION TO LOOP PLEASE LET ME KNOW
                while(running) {
                    Photo photo = controller.getNextPhoto();
                    controller.setWallpaper(photo, getApplicationContext(), getContentResolver());
                    wait(300000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}


/*

    public boolean changeWallpaper(String path){
        Uri data = Uri.parse(path);
        return changeWallpaper(data);
    }


    public boolean changeWallpaper(Uri data){
        try {
            InputStream inputStream = getContentResolver().openInputStream(data);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return changeWallpaper(bitmap);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public boolean changeWallpaper(Bitmap bitmap){
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try{
            myWallpaperManager.setBitmap(bitmap);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public boolean changeWallpaper(int drawable){
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try{
            myWallpaperManager.setResource(+drawable);
            return true;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }*/
