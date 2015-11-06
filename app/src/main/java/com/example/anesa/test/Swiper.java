package com.example.anesa.test;

/**
 * Created by Loso on 2015-11-05.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class Swiper extends FragmentPagerAdapter {

    public Swiper(FragmentManager fm) {  //konstruktor
        super(fm); //superklass

    }

    @Override
    public Fragment getItem(int sidor) { //hämtar sidor

        switch (sidor) {
            case 0:

                return new SwipeOne(); // kör SwipeOne klassen...
            case 1:
                return new SwipeTwo();
            case 2:
                return new SwipeThree();

            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {

        return 3; // returnerar antalet sidor som ska gå att swipa
    }


}
