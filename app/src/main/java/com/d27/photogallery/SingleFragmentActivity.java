package com.d27.photogallery;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

abstract public class SingleFragmentActivity extends AppCompatActivity {
    abstract Fragment createFragment();

    @LayoutRes
    public int getLayoutResId(){
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if(fragment ==null){
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, createFragment())
                    .commit();
        }
    }
}
