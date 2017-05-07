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
    ArrayList<String> photoPaths;

    public SetWallpaperService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        photoPaths = gatherPhotos(SetWallpaperService.this);

        Thread thread = new Thread(new PhotoFileThread(startId));
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    private ArrayList<String> gatherPhotos(Context context) {
        Uri uri;
        Cursor cursor;
        int columnIndexData;
        int columnIndexFolder;

        ArrayList<String> imageList = new ArrayList<String>();
        String absolutePath = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        cursor = context.getContentResolver().query(uri, projection, null, null, null);

        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        columnIndexFolder = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while (cursor.moveToNext()) {
            absolutePath = cursor.getString(columnIndexData);
            imageList.add(absolutePath);
        }

        return imageList;
    }

    final class PhotoFileThread implements Runnable {
        int startId;
        int photoIndex;

        public PhotoFileThread(int startId) {
            this.startId = startId;
            photoIndex = 0;
        }
        @Override
        public void run() {
            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(SetWallpaperService.this);

            synchronized (this) {
                try {
                    if (photoPaths.size() == 0) {
                        try {
                            myWallpaperManager.setResource(R.raw.test_photo);
                        }
                        catch (IOException e) {
                            Toast.makeText(SetWallpaperService.this, "Error displaying wallpaper", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        try {
                            FileInputStream photoStream = new FileInputStream(new File(photoPaths.get(photoIndex)));
                            myWallpaperManager.setStream(photoStream);
                        }
                        catch (Exception e) {
                            Toast.makeText(SetWallpaperService.this, "Error displaying wallpaper from file", Toast.LENGTH_SHORT).show();
                        }

                        photoIndex++;
                    }

                    wait(15000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (photoIndex >= photoPaths.size()) {
                    photoIndex = 0;
                }
            }
        }
    }
}
