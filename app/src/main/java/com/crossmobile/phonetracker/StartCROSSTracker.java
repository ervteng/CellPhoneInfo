package com.crossmobile.phonetracker;

/**
 * Created by ervin on 8/1/14.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


// Start CROSSTracker on Boot
public class StartCROSSTracker extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, GPSTracker.class);
            i.putExtra("IP", context.getString(R.string.defaultWifiAddress));
            i.putExtra("IP_mobile", context.getString(R.string.defaultWifiAddress));
            i.putExtra("DebugOn", false);
            i.putExtra("updateInterval",Integer.valueOf(context.getString(R.string.defaultInterval)));
            i.putExtra("DynamicOn", true);
            context.startService(i);
        }
    }
}