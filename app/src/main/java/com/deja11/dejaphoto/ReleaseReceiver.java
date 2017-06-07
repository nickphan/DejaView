package com.deja11.dejaphoto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

/**
 * Created by Carl on 6/4/2017.
 */
public class ReleaseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Photo released", Toast.LENGTH_SHORT).show();

        // reset the alarm by cancelling then rescheduling
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPIntent = PendingIntent.getBroadcast(context,
                Controller.ALARM_PENDING_INTENT_RC, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        mAlarmManager.cancel(alarmPIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + SetWallpaperService.interval, alarmPIntent);
        } else {
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + SetWallpaperService.interval,
                    SetWallpaperService.interval, alarmPIntent);
        }

        // create an intent to call the SetWallpaperServices with the appropriate code
        Intent releaseButtonIntent = new Intent(context, SetWallpaperService.class);
        releaseButtonIntent.putExtra(Controller.CODE_KEY, Controller.CODE_RELEASE);
        context.startService(releaseButtonIntent);
    }
}
