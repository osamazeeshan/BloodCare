package com.bloodcare.fragment.history;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bloodcare.R;
import com.bloodcare.adapter.PendingRequestAdapter;
import com.bloodcare.adapter.RequestHistoryAdapter;
import com.bloodcare.dao.RequestDao;
import com.bloodcare.firestore.BaseFireStore;
import com.bloodcare.firestore.DonateFireStore;
import com.bloodcare.firestore.RequestFireStore;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.startup.SignUpFragment;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by osamazeeshan on 26/01/2019.
 */

public class PendingRequestFragment extends CustomBaseFragment {

    PendingRequestAdapter mPendingRequestAdapter = null;

    public PendingRequestFragment() {
        setResourceId(R.layout.fragment_pending_request);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {

            final RecyclerView pendingReqRecyclerView = rootView.findViewById(R.id.pending_request_recycler_view);

            final String userId = getArguments().getString(RequestDao.COLUMN_USER_ID);
            final String requestId = getArguments().getString("reqId");
            final ArrayList<String> acceptedDonorsList = getArguments().getStringArrayList(RequestDao.COLUMN_ACCEPT_REQUESTS);
            if(userId == null || acceptedDonorsList == null) {
                return;
            }

            final ArrayList<DonateInfo> arrayList = new ArrayList<>();
            for(String acceptedDonor : acceptedDonorsList) {
                DonateFireStore.getAcceptedDonors(userId, acceptedDonor, requestId, new BaseFireStore.FireStoreCallback() {
                    @Override
                    public void done(Exception fireBaseException, Object object) {
                        Task<QuerySnapshot> task = (Task<QuerySnapshot>) object;
                        if(task == null) {
                            return;
                        }
                        if(task.getResult().size() > 0) {
                            for (final QueryDocumentSnapshot querySnapshot : task.getResult()) {
                                DonateInfo donateInfo = new DonateInfo();
                                long status = (long) querySnapshot.get(DonateFireStore.COLUMN_DONATE_COMPLETED);
                                if(status == 1) { // to only show pending status
                                    donateInfo.setDonateId(querySnapshot.getId());
                                    donateInfo.setDonorName(querySnapshot.getString(DonateFireStore.COLUMN_DONOR_NAME));
                                    donateInfo.setDonateBloodType(querySnapshot.getString(DonateFireStore.COLUMN_USER_BLOOD_TYPE));
                                    donateInfo.setDonateStatus(String.valueOf(status));

                                    if(!arrayList.contains(donateInfo)) {
                                        arrayList.add(donateInfo);
                                        if (mPendingRequestAdapter != null) {
                                            mPendingRequestAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                            CommonUtil.showProgressDialog(getActivity(), null, false);

                        } else {
                            CommonUtil.showProgressDialog(getActivity(), null, false);
                        }
                    }
                });

                mPendingRequestAdapter = new PendingRequestAdapter(arrayList, getActivity());

                pendingReqRecyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                pendingReqRecyclerView.setLayoutManager(linearLayoutManager);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                pendingReqRecyclerView.setAdapter(mPendingRequestAdapter);
            }
            CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_fetching_donors), true);

            Button pendingReqCompleteBtn = rootView.findViewById(R.id.pending_req_complete_btn);
            if(pendingReqCompleteBtn != null) {
                pendingReqCompleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!CommonUtil.isPendingStatus(arrayList)) {
                            CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_completing_request), true);

                            if(CommonUtil.isBloodReceived(arrayList)) {
                                RequestFireStore.updateCompleteRequest(requestId, CommonConstants.USER_REQUEST_RECEIVED_BLOOD);
                            } else {
                                RequestFireStore.updateCompleteRequest(requestId, CommonConstants.USER_REQUEST_DO_NOT_RECEIVED_BLOOD);
                            }

                            for (DonateInfo donateInfo : arrayList) {
                                DonateFireStore.updateDonateRequest(donateInfo.getDonateId(), Long.parseLong(donateInfo.getDonateStatus()));
                            }
                            CommonUtil.showProgressDialog(getActivity(), null, false);
                            PendingRequestFragment.this.dismissFragment(false, null);
                        } else {
                            CommonUtil.showProgressDialog(getActivity(), null, false);
                            CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_information), getString(R.string.dialog_message_select_donor), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                        }
                    }
                });
            }

        } catch (Exception e) {
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();
        }
    }
}
