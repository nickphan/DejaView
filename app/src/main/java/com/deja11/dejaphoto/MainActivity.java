package com.deja11.dejaphoto;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MainActivity extends Activity {

    DatabaseHelper myDb;
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);

        //set the icon and time and build the notification of deja photo
        int icon = R.drawable.ic_wallpaper;
        long when = System.currentTimeMillis();
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(icon)
                .setWhen(when)
                .build();
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // get the content view of the notification
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        notification.contentView = contentView;

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound
        mNotificationManager.notify(1, notification);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.notification,null);

        Button karmaButton = (Button)vi.findViewById(R.id.karma);
        String name = karmaButton.getText().toString();
        System.err.println(name);

        karmaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.err.print("we are here");
                Toast.makeText(MainActivity.this, "updated karma points", Toast.LENGTH_SHORT).show();
            }
        });

    }

/*
    public void release(View view){

        Toast.makeText(this, "the photo is released", Toast.LENGTH_SHORT).show();
    }

    public void previous(View view){
        Toast.makeText(this, "switch the previous photo", Toast.LENGTH_SHORT).show();
=======
>>>>>>> 6c64d701a9c03a9eec152f581c456e92489ca959
    }

    public void next(View view){
        Toast.makeText(this, "switch to next photo", Toast.LENGTH_SHORT).show();
    }
    */

}
