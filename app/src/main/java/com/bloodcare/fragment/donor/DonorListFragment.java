package com.bloodcare.fragment.donor;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.adapter.DonorAdapter;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.UserMapFragment;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;

import java.util.ArrayList;

import static com.bloodcare.utility.CommonConstants.REQUEST_ACCEPT_DONOR_SCREEN;

/**
 * Created by osamazeeshan on 02/10/2018.
 */

public class DonorListFragment extends CustomBaseFragment {

    private ImageView mDonorBack;

    public DonorListFragment() {
        setTitleStringId(R.string.app_name);
        setResourceId(R.layout.fragment_donor_list);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {
            mDonorBack = rootView.findViewById(R.id.donor_back_view);

            final ArrayList<DonorInfo> donorInfo = (ArrayList<DonorInfo>) getArguments().getSerializable("donor");

            if(donorInfo == null) {
                return;
            }

            final UserMapFragment userMapFragment = (UserMapFragment) getParentFragment();
            if (userMapFragment == null) {
                return;
            }

            if(mDonorBack != null) {
                mDonorBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userMapFragment.changeMapScreen(REQUEST_ACCEPT_DONOR_SCREEN);
                        userMapFragment.removeDonorListFragment(CommonConstants.USE_FRAGMENT_MANAGER_CHILD, true);
                    }
                });
            }


            RecyclerView donorRecyclerView = rootView.findViewById(R.id.donor_list_recycler_view);

            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("sdas");arrayList.add("qwer");arrayList.add("1234");arrayList.add("ghjkl");

            DonorAdapter donorAdapter = new DonorAdapter(donorInfo, getContext());

            donorRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            donorRecyclerView.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            donorRecyclerView.setAdapter(donorAdapter);


        } catch (Exception e) {

        }
    }

}
