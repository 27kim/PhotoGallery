package com.d27.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = ThumbnailDownloader.class.getSimpleName();
    private static final int MESSAGE_DOWNLOAD = 0;

    private Handler mRequestHandler;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T>{
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }
    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener){
        mThumbnailDownloadListener = listener;
    }

    public ThumbnailDownloader() {
        super(TAG);
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what ==MESSAGE_DOWNLOAD){
                    T target = (T) msg.obj;
                    Log.i(TAG, "handleMessage: get a request for URL :" + mRequestMap.get(target));
                    handleRequest(target);
                }
                super.handleMessage(msg);

            }
        };
    }

    private void handleRequest(final T target) {
        try{
            final String url = mRequestMap.get(target);
            if(url ==null){
                return;
            }
            byte [] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mRequestMap.get(target) != url){
                        return;
                    }
                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });

        }catch(IOException ioe){
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    public void queueThumbnail(T target, String url){
        Log.i(TAG, "got a URL : " + url);

        if(url ==null){
            mRequestMap.remove(target);
        }else{
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    public void clearQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
}
