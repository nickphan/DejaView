package com.deja11.dejaphoto;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Carl on 6/5/2017.
 */

public class AlbumUtils {


    /*
        Copies the source file photo into the specified destination.

        Code credits to: https://examples.javacodegeeks.com/core-java/io/file/4-ways-to-copy-file-in-java/
                            #2: copy files using channels
                 and to: https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
     */
    public static void copyPhoto(File source, File destination) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;

        inputChannel = new FileInputStream(source).getChannel();
        outputChannel = new FileOutputStream(destination).getChannel();
        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

        inputChannel.close();
        outputChannel.close();

    }


    public static String getPath(Context context, Uri uri, String selection, String[] selectionArgs)
            throws IllegalArgumentException {

        String[] projection = {MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver()
                .query(uri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            return cursor.getString(index);
        }

        return null;
    }

}
