package com.bloodcare.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.bloodcare.R;
import com.bloodcare.activity.MainActivity;
import com.bloodcare.dao.BaseDao;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;
import com.bloodcare.utility.NavigationMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Timer;
import java.util.TimerTask;

public class CustomBaseFragment extends Fragment {
	private int mTitleStringId = 0;
	private String mTitleString = null;
	private String mSubtitleString = null;
	private int mResourceId = 0;
	private boolean mShowHomeAsUp = false;
	private boolean mHideKeyboardOnStop = false;
	private int mEnterAnimationId = 0;
	private int mExitAnimationId = 0;
	private int mPopEnterAnimationId = 0;
	private int mPopExitAnimationId = 0;
	private boolean mMakeScreenShot = false;
	//private boolean mIsChild = false;
	private boolean mDisplayOptionMenu = true;
	private boolean mDisplayThreeDotsMenu = true;
	private boolean mDisplaySearchOption = false;
	private String mSearchQuery = null;
	private boolean mDisplayDrawerOption = false;
	private boolean mDisplayAddOption = false;
	private boolean mDisplayEditOption = false;
	private boolean mDisplayRetakeOption = false;
	private boolean mDisplayAnalyzeOption = false;
	private boolean mDisplayCaptureOption = false;
	private boolean mDisplayCalibrateSunOption = false;
	protected boolean mEnableCalibrateSunOption = true;
	private boolean mDisplayCalibrateCenterOption = false;
	protected boolean mEnableCalibrateCenterOption = true;
	private boolean mChangeCameraOption = false;
	private boolean mRefreshOption = false;
	private boolean mEnableTermsConditionDrawable = false;
	protected View mRootView = null;
	private ProgressBar mProgressBar = null;
	private int mResourceIdFocus = 0;
	private View mViewFocus = null;
	private int showProgressCount = 0;

	public void setTitleStringId(int titleStringId) {
		mTitleStringId = titleStringId;
	}

	//public void setTitleString(String titleString) {
	//	mTitleString = titleString;
	//}

	public int getProgressBarCount() { return showProgressCount; }

	public void setSubtitleString(String subtitleString) {
		mSubtitleString = subtitleString;
	}

	public void setResourceId(int resourceId) {
		mResourceId = resourceId;
	}

	public void setShowHomeAsUp(boolean showHomeAsUp) {
		mShowHomeAsUp = showHomeAsUp;
	}

	public boolean getHideKeyboardOnStop() {
		return mHideKeyboardOnStop;
	}

	public void setHideKeyboardOnStop(boolean hideKeyboardOnStop) {
		mHideKeyboardOnStop = hideKeyboardOnStop;
	}

	public void setEnterAnimationId(int enterAnimationId) {
		mEnterAnimationId = enterAnimationId;
	}

	public void setExitAnimationId(int exitAnimationId) {
		mExitAnimationId = exitAnimationId;
	}

	public void setPopEnterAnimationId(int popEnterAnimationId) {
		mPopEnterAnimationId = popEnterAnimationId;
	}

	public void setPopExitAnimationId(int popExitAnimationId) {
		mPopExitAnimationId = popExitAnimationId;
	}

	public void setMakeScreenShot(boolean makeScreenShot) {
		mMakeScreenShot = makeScreenShot;
	}

	//public void setIsChild(boolean isChild) {
	//	mIsChild = isChild;
	//}

	public void setDisplayOptionMenu(boolean displayOptionMenu) {
		mDisplayOptionMenu = displayOptionMenu;
	}

	public void setDisplayThreeDotsMenu(boolean displayThreeDotsMenu) {
		mDisplayThreeDotsMenu = displayThreeDotsMenu;
	}

	public void setRefreshOption(boolean refreshOption, boolean enableTermsConditionDrawable) {
		mRefreshOption = refreshOption;
		mEnableTermsConditionDrawable = enableTermsConditionDrawable;
	}

	public CustomBaseFragment() {
		// Required empty public constructor
	}

	public void setFocusWidget(int resourceIdFocus, View viewFocus) {
		mResourceIdFocus = resourceIdFocus;
		mViewFocus = viewFocus;
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
//		if (mRootView!= null) {
//			ViewGroup parent = (ViewGroup) mRootView.getParent();
//			if (parent != null)
//				parent.removeView(mRootView);
//		}
		View rootView = inflater.inflate(mResourceId, container, false);
		mRootView = rootView;
		try {
//			View rootView = inflater.inflate(mResourceId, container, false);
//			mRootView = rootView;
			if(mRootView != null) {
				//commented because of crash on s3 @ android 4.3, even on genymotion emulator
				//if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				//	rootView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
				//}
				mRootView.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View view, MotionEvent event) {
						return !(view instanceof DrawerLayout);
					}
				});

//				mProgressBar = (ProgressBar) rootView.findViewById(R.id.main_progress_bar);
//				if(mProgressBar != null) {
//					CommonUtil.setRoundedBackground(getActivity(), mProgressBar, CommonConstants.COLOR_BLACK_STRING, CommonConstants.VIEW_CORNER_RADIUS, null, 0);
//				}
			}

			onCreateFragmentView(mRootView, container, savedInstanceState);

//			EasyTracker easyTracker = EasyTracker.getInstance(getActivity().getApplication());
//			if(easyTracker != null) {
//				String fragmentTag = getTag();
//				if(!CommonUtil.isNullOrEmpty(fragmentTag)) {
//					easyTracker.set(Fields.SCREEN_NAME, fragmentTag);
//					easyTracker.send(MapBuilder.createAppView().build());
//				}
//			}

		} catch(Exception e) {
			e.printStackTrace();
		}

		return mRootView;
	}

	public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
	}

	public boolean onBackPressed() {
		try {
			if(!onBackButtonPress()) {
				NavigationMap.setAnimationsForPop(NavigationMap.getBackStackFragmentFromTop(getActivity().getSupportFragmentManager(), 0), NavigationMap.getBackStackFragmentFromTop(getActivity().getSupportFragmentManager(), 1));
				return false;
			} else {
				return true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean onBackButtonPress() {
		return false;
	}

//	public BaseDao getDao() {
//		return null;
//	}

//	public CustomBaseAdapter getAdapter() {
//		return null;
//	}

//	public boolean onQueryText(String query, @SuppressWarnings("UnusedParameters") boolean isSubmit) {
//		try {
//			CustomBaseAdapter customBaseAdapter = getAdapter();
//			if(customBaseAdapter == null) {
//				return true;
//			}
//			customBaseAdapter.setSearchQuery(query);
//			customBaseAdapter.notifyDataSetChanged();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		return true;
//	}

	public boolean onDrawerOptionSelected() {
		return true;
	}

	public boolean onAddOptionSelected() {
		return true;
	}

	public boolean onAnalyzeOptionSelected(boolean contextMenuCall) {
		return true;
	}

	public boolean onCaptureOptionSelected() {
		return true;
	}

	public boolean onCalibrateSunOptionSelected() {
		return true;
	}

	public boolean onCalibrateCenterOptionSelected() {
		return true;
	}

	public boolean onHomeOptionSelected() {
		return false;
	}

	public boolean onChangeCameraOptionSelected() {
		return true;
	}

	public boolean onRefreshOptionSelected() {
		return true;
	}

	/*public void dispatchTouchEventUp() {
		invalidateFocusWidget();
	}*/

//	public void onCloseContextOptionSelected() {
//		try {
//			BaseDao baseDao = getDao();
//			CustomBaseAdapter customBaseAdapter = getAdapter();
//			if(baseDao == null || customBaseAdapter == null) {
//				return;
//			}
//			baseDao.updateSelection(-1, false);
//			customBaseAdapter.notifyDataSetChanged();
//			MainActivity mainActivity = (MainActivity) getActivity();
//			if(mainActivity == null) {
//				return;
//			}
//			mainActivity.setActionMode(null);
//			mainActivity.showContextualActionBar(false);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public boolean onEditOptionSelected() {
//		try {
//			BaseDao baseDao = getDao();
//			CustomBaseAdapter customBaseAdapter = getAdapter();
//			if(baseDao == null || customBaseAdapter == null) {
//				return true;
//			}
//			long selectionCount = baseDao.getSelectionCount();
//			if(selectionCount < 1 || selectionCount > 1) {
//				CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_error), getString(R.string.select_only_one), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
//				return true;
//			}
//			if(!editSelected(baseDao)) {
//				return true;
//			}
//			baseDao.updateSelection(-1, false);
//			customBaseAdapter.notifyDataSetChanged();
//			MainActivity mainActivity = (MainActivity) getActivity();
//			if(mainActivity == null) {
//				return true;
//			}
//			mainActivity.showContextualActionBar(false);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		return true;
//	}

	public boolean onRetakeOptionSelected() {
		return true;
	}

	public boolean editSelected(BaseDao baseDao) {
		return true;
	}

	public boolean onDeleteOptionSelected() {
		return true;
	}

//	public boolean isActionModeActive() {
//		try {
//			MainActivity mainActivity = (MainActivity) getActivity();
//			//if(mainActivity == null) {
//			//	return false;
//			//}
//			return (mainActivity != null && mainActivity.isActionModeActive());
//			//if(!mainActivity.isActionModeActive()) {
//			//	return false;
//			//}
//			//return true;
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}

	@Override
	public void onStart() {
		super.onStart();

		try {
			onStartFragment();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected void onStartFragment() {
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			onResumeFragment();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected void onResumeFragment() {
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			onPauseFragment();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected void onPauseFragment() {
	}

	@Override
	public void onStop() {
		super.onStop();

		try {
			onStopFragment();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected void onStopFragment() {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			onDestroyFragment();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected void onDestroyFragment() {
	}

	@Override
	public void onDestroyView() {
		try {
			if(mMakeScreenShot) {
//				makeScreenShotNow(null);
			}
			onDestroyFragmentView();
		} catch(Exception e) {
			e.printStackTrace();
		}
		super.onDestroyView();
	}

	protected void onDestroyFragmentView() {
	}

//	public void makeScreenShotNow(Bitmap bitmap) {
//		try {
//			FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.main_activity_container);
//			FrameLayout childFrameLayout = (FrameLayout) getActivity().findViewById(R.id.child_fragment_container);
//			ImageView imageView = (ImageView) getActivity().findViewById(R.id.main_activity_background_image_view);
//			if(frameLayout == null) {
//				return;
//			}
//			if(bitmap == null) {
//				bitmap = loadBitmapFromView(frameLayout);
//				if(bitmap == null) {
//					return;
//				}
//			}
//			BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
//			if(imageView != null) {
//				imageView.setImageDrawable(bitmapDrawable);
//			} else {
//				CommonUtil.setViewBackground(frameLayout, bitmapDrawable);
//			}
//			if(childFrameLayout != null) {
//				CommonUtil.setViewBackground(childFrameLayout, bitmapDrawable);
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public void resetScreenShotNow() {
//		try {
//			FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.main_activity_container);
//			FrameLayout childFrameLayout = (FrameLayout) getActivity().findViewById(R.id.child_fragment_container);
//			ImageView imageView = (ImageView) getActivity().findViewById(R.id.main_activity_background_image_view);
//			if(imageView != null) {
//				imageView.setImageDrawable(null);
//			}
//			if(frameLayout != null) {
//				CommonUtil.setViewBackground(frameLayout, null);
//			}
//			if(childFrameLayout != null) {
//				CommonUtil.setViewBackground(childFrameLayout, null);
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
		AnimationSet animSet = new AnimationSet(true);
		try {
			Animation animation = null;
			if(transit > 0) {
				//return super.onCreateAnimation(transit, enter, nextAnim);
				animation = NavigationMap.loadAnimation(getActivity().getApplicationContext(), transit, enter);
			} else {
				if(nextAnim == R.anim.enter) {
					animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), mEnterAnimationId);
				} else if(nextAnim == R.anim.exit) {
					animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), mExitAnimationId);
				} else if(nextAnim == R.anim.pop_enter) {
					animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), mPopEnterAnimationId);
				} else if(nextAnim == R.anim.pop_exit) {
					animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), mPopExitAnimationId);
				}
			}
			if(animation != null) {
				final int currentAnim = nextAnim;
				animation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						if(currentAnim == R.anim.enter && mEnterAnimationId == R.anim.slide_in_down) {
							FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.main_activity_container);
							CommonUtil.setViewBackground(frameLayout, null);
						}
						if(currentAnim == R.anim.enter || currentAnim == R.anim.pop_exit) {
							mRootView.setDrawingCacheEnabled(false);
//							ifresetScreenShotNow();
						}
					}
				});

				animSet.addAnimation(animation);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return animSet;
	}

	public void presentChildFragment(Fragment fragment, String tag, boolean replace, Bundle args) {
		try {
			presentChildFragment(getActivity().getSupportFragmentManager(), getChildFragmentManager(), fragment, false, tag, replace, args);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void presentChildFragment(FragmentManager fragmentManager, FragmentManager childFragmentManager, Fragment fragment, boolean allowStateLoss, String tag, boolean replace, Bundle args) {
		if(fragment == null || fragmentManager == null || childFragmentManager == null)
			return;
		try {
			CustomBaseFragment.hideKeyboard(fragmentManager);
			//((CustomBaseFragment) fragment).setIsChild(true);
			FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			if(args != null) {
				fragment.setArguments(args);
			}
			if(replace) {
				fragmentTransaction.replace(R.id.child_fragment_container, fragment, tag);
			} else {
				fragmentTransaction.add(R.id.child_fragment_container, fragment, tag);
			}
			if(allowStateLoss) {
				fragmentTransaction.commitAllowingStateLoss();
			} else {
				fragmentTransaction.commit();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void dismissChildFragment(Fragment fragment, int useChildManager, boolean allowStateLoss, boolean animate) {
		try {
			FragmentManager childFragmentManager = null;
			if(useChildManager == CommonConstants.USE_FRAGMENT_MANAGER_SUPPORT) {
				childFragmentManager = getActivity().getSupportFragmentManager();
			} else if(useChildManager == CommonConstants.USE_FRAGMENT_MANAGER_PARENTS_CHILD) {
				childFragmentManager = getParentFragment().getChildFragmentManager();
			} else if(useChildManager == CommonConstants.USE_FRAGMENT_MANAGER_CHILD ){
				childFragmentManager = getChildFragmentManager();
			}
			dismissChildFragment(getActivity().getSupportFragmentManager(), childFragmentManager, fragment, allowStateLoss, animate);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void dismissChildFragment(FragmentManager fragmentManager, FragmentManager childFragmentManager, Fragment fragment, boolean allowStateLoss, boolean animate) {
		if(fragmentManager == null || childFragmentManager == null || fragment == null)
			return;
		try {
			CustomBaseFragment.hideKeyboard(fragmentManager);
			FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
			if(animate) {
				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
			}
			fragmentTransaction.remove(fragment);
			if(allowStateLoss) {
				fragmentTransaction.commitAllowingStateLoss();
			} else {
				fragmentTransaction.commit();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void presentFragment(Fragment fragment, String tag, boolean replace, boolean addToBackStack, Bundle args) {
		presentFragment((MainActivity) getActivity(), getActivity().getSupportFragmentManager(), fragment, false, tag, replace, addToBackStack, args);
	}

	public static void presentFragment(final MainActivity mainActivity, final FragmentManager fragmentManager, final Fragment fragment, final boolean allowStateLoss, final String tag, final boolean replace, final boolean addToBackStack, final Bundle args) {
		if(mainActivity == null || fragment == null || fragmentManager == null)
			return;
		try {
			CustomBaseFragment.hideKeyboard(fragmentManager);
			if(mainActivity.isKeyboardVisible()) {
				final Timer timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						try {
							timer.cancel();
							mainActivity.runOnUiThread(new Runnable() //run on ui thread
							{
								public void run() {
									try {
										presentFragmentInternal(fragmentManager, fragment, allowStateLoss, tag, replace, addToBackStack, args);
									} catch(Exception e) {
										e.printStackTrace();
									}
								}
							});
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}, CommonUtil.getIntegerResource(mainActivity.getApplicationContext(), R.integer.anim_duration3), CommonUtil.getIntegerResource(mainActivity.getApplicationContext(), R.integer.anim_duration3));
			} else {
				presentFragmentInternal(fragmentManager, fragment, allowStateLoss, tag, replace, addToBackStack, args);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void presentFragmentInternal(final FragmentManager fragmentManager, final Fragment fragment, boolean allowStateLoss, final String tag, final boolean replace, final boolean addToBackStack, final Bundle args) {
		try {
			NavigationMap.setAnimationsForPush((CustomBaseFragment) fragment, NavigationMap.getBackStackFragmentFromTop(fragmentManager, 0));
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
			if(args != null) {
				fragment.setArguments(args);
			}
			if(replace) {
				fragmentTransaction.replace(R.id.main_activity_container, fragment, tag);
			} else {
				fragmentTransaction.add(R.id.main_activity_container, fragment, tag);
			}
			if(addToBackStack) {
				fragmentTransaction.addToBackStack(fragment.getTag());
			}
			if(allowStateLoss) {
				fragmentTransaction.commitAllowingStateLoss();
			} else {
				fragmentTransaction.commit();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void dismissFragment(boolean toHome, String fragmentName) {
		dismissFragment(getActivity().getSupportFragmentManager(), toHome, getString(R.string.UserMapFragmentTag), fragmentName);
	}

	public static void dismissFragment(FragmentManager fragmentManager, boolean toHome, String homeFragmentTag, String fragmentName) {
		if(fragmentManager == null)
			return;
		try {
			CustomBaseFragment.hideKeyboard(fragmentManager);
			if(toHome) {
				NavigationMap.setAnimationsForPopHome(NavigationMap.getBackStackFragmentFromTop(fragmentManager, 0), (CustomBaseFragment)fragmentManager.findFragmentByTag(homeFragmentTag));
				fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			} else {
				if(CommonUtil.isNullOrEmpty(fragmentName)) {
					NavigationMap.setAnimationsForPop(NavigationMap.getBackStackFragmentFromTop(fragmentManager, 0), NavigationMap.getBackStackFragmentFromTop(fragmentManager, 1));
					fragmentManager.popBackStack();
				} else {
					//NavigationMap.setAnimationsForPopHome(NavigationMap.getBackStackFragmentFromTop(fragmentManager, 0), (CustomBaseFragment)fragmentManager.findFragmentByTag(homeFragmentTag));
					fragmentManager.popBackStack();//fragmentName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void hideKeyboard(FragmentManager fragmentManager) {
		if(fragmentManager == null) {
			return;
		}
		try {
			CustomBaseFragment customBaseFragment = NavigationMap.getBackStackFragmentFromTop(fragmentManager, 0);
			if(customBaseFragment == null) {
				return;
			}
			if(customBaseFragment.getHideKeyboardOnStop()) {
				customBaseFragment.hideKeyboard();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void hideKeyboard() {
		try {
			InputMethodManager input = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
			if(input != null) {
				Activity activity = getActivity();
				if(activity != null) {
					View focusView = activity.getCurrentFocus();
					if(focusView != null) {
						input.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
						focusView.clearFocus();
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkGooglePlayServicesAvailability()
	{
		try {
			int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
			if(resultCode != ConnectionResult.SUCCESS)
			{
				if(resultCode == ConnectionResult.SERVICE_MISSING || resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED || resultCode == ConnectionResult.SERVICE_DISABLED) {
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), 1);
					if(dialog != null) {
						dialog.show();
					}
					return false;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static Bitmap loadBitmapFromView(View view) {
		if(view == null) {
			return null;
		}
		try {
			if(view.getWidth() <= 0 || view.getHeight() <= 0) {
				return null;
			}
			Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
			if(bitmap == null) {
				return null;
			}
			Canvas canvas = new Canvas(bitmap);
			view.layout(0, 0, view.getWidth(), view.getHeight());
			view.draw(canvas);
			return bitmap;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	public void invalidateActivityOptionsMenu(MainActivity mainActivity) {
//		if(mainActivity == null) {
//			return;
//		}
//		try {
//			mainActivity.setDisplayOptionMenu(mDisplayOptionMenu);
//			mainActivity.setDisplayThreeDotsMenu(mDisplayThreeDotsMenu);
//			mainActivity.setDisplaySearchOption(mDisplaySearchOption, mSearchQuery);
//			mainActivity.setDisplayDrawerOption(mDisplayDrawerOption);
//			mainActivity.setDisplayAddOption(mDisplayAddOption);
//			//mainActivity.setDisplayEditOption(mDisplayEditOption);
//			mainActivity.setDisplayRetakeOption(mDisplayRetakeOption);
//			mainActivity.setDisplayAnalyzeOption(mDisplayAnalyzeOption);
//			mainActivity.setDisplayCaptureOption(mDisplayCaptureOption);
//			mainActivity.setDisplayCalibrateSunOption(mDisplayCalibrateSunOption, mEnableCalibrateSunOption);
//			mainActivity.setDisplayCalibrateCenterOption(mDisplayCalibrateCenterOption, mEnableCalibrateCenterOption);
//			mainActivity.setChangeCameraOption(mChangeCameraOption);
//			mainActivity.setRefreshOption(mRefreshOption, mEnableTermsConditionDrawable);
//			mainActivity.supportInvalidateOptionsMenu();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}

	public void updateActionBar(MainActivity mainActivity) {
		if(mainActivity == null) {
			return;
		}
		try {
			ActionBar actionBar = mainActivity.getSupportActionBar();
			if(actionBar != null) {
				if(!CommonUtil.isNullOrEmpty(mTitleString)) {
					actionBar.setTitle(mTitleString);
				} else {
					actionBar.setTitle(mTitleStringId);
				}
				actionBar.setSubtitle(mSubtitleString);
				actionBar.setDisplayHomeAsUpEnabled(mShowHomeAsUp);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void showProgressBar(boolean show) {
		if(mProgressBar == null) {
			return;
		}
		try {
			if(show) {
				showProgressCount++;
			} else {
				showProgressCount--;
			}
			if(showProgressCount <= 0) {
				mProgressBar.setVisibility(View.GONE);
			} else {
				mProgressBar.setVisibility(View.VISIBLE);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

//	public boolean shouldPresentStartUpFragment(boolean presentFragment) {
//		try {
//			MainActivity mainActivity = (MainActivity) getActivity();
//			if(mainActivity == null) {
//				CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_error), getString(R.string.error_occurred), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
//				return false;
//			}
//			return mainActivity.shouldPresentStartUpFragment(presentFragment);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}

	public void invalidateFocusWidget() {
		try {
				final Timer timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						try {
							timer.cancel();
							getActivity().runOnUiThread(new Runnable() //run on ui thread
							{
								public void run() {
									try {
										if(mResourceIdFocus <= 0 && mViewFocus == null) {
											//mResourceIdFocus = android.R.id.home;
											return;
										}
										View view = null;
										if(mResourceIdFocus > 0) {
											FragmentActivity fragmentActivity = getActivity();
											if(fragmentActivity == null) {
												return;
											}
											view = fragmentActivity.findViewById(mResourceIdFocus);
										} else {
											view = mViewFocus;
										}
										if(view == null) {
											return;
										}
										view.setFocusable(true);
										view.setFocusableInTouchMode(true);
										view.requestFocus();
										view.requestFocusFromTouch();
										if(view instanceof ListView) {
											//((ListView) view).setSelection(0);
											ListView listView = (ListView) view;
											int index = listView.getFirstVisiblePosition();
											View v = listView.getChildAt(0);
											int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());
											listView.setSelectionFromTop(index, top);
										}
									} catch(Exception e) {
										e.printStackTrace();
									}
								}
							});
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}, CommonUtil.getIntegerResource(getActivity().getApplicationContext(), R.integer.anim_duration3), CommonUtil.getIntegerResource(getActivity().getApplicationContext(), R.integer.anim_duration3));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
