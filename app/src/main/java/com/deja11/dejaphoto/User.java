package com.deja11.dejaphoto;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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
    public User(String user){
        sharing = false;
        username = user;
        friends = new HashMap<String, String>();
    }

    public User(DatabaseReference databaseReference){
        Query queryRef = databaseReference;
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot == null || dataSnapshot.getValue() == null){
                    /*User doesn't exist*/
                    //POINT 1
                }else{
                    /*User exists*/
                    username = dataSnapshot.getKey();
                    String share = dataSnapshot.child("sharing").getValue().toString();
                    if(share.equals("true")){
                        sharing = true;
                    }else{
                        sharing = false;
                    }
                    
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //POINT 2
        while(username == null){
            try{
                Thread.sleep(500);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
    public void setFriend(String name, String mutual){
        friends.put(name, mutual);
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
    public ArrayList<String> getFriends(){
        ArrayList<String> myFriends = new ArrayList<String>();
        Set<String> keys = friends.keySet();
        for(String key: keys){
            String value = friends.get(key);
            if(value.equals("true")){
                myFriends.add(key);
            }
        }
        return myFriends;
    }
    public boolean isFriendOf(String name){
        if(friends.containsKey(name)){
            if(friends.get(name).equals("true")){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    public boolean friendExists(String name){
        if(friends.containsKey(name)){
            return true;
        }else{
            return false;
        }
    }
}
