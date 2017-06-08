package com.deja11.dejaphoto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by Carl on 6/7/2017.
 *
 * This class is a receiver that waits for the sync alarm to fire.
 * The alarm is for syncing the local database with the cloud.
 */

public class SyncReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm", "Sync Alarm Fired");

        // for SDKs higher than 19 (KITKAT), we have to reschedule the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent syncIntent = new Intent(context, SyncReceiver.class);
            PendingIntent syncPIntent = PendingIntent.getBroadcast(context,
                    Controller.SYNC_PENDING_INTENT_RC, syncIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + Controller.SYNC_INTERVAL, syncPIntent);

        }

        // create an intent to call the SetWallpaperServices with the appropriate code
        Intent serviceIntent = new Intent(context, SetWallpaperService.class);
        serviceIntent.putExtra(Controller.CODE_KEY, Controller.CODE_SYNC);
        context.startService(serviceIntent);
    }
}
