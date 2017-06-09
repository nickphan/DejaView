package com.deja11.dejaphoto;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Carl on 6/5/2017.
 *
 * This class is a utility class that contains methods for file handling.
 */

public class AlbumUtils {

    /**
     * Copies the source file photo into the specified destination.
     *
     * Code credits to: https://examples.javacodegeeks.com/core-java/io/file/4-ways-to-copy-file-in-java/
     * #2: copy files using channels
     *
     * @param source        - the file to be copied
     * @param destination   - the place where the file will be copied to
     * @throws IOException  - when the file does not exist or when the app does not have
     *                        permission to write to storage
     */
    public static void copyPhoto(File source, File destination) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;

        // create file channels to allow easy file copying
        inputChannel = new FileInputStream(source).getChannel();
        outputChannel = new FileOutputStream(destination).getChannel();
        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

        inputChannel.close();
        outputChannel.close();

        Log.i("File Change", "Successfully Copied Photo " + destination.getPath());
    }

    /**
     * Deletes a specific photo with the specified path.
     *
     * @param photoPath - the file path of the photo
     */
    public static void deletePhoto(String photoPath) {
        try {
            new File(photoPath).delete();
            Log.i("File Change", "Deleted " + photoPath);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("File Change", "Delete Unsuccessful for " + photoPath);
        }
    }

    /**
     * Deletes all the photos of a specific friend.
     *
     * @param friendName
     */
    public static void deleteAllPhotosOfFriend(String friendName) {
        File friendFolder = new File(Controller.DEJAPHOTOFRIENDSPATH, friendName);

        if (friendFolder.exists()) {
            String[] photos = friendFolder.list();

            // loop through each of the photos in the folder and try to delete them
            for (String photo : photos) {
                try {
                    new File(friendFolder, photo).delete();
                    Log.i("File Change", "Deleted " + photo);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("File Change", "Delete Unsuccessful for " + photo);
                }
            }
        }
    }

    /**
     * Deletes all the photos in the DejaPhotoFriend album.
     */
    public static void deleteAllFriendPhotos() {
        File dejaPhotoFriendFolder = new File(Controller.DEJAPHOTOFRIENDSPATH);

        if (dejaPhotoFriendFolder.exists()) {
            String[] friends = dejaPhotoFriendFolder.list();

            for(String friend : friends) {
                deleteAllPhotosOfFriend(friend);
            }
        }
    }

    /**
     * Converts a content URI to a generic file URI.
     *
     * Code credits to: https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
     *
     * @param context       - the application context
     * @param uri           - the location of the file in the storage
     * @param selection     - what field is used to choose file (in this case is "_id")
     * @param selectionArgs - unique field parameter to identify the file
     * @return      - the String representation of the URI of the file
     *                (which can be used to instantiate a File object)
     * @throws IllegalArgumentException    - when the file does not exist
     */
    public static String getPath(Context context, Uri uri, String selection, String[] selectionArgs)
            throws IllegalArgumentException {

        String[] projection = {MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver()
                .query(uri, projection, selection, selectionArgs, null);

        // if the photo with the given id (selectionArgs) exists, which it should
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            return cursor.getString(index);
        }

        return null;
    }

}
