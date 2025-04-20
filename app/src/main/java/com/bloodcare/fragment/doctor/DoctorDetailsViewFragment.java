package com.bloodcare.fragment.doctor;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.UserMapFragment;
import com.bloodcare.fragment.hospital.HospitalInfo;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;

import static com.bloodcare.utility.CommonConstants.DOCTOR_NEARBY_SCREEN;
import static com.bloodcare.utility.CommonConstants.HOSPITAL_NEARBY_SCREEN;

/**
 * Created by osamazeeshan on 30/12/2018.
 */

public class DoctorDetailsViewFragment extends CustomBaseFragment {

    public DoctorDetailsViewFragment() {
        setTitleStringId(R.string.app_name);
        setResourceId(R.layout.doctor_details_view);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {
            TextView docCancelView = rootView.findViewById(R.id.doc_cancel_view);
            TextView docType = rootView.findViewById(R.id.doc_type);
            TextView docName = rootView.findViewById(R.id.doc_name);
            TextView docDistance = rootView.findViewById(R.id.doc_distance);
            TextView docAddress= rootView.findViewById(R.id.doc_address);
            final ImageView docImg = rootView.findViewById(R.id.doc_img);
            Button docCall = rootView.findViewById(R.id.doc_call);
            Button docMessage = rootView.findViewById(R.id.doc_message);

            final DoctorInfo doctorInfo = (DoctorInfo) getArguments().getSerializable("doctor");
            final int screenMode = getArguments().getInt("screen");
            if(doctorInfo == null) {
                return;
            }

            if(docName != null) {
                docName.setText(doctorInfo.getDocName());
            }
            if(docDistance != null) {
                docDistance.setText(String.format("%s: %s", getString(R.string.distance_text), doctorInfo.getDocDistance()));
            }
            if(docType != null) {
                docType.setText(String.format("%s: %s", getString(R.string.doctor_type_text), doctorInfo.getDocType()));
            }
            if(docAddress != null) {
                docAddress.setText(String.format("%s: %s", getString(R.string.doctor_address_text), doctorInfo.getDocAddress()));
            }

            final UserMapFragment userMapFragment = (UserMapFragment) getParentFragment();
            if (userMapFragment == null) {
                return;
            }


            if(docCancelView != null) {
                docCancelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userMapFragment.changeMapScreen(DOCTOR_NEARBY_SCREEN);
                        // userMapFragment.updateCurrentLocation(12f);
                        userMapFragment.setIsCallOrMessage(false);
                        userMapFragment.removeDoctorDialogFragment(CommonConstants.USE_FRAGMENT_MANAGER_CHILD, true);
                    }
                });
            }

            if(docCall != null) {
                docCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtil.dialNumber(getActivity(), doctorInfo.getDocContact());
                        userMapFragment.setIsCallOrMessage(true);
                    }
                });
            }

            if(docMessage != null) {
                docMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtil.sendMessage(getActivity(), doctorInfo.getDocContact());
                        userMapFragment.setIsCallOrMessage(true);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
