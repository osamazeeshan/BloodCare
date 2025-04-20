package com.bloodcare.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public abstract class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {
	protected ArrayList<Long> mRowList = null;
	protected Context mContext = null;
	protected String mSearchQuery = null;
	protected int mResourceId = 0;
	private OnItemClickListener mOnItemClickListener;
	public long mLastClickTime = 0;
	private static final long MIN_CLICK_INTERVAL = 1000;
	private Activity mActivity = null;
	protected abstract void updateRowList();
	protected abstract CustomRecyclerAdapter.ViewHolder getNewView(ViewGroup parent, int viewType);
	protected abstract void bindView(CustomRecyclerAdapter.ViewHolder holder, int position);

	public void setSearchQuery(String searchQuery) {
		mSearchQuery = searchQuery;
	}

	public String getSearchQuery() {
		return mSearchQuery;
	}

	public CustomRecyclerAdapter(Context context) {
		//mMainActivity = (MainActivity) fragmentActivity;
		mContext = context;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	@Override
	public int getItemCount() {
		try {
			mRowList = null;
			updateRowList();
			if(mRowList == null) {
				return 0;
			}
			return mRowList.size();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public long getItemId(int position) {
		if(mRowList == null || position < 0 || position >= mRowList.size()) {
			return -1;
		}
		try {
			Long listIndex = mRowList.get(position);
			if(listIndex == null) {
				return -1;
			}
			return listIndex;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public Object getItem(int pos) {
		if(pos <= 0) {
			return null;
		}
		try {
			return 0;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CustomRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		try {
			return getNewView(parent, viewType);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onBindViewHolder(CustomRecyclerAdapter.ViewHolder holder, int position) {
		try {
			bindView(holder, position);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private long mId;

		public ViewHolder(View itemView) {
			super(itemView);

		}

		public void bind(long id) {
			mId = id;
		}
		public void lastClickTime(long time) {
			mLastClickTime = time;
		}
	}

	public static interface OnItemClickListener {
		public void onItemClick(long id);
	}

}
