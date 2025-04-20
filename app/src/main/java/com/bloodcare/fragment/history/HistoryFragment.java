package com.bloodcare.fragment.history;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.adapter.CustomPagerAdapter;
import com.bloodcare.fragment.CustomBaseFragment;

/**
 * Created by osamazeeshan on 15/12/2018.
 */

public class HistoryFragment extends CustomBaseFragment {

    public HistoryFragment() {
        //    setTitleStringId(R.string.sign_in_title);
        setResourceId(R.layout.fragment_history);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {
            ImageView backView = rootView.findViewById(R.id.back_arrow);
            ViewPager viewPager = rootView.findViewById(R.id.view_pager);
            viewPager.setAdapter(new CustomPagerAdapter(getChildFragmentManager()));

            // Give the TabLayout the ViewPager
            TabLayout tabLayout = rootView.findViewById(R.id.tab_layout);
            if(tabLayout != null) {
                tabLayout.setupWithViewPager(viewPager);
            }

            if(backView != null) {
                backView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HistoryFragment.this.dismissFragment(true, null);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
