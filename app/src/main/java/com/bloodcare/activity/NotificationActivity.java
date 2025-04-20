package com.bloodcare.activity;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bloodcare.R;
import com.bloodcare.dao.RequestDao;
import com.bloodcare.dao.UserDao;
import com.bloodcare.firestore.BaseFireStore;
import com.bloodcare.firestore.RequestFireStore;
import com.bloodcare.firestore.UserFireStore;
import com.bloodcare.fragment.UserMapFragment;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;
import com.bloodcare.utility.SharedPreferenceHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by osamazeeshan on 31/08/2018.
 */

public class NotificationActivity extends AppCompatActivity {

    private String mRequestId = null;
    private String mRequesterId = "";
    private MediaPlayer mPlayNotificationSound = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        TextView requesterNameText = findViewById(R.id.requester_name_text);
        TextView requesterBloodTypeText = findViewById(R.id.requester_blood_type_text);

        Bundle bundle = getIntent().getExtras();
        boolean playSound = false;
        String bloodType = "";
        String requesterName = "";
        String timeStamp = "";

        if(bundle != null) {
            mRequestId = bundle.getString(CommonConstants.REQUEST_ID_KEY);
            mRequesterId = bundle.getString(CommonConstants.REQUESTER_ID_KEY);
            bloodType = bundle.getString(CommonConstants.REQUESTER_BLOOD_TYPE_KEY);
            requesterName = bundle.getString(CommonConstants.REQUESTER_NAME_KEY);
            timeStamp = bundle.getString(CommonConstants.REQUEST_TIME_STAMP);

            playSound = bundle.getBoolean("playSound");
        }

        checkRequestStatus();

        if(!CommonUtil.isNullOrEmpty(timeStamp)) {
            long curTimeStamp = System.currentTimeMillis();
            long notificationExpiry = (curTimeStamp - Long.parseLong(timeStamp)) / 1000;
            if (notificationExpiry > 48) {
                if (mPlayNotificationSound != null) {
                    mPlayNotificationSound.stop();
                }
                finish();
            }
        }

        if(requesterNameText != null) {
            requesterNameText.setText(requesterName);
        }

        if(requesterBloodTypeText != null) {
            String text = String.format(getString(R.string.requesting_blood_type), bloodType);
            requesterBloodTypeText.setText(text);
        }

        Log.d("playSound", String.valueOf(playSound));
        if(playSound) {
            mPlayNotificationSound = MediaPlayer.create(getApplicationContext(), R.raw.blood_notification);
            mPlayNotificationSound.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
//            mPlayNotificationSound.prepareAsync();

            //mPlayNotificationSound.start();
        }

        UserMapFragment userMapFragment = new UserMapFragment();
        //userMapFragment.changeMapScreen(CommonConstants.DONOR_ACCEPT_SCREEN);

        UserDao userDao = new UserDao(getApplicationContext());
        final UserDao.SingleUser singleUser = userDao.getRowById(null);
        Button acceptBtn = findViewById(R.id.accept_btn);
        if(acceptBtn != null) {
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    RequestFireStore.getCompleteRequestStatus(mRequestId, new BaseFireStore.FireStoreCallback() {
                        @Override
                        public void done(Exception fireBaseException, Object object) {
                            if(object != null) {
                                try {
                                    Task<DocumentSnapshot> task = (Task<DocumentSnapshot>) object;
                                    if(task.getResult().getLong(RequestDao.COLUMN_REQUEST_COMPLETED) == CommonConstants.USER_REQUEST_CANCELED) {
                                        if (mPlayNotificationSound != null) {
                                            mPlayNotificationSound.stop();
                                        }
                                        Toast.makeText(getApplicationContext(), getString(R.string.dialog_message_request_canceled), Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        RequestFireStore.updateRequest(mRequestId, "tokenId", singleUser.userId);
                                        if(mPlayNotificationSound != null) {
                                            mPlayNotificationSound.stop();
                                        }
                                        UserFireStore.updateDonorStatus(singleUser.userId, false);  // only one request per time
                                        SharedPreferenceHelper.setKeyValue(getApplicationContext(), CommonConstants.REQUEST_ID, mRequestId);
                                        SharedPreferenceHelper.setKeyValue(getApplicationContext(), CommonConstants.DONOR_ACCEPT_REQUESTER_ID, mRequesterId);
                                        //set mode to donor_screen in map_fragment which shows location between donor and requester

                                        finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {

                            }
                        }
                    });
                }
            });
        }

        Button rejectBtn = findViewById(R.id.reject_btn);
        if(rejectBtn != null) {
            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mPlayNotificationSound != null) {
                        mPlayNotificationSound.stop();
                    }
                    finish();
                }
            });
        }

        if(playSound) {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mPlayNotificationSound != null) {
                        mPlayNotificationSound.stop();
                    }
                    finish();
                }
            }, 30000, 1);
        }
    }

    private void checkRequestStatus() {
        try {
            RequestFireStore.getCompleteRequestStatus(mRequestId, new BaseFireStore.FireStoreCallback() {
                @Override
                public void done(Exception fireBaseException, Object object) {
                    if(object != null) {
                        try {
                            Task<DocumentSnapshot> task = (Task<DocumentSnapshot>) object;
                            if(task.getResult().getLong(RequestDao.COLUMN_REQUEST_COMPLETED) == CommonConstants.USER_REQUEST_CANCELED) {
                                if (mPlayNotificationSound != null) {
                                    mPlayNotificationSound.stop();
                                }
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
