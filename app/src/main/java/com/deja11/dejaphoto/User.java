package com.deja11.dejaphoto;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

/**
 * Created by thefr on 5/31/2017.
 */

public class User {
    private boolean sharing;
    private String username;
    private HashMap<String, String> friends;


    public User(){
        sharing = false;
        username = "";
        friends = new HashMap<String, String>();
    }

    public User(DatabaseReference databaseReference){
        Query queryRef = databaseReference.child("Test@gmailcom");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getKey();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     *
     * SETTERS
     *
     * */
    public void setSharing(boolean share){
        sharing = share;
    }
    public void setUsername(String user){
        username = user;
    }
    public void setFriend(String name, boolean mutual){
        friends.put(name, String.valueOf(mutual));
    }





    /**
     *
     * GETTERS
     *
     * */
    public boolean isSharing(){
        return sharing;
    }
    public String getUsername(){
        return username;
    }
    public boolean isFriendOf(String name){
        if(friends.containsKey(name)){
            if(friends.get(name) == "true"){
                return true;
            }else{
                return false;
            }

        }else{
            return false;
        }
    }
}
