package com.bloodcare.fragment.donor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.firestore.BaseFireStore;
import com.bloodcare.firestore.UserFireStore;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.UserMapFragment;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import static com.bloodcare.utility.CommonConstants.DONOR_ACCEPT_SCREEN;
import static com.bloodcare.utility.CommonConstants.REQUEST_ACCEPT_DONOR_SCREEN;

/**
 * Created by osamazeeshan on 14/10/2018.
 */

public class CustomDialogBoxFragment extends CustomBaseFragment {

    public CustomDialogBoxFragment() {
        setTitleStringId(R.string.app_name);
        setResourceId(R.layout.custom_dialog_box);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {
            TextView customDialogCancelView = rootView.findViewById(R.id.custom_dialog_cancel_view);
            TextView customDialogName = rootView.findViewById(R.id.custom_dialog_donor_name);
            TextView customDialogBloodType = rootView.findViewById(R.id.custom_dialog_donor_blood_type);
            TextView customDialogDistance = rootView.findViewById(R.id.custom_dialog_donor_distance);
            final ImageView customDialogDonorImg = rootView.findViewById(R.id.custom_dialog_donor_img);
            Button customDialogCall = rootView.findViewById(R.id.custom_dialog_call);
            Button customDialogMessage = rootView.findViewById(R.id.custom_dialog_message);

            final DonorInfo donorInfo = (DonorInfo) getArguments().getSerializable("donor");
            final int screenMode = getArguments().getInt("screen");
            if(donorInfo == null) {
                return;
            }

            if(customDialogName != null) {
                customDialogName.setText(donorInfo.getRequesterName());
            }
            if(customDialogDistance != null) {
                customDialogDistance.setText(String.format("%s: %s", getString(R.string.distance_text), donorInfo.getDonorDistanceFUser()));
            }
            if(customDialogBloodType != null) {
                customDialogBloodType.setText(String.format("%s: %s", getString(R.string.blood_type_text), donorInfo.getDonorBloodType()));
            }

            if(donorInfo.getDonorPhotoBitmap() == null) {
                UserFireStore.getUserPhoto(donorInfo.donorUId, donorInfo.donorName, new BaseFireStore.FireStoreCallback() {
                    @Override
                    public void done(Exception fireBaseException, Object object) {
                        Bitmap usrBitmap = (Bitmap) object;
                        Bitmap displayBitmap = CommonUtil.scaleCenterCrop(usrBitmap, 400, 400, true);
                        customDialogDonorImg.setImageBitmap(displayBitmap);
                        donorInfo.setDonorPhotoBitmap(displayBitmap);
                    }
                });
            } else {
                customDialogDonorImg.setImageBitmap(donorInfo.getDonorPhotoBitmap());
            }

            final UserMapFragment userMapFragment = (UserMapFragment) getParentFragment();
            if (userMapFragment == null) {
                return;
            }

            if(customDialogCancelView != null) {
                customDialogCancelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(screenMode == CommonConstants.DONOR_ACCEPT_SCREEN) {
                            userMapFragment.changeMapScreen(DONOR_ACCEPT_SCREEN);
                        } else {
                            userMapFragment.changeMapScreen(REQUEST_ACCEPT_DONOR_SCREEN);
                        }
                        userMapFragment.setIsCallOrMessage(false);
                        userMapFragment.removeCustomDialogFragment(CommonConstants.USE_FRAGMENT_MANAGER_CHILD, true);
                    }
                });
            }

            if(customDialogCall != null) {
                customDialogCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtil.dialNumber(getActivity(), donorInfo.getDonorContact());
                        userMapFragment.setIsCallOrMessage(true);
                    }
                });
            }

            if(customDialogMessage != null) {
                customDialogMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtil.sendMessage(getActivity(), donorInfo.getDonorContact());
                        userMapFragment.setIsCallOrMessage(true);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
