package com.deja11.dejaphoto;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.deja11.dejaphoto.DatabaseHelper.ALBUMPREFIX;
import static com.deja11.dejaphoto.DatabaseHelper.COL_FILE_NAME_9;
import static com.deja11.dejaphoto.DatabaseHelper.COL_ID_1;
import static com.deja11.dejaphoto.DatabaseHelper.COL_KARMA_8;
import static com.deja11.dejaphoto.DatabaseHelper.COL_OWNER_10;
import static com.deja11.dejaphoto.DatabaseHelper.COL_PATH_2;
import static com.deja11.dejaphoto.DatabaseHelper.COL_REL_7;
import static com.deja11.dejaphoto.DatabaseHelper.TABLE_NAME;
import static com.deja11.dejaphoto.DatabaseHelper.TAGDATABASE;
import static com.deja11.dejaphoto.DatabaseHelper.currentUserName;
import static com.deja11.dejaphoto.DatabaseHelper.COL_LAT_3;
import static com.deja11.dejaphoto.DatabaseHelper.COL_LONG_4;
import static com.deja11.dejaphoto.DatabaseHelper.COL_DATE_5;
import static com.deja11.dejaphoto.DatabaseHelper.COL_DEJA_6;

/**
 * Created by Tee on 6/4/17.
 */

public class FirebaseHelper {
    public FirebaseHelper(Context context){

    }

    //TODO Firebase stuff
    // TODO remove
    FirebaseDatabase dejabase = FirebaseDatabase.getInstance();
    DatabaseReference mdejaRef = dejabase.getReference();

    FirebaseStorage dejaStorage = FirebaseStorage.getInstance();
    StorageReference mdejaStorage = dejaStorage.getReference();
    /**
     * Insert a new photo into the database
     *
     * @param phoneLocation path to the photo
     * @param geoLat        Latitude of the location photo was taken
     * @param geoLong       Longtitude of the location photo was taken
     * @param date          Date and time photo was taken
     * @param dejapoints    point assigned to the photo
     * @param isReleased    whether or not the photo is released
     * @param isKarma       whether or not the photo is karma'd
     * @return true if insertion is successful, otherwise false
     */
    public void insertFirebaseData(String phoneLocation, double geoLat, double geoLong, String date, int dejapoints, int isReleased, int isKarma) {



        HashMap<String , String> contentValues = new HashMap<>();
        String photoName = Uri.fromFile(new File(phoneLocation)).getLastPathSegment();
        int period = photoName.indexOf('.');
        String photoNameFix = photoName.substring(0, period) + photoName.substring(period+1);


        // Put all data in a container
        contentValues.put(COL_PATH_2, phoneLocation);
        contentValues.put(COL_LAT_3, geoLat+"");
        contentValues.put(COL_LONG_4, geoLong+"");
        contentValues.put(COL_DATE_5, date);
        contentValues.put(COL_DEJA_6, dejapoints+"");
        contentValues.put(COL_REL_7, isReleased+"");
        contentValues.put(COL_KARMA_8, isKarma+"");
        contentValues.put(COL_FILE_NAME_9,photoName);
        contentValues.put(COL_OWNER_10,currentUserName);


        mdejaRef.child("images").child(currentUserName).child(photoNameFix).setValue(contentValues);

        //mdejaRef.child("images").child(currentUserName).child(""+index++).setValue(Uri.fromFile(new File (phoneLocation)).getLastPathSegment());
        Log.i(TAGDATABASE, "Data inserted correctly");
    }


    public void insertFirebaseStorage(String phoneLocation){
        //insert into storage
        UploadTask uploadTask;
        Uri file = Uri.fromFile(new File(phoneLocation));
        StorageReference photoRef = mdejaStorage.child("images/"+currentUserName+"/"+file.getLastPathSegment());
        uploadTask = photoRef.putFile(file);
    }

    public void downloadFriendPhotos(final Context context){


        // Create a director if it doesn't exit


        Query queryRef = mdejaRef.child("images").child(currentUserName);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photoName;
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    photoName = eventSnapshot.child(COL_FILE_NAME_9).getValue().toString();
                    downloadAPhoto(currentUserName, photoName);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}});










/*
        Query queryRef = mdejaRef.child("images").child(currentUserName);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) { //
                    Toast.makeText(context, eventSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
        //StorageReference folderRef = mStorage.child("images").child("Teehee@gmailcom");
        //Query queryRef = myFirebaseRef.child("User").orderByChild("age");//.limitToFirst(1);


        //String root = Environment.getExternalStorageDirectory().toString();
        /*
        final File myFile = new File(storagePath,"6_eiffel_tower.jpg");

        StorageReference riversRef = mStorage.child("images/6_eiffel_tower.jpg");


        riversRef.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Toast.makeText(getBaseContext(), "file created",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getBaseContext(), "not created",Toast.LENGTH_LONG).show();

            }
        });*/

    }

    public void downloadAPhoto(String userName, String photoName){

        File storagePath = new File(Environment.getExternalStorageDirectory(), "/Deja/myfriends");

        // Create direcorty if not exists
        if(!storagePath.exists()) {
            //Toast.makeText(context, "storage created",Toast.LENGTH_LONG).show();
            storagePath.mkdirs();
        }

        File myFile = new File(storagePath,photoName);
        StorageReference riversRef = mdejaStorage.child("images").child(userName +"/"+photoName);
        riversRef.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                //Toast.makeText(context, "file created",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                //Toast.makeText(context,"not created",Toast.LENGTH_LONG).show();

            }
        });
        //Toast.makeText(context, listOfPhotosToDownload.get(i++), Toast.LENGTH_SHORT).show();


    }

    public void updateFirebase(String userName, String photoLocation, String column ,String newValue){

        String photoName = Uri.fromFile(new File (photoLocation)).getLastPathSegment();
        int period = photoName.indexOf('.');
        String photoNameFix = photoName.substring(0, period) + photoName.substring(period+1);
        mdejaRef.child("images").child(userName).child(photoNameFix).child(column).setValue("1");
        //mdejaRef.child("images").child(currentUserName).child(photoNameFix).child("test").setValue("testing");

    }

    public void addFriend(final String user, final String friend){
        final boolean[] check = new boolean[1];
        check[0] = false;
        DatabaseReference databaseReference = mdejaRef.child("users");
        Query query = databaseReference.child(user).orderByChild("friends").equalTo(friend);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot == null || dataSnapshot.getValue() == null){
                    //he hasn't added you yet
                    mdejaRef.child("users").child(friend).child("friends").child(user).setValue("false");
                    check[0] = true;
                }else{
                    if(dataSnapshot.getValue().toString().equals("false")){
                        mdejaRef.child("users").child(user).child("friends").child(friend).setValue("true");
                        mdejaRef.child("users").child(friend).child("friends").child(user).setValue("true");
                        check[0] = true;
                    }
                    check[0] = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        while(!check[0]){
            try{
                Thread.sleep(500);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public boolean getSharing(String username){
        final boolean[] sharing = new boolean[2];
        DatabaseReference databaseReference = mdejaRef.child("user").child(username);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sharingString = dataSnapshot.child("sharing").getValue().toString();
                if(sharingString.equals("true")){
                    sharing[0] = true;
                    sharing[1] = true;
                }else{
                    sharing[0] = false;
                    sharing[1] = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return sharing[0];
    }

    public ArrayList<String> getFriends(String username){
        final ArrayList<String> friends = new ArrayList<String>();
        final boolean[] check = new boolean[1];
        DatabaseReference databaseReference = mdejaRef.child("users").child(username);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot friendSnapshot : dataSnapshot.child("friends").getChildren()){
                    String isFriend = friendSnapshot.getValue().toString();
                    if(isFriend.equals("true")){
                        friends.add(friendSnapshot.getKey());
                    }
                }
                check[0] = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        while(!check[0]){
            try{
                Thread.sleep(500);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        return friends;
    }

}
