package com.d27.photogallery;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context){
        return new Intent(context, PhotoGalleryActivity.class);
    }
    @Override
    Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}