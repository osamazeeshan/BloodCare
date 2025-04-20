package com.bloodcare.fragment.history;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.adapter.DonateHistoryAdapter;
import com.bloodcare.adapter.RequestHistoryAdapter;
import com.bloodcare.dao.RequestDao;
import com.bloodcare.dao.UserDao;
import com.bloodcare.firestore.BaseFireStore;
import com.bloodcare.firestore.DonateFireStore;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.donor.DonorInfo;
import com.bloodcare.utility.CommonUtil;
import com.bloodcare.utility.ReachabilityTest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Created by osamazeeshan on 15/12/2018.
 */

public class DonationHistoryFragment extends CustomBaseFragment {

    DonateHistoryAdapter mDonateHistoryAdapter = null;
    private TextView mNoBloodDonateText;

    public DonationHistoryFragment() {
        //    setTitleStringId(R.string.sign_in_title);
        setResourceId(R.layout.fragment_donate_history);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {
            final RecyclerView donateRecyclerView = rootView.findViewById(R.id.donate_history_recycler_view);
            mNoBloodDonateText = rootView.findViewById(R.id.no_blood_donate_text);
            setBloodDonateVisibility(false);

            UserDao userDao = new UserDao(getContext());
            UserDao.SingleUser singleUser = userDao.getRowById(null);

            if(!ReachabilityTest.CheckInternet(getContext())) {
                CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_information), getString(R.string.error_no_internet), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                return;
            }

            final ArrayList<DonateInfo> arrayList = new ArrayList<>();
            DonateFireStore.getDonateHistory(singleUser.userId, new BaseFireStore.FireStoreCallback() {
                @Override
                public void done(Exception fireBaseException, Object object) {
                    if(object != null) {
                        Task<QuerySnapshot> task = (Task<QuerySnapshot>) object;
                        if(task.getResult().size() > 0) {
                            for (final QueryDocumentSnapshot querySnapshot : task.getResult()) {
                                BaseFireStore.getMapSnapShot(DonateFireStore.COLLECTION_NAME, querySnapshot.getId(), new BaseFireStore.FireStoreCallback() {
                                    @Override
                                    public void done(Exception fireBaseException, Object object) {
                                        Bitmap donateMapSnapShot = (Bitmap) object;

                                        DonateInfo donateInfo = new DonateInfo();
                                        donateInfo.setDonateToName(querySnapshot.getString(DonateFireStore.COLUMN_REQUESTER_NAME));
                                        donateInfo.setDonateBloodType(querySnapshot.getString(DonateFireStore.COLUMN_USER_BLOOD_TYPE));
                                        String status = CommonUtil.getDonateStatus(querySnapshot.getLong(DonateFireStore.COLUMN_DONATE_COMPLETED));
                                        if (!CommonUtil.isNullOrEmpty(status)) {
                                            donateInfo.setDonateStatus(String.valueOf(status));
                                        }
                                        String date = CommonUtil.getDate(Long.parseLong(querySnapshot.getString(DonateFireStore.COLUMN_CREATION_TIMESTAMP)));
                                        donateInfo.setDonateDateTime(date);
                                        donateInfo.setDonateDistance(querySnapshot.getString(DonateFireStore.COLUMN_DISTANCE));

                                        if (donateMapSnapShot != null) {
                                            donateInfo.setRequesterMapSnapShot(donateMapSnapShot);
                                        }

                                        arrayList.add(donateInfo);
                                        if (mDonateHistoryAdapter != null) {
                                            mDonateHistoryAdapter.notifyDataSetChanged();
                                        }

                                        CommonUtil.showProgressDialog(getActivity(), null, false);
                                    }
                                });
                            }
                        } else {
                            CommonUtil.showProgressDialog(getActivity(), null, false);
                            setBloodDonateVisibility(true);
//                            CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_information), getString(R.string.dialog_message_no_donate_history), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                        }

                        mDonateHistoryAdapter = new DonateHistoryAdapter(arrayList, getContext());

                        donateRecyclerView.setHasFixedSize(true);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        donateRecyclerView.setLayoutManager(linearLayoutManager);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                        donateRecyclerView.setAdapter(mDonateHistoryAdapter);
                    } else {
                        CommonUtil.showProgressDialog(getActivity(), null, false);
                        CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_information), fireBaseException.getMessage(), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                    }
                }
            });
            CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_fetching_donate_history), true);

        } catch (Exception e) {
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();
        }
    }

    private void setBloodDonateVisibility(boolean visible) {
        if(mNoBloodDonateText != null) {
            if(visible) {
                mNoBloodDonateText.setVisibility(View.VISIBLE);
            } else {
                mNoBloodDonateText.setVisibility(View.GONE);
            }
        }
    }
}
