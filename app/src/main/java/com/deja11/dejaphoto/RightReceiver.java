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
 * This class is a receiver for the right button (next photo) in the notification bar.
 */
public class RightReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "Right Button (Next Photo)");

        // reset the alarm by cancelling it and then rescheduling
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPIntent = PendingIntent.getBroadcast(context,
                Controller.ALARM_PENDING_INTENT_RC, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        mAlarmManager.cancel(alarmPIntent);

        // reschedule the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + SetWallpaperService.interval, alarmPIntent);
        } else {
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + SetWallpaperService.interval,
                    SetWallpaperService.interval, alarmPIntent);
        }

        // create an intent to call the SetWallpaperServices with the appropriate code
        Intent nextButtonIntent = new Intent(context, SetWallpaperService.class);
        nextButtonIntent.putExtra(Controller.CODE_KEY, Controller.CODE_NEXT_PHOTO);
        context.startService(nextButtonIntent);
    }
}
