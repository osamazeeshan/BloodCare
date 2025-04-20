package com.bloodcare.fragment.history;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.adapter.DonorAdapter;
import com.bloodcare.adapter.RequestHistoryAdapter;
import com.bloodcare.dao.RequestDao;
import com.bloodcare.dao.UserDao;
import com.bloodcare.firestore.BaseFireStore;
import com.bloodcare.firestore.RequestFireStore;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;
import com.bloodcare.utility.ReachabilityTest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by osamazeeshan on 14/12/2018.
 */

public class RequestHistoryFragment extends CustomBaseFragment {

    RequestHistoryAdapter mRequestHistoryAdapter = null;
    private TextView mNoBloodReqText;

    public RequestHistoryFragment() {
    //    setTitleStringId(R.string.sign_in_title);
        setResourceId(R.layout.fragment_request_history);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {
            final RecyclerView donorRecyclerView = rootView.findViewById(R.id.request_history_recycler_view);
            setBloodReqVisibility(false);
            mNoBloodReqText = rootView.findViewById(R.id.no_blood_req_text);

            UserDao userDao = new UserDao(getContext());
            UserDao.SingleUser singleUser = userDao.getRowById(null);

            if(!ReachabilityTest.CheckInternet(getContext())) {
                CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_information), getString(R.string.error_no_internet), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                return;
            }

            int dismissDialogCounter = 0;
            final ArrayList<RequestInfo> arrayList = new ArrayList<>();
            RequestFireStore.getRequestHistory(singleUser.userId, new BaseFireStore.FireStoreCallback() {
                @Override
                public void done(Exception fireBaseException, Object object) {
                    if(object != null) {
                        Task<QuerySnapshot> task = (Task<QuerySnapshot>) object;
                        if(task.getResult().size() > 0) {
                            for (final QueryDocumentSnapshot querySnapshot : task.getResult()) {
                                BaseFireStore.getMapSnapShot(RequestDao.COLLECTION_NAME, querySnapshot.getId(), new BaseFireStore.FireStoreCallback() {
                                    @Override
                                    public void done(Exception fireBaseException, Object object) {
                                        Bitmap requestMapSnapShot = (Bitmap) object;

                                        RequestInfo requestInfo = new RequestInfo();
                                        //retrieve data from history table firebase
                                        requestInfo.setRequesterId(querySnapshot.getId());
                                        requestInfo.setUserId(querySnapshot.getString(RequestDao.COLUMN_USER_ID));
                                        requestInfo.setRequesterName(querySnapshot.getString(RequestDao.COLUMN_USER_NAME));
                                        requestInfo.setRequesterBloodType(querySnapshot.getString(RequestDao.COLUMN_USER_BLOOD_TYPE));
                                        requestInfo.setRequesterAcceptedArray((List<String>) querySnapshot.get(RequestDao.COLUMN_ACCEPT_REQUESTS));
                                        String status = CommonUtil.getRequestStatus(querySnapshot.getLong(RequestDao.COLUMN_REQUEST_COMPLETED));
                                        if (!CommonUtil.isNullOrEmpty(status)) {
                                            requestInfo.setRequesterStatus(String.valueOf(status));
                                        }
                                        String date = CommonUtil.getDate(Long.parseLong(querySnapshot.getString(RequestDao.COLUMN_CREATION_TIMESTAMP)));
                                        requestInfo.setRequesterDateTime(date);
                                        if (requestMapSnapShot != null) {
                                            requestInfo.setRequesterMapSnapShot(requestMapSnapShot);
                                        }
                                        arrayList.add(requestInfo);
                                        if (mRequestHistoryAdapter != null) {
                                            mRequestHistoryAdapter.notifyDataSetChanged();
                                        }

                                        CommonUtil.showProgressDialog(getActivity(), null, false);
                                    }
                                });
                            }
                        } else {
                            CommonUtil.showProgressDialog(getActivity(), null, false);
                            setBloodReqVisibility(true);
                            //CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_information), getString(R.string.dialog_message_no_request_history), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                        }

                        mRequestHistoryAdapter = new RequestHistoryAdapter(arrayList, getActivity());

                        donorRecyclerView.setHasFixedSize(true);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        donorRecyclerView.setLayoutManager(linearLayoutManager);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                        donorRecyclerView.setAdapter(mRequestHistoryAdapter);
                    } else {
                        CommonUtil.showProgressDialog(getActivity(), null, false);
                        CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_information), fireBaseException.getMessage(), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                    }
                }
            });
            CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_fetching_request_history), true);

        } catch (Exception e) {
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();
        }
    }

    private void setBloodReqVisibility(boolean visible) {
        if(mNoBloodReqText != null) {
            if(visible) {
                mNoBloodReqText.setVisibility(View.VISIBLE);
            } else {
                mNoBloodReqText.setVisibility(View.GONE);
            }
        }
    }
}
