package com.bloodcare.fragment.startup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.activity.MainActivity;
import com.bloodcare.dao.BaseDao;
import com.bloodcare.dao.UserDao;
import com.bloodcare.firestore.BaseFireStore;
import com.bloodcare.firestore.UserFireStore;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.UserMapFragment;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;
import com.bloodcare.utility.ReachabilityTest;
import com.bloodcare.utility.SharedPreferenceHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.iid.FirebaseInstanceId;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.bloodcare.utility.CommonConstants.PASSWORD_MIN_LEN;
import static com.bloodcare.utility.LogTags.TAG_USER_STORE_FAILED;

public class SignUpFragment extends CustomBaseFragment {

    private UserDao mUserDao = null;
    private int PICK_IMAGE_REQUEST = 1998;
    byte[] mFireStoreBytes = null;
    private MainActivity mActivity = null;
    private String mUserId = null;

    private String authUserId = null;
    private String authEmail= null;

    ImageView mSignUpImageView = null;

    public SignUpFragment() {
        setTitleStringId(R.string.sign_up_title);
        setResourceId(R.layout.fragment_sign_up);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {

            final Button createUserBtn = rootView.findViewById(R.id.sign_up_done_button);
            final EditText nameEditText = rootView.findViewById(R.id.sign_up_name_text);
            final EditText emailEditText = rootView.findViewById(R.id.sign_up_email_text);
            final EditText passwordEditText = rootView.findViewById(R.id.sign_up_password_text);
            final EditText retypePasswordEditText = rootView.findViewById(R.id.sign_up_retype_password_text);
            final EditText phoneNumEditText = rootView.findViewById(R.id.sign_up_phone_num_text);
            final EditText ageEditText = rootView.findViewById(R.id.sign_up_age_text);
            final ScrollView containerScrollView = rootView.findViewById(R.id.sign_up_container_scroll_view);
            final Spinner genderSpinner = rootView.findViewById(R.id.sign_up_gender_spinner);
            final Spinner bloodTypeSpinner = rootView.findViewById(R.id.sign_up_blood_type_spinner);
            TextView screenTitle = rootView.findViewById(R.id.screen_title);
            mSignUpImageView = rootView.findViewById(R.id.sign_up_image_view);
            Button userImageButton = rootView.findViewById(R.id.user_image_button);

            mActivity = (MainActivity) getActivity();
            mActivity.refreshToolbar(false);

            if(mUserDao == null) {
                mUserDao = new UserDao(getContext());
            }

            if(getArguments() != null) {
                authUserId = getArguments().getString("userId");
                authEmail = getArguments().getString("email");
            }

            if(!CommonUtil.isNullOrEmpty(authUserId)) {
                emailEditText.setVisibility(View.GONE);
                passwordEditText.setVisibility(View.GONE);
                retypePasswordEditText.setVisibility(View.GONE);
            }

            ArrayAdapter<String> genderAdapter = null;
            if(genderSpinner != null) {
                ArrayList<String> genderList = new ArrayList<>();
                genderList.add(getString(R.string.gender_text));
                genderList.add("Male");
                genderList.add("Female");
                genderList.add("Other");

                //ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, genderList);
                genderAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_items, genderList){
                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(position == 0){
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        }
                        else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };
                genderSpinner.setAdapter(genderAdapter);
            }

            ArrayAdapter<String> bloodAdapter = null;
            if(bloodTypeSpinner != null) {
                ArrayList<String> bloodType = new ArrayList<>();
                bloodType.add(getString(R.string.blood_type_text));
                bloodType.add(CommonConstants.BLOOD_TYPE_A_POS);
                bloodType.add(CommonConstants.BLOOD_TYPE_A_NEG);
                bloodType.add(CommonConstants.BLOOD_TYPE_B_POS);
                bloodType.add(CommonConstants.BLOOD_TYPE_B_NEG);
                bloodType.add(CommonConstants.BLOOD_TYPE_O_POS);
                bloodType.add(CommonConstants.BLOOD_TYPE_O_NEG);
                bloodType.add(CommonConstants.BLOOD_TYPE_AB_POS);
                bloodType.add(CommonConstants.BLOOD_TYPE_AB_NEG);

                //final ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, bloodType);
                bloodAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_items, bloodType){
                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(position == 0){
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        }
                        else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };

                bloodTypeSpinner.setAdapter(bloodAdapter);
            }

            UserDao userDao = new UserDao(getContext());
            final UserDao.SingleUser singleUser = userDao.getRowById(null);
            if(singleUser != null) {
                if(!CommonUtil.isNullOrEmpty(singleUser.userId)) {
                    mUserId = singleUser.userId;
                }
                if(!CommonUtil.isNullOrEmpty(singleUser.name)) {
                    nameEditText.setText(singleUser.name);
                }
                if(!CommonUtil.isNullOrEmpty(singleUser.email)) {
                    emailEditText.setText(singleUser.email);
                }
                if(!CommonUtil.isNullOrEmpty(singleUser.cellNo)) {
                    phoneNumEditText.setText(singleUser.cellNo);
                }
                if(!CommonUtil.isNullOrEmpty(singleUser.age)) {
                    ageEditText.setText(singleUser.age);
                }
                if(!CommonUtil.isNullOrEmpty(singleUser.bloodType)) {
                    bloodTypeSpinner.setSelection(bloodAdapter.getPosition(singleUser.bloodType));
                }
                if(!CommonUtil.isNullOrEmpty(singleUser.bloodType)) {
                    genderSpinner.setSelection(genderAdapter.getPosition(singleUser.gender));
                }
                createUserBtn.setText("Update");
                screenTitle.setText("Profile");
            }

            if(userImageButton != null) {
                userImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            if(createUserBtn != null) {
                createUserBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = null;
                        String email = null;
                        if(!ReachabilityTest.CheckInternet(getContext())) {
                            CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_information), getString(R.string.error_no_internet), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                            return;
                        }

                        final UserDao.SingleUser singleUser = new UserDao.SingleUser();

                        if(nameEditText == null) {
                            return;
                        }
                        String name = CommonUtil.validateInputText(getActivity(), SignUpFragment.this, nameEditText, R.string.name_hint, CommonConstants.NAME_MIN_LEN, false, false);
                        if(CommonUtil.isNullOrEmpty(name)) {
                            return;
                        }
                        singleUser.name = name;

                        if(CommonUtil.isNullOrEmpty(authEmail)) {
                            if (emailEditText == null) {
                                return;
                            }
                            email = CommonUtil.validateInputText(getActivity(), SignUpFragment.this, emailEditText, R.string.email_text, 1, true, false);
                            if (CommonUtil.isNullOrEmpty(email)) {
                                return;
                            }
                        } else {
                            email = authEmail;
                        }
                        singleUser.email = email;

                        if(phoneNumEditText == null) {
                            return;
                        }
                        String cellNum = CommonUtil.validateInputText(getActivity(), SignUpFragment.this, phoneNumEditText, R.string.phone_no_hint, CommonConstants.CELL_PHONE_MIN_LEN, false, true);
                        if(CommonUtil.isNullOrEmpty(cellNum)) {
                            return;
                        }
                        singleUser.cellNo = cellNum;

                        if(ageEditText == null) {
                            return;
                        }
                        String age = CommonUtil.validateInputText(getActivity(), SignUpFragment.this, ageEditText, R.string.age_hint, CommonConstants.AGE_MIN_LEN, false, false);
                        if(CommonUtil.isNullOrEmpty(age)) {
                            return;
                        }
                        singleUser.age = age;

                        if(CommonUtil.isNullOrEmpty(authUserId)) {
                            if (passwordEditText == null) {
                                return;
                            }
                            password = CommonUtil.validateInputText(getActivity(), SignUpFragment.this, passwordEditText, R.string.password_text, PASSWORD_MIN_LEN, false, false);
                            if (CommonUtil.isNullOrEmpty(password)) {
                                return;
                            }

                            if (retypePasswordEditText == null) {
                                return;
                            }
                            String reTypePassword = CommonUtil.validateInputText(getActivity(), SignUpFragment.this, retypePasswordEditText, R.string.retype_password_hint, PASSWORD_MIN_LEN, false, false);
                            if (CommonUtil.isNullOrEmpty(reTypePassword)) {
                                return;
                            }

                            if (!password.equals(reTypePassword)) {
                                String message = String.format(getString(R.string.do_not_match), getString(R.string.password_text).toUpperCase(), getString(R.string.retype_password_hint).toUpperCase());
                                CommonUtil.showDialogAndSetFocus(getActivity(), SignUpFragment.this, retypePasswordEditText, message);
                                return;
                            }
                        }

                        if(genderSpinner == null) {
                            return;
                        }
                        String gender = CommonUtil.validateSpinnerText(getActivity(), SignUpFragment.this, genderSpinner, R.string.gender_text);
                        if(CommonUtil.isNullOrEmpty(gender)) {
                            return;
                        }
                        singleUser.gender = gender;

                        if(bloodTypeSpinner == null) {
                            return;
                        }
                        String bloodType = CommonUtil.validateSpinnerText(getActivity(), SignUpFragment.this, bloodTypeSpinner, R.string.blood_type_text);
                        if(CommonUtil.isNullOrEmpty(bloodType)) {
                            return;
                        }
                        singleUser.bloodType = bloodType;


                        //singleUser.bloodType = "B+";
                        //singleUser.gender = "male";
                        singleUser.isDonor = true;
//                        singleUser.address = "Islamabad";

                        singleUser.creationTimeStamp = String.valueOf(System.currentTimeMillis());
                        singleUser.deviceToken = FirebaseInstanceId.getInstance().getToken();

                        if(CommonUtil.isNullOrEmpty(mUserId) && CommonUtil.isNullOrEmpty(authUserId)) {
                            UserFireStore.signUprWithEmailAndPassword(singleUser.email, password, getActivity(), new BaseFireStore.FireStoreCallback() {
                                @Override
                                public void done(Exception fireBaseException, Object object) {
                                    if (fireBaseException == null) {
                                        singleUser.userId = ((FirebaseUser) object).getUid();
                                        //singleUser.creationTimeStamp = ((FirebaseUser) ((FirebaseUser) object).getProviderData());
                                        if (mFireStoreBytes != null) {
                                            UserFireStore.saveUserPhoto(singleUser.userId, mFireStoreBytes, singleUser.name);
                                        }
                                        createUser(singleUser, BaseDao.SQL_INSERT);

                                    } else {
                                        CommonUtil.showProgressDialog(getActivity(), null, false);
                                        CommonUtil.showDialog(getContext(), getString(R.string.error_occurred), fireBaseException.getMessage(), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                                        Log.d(TAG_USER_STORE_FAILED, getString(R.string.user_create_update_error));
                                    }
                                }
                            });
                            CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_create_user), true);
                        } else if (!CommonUtil.isNullOrEmpty(mUserId)) {
                            singleUser.userId = mUserId;
                            if (mFireStoreBytes != null) {
                                UserFireStore.saveUserPhoto(singleUser.userId, mFireStoreBytes, singleUser.name);
                            }
                            createUser(singleUser, BaseDao.SQL_UPDATE);
                            CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_update_user), true);
                        } else if (!CommonUtil.isNullOrEmpty(authUserId)) {
                            singleUser.userId = authUserId;
                            if (mFireStoreBytes != null) {
                                UserFireStore.saveUserPhoto(singleUser.userId, mFireStoreBytes, singleUser.name);
                            }
                            createUser(singleUser, BaseDao.SQL_INSERT);
                            CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_create_user), true);
                        }

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createUser(final UserDao.SingleUser singleUser, final int operation) {
        try {
            UserFireStore.createUser(getContext(), singleUser, new BaseFireStore.FireStoreCallback() {
                @Override
                public void done(Exception fireBaseException, Object object) {
                    if (fireBaseException == null) {
                        long result = mUserDao.createOrUpdate(singleUser, operation, singleUser.userId);
                        if (result <= -1) {
                            CommonUtil.showDialog(getContext(), getString(R.string.error_occurred), getString(R.string.user_create_update_error), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                            Log.d(TAG_USER_STORE_FAILED, getString(R.string.user_create_update_error));
                        }
                        SharedPreferenceHelper.setKeyValue(getContext(), CommonConstants.APP_FIREBASE_USER_AUTHENTICATE, "1");
                        CommonUtil.showProgressDialog(getActivity(), null, false);
                        mActivity.updateDrawerItems();

                        if(operation == BaseDao.SQL_INSERT) {
                            UserMapFragment userMapFragment = new UserMapFragment();
                            SignUpFragment.this.presentFragment(userMapFragment, getString(R.string.UserMapFragmentTag), true, false, null);
                            mActivity.setUserFragment(userMapFragment);
                        }
                        SignUpFragment.this.dismissFragment(true, null);
//                    dismissFragment(false, getString(R.string.SignUpFragmentTag));

                    } else {
                        CommonUtil.showProgressDialog(getActivity(), null, false);
                        CommonUtil.showDialog(getContext(), getString(R.string.error_occurred), fireBaseException.getMessage(), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                        Log.d(TAG_USER_STORE_FAILED, getString(R.string.user_create_update_error));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackButtonPress() {
        if(mUserId == null) {
            SignUpFragment.this.presentFragment(new SignInFragment(), getString(R.string.SignInFragmentTag), true, false, null);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1998 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            try {
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                Bitmap fireStoreBitmap = CommonUtil.scaleCenterCrop(bitmap, 800, 800, false);
                Bitmap displayBitmap = CommonUtil.scaleCenterCrop(bitmap, 400, 400, true);
                //Log.d(TAG, String.valueOf(bitmap));
                mFireStoreBytes = CommonUtil.getByteFromBitmap(fireStoreBitmap);

                if (mSignUpImageView != null) {
                    mSignUpImageView.setImageBitmap(displayBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
