package com.deja11.dejaphoto;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.deja11.dejaphoto.DatabaseHelper.COL_DATE_5;
import static com.deja11.dejaphoto.DatabaseHelper.COL_DEJA_6;
import static com.deja11.dejaphoto.DatabaseHelper.COL_FILE_NAME_9;
import static com.deja11.dejaphoto.DatabaseHelper.COL_KARMA_8;
import static com.deja11.dejaphoto.DatabaseHelper.COL_LAT_3;
import static com.deja11.dejaphoto.DatabaseHelper.COL_LOC_NAME_11;
import static com.deja11.dejaphoto.DatabaseHelper.COL_LONG_4;
import static com.deja11.dejaphoto.DatabaseHelper.COL_OWNER_10;
import static com.deja11.dejaphoto.DatabaseHelper.COL_PATH_2;
import static com.deja11.dejaphoto.DatabaseHelper.COL_REL_7;
import static com.deja11.dejaphoto.DatabaseHelper.COL_TOTAL_KARMA_12;
import static com.deja11.dejaphoto.DatabaseHelper.TAGDATABASE;

/**
 * Created by Tee on 6/4/17.
 */

public class FirebaseHelper {
    Context context;

    FirebaseDatabase dejabase;
    DatabaseReference mdejaRef;
    FirebaseStorage dejaStorage;
    StorageReference mdejaStorage;

    public FirebaseHelper(Context context) {
        dejabase = FirebaseDatabase.getInstance();
        mdejaRef = dejabase.getReference();
        this.context = context;
        dejaStorage = FirebaseStorage.getInstance();
        mdejaStorage = dejaStorage.getReference();
    }

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
    public void insertFirebaseData(String phoneLocation, double geoLat, double geoLong, String date, int dejapoints, int isReleased, int isKarma, String photoName, String userName, String locationName, String totalKarma) {


        HashMap<String, String> contentValues = new HashMap<>();
        int period = photoName.indexOf('.');
        String photoNameFix = photoName.substring(0, period) + photoName.substring(period + 1);


        // Put all data in a container
        contentValues.put(COL_PATH_2, phoneLocation);
        contentValues.put(COL_LAT_3, geoLat + "");
        contentValues.put(COL_LONG_4, geoLong + "");
        contentValues.put(COL_DATE_5, date);
        contentValues.put(COL_DEJA_6, dejapoints + "");
        contentValues.put(COL_REL_7, isReleased + "");
        contentValues.put(COL_KARMA_8, isKarma + "");
        contentValues.put(COL_FILE_NAME_9, photoName);
        contentValues.put(COL_OWNER_10, userName);
        contentValues.put(COL_LOC_NAME_11, locationName);
        contentValues.put(COL_TOTAL_KARMA_12, totalKarma);


        mdejaRef.child("images").child(userName).child(photoNameFix).setValue(contentValues);

        Log.i(TAGDATABASE, "Data inserted correctly");
    }


    public void insertFirebaseStorage(String phoneLocation) {


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username = sharedPreferences.getString("username", "unknown");
        //insert into storage
        UploadTask uploadTask;

        // Use file2 to get a correct name for firebase storage
        Uri file2 = Uri.fromFile(new File(phoneLocation));
        Uri file = compress(phoneLocation);
        Log.d("FIREBASE STORAGE", file2.getLastPathSegment() + " will be uploaded");
        StorageReference photoRef = mdejaStorage.child("images/" + username + "/" + file2.getLastPathSegment());

        uploadTask = photoRef.putFile(file);
    }

    public ArrayList<String> downloadFriendPhotos(final Context context, final String friendUserName) {
        Log.d("SYNC", "DOWNLOADING FROM " + friendUserName);
        Toast.makeText(context, "Downloading from " + friendUserName, Toast.LENGTH_LONG).show();

        // Create a director if it doesn't exit

        Query queryRef = mdejaRef.child("images").child(friendUserName);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //TODO refactor this mess
                String phoneLocation;
                GeoLocation geoLocation;
                int totalKarma;

                // newly added field to accomodate for the updated column
                String dateString;
                String owner;
                String locationName;
                String photoName;
                boolean released;
                boolean karma;

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {


                    photoName = eventSnapshot.child(COL_FILE_NAME_9).getValue().toString();
                    locationName = eventSnapshot.child(COL_LOC_NAME_11).getValue().toString();
                    geoLocation = new GeoLocation(Double.valueOf(eventSnapshot.child(COL_LAT_3).getValue().toString()), Double.valueOf(eventSnapshot.child(COL_LONG_4).getValue().toString()));
                    owner = eventSnapshot.child(COL_OWNER_10).getValue().toString();
                    dateString = eventSnapshot.child(COL_DATE_5).getValue().toString();
                    totalKarma = Integer.valueOf(eventSnapshot.child(COL_TOTAL_KARMA_12).getValue().toString());
                    released = Boolean.valueOf(eventSnapshot.child(COL_KARMA_8).getValue().toString());



                    Photo toBeDownloadedPhoto = new Photo(null, geoLocation, null, 0, released, false, totalKarma, dateString, photoName, owner, locationName);
                    downloadAPhoto(friendUserName, photoName, context, toBeDownloadedPhoto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return null;

    }

    public void tryToInsertFirebase(final String absolutePath, final double geoLat, final double geoLong, final String date, int dejapoints, int isReleased, int isKarma, final String photoName, final
    String photouserName, final String locationName, final String totalKarma) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String username = sharedPreferences.getString("username", "unknown");
        int period = photoName.indexOf('.');
        String photoNameFix = photoName.substring(0, period) + photoName.substring(period + 1);

        Query queryRef = mdejaRef.child("images").child(username).child(photoNameFix);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                if (snapshot == null || snapshot.getValue() == null) {
                    //Toast.makeText(MainActivity.this, "No record found", Toast.LENGTH_SHORT).show();


                    insertFirebaseData(absolutePath, geoLat, geoLong, date, 0, 0, 0, photoName, username, locationName, totalKarma);
                    insertFirebaseStorage(absolutePath);


                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });


    }

    public int getTotalKarma(String ownerName, String photoName) {
        final int[] karma = new int[1];
        Log.i("getTotalKarma", ownerName);
        Log.i("getTotalKarma", photoName);
        DatabaseReference databaseReference = mdejaRef.child("images").child(ownerName).child(photoName).child("TOTALKARMA");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String karmaString = dataSnapshot.getValue().toString();
                karma[0] = Integer.valueOf(karmaString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return karma[0];

    }

    public void downloadAPhoto(String userName, String photoName, final Context context, Photo photo) {

        File storagePath = new File(Controller.DEJAPHOTOFRIENDSPATH, photo.getOwner());

        // Create direcorty if not exists
        if (!storagePath.exists()) {
            //Toast.makeText(context, "storage created",Toast.LENGTH_LONG).show();
            storagePath.mkdirs();
        } else {
            //Toast.makeText(context, "storage already created",Toast.LENGTH_LONG).show();
        }
        try {

            final File myFile = new File(storagePath, photoName);
            StorageReference riversRef = mdejaStorage.child("images").child(userName + "/" + photoName);
            riversRef.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    //Toast.makeText(context, "file created " + myFile.getPath(),Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            databaseHelper.tryToInsertData(myFile.getPath(), photo.getGeoLocation().getLatitude(), photo.getGeoLocation().getLongitude(), photo.getDateString(), 0, 0, 0, photoName, photo.getOwner(), photo.getLocationName(), photo.getTotalKarma());
        } catch (Exception e) {
            Log.e("Exception RejecTED", "file not found");
        }


    }

    public void updateFirebase(String userName, String photoLocation, String column, String newValue) {

        String photoName = Uri.fromFile(new File(photoLocation)).getLastPathSegment();
        int period = photoName.indexOf('.');
        String photoNameFix = photoName.substring(0, period) + photoName.substring(period + 1);
        mdejaRef.child("images").child(userName).child(photoNameFix).child(column).setValue(newValue);
        Log.d("Karma", "images/" + userName + "/" + photoNameFix + "/" + column + "/" + newValue);
        //mdejaRef.child("images").child(currentUserName).child(photoNameFix).child("test").setValue("testing");

    }


    public void addFriend(final String user, final String friend) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbR = firebaseDatabase.getReference();
        final boolean[] check = new boolean[1];
        check[0] = false;
        DatabaseReference databaseReference = dbR.child("users");
        //Query query = databaseReference.child(user).orderByChild("friends").equalTo(friend);
        Query query = databaseReference.child(user).child("friends").child(friend);
        Log.i("FirebaseHelper", "Pre add listener");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("FirebaseHelper", "have dataSnapshot");
                if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                    //he hasn't added you yet
                    mdejaRef.child("users").child(friend).child("friends").child(user).setValue("false");
                    //check[0] = true;
                } else {
                    if (dataSnapshot.getValue().toString().equals("false")) {
                        mdejaRef.child("users").child(user).child("friends").child(friend).setValue("true");
                        mdejaRef.child("users").child(friend).child("friends").child(user).setValue("true");
                        //check[0] = true;
                    }
                    //check[0] = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Error", "Firebase addFriend failed");
                check[0] = true;
            }
        });
        Log.i("FirebaseHelper", "Post Add Listener");
        /*while(!check[0]){
            try{
                Thread.sleep(500);

            }catch (Exception e){
                e.printStackTrace();
            }
        }*/
    }

    public void createUser(String username) {
        DatabaseReference databaseReference = mdejaRef.child("users");
        databaseReference.child(username).child("sharing").setValue("false");
        databaseReference.child(username).child("username").setValue(username);
        databaseReference.child(username).child("friends").child("fake").setValue("false");
    }

    public void setSharing(String name, boolean value) {
        DatabaseReference databaseReference = mdejaRef.child("users").child(name).child("sharing");
        databaseReference.setValue(String.valueOf(value));
    }


    public boolean getSharing(String username) {
        final boolean[] sharing = new boolean[2];
        DatabaseReference databaseReference = mdejaRef.child("user").child(username);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                    sharing[1] = true;
                    sharing[0] = false;
                } else {
                    String sharingString = dataSnapshot.child("sharing").getValue().toString();
                    if (sharingString.equals("true")) {
                        sharing[0] = true;
                        sharing[1] = true;
                    } else {
                        sharing[0] = false;
                        sharing[1] = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        while (!sharing[1]) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sharing[0];
    }

    public void updateRelease(String username, String photoPath) {
        String photoName = Uri.fromFile(new File(photoPath)).getLastPathSegment();
        int period = photoName.indexOf('.');
        String photoNameFix = photoName.substring(0, period) + photoName.substring(period + 1);

        //COL_OWNER_10
        mdejaRef.child("images").child(username).child(photoNameFix).child(COL_REL_7).setValue("1");
    }
    /*DO WE NEED THE METHODS UNDER HERE?*/


    public ArrayList<Pair<String, String>> getFriendsSharing(final String username) {
        final ArrayList<Pair<String, String>> friends = new ArrayList<Pair<String, String>>();
        final boolean[] check = new boolean[1];
        //DatabaseReference databaseReference = mdejaRef.child("users").child(username);
        DatabaseReference databaseReference = mdejaRef.child("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                    check[0] = true;
                } else {
                    for (DataSnapshot friendSnapshot : dataSnapshot.child(username).child("friends").getChildren()) {
                        //for(DataSnapshot friendSnapshot : dataSnapshot.child("friends").getChildren()){
                        String key = friendSnapshot.getKey();
                        String val = friendSnapshot.getValue().toString();

                        if (val.equals("true")) {
                            val = dataSnapshot.child(key).child("sharing").getValue().toString();
                        }

                        Pair<String, String> pair = new Pair<String, String>(key, val);
                        friends.add(pair);
                    }

                    check[0] = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Pair<String, String> pair = friends.get(0);
        //pair.first;
        //pair.second;

        while (!check[0]) {
            try {
                Thread.sleep(500);
                Log.i("waiting", "getFriends");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return friends;
    }

    public String getUsername(String username) {
        final boolean[] check = new boolean[1];
        final String[] name = new String[1];
        final DatabaseReference databaseReference = mdejaRef.child("user").child(username);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                    check[0] = true;
                    name[0] = "";
                } else {
                    name[0] = dataSnapshot.getKey();
                    check[0] = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        while (!check[0]) {
            try {
                Thread.sleep(500);
                Log.i("waiting", "getusername");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return name[0];
    }

    public ArrayList<String> getPhotos() {
        final boolean[] check = new boolean[1];
        final ArrayList<String> photoNames = new ArrayList<>();
        final DatabaseReference databaseReference = mdejaRef.child("images").child("physicalDevice@teesphonecom");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childrenSnapshot : dataSnapshot.getChildren()) {
                    photoNames.add(childrenSnapshot.getKey());
                }
                check[0] = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                check[0] = true;
            }
        });
        while (!check[0]) {
            try {
                Thread.sleep(500);
                Log.d("waiting", "getPhotos");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return photoNames;
    }

    private Uri compress(String photoPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(new File(photoPath).getAbsolutePath());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), compressedBitmap, "title", null);
        return Uri.parse(path);
    }
}
