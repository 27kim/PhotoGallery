package com.d27.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.fragment.app.Fragment;

import static com.d27.photogallery.PollService.RECEIVER_PERMISSION;

public class VisibleFragment extends Fragment {
    private static final String TAG = VisibleFragment.class.getSimpleName();

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mOnShowNotification , new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION) , RECEIVER_PERMISSION, null);
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mOnShowNotification);
        super.onStop();
    }

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: VisibleFragment ACTION_SHOW_NOTIFICATION" + intent.getAction());
//            Toast.makeText(context, "got a broadcast : " + intent.getAction(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "onReceive: Canceling notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };
}
