package com.d27.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {

    private static final String STARTUP_RECEIVER = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(STARTUP_RECEIVER, "onReceive: " + intent.getAction());
    }
}
