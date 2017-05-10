package com.deja11.dejaphoto;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.InputStream;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class Controller {


    // replace the string with the real location as the default value
    String albumLocation = "cameraAlbum";

    // database that contains the photo locations on the phone
    // someDataBase photoDatabase;

    CountDownTimer countDown;

    // cache that stores the previous 10 photos
    ArrayList<Photo> queue = new ArrayList<Photo>();
    ArrayList<String> queueStrings = new ArrayList<String>();
    int currIndex;



    public Controller(Context context){
        queueStrings = gatherPhotos(context);
        for(int i = 0; i < queueStrings.size(); i++){
            Photo photo = new Photo(queueStrings.get(i));
            queue.add(photo);
        }
    }

    /**
     * Get the next photo to display
     * @return the next photo
     */
    public Photo getNextPhoto(){
        if(currIndex+1 < queue.size()){
            currIndex++;
        }else{
            currIndex = 0;
        }

        if(queue.get(currIndex).isReleased()){
            return getNextPhoto();
        }else{
            return queue.get(currIndex);
        }
    }

    /**
     * Get the previous photo displayed
     * @return the previous photo
     */
    public Photo getPreviousPhoto() {
        return null;
    }

    /**
     * Get the current photo that is displayed as the wallpaper
     * @return the current wallpaper as a photo
     */
    public Photo getCurrentWallpaper() {
        return queue.get(currIndex);
    }

    void karmaPhoto(){
        Photo photo = getCurrentWallpaper();
        if(!photo.isKarma()){
            photo.setKarma(true);
        }
    }

    /**
     * Remove the current photo shown on the homepage from the cycle
     */
    void releasePhoto(){
        Photo photo = getCurrentWallpaper();
        photo.setReleased(true);
    }



    /**
     * Set the desired photo to be the wallpaper
     * @param photo the photo acquired from getNextPhoto()
     *        context the Activity context
     *        contentResolver ...
     * @return true if the wallpaper was set. false otherwise
     */

    boolean setWallpaper(Photo photo, Context context){
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        if(photo.getPhotoLocation() == null){
            try{
                myWallpaperManager.setResource(+R.drawable.default_image);
                return false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        Uri data = Uri.parse(photo.getPhotoLocation());
        try {
            FileInputStream photoStream = new FileInputStream(new File(queue.get(currIndex).getPhotoLocation()));
            myWallpaperManager.setStream(photoStream);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**/
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
}
