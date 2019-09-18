package com.d27.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class PollService extends IntentService {
    private static final String TAG = PollService.class.getSimpleName();
    private static final int POLL_INTERVAL = 1000 * 60;
    public static final String RECEIVER_PERMISSION = "com.d27.photogallery.PRIVATE";
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";
    public static String ACTION_SHOW_NOTIFICATION = "com.com.d27.photogallery.SHOW_NOTIFICATION";

    public PollService() {
        super(TAG);
    }


    /**
     * onHandleIntent : background thread 에서 실행 됨
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }
        Log.i(TAG, "onHandleIntent: " + intent);

        String query = QueryPreferences.getStoredQuery(getApplicationContext());
        String lastResultId = QueryPreferences.getLastResultId(getApplicationContext());
        List<GalleryItem> items;
        if (query == null) {
            items = new FlickrFetchr().fetchRecentPhotos();
        } else {
            items = new FlickrFetchr().searchPhotos(query);
        }

        if (items.size() == 0) return;

        String resultId = items.get(0).getId();

        if (resultId == lastResultId) {
            Log.i(TAG, "onHandleIntent: old result");
        } else {
            Log.i(TAG, "onHandleIntent: new result");
            QueryPreferences.setLastResultId(getApplicationContext(), resultId);

            Intent intent1 = MainActivity.newIntent(getApplicationContext());
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent1, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(getString(R.string.new_picture_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(getString(R.string.new_picture_title))
                    .setContentText(getString(R.string.new_picture_text))
                    .setContentIntent(pendingIntent)
                    //클릭 시 자동으로 사라짐
                    .setAutoCancel(true)
                    .build();
            /*
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
            }

            // id값은
            // 정의해야하는 각 알림의 고유한 int값
            notificationManager.notify(1, notification);
            */

            //orderedBroadcast 로 변경
//            sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), RECEIVER_PERMISSION);
            Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
            i.putExtra(REQUEST_CODE, 0);
            i.putExtra(NOTIFICATION, notification);
            sendOrderedBroadcast(i, RECEIVER_PERMISSION, null, null, Activity.RESULT_OK, null, null);
        }

    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = PollService.newIntent(context);
        //PendingIntent.getService : param 으로 담긴 intent를 startService의 호출을 내부적으로 수행함
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
        QueryPreferences.setAlarmOn(context, isOn);
    }

    public static boolean isAlarmOn(Context context) {
        PendingIntent pendingIntent = PendingIntent
                .getService
                        (
                                context
                                , 0
                                , PollService.newIntent(context)
                                , PendingIntent.FLAG_NO_CREATE
                        );

        return pendingIntent != null;
    }
}
