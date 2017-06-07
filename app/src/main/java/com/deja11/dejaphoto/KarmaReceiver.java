package com.deja11.dejaphoto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Carl on 6/4/2017.
 */
public class KarmaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Photo karma'ed", Toast.LENGTH_SHORT).show();

        // create an intent to call the SetWallpaperServices with the appropriate code
        Intent karmaButtonIntent = new Intent(context, SetWallpaperService.class);
        karmaButtonIntent.putExtra(Controller.CODE_KEY, Controller.CODE_KARMA);
        context.startService(karmaButtonIntent);
    }
}
