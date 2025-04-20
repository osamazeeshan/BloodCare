package com.bloodcare.fragment.startup;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bloodcare.R;
import com.bloodcare.activity.MainActivity;
import com.bloodcare.dao.BaseDao;
import com.bloodcare.dao.UserDao;
import com.bloodcare.firestore.BaseFireStore;
import com.bloodcare.firestore.UserFireStore;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.UserMapFragment;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;
import com.bloodcare.utility.SharedPreferenceHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;
import static com.bloodcare.utility.CommonConstants.PASSWORD_MIN_LEN;
import static com.bloodcare.utility.LogTags.TAG_USER_STORE_FAILED;
import static com.facebook.FacebookSdk.getApplicationContext;


public class SignInFragment extends CustomBaseFragment {

    private UserDao mUserDao = null;
    private MainActivity mActivity;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    public SignInFragment() {
        setTitleStringId(R.string.sign_in_title);
        setResourceId(R.layout.fragment_sign_in);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {

            Button signInBtn = rootView.findViewById(R.id.sign_in_btn);
            TextView signUpView = rootView.findViewById(R.id.sign_up_view);
            ImageView signInGmailView = rootView.findViewById(R.id.sign_in_gmail_view);
            final LoginButton signInFacebookView = rootView.findViewById(R.id.sign_in_facebook_view);
            ImageView signInFacebookImage = rootView.findViewById(R.id.sign_in_facebook_view_image);

            final EditText emailEditText = rootView.findViewById(R.id.email_edit_text);
            final EditText passwordEditText = rootView.findViewById(R.id.password_edit_text);
            final TextView forgotYourPassword = rootView.findViewById(R.id.forgot_your_password);
            final ScrollView containerScrollView = (ScrollView) rootView.findViewById(R.id.sign_in_container_scroll_view);

            mActivity = (MainActivity) getActivity();
            mActivity.refreshToolbar(false);

            if(mUserDao == null) {
                mUserDao = new UserDao(getContext());
            }

            if(signInGmailView != null) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.server_client_id))
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
                signInGmailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, CommonConstants.GOOGLE_AUTH_REQ_CODE);
                        CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_verify_user), true);
                    }
                });
            }

            if(forgotYourPassword != null) {
                forgotYourPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SignInFragment.this.presentFragment(new ForgotPasswordFragment(), getString(R.string.ForgotPasswordFragmentTag), true, true, null);
                    }
                });
            }

//            mAuth = FirebaseAuth.getInstance();

            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

            if(signInFacebookImage != null) {
                signInFacebookImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signInFacebookView.performClick();
                    }
                });
            }

            if(signInFacebookView != null) {

                // Get hash for the app to use in Facebook App Authentication
//                try {
//                    PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo("com.bloodcare", PackageManager.GET_SIGNATURES);
//                    for (Signature signature : info.signatures) {
//                        MessageDigest md = MessageDigest.getInstance("SHA");
//                        md.update(signature.toByteArray());
//                        String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
//                        Log.e("MY KEY HASH:", sign);
//                        Toast.makeText(getApplicationContext(),sign, Toast.LENGTH_LONG).show();
//                        emailEditText.setText(sign);
//                    }
//                } catch (PackageManager.NameNotFoundException e) {
//                } catch (NoSuchAlgorithmException e) {
//                }

                mCallbackManager = CallbackManager.Factory.create();
                signInFacebookView.setReadPermissions("email", "public_profile");

                LoginManager.getInstance().registerCallback(mCallbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
//                                Toast.makeText(getContext(), loginResult.getAccessToken().toString(), Toast.LENGTH_LONG).show();
                                handleFacebookAccessToken(loginResult.getAccessToken());
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(getContext(), "CANCELED", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                Toast.makeText(getContext(), exception.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        });
            }

            if (signInBtn != null) {
                signInBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //String userId = mAuth.getCurrentUser().getUid();
                        String email = CommonUtil.validateInputText(getActivity(), SignInFragment.this, emailEditText, R.string.email_text, 1, true, false);
                        if(email == null) {
                            return;
                        }

                        String password = CommonUtil.validateInputText(getActivity(), SignInFragment.this, passwordEditText, R.string.password_text, PASSWORD_MIN_LEN, false, false);
                        if(password == null) {
                            return;
                        }

                        //UserDao.SingleUser singleUser = mUserDao.getRowById(null);
                        UserFireStore.authenticateUser(email, password, getActivity(), new BaseFireStore.FireStoreCallback() {
                            @Override
                            public void done(Exception fireBaseException, Object object) {
                                if(fireBaseException == null) {
                                    String userId = object.toString();
                                    signInUser(userId, null);
                                } else {
                                    CommonUtil.showProgressDialog(getActivity(), null, false);
                                    CommonUtil.showDialog(getContext(), getString(R.string.error_occurred), fireBaseException.getMessage(), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                                    Log.d(TAG_USER_STORE_FAILED, getString(R.string.user_create_update_error));
                                }
                            }
                        });
                        CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_verify_user), true);
                    }
                });
            }

            if(signUpView != null) {
                signUpView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SignInFragment.this.presentFragment(new SignUpFragment(), getString(R.string.SignUpFragmentTag), true, false, null);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void googleSignIn(GoogleSignInAccount account) {
       try {
           UserFireStore.firebaseAuthWithGoogle(account, getActivity(), new BaseFireStore.FireStoreCallback() {
               @Override
               public void done(Exception fireBaseException, Object object) {
                   if(object != null) {
                       FirebaseUser user = (FirebaseUser) object;
                       signInUser(user.getUid(), user.getEmail());
                   } else {
                       CommonUtil.showProgressDialog(getActivity(), null, false);
                       CommonUtil.showDialog(getContext(), getString(R.string.error_occurred), fireBaseException.getMessage(), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                       Log.d(TAG_USER_STORE_FAILED, getString(R.string.user_create_update_error));
                   }
               }
           });
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        try {
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_verify_user), true);
            UserFireStore.firebaseAuthWithFacebook(token, getActivity(), new BaseFireStore.FireStoreCallback() {
                @Override
                public void done(Exception fireBaseException, Object object) {
                    if(object != null) {
                        FirebaseUser user = (FirebaseUser) object;
                        signInUser(user.getUid(), user.getEmail());
                    } else {
                        CommonUtil.showProgressDialog(getActivity(), null, false);
                        CommonUtil.showDialog(getContext(), getString(R.string.error_occurred), fireBaseException.getMessage(), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                        Log.d(TAG_USER_STORE_FAILED, getString(R.string.user_create_update_error));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void signInUser(final String userId, final String email) {
        try {
            final String newToken = FirebaseInstanceId.getInstance().getToken();
            UserFireStore.updateDeviceToken(userId, newToken);

            UserFireStore.getSingleUserData(userId, getActivity(), new BaseFireStore.FireStoreCallback() {
                @Override
                public void done(Exception fireBaseException, Object object) {
                    if (fireBaseException == null) {
                        final UserDao.SingleUser singleUser = (UserDao.SingleUser) object;
                        singleUser.deviceToken = newToken;
                        long result = mUserDao.createOrUpdate(singleUser, BaseDao.SQL_INSERT, null);
                        if (result <= 0) {
                            CommonUtil.showDialog(getContext(), getString(R.string.error_occurred), getString(R.string.user_create_update_error), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                            Log.d(TAG_USER_STORE_FAILED, getString(R.string.user_create_update_error));
                        }
                        mActivity.updateDrawerItems();
                        SharedPreferenceHelper.setKeyValue(getContext(), CommonConstants.APP_FIREBASE_USER_AUTHENTICATE, "1");
                        CommonUtil.showProgressDialog(getActivity(), null, false);

                        UserMapFragment userMapFragment = new UserMapFragment();
                        SignInFragment.this.presentFragment(userMapFragment, getString(R.string.UserMapFragmentTag), true, false, null);
                        mActivity.setUserFragment(userMapFragment);
                    } else if (fireBaseException.getMessage().equals("No such document")) {
                        Bundle bundle = new Bundle();
                        CommonUtil.showProgressDialog(getActivity(), null, false);
                        bundle.putString("userId", userId);
                        bundle.putString("email", email);
                        SignInFragment.this.presentFragment(new SignUpFragment(), getString(R.string.SignUpFragmentTag), true, false, bundle);
                    } else {
                        CommonUtil.showProgressDialog(getActivity(), null, false);
                        CommonUtil.showDialog(getContext(), getString(R.string.error_occurred), fireBaseException.getMessage(), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                        Log.d(TAG_USER_STORE_FAILED, getString(R.string.user_create_update_error));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == CommonConstants.GOOGLE_AUTH_REQ_CODE) {
          Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
          try {
              GoogleSignInAccount account = task.getResult(ApiException.class);
              googleSignIn(account);
          } catch (ApiException e) {
              Log.w(TAG, "Google sign in failed", e);
          }
      }
      if(requestCode == 64206) {
          mCallbackManager.onActivityResult(requestCode, resultCode, data);
      }
    }

}
