package com.deja11.dejaphoto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by Carl on 6/4/2017.
 *
 * This class is a receiver that waits for the wallpaper alarm to fire.
 * The alarm is for the automatic wallpaper change.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "Scheduled Alarm");

        // for SDKs higher than 19 (KITKAT), we have to reschedule the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent alarmPIntent = PendingIntent.getBroadcast(context,
                    Controller.ALARM_PENDING_INTENT_RC, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + SetWallpaperService.interval, alarmPIntent);
        }

        // create an intent to call the SetWallpaperServices with the appropriate code
        Intent serviceIntent = new Intent(context, SetWallpaperService.class);
        serviceIntent.putExtra(Controller.CODE_KEY, Controller.CODE_NEXT_PHOTO);
        context.startService(serviceIntent);
    }
}
