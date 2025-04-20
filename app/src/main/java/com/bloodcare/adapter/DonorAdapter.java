package com.bloodcare.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloodcare.R;
import com.bloodcare.firestore.BaseFireStore;
import com.bloodcare.firestore.UserFireStore;
import com.bloodcare.fragment.donor.DonorInfo;
import com.bloodcare.utility.CommonUtil;

import java.util.ArrayList;

/**
 * Created by osamazeeshan on 07/10/2018.
 */

public class DonorAdapter extends CustomRecyclerAdapter {

    private Context mContext;
    private ArrayList<DonorInfo> mArrayList;

    public DonorAdapter(ArrayList<DonorInfo> arrayList, Context context) {
        super(context);
        if(arrayList == null) {
            return;
        }
        mArrayList = arrayList;
        mContext = context;
    }


    @Override
    protected void updateRowList() {
    }

    @Override
    protected CustomRecyclerAdapter.ViewHolder getNewView(ViewGroup parent, int viewType) {
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_donor_list, parent, false);
            DonorViewHolder donorViewHolder = new DonorViewHolder(view);

			/*pointViewHolder.monthly.setOnClickListener(new OnSingleClickListener() {
				@Override
				public void onSingleClick(View v) {
					try {
						Bundle args = new Bundle();
						args.putString(ClientDao.COLUMN_CLIENT_ID, mClientId);
						args.putString(ArrayDao.COLUMN_ARRAY_ID, mArrayId);
						String tag = (String) v.getTag();
						args.putString(PointDao.COLUMN_POINT_ID, tag);
						CustomBaseFragment.presentFragment((MainActivity) mFragmentActivity, mFragmentActivity.getSupportFragmentManager(), new ReportMonthlyFragment(), mFragmentActivity.getString(R.string.ReportMonthlyFragmentTag), true, true, args);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});*/
//            donorViewHolder.annualProgressBar.setSubTitleSize((int) CommonUtil.convertDpToPixel(CommonConstants.PROGRESSBAR_SUBTITLE_FONT_SIZE - 2, mContext));
//            donorViewHolder.annualProgressBar.setTitleSize((int) CommonUtil.convertDpToPixel(CommonConstants.PROGRESSBAR_TITLE_FONT_SIZE - 5, mContext));
            return donorViewHolder;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void bindView(CustomRecyclerAdapter.ViewHolder holder, final int position) {
        try {
            final DonorViewHolder donorViewHolder = (DonorViewHolder) holder;

            donorViewHolder.donorName.setText(mArrayList.get(position).donorName);
            String bloodText = String.format("%s: %s", mContext.getString(R.string.blood_type_text), mArrayList.get(position).donorBloodType);
            String distanceText = String.format("%s: %s", mContext.getString(R.string.distance_text), mArrayList.get(position).donorDistanceFUser);
            donorViewHolder.donorBloodType.setText(bloodText);
            donorViewHolder.donorDistance.setText(distanceText);
            donorViewHolder.donorContact = mArrayList.get(position).donorContact;
            String indexText = String.format("%s %d",mContext.getString(R.string.donor_text), (position + 1));
            donorViewHolder.donorIndex.setText(indexText);

            if(mArrayList.get(position).getDonorPhotoBitmap() == null) {
                UserFireStore.getUserPhoto(mArrayList.get(position).donorUId, mArrayList.get(position).donorName, new BaseFireStore.FireStoreCallback() {
                    @Override
                    public void done(Exception fireBaseException, Object object) {
                        Bitmap usrBitmap = (Bitmap) object;
                        Bitmap displayBitmap = CommonUtil.scaleCenterCrop(usrBitmap, 400, 400, true);
                        donorViewHolder.donorImageView.setImageBitmap(displayBitmap);
                        mArrayList.get(position).setDonorPhotoBitmap(displayBitmap);
                    }
                });
            } else {
                donorViewHolder.donorImageView.setImageBitmap(mArrayList.get(position).getDonorPhotoBitmap());
            }

            if(donorViewHolder.donorMessageView != null) {
                donorViewHolder.donorMessageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtil.sendMessage((FragmentActivity) mContext, donorViewHolder.donorContact);
                    }
                });
            }

            if(donorViewHolder.donorCallView != null) {
                donorViewHolder.donorCallView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtil.dialNumber((FragmentActivity) mContext, donorViewHolder.donorContact);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class DonorViewHolder extends CustomRecyclerAdapter.ViewHolder{

        TextView donorIndex;
        TextView donorName;
        TextView donorBloodType;
        TextView donorDistance;
        ImageView donorCallView;
        ImageView donorMessageView;
        ImageView donorImageView;
        String donorContact;

        public DonorViewHolder(View itemView) {
            super(itemView);
            donorIndex = itemView.findViewById(R.id.list_donor_index);
            donorName = itemView.findViewById(R.id.list_donor_name);
            donorBloodType = itemView.findViewById(R.id.list_donor_blood_type);
            donorDistance = itemView.findViewById(R.id.list_donor_distance);
            donorCallView = itemView.findViewById(R.id.list_donor_call);
            donorMessageView = itemView.findViewById(R.id.list_donor_message);
            donorImageView = itemView.findViewById(R.id.list_donor_image_view);

        }

    }
}
