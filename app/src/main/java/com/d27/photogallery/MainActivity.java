package com.d27.photogallery;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context){
        return new Intent(context, MainActivity.class);
    }

    @Override
    Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
