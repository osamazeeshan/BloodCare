package com.bloodcare.utility;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

import com.bloodcare.R;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.UserMapFragment;
import com.bloodcare.fragment.drawer.AboutFragment;
import com.bloodcare.fragment.drawer.ProfileFragment;
import com.bloodcare.fragment.startup.SignInFragment;
import com.bloodcare.fragment.startup.SignUpFragment;


public final class NavigationMap {

	private NavigationMap() {
	}

	public static void setAnimationsForPush(CustomBaseFragment newFragment, CustomBaseFragment previousFragment) {
		try {
			if (previousFragment == null && (newFragment instanceof UserMapFragment)) {
				setAnimationsNone(newFragment, false);
				return;
			}
//			} else if(!(previousFragment instanceof SignUpFragment) && (newFragment instanceof VerifyFragment)) {
//				setAnimationsLeftBottom(newFragment);
//				setAnimationsNone(previousFragment, true);
//				return;
//			} else if(!(previousFragment instanceof StartUpFragment) && !(previousFragment instanceof SettingsFragment) && (newFragment instanceof SignInFragment)) {
//				setAnimationsLeftBottom(newFragment);
//				setAnimationsNone(previousFragment, true);
//				return;
//			} else if((newFragment instanceof StartUpFragment) || (newFragment instanceof SettingsFragment) || (newFragment instanceof AboutFragment)) {
//				setAnimationsLeftBottom(newFragment);
//				setAnimationsNone(previousFragment, true);
//				return;
//			}
			setAnimationsLeftRight(newFragment);
			setAnimationsLeftRight(previousFragment);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void setAnimationsForPop(CustomBaseFragment topFragment, CustomBaseFragment bottomFragment) {
		try {
			if((topFragment instanceof SignInFragment) || (topFragment instanceof ProfileFragment) || (topFragment instanceof AboutFragment)) {
				setAnimationsLeftBottom(topFragment);
				setAnimationsNone(bottomFragment, false);
				return;
			}
//			} else if(!(bottomFragment instanceof SignUpFragment) && (topFragment instanceof VerifyFragment)) {
//				setAnimationsLeftBottom(topFragment);
//				setAnimationsNone(bottomFragment, false);
//				return;
//			} else if(!(bottomFragment instanceof StartUpFragment) && !(bottomFragment instanceof SettingsFragment) && (topFragment instanceof SignInFragment)) {
//				setAnimationsLeftBottom(topFragment);
//				setAnimationsNone(bottomFragment, false);
//				return;
//			}
			setAnimationsLeftRight(topFragment);
			setAnimationsLeftRight(bottomFragment);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void setAnimationsForPopHome(CustomBaseFragment topFragment, CustomBaseFragment bottomFragment) {
		try {
			if((topFragment instanceof SignInFragment) || (topFragment instanceof SignInFragment) || (topFragment instanceof SignUpFragment)) {
				setAnimationsLeftBottom(topFragment);
				setAnimationsNone(bottomFragment, false);
				return;
			}
			setAnimationsLeftRight(topFragment);
			setAnimationsLeftRight(bottomFragment);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void setAnimationsLeftRight(CustomBaseFragment fragment) {
		if(fragment == null) {
			return;
		}
		try {
			fragment.setEnterAnimationId(R.anim.slide_in_right);
			fragment.setExitAnimationId(R.anim.slide_out_left);
			fragment.setPopEnterAnimationId(R.anim.slide_in_left);
			fragment.setPopExitAnimationId(R.anim.slide_out_right);
			fragment.setMakeScreenShot(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void setAnimationsNone(CustomBaseFragment fragment, boolean makeScreenShot) {
		if(fragment == null) {
			return;
		}
		try {
			fragment.setEnterAnimationId(R.anim.slide_none);
			fragment.setExitAnimationId(R.anim.slide_none);
			fragment.setPopEnterAnimationId(R.anim.slide_none);
			fragment.setPopExitAnimationId(R.anim.slide_none);
			fragment.setMakeScreenShot(makeScreenShot);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void setAnimationsLeftBottom(CustomBaseFragment fragment) {
		if(fragment == null) {
			return;
		}
		try {
			fragment.setEnterAnimationId(R.anim.slide_in_down);
			fragment.setExitAnimationId(R.anim.slide_out_left);
			fragment.setPopEnterAnimationId(R.anim.slide_in_left);
			fragment.setPopExitAnimationId(R.anim.slide_out_down);
			fragment.setMakeScreenShot(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static CustomBaseFragment getBackStackFragmentFromTop(FragmentManager fragmentManager, int index) {
		if(fragmentManager == null) {
			return null;
		}
		try {
			if(fragmentManager.getBackStackEntryCount() == 0) {
				if(index == 0) {
					Fragment fragment = fragmentManager.findFragmentById(R.id.main_activity_container);
					if(fragment instanceof CustomBaseFragment)
						return (CustomBaseFragment) fragment;
				}
				return null;
			}

			if(index >= fragmentManager.getBackStackEntryCount()) {
				return null;
			}

			FragmentManager.BackStackEntry backStackEntry=fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1-index);
			String tagName=backStackEntry.getName();
			Fragment fragment=fragmentManager.findFragmentByTag(tagName);
			if(fragment instanceof CustomBaseFragment)
				return (CustomBaseFragment) fragment;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static final Interpolator DECELERATE_QUINT = new DecelerateInterpolator(2.5f);
	static final Interpolator DECELERATE_CUBIC = new DecelerateInterpolator(1.5f);

	private static Animation makeOpenCloseAnimation(Context context, float startScale, float endScale, float startAlpha, float endAlpha) {
		AnimationSet set = new AnimationSet(false);
		ScaleAnimation scale = new ScaleAnimation(startScale, endScale, startScale, endScale, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
		scale.setInterpolator(DECELERATE_QUINT);
		scale.setDuration(CommonUtil.getIntegerResource(context, android.R.integer.config_mediumAnimTime));
		set.addAnimation(scale);
		AlphaAnimation alpha = new AlphaAnimation(startAlpha, endAlpha);
		alpha.setInterpolator(DECELERATE_CUBIC);
		alpha.setDuration(CommonUtil.getIntegerResource(context, android.R.integer.config_mediumAnimTime));
		set.addAnimation(alpha);
		return set;
	}

	private static Animation makeFadeAnimation(Context context, float start, float end) {
		AlphaAnimation anim = new AlphaAnimation(start, end);
		anim.setInterpolator(DECELERATE_CUBIC);
		anim.setDuration(CommonUtil.getIntegerResource(context, android.R.integer.config_mediumAnimTime));
		return anim;
	}

	public static Animation loadAnimation(Context context, int transit, boolean enter) {
		switch (transit) {
			case FragmentTransaction.TRANSIT_FRAGMENT_OPEN: {
				if(enter) {
					return makeOpenCloseAnimation(context, 1.125f, 1.0f, 0, 1);
				} else {
					return makeOpenCloseAnimation(context, 1.0f, .975f, 1, 0);
				}
			}
			case FragmentTransaction.TRANSIT_FRAGMENT_CLOSE: {
				if(enter) {
					return makeOpenCloseAnimation(context, .975f, 1.0f, 0, 1);
				} else {
					return makeOpenCloseAnimation(context, 1.0f, 1.075f, 1, 0);
				}
			}
			case FragmentTransaction.TRANSIT_FRAGMENT_FADE: {
				if(enter) {
					return makeFadeAnimation(context, 0, 1);
				} else {
					return makeFadeAnimation(context, 1, 0);
				}
			}
		}
		return null;
	}

}
