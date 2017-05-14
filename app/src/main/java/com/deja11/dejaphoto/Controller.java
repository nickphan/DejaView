package com.deja11.dejaphoto;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.InputStream;
import java.util.Stack;

/**
 * Created by shuai9532 on 5/6/17.
 */

public class Controller implements Parcelable{

    /**
     * TODO
     *  - replace arraylists with databasehelper
     *  - TEST THE SETWALLPAPER METHOD
     * */


    DatabaseHelper databaseHelper;
    Context context;
    Photo currPhoto;
    private static int X;
    private static int Y;
    //For prev
    LinkedList<Photo> cache;

    //For Parcelable
    int mData;

    /**
     * Constructor with context
     * */
    public Controller(Context context){
        this.context = context;
        databaseHelper = new DatabaseHelper(this.context);
        databaseHelper.initialize(this.context);
        cache = new LinkedList<Photo>();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        X= size.x;
        Y= size.y;
    }

    /**
     * Get the next photo to display
     * @return the next photo
     *      either from cache or from DatabaseHelper
     * */
    public Photo getNextPhoto(){
        Photo photo = databaseHelper.getNextPhoto();
        if(currPhoto == null){
            if(photo.isReleased()){
                return getNextPhoto();
            }else{
                return photo;
            }
        }else{
            int currIndex = cache.indexOf(currPhoto);
            if(currIndex == -1){
                if(photo.isReleased()){
                    return getNextPhoto();
                }else{
                    return photo;
                }
            }else if(currIndex == cache.size()-1){
                if(photo.isReleased()){
                    return getNextPhoto();
                }else{
                    return photo;
                }
            }else{
                return cache.get(currIndex+1);
            }
        }
    }

    /**
     * Get the previous photo displayed
     * @return the previous photo
     *      from the cache or null if necessary
     * */
    public Photo getPreviousPhoto() {
        if(currPhoto == null){
            /*SOME ERROR MESSAGE*/
            return null;
        }else{
            int currIndex = cache.indexOf(currPhoto);
            if(currIndex == -1){
                return cache.getLast();
            }else if(currIndex == 0){
                return null;
            }else{
                return cache.get(currIndex-1);
            }
        }
    }

    /**
     * Get the current photo that is displayed as the wallpaper
     * @return the current wallpaper as a photo
     */
    public Photo getCurrentWallpaper() {
        return currPhoto;
    }

    /**
     * Set current photo karma field to true
     */
    public boolean karmaPhoto(){
        Photo photo = getCurrentWallpaper();
        if(!photo.isKarma()){
            photo.setKarma(true);
            databaseHelper.updateKarma(photo.getPhotoLocation());
            return true;
        }else{
            //Toast.makeText(context, "Photo has already been Karma'd", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Remove the current photo shown on the homepage from the cycle
     */
    void releasePhoto(){
        if(currPhoto != null){
            currPhoto.setReleased(true);
            databaseHelper.updateRelease(currPhoto.getPhotoLocation());
            int currIndex = cache.indexOf(currPhoto);
            if(currIndex == -1){
                currPhoto = cache.getLast();
                Photo photo = getNextPhoto();
                setWallpaper(photo);
            }else if(currIndex == cache.size()-1){
                cache.remove(cache.size()-1);
                currPhoto = cache.getLast();
                Photo photo = getNextPhoto();
                setWallpaper(photo);
            }else{
                cache.remove(currIndex);
                currPhoto = null;
                setWallpaper(cache.get(currIndex));
            }
        }
    }

    /**
     * Set the desired photo to be the wallpaper
     * @param photo the photo acquired from getNextPhoto()
     *        context the Activity context
     *        contentResolver ...
     * @return true if the wallpaper was set. false otherwise
     */
    boolean setWallpaper(Photo photo){
        if(photo == null){
            return false;
        }
        if(currPhoto == null){
            int nextPhoto = cache.indexOf(photo);
            if(nextPhoto == -1) {
                currPhoto = photo;
                cache.add(photo);
                if(cache.size() > 10){
                    cache.remove(0);
                }
            }else{
                currPhoto = photo;
            }
            return setWallpaper(photo.phoneLocation, "Hello World");
        }else{
            int currIndex = cache.indexOf(currPhoto);
            if(currIndex == -1){
                cache.add(currPhoto);
                if(cache.size() > 10){
                    cache.remove(0);
                }
                currPhoto = photo;
                return setWallpaper(photo.phoneLocation, "Hello World");
            }else{
                currPhoto = photo;
                return setWallpaper(photo.phoneLocation, "Hello World");
            }
        }
    }

    //set the wallpaper without a location displayed
    boolean setWallpaper(String photoPath){
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        if(photoPath == null){
            try{
                myWallpaperManager.setResource(+R.drawable.default_image);
                return false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        //Uri data = Uri.parse(photoPath);
        try {
            FileInputStream photoStream = new FileInputStream(new File(photoPath));
            myWallpaperManager.setStream(photoStream);

            //InputStream inputStream = context.getContentResolver().openInputStream(data);
            //Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            //BitmapDrawable drawble = writeTextOnWallpaper(bitmap,geoLocation);
            //myWallpaperManager.setBitmap(bitmap);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //setting the wallpaper with the lcoation displayed
    boolean setWallpaper(String photoPath, String geoLocation){
        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(context);
        if(photoPath == null){
            try{
                myWallpaperManager.setResource(+R.drawable.default_image);
                return false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        try {

            Bitmap bitmap = BitmapFactory.decodeFile(new File(photoPath).getAbsolutePath());
            Bitmap mutableBitmap= Bitmap.createBitmap(X,Y,bitmap.getConfig());
            writeBitmapOnMutable(mutableBitmap,bitmap);
            //writeTextOnWallpaper(mutableBitmap, geoLocation);
            myWallpaperManager.setBitmap(mutableBitmap);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void writeBitmapOnMutable(Bitmap mutableBitmap, Bitmap bitmap){
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(bitmap,0,0,null);
    }
    private void writeTextOnWallpaper(Bitmap mutableBitmap, String text){
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        //paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.RED);
        paint.setTextSize(60);
        canvas.drawText("CSE110", 40, Y-paint.getTextSize(), paint);

    }

    /*NECESSARY METHODS TO IMPLEMENT PARCELABLE*/

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(i);
    }
    public static final Parcelable.Creator<Controller> CREATOR
            = new Parcelable.Creator<Controller>() {
        public Controller createFromParcel(Parcel in) {
            return new Controller(in);
        }

        public Controller[] newArray(int size) {
            return new Controller[size];
        }
    };
    private Controller(Parcel in) {
        mData = in.readInt();
    }
}
