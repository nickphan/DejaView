package com.deja11.dejaphoto;

import android.provider.ContactsContract;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by thefr on 5/31/2017.
 */

public class UserTest {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myFirebaseRef = database.getReference();


    @Test
    public void TestInitializeWithDatabaseReference(){
        DatabaseReference databaseReference = myFirebaseRef;

        //User testUser = new User(databaseReference);

        final String username = "Test@gmailcom";
        final String password = "valueOfPassword";

        User testUser = new User();

        testUser.setFromDatabaseReference(databaseReference);

        /*
        Query queryRef = databaseReference.child("Test@gmailcom");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                assertEquals(username, dataSnapshot.getKey());
                assertEquals(password, dataSnapshot.child("password").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/


        //assertEquals(username, databaseReference.getKey());
        assertEquals(username, testUser.getUsername());
        assertEquals(password, testUser.getPassword());
        //assertFalse(testUser.isSharing());

        //assertTrue(testUser.isFriendOf("friend1"));
        //assertTrue(testUser.isFriendOf("friend2"));
        //assertFalse(testUser.isFriendOf("friend3"));
        //assertTrue(testUser.isFriendOf("friend4"));

    }

}
