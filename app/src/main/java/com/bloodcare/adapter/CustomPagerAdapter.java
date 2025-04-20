package com.bloodcare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bloodcare.fragment.history.DonationHistoryFragment;
import com.bloodcare.fragment.history.RequestHistoryFragment;

public class CustomPagerAdapter extends FragmentStatePagerAdapter {

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        try {
            switch (position) {
                case 0:
                    return new RequestHistoryFragment();
                case 1:
                    return new DonationHistoryFragment();
                default:
                    return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        try {
            switch (position ) {
                case 0:
                    return "REQUEST HISTORY";
                case 1:
                    return "DONATION HISTORY";
                default:
                    return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}
