package com.example.anesa.test;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

/**
 * Created by Loso on 2015-11-05.
 */
public class sida3 extends FragmentActivity {
    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sida3);
        viewpager = (ViewPager) findViewById(R.id.pager);
        Swiper padapter = new Swiper(getSupportFragmentManager());
        viewpager.setAdapter(padapter);
    }
}

