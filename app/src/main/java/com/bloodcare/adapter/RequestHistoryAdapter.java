package com.bloodcare.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.activity.MainActivity;
import com.bloodcare.dao.RequestDao;
import com.bloodcare.firestore.RequestFireStore;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.UserMapFragment;
import com.bloodcare.fragment.history.PendingRequestFragment;
import com.bloodcare.fragment.history.RequestInfo;
import com.bloodcare.fragment.startup.SignInFragment;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;

import java.util.ArrayList;

import static com.bloodcare.utility.CommonConstants.REQUEST_ACCEPT_CUSTOM_DIALOG_BOX_FRAGMENT;

/**
 * Created by osamazeeshan on 23/12/2018.
 */

public class RequestHistoryAdapter extends CustomRecyclerAdapter {


    ArrayList<RequestInfo> mArrayList = null;
    FragmentActivity mFragmentActivity = null;

    public RequestHistoryAdapter(ArrayList<RequestInfo> arrayList, FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        mArrayList = arrayList;
        mFragmentActivity = fragmentActivity;
    }

    @Override
    protected void updateRowList() {
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    protected ViewHolder getNewView(ViewGroup parent, int viewType) {
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_request_history_list, parent, false);
            RequestHistoryViewHolder requestHistoryViewHolder = new RequestHistoryViewHolder(view);

            return requestHistoryViewHolder;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void bindView(ViewHolder holder, final int position) {
        try {
            final RequestHistoryViewHolder requestHistoryViewHolder = (RequestHistoryViewHolder) holder;
            requestHistoryViewHolder.requesterName.setText(mArrayList.get(position).getRequesterName());
            requestHistoryViewHolder.requesterBloodType.setText(mArrayList.get(position).getRequestBloodType());
            requestHistoryViewHolder.requesterStatus.setText(mArrayList.get(position).getRequestStatus());
            requestHistoryViewHolder.requesterDateTime.setText(mArrayList.get(position).getRequestDateTime());
            if(mArrayList.get(position).getRequestMapSnapShot() != null) {
                requestHistoryViewHolder.requesterMapSnapShot.setImageBitmap(mArrayList.get(position).getRequestMapSnapShot());
            }

            String requestStatus = mArrayList.get(position).getRequestStatus();
            final String requestId = mArrayList.get(position).getRequesterId();
            if(requestStatus.equals("Pending")) {
                requestHistoryViewHolder.requesterStatus.setText(requestStatus);
                requestHistoryViewHolder.requesterStatus.setVisibility(View.GONE);
                requestHistoryViewHolder.requestPendingBtn.setVisibility(View.VISIBLE);
            } else {
                requestHistoryViewHolder.requesterStatus.setText(requestStatus);
                requestHistoryViewHolder.requesterStatus.setVisibility(View.VISIBLE);
                requestHistoryViewHolder.requestPendingBtn.setVisibility(View.GONE);
            }

            requestHistoryViewHolder.requestPendingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    bundle.putString(RequestDao.COLUMN_USER_ID, mArrayList.get(position).getUserId());
                    bundle.putString("reqId", requestId);
                    bundle.putStringArrayList(RequestDao.COLUMN_ACCEPT_REQUESTS, (ArrayList<String>) mArrayList.get(position).getRequesterAcceptedArray());
                    CustomBaseFragment.presentFragment((MainActivity) mFragmentActivity, mFragmentActivity.getSupportFragmentManager(), new PendingRequestFragment(), false, mFragmentActivity.getString(R.string.PendingRequestFragmentTag), true, true, bundle);

//                    String textString = String.format("Have you received the blood?");
//                    CommonUtil.showDialog(mContext, mContext.getString(R.string.dialog_title_information), textString, android.R.drawable.ic_dialog_alert, "Yes", "No", new CommonUtil.AlertDialogOnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if(which == -1) {
//                                RequestFireStore.updateCompleteRequest(requestId, CommonConstants.USER_REQUEST_RECEIVED_BLOOD);
//                                requestHistoryViewHolder.requesterStatus.setText("Blood Received");
//                            } else {
//                                RequestFireStore.updateCompleteRequest(requestId, CommonConstants.USER_REQUEST_DO_NOT_RECEIVED_BLOOD);
//                                requestHistoryViewHolder.requesterStatus.setText("Blood Not Received");
//                            }
//                            requestHistoryViewHolder.requesterStatus.setVisibility(View.VISIBLE);
//                            requestHistoryViewHolder.requestPendingBtn.setVisibility(View.GONE);
//
//                            //updateCurrentLocation(12f);
//                        }
//                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class RequestHistoryViewHolder extends CustomRecyclerAdapter.ViewHolder{

        TextView requesterName;
        TextView requesterBloodType;
        TextView requesterStatus;
        TextView requesterDateTime;
        ImageView requesterMapSnapShot;
        Button requestPendingBtn;

        public RequestHistoryViewHolder(View itemView) {
            super(itemView);
            requesterName = itemView.findViewById(R.id.requester_name);
            requesterBloodType = itemView.findViewById(R.id.requester_blood_type);
            requesterStatus = itemView.findViewById(R.id.requester_status);
            requesterDateTime = itemView.findViewById(R.id.requester_date_time);
            requesterMapSnapShot = itemView.findViewById(R.id.requester_map_snap_shot);
            requestPendingBtn = itemView.findViewById(R.id.request_pending_btn);
        }

    }
}
