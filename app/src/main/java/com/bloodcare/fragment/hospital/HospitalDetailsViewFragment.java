package com.bloodcare.fragment.hospital;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.UserMapFragment;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;

import static com.bloodcare.utility.CommonConstants.HOSPITAL_NEARBY_SCREEN;

/**
 * Created by osamazeeshan on 29/12/2018.
 */

public class HospitalDetailsViewFragment extends CustomBaseFragment {

    public HospitalDetailsViewFragment() {
        setTitleStringId(R.string.app_name);
        setResourceId(R.layout.hospital_details_view);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {
            TextView dialogCancelView = rootView.findViewById(R.id.dialog_cancel_view);
            TextView dialogHosType = rootView.findViewById(R.id.dialog_hos_type);
            TextView dialogName = rootView.findViewById(R.id.dialog_hos_name);
            TextView dialogDistance = rootView.findViewById(R.id.dialog_hos_distance);
            TextView dialogAddress= rootView.findViewById(R.id.dialog_hos_address);
            final ImageView dialogHosImg = rootView.findViewById(R.id.dialog_hos_img);
            Button dialogCall = rootView.findViewById(R.id.dialog_hos_call);
            Button dialogMessage = rootView.findViewById(R.id.dialog_hos_message);

            final HospitalInfo hospitalInfo = (HospitalInfo) getArguments().getSerializable("hospital");
            final int screenMode = getArguments().getInt("screen");
            if(hospitalInfo == null) {
                return;
            }

            if(dialogName != null) {
                dialogName.setText(hospitalInfo.getHospitalName());
            }
            if(dialogDistance != null) {
                dialogDistance.setText(String.format("%s: %s", getString(R.string.distance_text), hospitalInfo.getHospitalDistance()));
            }
            if(dialogHosType != null) {
                dialogHosType.setText(String.format("%s: %s", getString(R.string.hospital_type_text), hospitalInfo.hospitalType));
            }
            if(dialogAddress != null) {
                dialogAddress.setText(String.format("%s: %s", getString(R.string.hospital_address_text), hospitalInfo.getHospitalAddress()));
            }

            final UserMapFragment userMapFragment = (UserMapFragment) getParentFragment();
            if (userMapFragment == null) {
                return;
            }

            if(dialogCancelView != null) {
                dialogCancelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userMapFragment.changeMapScreen(HOSPITAL_NEARBY_SCREEN);
                       // userMapFragment.updateCurrentLocation(12f);
                        userMapFragment.setIsCallOrMessage(false);
                        userMapFragment.removeHospitalDialogFragment(CommonConstants.USE_FRAGMENT_MANAGER_CHILD, true);
                    }
                });
            }

            if(dialogCall != null) {
                dialogCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtil.dialNumber(getActivity(), hospitalInfo.getHospitalContact());
                        userMapFragment.setIsCallOrMessage(true);
                    }
                });
            }

            if(dialogMessage != null) {
                dialogMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtil.sendMessage(getActivity(), hospitalInfo.getHospitalContact());
                        userMapFragment.setIsCallOrMessage(true);
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
