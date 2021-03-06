package com.deja11.dejaphoto;

import android.provider.ContactsContract;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by thefr on 5/31/2017.
 */

public class UserTest {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myFirebaseRef = database.getReference();

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);


    @Test
    public void TestInitializeWithDatabaseReference(){

        final String username = "Test@gmailcom";
        final String password = "valueOfPassword";


        Query queryRef = myFirebaseRef.child("Test@gmailcom");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot == null || dataSnapshot.getValue() == null){
                    /*User doesn't exist*/
                    //POINT 1
                }else{
                    /*User exists*/
                    assertEquals(username, dataSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        User testUser = new User(myFirebaseRef.child("Test@gmailcom"));
        assertEquals(username, testUser.getUsername());

    }

    DatabaseMediator databaseMediator;

    @Test
    public void TestCreateUser(){
        databaseMediator = new DatabaseMediator(mainActivityActivityTestRule.getActivity());
        //databaseMediator.createUser("nick1@ucsdedu");
        //databaseMediator.createUser("nick2@ucsdedu");
        //databaseMediator.createUser("nick3@ucsdedu");
        assertTrue(true);
    }

    @Test
    public void TestAddFriends(){
        databaseMediator = new DatabaseMediator(mainActivityActivityTestRule.getActivity());
        //databaseMediator.addFriendFirebase("nick2@ucsdedu", "nick1@ucsdedu");
        //databaseMediator.addFriendFirebase("nick1@ucsdedu", "nick2@ucsdedu");
        databaseMediator.addFriendFirebase("nick3@ucsdedu", "nick2@ucsdedu");
        assertTrue(true);
    }

    @Test
    public void TestSetSharing(){
        databaseMediator = new DatabaseMediator(mainActivityActivityTestRule.getActivity());
        databaseMediator.setSharing("nick1@ucsdedu", false);
        assertTrue(true);
    }

}
