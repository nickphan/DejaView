package com.deja11.dejaphoto;

import android.app.Activity;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class SetWallpaperService extends Service {
    Controller controller;

    public SetWallpaperService() {
        controller = new Controller(SetWallpaperService.this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

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
    }
}
