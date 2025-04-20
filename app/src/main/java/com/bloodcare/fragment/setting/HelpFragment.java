package com.bloodcare.fragment.setting;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bloodcare.R;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.utility.CommonUtil;

/**
 * Created by osamazeeshan on 31/12/2018.
 */

public class HelpFragment extends CustomBaseFragment {
    private WebView mWebView;
    private boolean isProgressShown = false;

    public HelpFragment() {
        setResourceId(R.layout.help_fragment);
        setShowHomeAsUp(false);
        setRefreshOption(true, true);
        setDisplayThreeDotsMenu(false);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {

            mWebView = (WebView) rootView.findViewById(R.id.terms_conditions_webview);

            if(mWebView != null) {
                WebSettings settings = mWebView.getSettings();
                if(settings != null) {
                    settings.setJavaScriptEnabled(true);
                }
                mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

                mWebView.setWebViewClient(new WebViewClient() {

                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        try {
                            if (!isProgressShown) {
                                CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_loading), true);
                                isProgressShown = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onPageFinished(WebView view, String url) {
                        try {
                            if (isProgressShown) {
                                CommonUtil.showProgressDialog(getActivity(), null, false);
                                isProgressShown = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        try {
                            if (errorCode == -2) {
                                CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_error), getString(R.string.error_no_internet), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                            } else {
                                CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_error), description, android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                            }
                            //	mWebView.setBackgroundColor(getContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                mWebView.loadUrl("http://www.blood-care.com/help/");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onRefreshOptionSelected() {
        try {
            if(mWebView != null) {
                mWebView.loadUrl("http://www.blood-care.com/help/");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
