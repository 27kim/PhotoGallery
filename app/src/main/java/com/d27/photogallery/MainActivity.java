package com.d27.photogallery;

import androidx.fragment.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

    @Override
    Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
