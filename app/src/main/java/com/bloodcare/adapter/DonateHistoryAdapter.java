package com.bloodcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.fragment.history.DonateInfo;

import java.util.ArrayList;

/**
 * Created by osamazeeshan on 23/12/2018.
 */

public class DonateHistoryAdapter extends CustomRecyclerAdapter {

    ArrayList<DonateInfo> mArrayList = null;
    Context mContext = null;

    public DonateHistoryAdapter(ArrayList<DonateInfo> arrayList, Context context) {
        super(context);
        mArrayList = arrayList;
        mContext = context;
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
            View view = layoutInflater.inflate(R.layout.item_donate_history_list, parent, false);
            DonateHistoryViewHolder donateHistoryViewHolder = new DonateHistoryViewHolder(view);

            return donateHistoryViewHolder;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void bindView(ViewHolder holder, int position) {
        try {
            final DonateHistoryViewHolder donateHistoryViewHolder = (DonateHistoryViewHolder) holder;
            donateHistoryViewHolder.donateToName.setText(mArrayList.get(position).geDonateToName());
            donateHistoryViewHolder.donateBloodType.setText(mArrayList.get(position).getDonateBloodType());
            donateHistoryViewHolder.donateStatus.setText(mArrayList.get(position).getDonateStatus());
            donateHistoryViewHolder.donateDateTime.setText(mArrayList.get(position).getDonateDateTime());
            donateHistoryViewHolder.donateDistance.setText(mArrayList.get(position).getDonateDistance());
            if(mArrayList.get(position).getDonateMapSnapShot() != null) {
                donateHistoryViewHolder.donateMapSnapShot.setImageBitmap(mArrayList.get(position).getDonateMapSnapShot());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class DonateHistoryViewHolder extends CustomRecyclerAdapter.ViewHolder{

        TextView donateToName;
        TextView donateBloodType;
        TextView donateStatus;
        TextView donateDateTime;
        TextView donateDistance;
        ImageView donateMapSnapShot;

        public DonateHistoryViewHolder(View itemView) {
            super(itemView);
            donateToName = itemView.findViewById(R.id.donate_to_name);
            donateBloodType = itemView.findViewById(R.id.donate_blood_type);
            donateStatus = itemView.findViewById(R.id.donate_status);
            donateDateTime = itemView.findViewById(R.id.donate_date_time);
            donateDistance = itemView.findViewById(R.id.donate_distance);
            donateMapSnapShot = itemView.findViewById(R.id.donate_map_snap_shot);
        }

    }
}
