package com.bloodcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.fragment.history.DonateInfo;
import com.bloodcare.utility.CommonUtil;

import java.util.ArrayList;

/**
 * Created by osamazeeshan on 26/01/2019.
 */

public class PendingRequestAdapter extends CustomRecyclerAdapter {

    final ArrayList<DonateInfo> mArrayList;

    public PendingRequestAdapter(ArrayList<DonateInfo> arrayList, Context context) {
        super(context);
        mArrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    protected void updateRowList() {

    }

    @Override
    protected ViewHolder getNewView(ViewGroup parent, int viewType) {
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_pending_request, parent, false);
            PendingRequestAdapter.PendingRequestViewHolder pendingRequestViewHolder = new PendingRequestAdapter.PendingRequestViewHolder(view);

            return pendingRequestViewHolder;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void bindView(ViewHolder holder, final int position) {
        try {
            final PendingRequestViewHolder pendingRequestViewHolder = (PendingRequestViewHolder) holder;
            pendingRequestViewHolder.pendingDonorName.setText(mArrayList.get(position).getDonorName());

            pendingRequestViewHolder.pendingDonorNoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mArrayList.get(position).setDonateStatus("3");
                    pendingRequestViewHolder.pendingDonorNoBtn.setBackgroundResource(R.color.colorNoBtnOn);
                    pendingRequestViewHolder.pendingDonorYesBtn.setBackgroundResource(R.color.colorYesBtnOff);
                }
            });

            pendingRequestViewHolder.pendingDonorYesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mArrayList.get(position).setDonateStatus("2");
                    pendingRequestViewHolder.pendingDonorYesBtn.setBackgroundResource(R.color.colorYesBtnOn);
                    pendingRequestViewHolder.pendingDonorNoBtn.setBackgroundResource(R.color.colorNoBtnOff);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class PendingRequestViewHolder extends CustomRecyclerAdapter.ViewHolder{

        TextView pendingDonorName;
        Button pendingDonorYesBtn;
        Button pendingDonorNoBtn;

        public PendingRequestViewHolder(View itemView) {
            super(itemView);
            pendingDonorName = itemView.findViewById(R.id.pending_req_donor_name);
            pendingDonorYesBtn = itemView.findViewById(R.id.pending_req_donor_yes_btn);
            pendingDonorNoBtn = itemView.findViewById(R.id.pending_req_donor_no_btn);
        }

    }
}
