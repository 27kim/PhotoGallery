package com.d27.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumnailDownloader<T> extends HandlerThread {
    private static final String TAG = ThumnailDownloader.class.getSimpleName();
    private static final int MESSAGE_DOWNLOAD = 0;

    private Handler mRequestHandler;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();

    public ThumnailDownloader() {
        super(TAG);
    }
    public void queueThumbmail(T target, String url){
        Log.i(TAG, "got a URL : " + url);

        if(url ==null){
            mRequestMap.remove(target);
        }else{
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD){
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL : " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    private void handleRequest(T target) {
        try{
            final String url = mRequestMap.get(target);
            if(url ==null){
                return;
            }
            byte [] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

        }catch(IOException ioe){
            Log.e(TAG, "Error downloading image", ioe);
        }
    }
}
