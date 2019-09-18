package com.d27.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import static com.d27.photogallery.PollService.NOTIFICATION;
import static com.d27.photogallery.PollService.REQUEST_CODE;

public class NotificationReceiver extends BroadcastReceiver {
    private final String TAG = NotificationReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: " + getResultCode());
        if(getResultCode() != Activity.RESULT_OK){
            return;
        }

        int requestCode = intent.getIntExtra(REQUEST_CODE, 0);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, notification);
    }
}
