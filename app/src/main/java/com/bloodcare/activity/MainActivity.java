package com.bloodcare.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bloodcare.R;
import com.bloodcare.dao.UserDao;
import com.bloodcare.firestore.BaseFireStore;
import com.bloodcare.firestore.UserFireStore;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.UserMapFragment;
import com.bloodcare.fragment.history.HistoryFragment;
import com.bloodcare.fragment.setting.HelpFragment;
import com.bloodcare.fragment.startup.SignInFragment;
import com.bloodcare.fragment.startup.SignUpFragment;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;
import com.bloodcare.utility.CustomSQLiteOpenHelper;
import com.bloodcare.utility.NavigationMap;
import com.bloodcare.utility.SharedPreferenceHelper;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.bloodcare.utility.LogTags.TAG_GPS_STATUS;
import static com.bloodcare.utility.LogTags.TAG_LOG_OUT;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public String TAG = "UserAuthenticate";
    private FirebaseAuth mAuth;

    private View mActivityRootView = null;
    private Toolbar mToolbar = null;
    private DrawerLayout mDrawerLayout;
    private TextView mUsrName;
    private SwitchCompat mNavSwitcher;

    private UserMapFragment mUserMapFragment = null;
    private SignInFragment mSignInFragment = null;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String PERMISSION_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;

    private  FirebaseFirestore mFirebaseFirestore;

    String mHTTPCall = "https://us-central1-blood-care-dce74.cloudfunctions.net/";

    String mUID = null;

    @Override
    protected void onResume() {
//        Intent intent = new Intent(this, NotificationActivity.class);
//        startActivity(intent);
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1969) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if(mUserMapFragment != null) {
                    mUserMapFragment.onActivityResult(requestCode, resultCode, data);
                    Log.d(TAG_GPS_STATUS, "OKAY");
                }
                //isGPSOn = true;
                //updatedLocationFromGPS();
            }
        }
        if(requestCode == 64206) {
            if(mSignInFragment != null) {
                mSignInFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivityRootView = findViewById(R.id.main_activity_container);

        mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);


        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        navSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//                if(checked) {
//                    Toast.makeText(getApplicationContext(), "You registered as a Blood Donor!", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "You are unregistered as a Blood Donor!", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_switch);
        View actionView = MenuItemCompat.getActionView(menuItem);

        mNavSwitcher = actionView.findViewById(R.id.switcher);
        mNavSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final UserDao userDao = new UserDao(getApplicationContext());
                    final UserDao.SingleUser singleUser = userDao.getRowById(null);

                    CommonUtil.showProgressDialog(MainActivity.this, getString(R.string.dialog_message_registered_as_a_donor), true);
                    UserFireStore.updateUserDonorStatus(singleUser.userId, mNavSwitcher.isChecked(), new BaseFireStore.FireStoreCallback() {
                        @Override
                        public void done(Exception fireBaseException, Object object) {
                            if(fireBaseException == null) {
                                userDao.updateUserDonorStatus(singleUser.userId, mNavSwitcher.isChecked());
                                if(mNavSwitcher.isChecked()) {
                                    Toast.makeText(getApplicationContext(), "You are registered as a Blood Donor!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "You are unregistered as a Blood Donor!", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Error occurred while updating user record!", Toast.LENGTH_LONG).show();
                            }
                            CommonUtil.showProgressDialog(MainActivity.this, null, false);
                        }
                    });

                } catch (Exception e) {
                    CommonUtil.showProgressDialog(MainActivity.this, null, false);
                    e.printStackTrace();
                }
            }
        });

//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
//                super.onDrawerClosed(drawerView);
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
//                super.onDrawerOpened(drawerView);
//            }
//        };
//
//        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
////        actionBarDrawerToggle.syncState();
////
////
//        ActionBar actionbar = getSupportActionBar();
////
//        assert actionbar != null;
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
////
//        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
//        actionbar.setIcon(R.drawable.ic_menu);


        // screen always on
        Window window = getWindow();
        if(window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        mAuth = FirebaseAuth.getInstance();
        mSignInFragment = new SignInFragment();
//        mSignInFragment.setFacebookPermission();
//        Button retrieveBtn = findViewById(R.id.retrieve_data_btn);
//        final EditText editField = findViewById(R.id.edit_field);

        if(!SharedPreferenceHelper.isKeyExists(getApplicationContext(), CommonConstants.APP_FIREBASE_USER_AUTHENTICATE)) {
//            UserDao userDao = new UserDao(getApplicationContext());
//            UserDao.SingleUser singleUser = userDao.getRowById(null);
//            UserFireStore.updateDeviceToken(singleUser.userId, "null");

            UserFireStore.logoutUser();
        }

        mFirebaseFirestore = FirebaseFirestore.getInstance();
//        String updatedToken = FirebaseInstanceId.getInstance().getToken();

        // store info in shared preferences about logIn user
        if(FirebaseAuth.getInstance().getCurrentUser() == null) { //when reninstall the app user is logedin why?? BUG
            CustomBaseFragment.presentFragment(MainActivity.this, getSupportFragmentManager(), mSignInFragment, true, getString(R.string.SignInFragmentTag), true, false, null);
        } else {
            mUserMapFragment = new UserMapFragment(); //not called in case of signIn and signUp handle that scenario!
            CustomBaseFragment.presentFragment(MainActivity.this, getSupportFragmentManager(), mUserMapFragment, true, getString(R.string.UserMapFragmentTag), true, false, null);

        }

        updateDrawerItems();
        requestPermission();
    }

    public void updateDrawerItems() {
        try {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    timer.cancel();
                    UserDao userDao = new UserDao(getApplicationContext());
                    final UserDao.SingleUser singleUser = userDao.getRowById(null);
                    if(singleUser == null) {
                        return;
                    }
                    UserFireStore.getUserPhoto(singleUser.userId, singleUser.name, new BaseFireStore.FireStoreCallback() {
                        @Override
                        public void done(Exception fireBaseException, Object object) {
                            Bitmap userBitmap = null;
                            if(object != null) {
                                userBitmap = (Bitmap) object;
                            }
                            final Bitmap finalBitmap = userBitmap;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateNavDrawerItems(singleUser, finalBitmap);
                                }
                            });
                        }
                    });

                }
            },1000,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openNavigationDrawer() {
        if(mDrawerLayout == null) {
            return;
        }
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_FINE_LOCATION)) {
                Toast.makeText(this,
                        "Location permission are required", Toast.LENGTH_LONG).show();
            }
            if(shouldShowRequestPermissionRationale(PERMISSION_CALL_PHONE)) {
                Toast.makeText(this,
                        "Phone call permission are required", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{PERMISSION_COARSE_LOCATION, PERMISSION_FINE_LOCATION, PERMISSION_CALL_PHONE, PERMISSION_READ_STORAGE, PERMISSION_WRITE_STORAGE}, PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            CustomBaseFragment customBaseFragment = NavigationMap.getBackStackFragmentFromTop(getSupportFragmentManager(), 0);
            if(customBaseFragment == null) {
                super.onBackPressed();
                return;
            }
            if(customBaseFragment.onBackPressed()) {
                return;
            }

            super.onBackPressed();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isKeyboardVisible() {
        if(mActivityRootView == null) {
            return false;
        }
        try {
            Rect rect = new Rect(); //r will be populated with the coordinates of your view that area still visible.
            mActivityRootView.getWindowVisibleDisplayFrame(rect);
            int heightDiff = mActivityRootView.getRootView().getHeight() - (rect.bottom - rect.top);
            if(heightDiff > 100) {
                //if more than 100 pixels, its probably a keyboard...
                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void refreshToolbar(boolean show) {
        try {
            if (show) {
                mToolbar.setVisibility(View.VISIBLE);
            } else {
                mToolbar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNavDrawerItems(UserDao.SingleUser singleUser, Bitmap userBitmap) {
        if(singleUser == null) {
            return;
        }
        try {
            mUsrName = findViewById(R.id.user_name_text_view);
            ImageView drawerUserImageView = findViewById(R.id.drawer_user_image_view);

            mUsrName.setText(singleUser.name);
            if(userBitmap != null) {
                Bitmap displayBitmap = CommonUtil.scaleCenterCrop(userBitmap, 400, 400, true);
                drawerUserImageView.setImageBitmap(displayBitmap);
            }
            mNavSwitcher.setChecked(singleUser.isDonor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserFragment(UserMapFragment userFragment) {
        mUserMapFragment = userFragment;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            CustomBaseFragment.presentFragment(MainActivity.this, getSupportFragmentManager(), new SignUpFragment(), false, getString(R.string.SignUpFragmentTag), false, true, null);
        } else if (id == R.id.nav_history) {
            CustomBaseFragment.presentFragment(MainActivity.this, getSupportFragmentManager(), new HistoryFragment(), false, getString(R.string.HistoryFragmentTag), false, true, null);
        } else if (id == R.id.nav_blood_donors) {
            mUserMapFragment.changeMapScreen(CommonConstants.REQUEST_HOME_SCREEN);
            mUserMapFragment.setDropDownAdapter(CommonConstants.REQUEST_HOME_SCREEN);
            mUserMapFragment.updateCurrentLocation(12f);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_hospitals) {
            mUserMapFragment.changeMapScreen(CommonConstants.HOSPITAL_MAIN_SCREEN);
            mUserMapFragment.setDropDownAdapter(CommonConstants.HOSPITAL_MAIN_SCREEN);
            mUserMapFragment.updateCurrentLocation(12f);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_doctors) {
            mUserMapFragment.changeMapScreen(CommonConstants.DOCTOR_MAIN_SCREEN);
            mUserMapFragment.setDropDownAdapter(CommonConstants.DOCTOR_MAIN_SCREEN);
           // mUserMapFragment.updateCurrentLocation(12f);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_help) {
            CustomBaseFragment.presentFragment(MainActivity.this, getSupportFragmentManager(), new HelpFragment(), false, getString(R.string.HelpFragmentTag), false, true, null);
        }
        else if (id == R.id.nav_log_out) {
            Log.d(TAG_LOG_OUT, "log_out");
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logoutUser() {
        try {
            // update device token to 'null' so every time user login it will generate new token and save in firestore and local db
            UserDao userDao = new UserDao(getApplicationContext());
            UserDao.SingleUser singleUser = userDao.getRowById(null);
            UserFireStore.updateDeviceToken(singleUser.userId, "null");

            UserFireStore.logoutUser();
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            //userDao.deleteAll();
            CustomSQLiteOpenHelper customSQLiteOpenHelper = CustomSQLiteOpenHelper.getInstance(getApplicationContext());
            customSQLiteOpenHelper.deleteTables();
            CustomBaseFragment.presentFragment(MainActivity.this, getSupportFragmentManager(), new SignInFragment(), true, getString(R.string.SignInFragmentTag), true, false, null);
            CommonUtil.showDialog(this, getString(R.string.dialog_title_information), getString(R.string.sign_out_successful), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

//    public DatabaseReference getReference(String childNode) {
//        return FirebaseDatabase.getInstance().getReference(childNode).push();
//    }


}
