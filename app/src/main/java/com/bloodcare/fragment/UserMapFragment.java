package com.bloodcare.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bloodcare.R;
import com.bloodcare.activity.MainActivity;
import com.bloodcare.dao.RequestDao;
import com.bloodcare.dao.UserDao;
import com.bloodcare.firestore.BaseFireStore;
import com.bloodcare.firestore.DoctorFireStore;
import com.bloodcare.firestore.DonateFireStore;
import com.bloodcare.firestore.HospitalFireStore;
import com.bloodcare.firestore.RequestFireStore;
import com.bloodcare.firestore.UserFireStore;
import com.bloodcare.fragment.doctor.DoctorDetailsViewFragment;
import com.bloodcare.fragment.doctor.DoctorInfo;
import com.bloodcare.fragment.donor.CustomDialogBoxFragment;
import com.bloodcare.fragment.donor.DonorInfo;
import com.bloodcare.fragment.donor.DonorListFragment;
import com.bloodcare.fragment.hospital.HospitalDetailsViewFragment;
import com.bloodcare.fragment.hospital.HospitalInfo;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;

import com.bloodcare.utility.ReachabilityTest;
import com.bloodcare.utility.SharedPreferenceHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static com.bloodcare.utility.CommonConstants.DOCTOR_DETAILS_SCREEN;
import static com.bloodcare.utility.CommonConstants.DOCTOR_MAIN_SCREEN;
import static com.bloodcare.utility.CommonConstants.DOCTOR_NEARBY_SCREEN;
import static com.bloodcare.utility.CommonConstants.DONOR_ACCEPT_SCREEN;
import static com.bloodcare.utility.CommonConstants.HOSPITAL_DIALOG_SCREEN;
import static com.bloodcare.utility.CommonConstants.HOSPITAL_MAIN_SCREEN;
import static com.bloodcare.utility.CommonConstants.HOSPITAL_NEARBY_SCREEN;
import static com.bloodcare.utility.CommonConstants.REQUEST_ACCEPT_CUSTOM_DIALOG_BOX_FRAGMENT;
import static com.bloodcare.utility.CommonConstants.REQUEST_ACCEPT_DONOR_LIST_FRAGMENT;
import static com.bloodcare.utility.CommonConstants.REQUEST_ACCEPT_DONOR_SCREEN;
import static com.bloodcare.utility.CommonConstants.REQUEST_BLOOD_CONFIRM_SCREEN;
import static com.bloodcare.utility.CommonConstants.REQUEST_BLOOD_SEARCH_SCREEN;
import static com.bloodcare.utility.CommonConstants.REQUEST_HOME_SCREEN;
import static com.bloodcare.utility.CommonConstants.REQUEST_LOCATION_CONFIRM_SCREEN;
import static com.bloodcare.utility.CommonUtil.getAddressFromLocation;
import static com.bloodcare.utility.CommonUtil.isNullOrEmpty;
import static com.bloodcare.utility.LogTags.TAG_GPS_STATUS;
import static com.bloodcare.utility.LogTags.TAG_QUERY;

public class UserMapFragment extends CustomBaseFragment implements
        OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnMarkerClickListener,
        ResultCallback<LocationSettingsResult> {

    private ListenerRegistration mRealTimeUpdateListener = null;
    private LocationManager mLocationManager = null;
    private LocationRequest mLocationRequest;
    private LatLng mCurrentLocation = null;
    final static int REQUEST_LOCATION = 1969;
    private boolean isGPSOn = false;
    private String mRequestId = null;
    private String mDonateId = null;

    private int mScreenMode = REQUEST_HOME_SCREEN;
    private int mMapCameraStatus;
    private boolean mAnimateCamera = false;
    private boolean mIsSatellite = false;
    private boolean mIsGpsPopUp = false;
    private boolean mIsSearchLocation = false;
    private boolean mIsCallOrMessage = false;
    private boolean mBloodRequestCanceled = false;

    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient = null;
    private LocationCallback mLocationCallback = null;
    protected GoogleApiClient mGoogleApiClient = null;
    private DonorListFragment mDonorListFragment;
    private CustomDialogBoxFragment mCustomDialogBoxFragment;
    private HospitalDetailsViewFragment mHospitalDetailsViewFragment;
    private DoctorDetailsViewFragment mDoctorDetailsViewFragment;

    private List<String> mDonorIds;
    private ArrayList<DonorInfo> mDonorInfoList;
    private ArrayList<Marker> mMarkerList = null;
    ArrayList<GeoPoint> mNearbyDonorsLocation = null;

    private PlaceAutocompleteFragment mPlaceAutoCompleteFragment;
    private RelativeLayout mAddressContainer;
    private RelativeLayout mDonorFoundContainer;
    private TextView mDonorsFoundText;
    private TextView mAddressTextField;
    private ImageView mBackNavButton;
    private Spinner mSearchBloodType;
    private EditText mSearchLocation;
    private Button mRequestBloodBtn;
    private ImageView mConfirmLocationView;
    private Button mSwitchSatelliteMap;
    private ImageView mSearchingDonorView;
    private TextView mRequestCancelTextView;
    private Button mDonorListBtn;
    private RelativeLayout mSearchLocationContainer;
    private GifImageView mFindingDonorsGif;
    private RelativeLayout mSearchBloodTypeContainer;
    private Button mCurrentLocationMap;
    private ImageView mSearchIcon;


    public UserMapFragment() {
        setTitleStringId(R.string.app_name);
        setResourceId(R.layout.fragment_user_map);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {
            mRequestBloodBtn = rootView.findViewById(R.id.request_blood_btn);
            mSearchBloodType = rootView.findViewById(R.id.search_blood_type);
            //mSearchLocation = rootView.findViewById(R.id.search_location);
            mAddressContainer = rootView.findViewById(R.id.address_container);
            mDonorFoundContainer = rootView.findViewById(R.id.donor_found_container);
            mDonorsFoundText = rootView.findViewById(R.id.donors_found_text);
            mAddressTextField = rootView.findViewById(R.id.address_text_field);
            mBackNavButton = rootView.findViewById(R.id.back_button_view);
            mConfirmLocationView = rootView.findViewById(R.id.confirm_location_view);
            mSwitchSatelliteMap = rootView.findViewById(R.id.switch_satellite_map);
            mSearchingDonorView = rootView.findViewById(R.id.searching_donors_view);
            mRequestCancelTextView = rootView.findViewById(R.id.request_cancel_text_view);
            mDonorListBtn = rootView.findViewById(R.id.show_donor_list_button);
            mSearchLocationContainer = rootView.findViewById(R.id.search_location_container);
            mCurrentLocationMap = rootView.findViewById(R.id.current_location_map);
            mSearchBloodTypeContainer = rootView.findViewById(R.id.search_blood_type_container);
            mFindingDonorsGif = rootView.findViewById(R.id.finding_donors_gif);
            mSearchIcon = rootView.findViewById(R.id.search_icon);

            final MainActivity activity = (MainActivity) getActivity();

            assert activity != null;

            activity.refreshToolbar(false);

//        MapFragment mapFragment1 = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);


//        android.support.v4.app.FragmentManager fmanager = activity.getSupportFragmentManager();
//        android.support.v4.app.Fragment fragment = fmanager.findFragmentById(R.id.map);
//        SupportMapFragment supportmapfragment = (SupportMapFragment)fragment;
            //mMap = supportmapfragment.getMap();

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

            if (mapFragment != null) {
                FragmentManager fm = getFragmentManager();
                assert fm != null;
                FragmentTransaction ft = fm.beginTransaction();
                mapFragment = SupportMapFragment.newInstance();
                ft.replace(R.id.map, mapFragment).commit();

                mapFragment.getMapAsync(this);
            }

            mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            initializeGoogleApi();
            //checkForGPSStatus();
//            getUpdatedLocation();
//        if (hasPermission()) {
//            getUpdatedLocation();
//        } else {
//            requestPermission();
//        }

            if(mFindingDonorsGif != null) {
                mFindingDonorsGif.setImageResource(R.drawable.please_wait_req_blood);
            }

            if(mSearchIcon != null) {
                mSearchIcon.setImageResource(R.mipmap.search_blood_icon);
            }

            UserDao userDao = new UserDao(getContext());
            UserDao.SingleUser singleUser = new UserDao.SingleUser();
            singleUser = userDao.getRowById(null);
            UserFireStore.updateDonorStatus(singleUser.userId, true);   // status to true for blood donation

//            RequestFireStore.checkPendingRequest(singleUser.userId, new BaseFireStore.FireStoreCallback() {
//                @Override
//                public void done(Exception fireBaseException, Object object) {
//                    if(object != null) {
//                        final ArrayList<String> requestDetail = (ArrayList<String>) object;
//                        if(requestDetail.size() > 0) {
//                            long date = Long.parseLong(requestDetail.get(1));
//                            String textString = String.format("You have requested Blood: %s on %s have you received it?", requestDetail.get(0), CommonUtil.getDate(date));
//                            CommonUtil.showDialog(getContext(), getString(R.string.dialog_title_information), textString, android.R.drawable.ic_dialog_alert, "Yes", "No", new CommonUtil.AlertDialogOnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    if(which == -1) {
//                                        RequestFireStore.updateCompleteRequest(requestDetail.get(2), CommonConstants.USER_REQUEST_RECEIVED_BLOOD);
//                                    } else {
//                                        RequestFireStore.updateCompleteRequest(requestDetail.get(2), CommonConstants.USER_REQUEST_DO_NOT_RECEIVED_BLOOD);
//                                    }
//                                    //updateCurrentLocation(12f);
//                                }
//                            });
//                        }
//                    }
//                }
//            });

//            DonateFireStore.checkPendingDonateRequest(singleUser.userId, new BaseFireStore.FireStoreCallback() {
//                @Override
//                public void done(Exception fireBaseException, Object object) {
//                    final ArrayList<String> donateDetail = (ArrayList<String>) object;
//                    if(donateDetail.size() > 0) {
//                        long date = Long.parseLong(donateDetail.get(1));
//                        String textString = String.format("Have you Donated the Blood %s on %s?", donateDetail.get(0), CommonUtil.getDate(date));
//                        CommonUtil.showDialog(getContext(), getString(R.string.dialog_title_information), textString, android.R.drawable.ic_dialog_alert, "Yes", "No", new CommonUtil.AlertDialogOnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if(which == -1) {
//                                    DonateFireStore.updateDonateRequest(donateDetail.get(2), CommonConstants.USER_DONATE_BLOOD);
//                                } else {
//                                    DonateFireStore.updateDonateRequest(donateDetail.get(2), CommonConstants.USER_DO_NOT_DONATE_BLOOD);
//                                }
//                                //updateCurrentLocation(12f);
//                            }
//                        });
//                    }
//                }
//            });

//            String checkVal = SharedPreferenceHelper.getKeyValue(getContext(), CommonConstants.CHECK_REQUEST_COMPLETE, "0");
//            if(checkVal.equals("0")) {
//                changeMapScreen(REQUEST_HOME_SCREEN);
//            } else {
//                changeMapScreen(REQUEST_ACCEPT_DONOR_SCREEN);
//            }

            if(mSearchBloodType != null) {
//                ArrayList<String> bloodType = new ArrayList<>();
//                bloodType.add("Blood Group ?");
//                bloodType.add(CommonConstants.BLOOD_TYPE_A_POS);
//                bloodType.add(CommonConstants.BLOOD_TYPE_A_NEG);
//                bloodType.add(CommonConstants.BLOOD_TYPE_B_POS);
//                bloodType.add(CommonConstants.BLOOD_TYPE_B_NEG);
//                bloodType.add(CommonConstants.BLOOD_TYPE_O_POS);
//                bloodType.add(CommonConstants.BLOOD_TYPE_O_NEG);
//                bloodType.add(CommonConstants.BLOOD_TYPE_AB_POS);
//                bloodType.add(CommonConstants.BLOOD_TYPE_AB_NEG);
//
//                final ArrayAdapter<String> bloodAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, bloodType){
//                    @Override
//                    public boolean isEnabled(int position){
//                        if(position == 0)
//                        {
//                            // Disable the first item from Spinner
//                            // First item will be use for hint
//                            return false;
//                        }
//                        else
//                        {
//                            return true;
//                        }
//                    }
//                    @Override
//                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                        View view = super.getDropDownView(position, convertView, parent);
//                        TextView tv = (TextView) view;
//                        if(position == 0){
//                            // Set the hint text color gray
//                            tv.setTextColor(Color.GRAY);
//                        }
//                        else {
//                            tv.setTextColor(Color.BLACK);
//                        }
//                        return view;
//                    }
//                };

                setDropDownAdapter(REQUEST_HOME_SCREEN);
                mSearchBloodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        if(position != 0) {
                            mRequestBloodBtn.setVisibility(View.VISIBLE);
                            if(mScreenMode == DOCTOR_MAIN_SCREEN)
                                Toast.makeText(getContext(), CommonUtil.getDocDesWithID(getContext(), position), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

//                final ArrayAdapter<String> bloodAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, bloodType);
//                mSearchBloodType.setAdapter(bloodAdapter);

            }

            mPlaceAutoCompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete);
            if(mPlaceAutoCompleteFragment != null) {
                mPlaceAutoCompleteFragment.setHint("Location ?");
                AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("PK").build();
                mPlaceAutoCompleteFragment.setFilter(filter);

//            PlaceAutocompleteFragment placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
                mPlaceAutoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {

                        Log.d("Maps", "Place selected: " + place.getName());
                        mIsSearchLocation = true;
                        if(mScreenMode == REQUEST_LOCATION_CONFIRM_SCREEN){
                            updateLocationOnMap(place.getLatLng(), true, 18f, true, true);
                        } else {
                            updateLocationOnMap(place.getLatLng(), false, 14f, false,true);
                        }
//                        if(mScreenMode == REQUEST_LOCATION_CONFIRM_SCREEN) {
//                            UserDao userDao = new UserDao(getContext());
//                            final UserDao.SingleUser singleUser = userDao.getRowById(null);
//                            addDonorMarker(singleUser, place.getLatLng().latitude, place.getLatLng().longitude, mSearchBloodType.getSelectedItem().toString());
//                        }
                    }

                    @Override
                    public void onError(Status status) {
                        Log.d("Maps", "An error occurred: " + status);
                    }
                });

                mPlaceAutoCompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateCurrentLocation(12f);
                        mIsSearchLocation = false;
                        mPlaceAutoCompleteFragment.setText("");
                    }
                });
            }

            if (mRequestBloodBtn != null) {
                mRequestBloodBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //checkForGPSStatus();
                        if(!ReachabilityTest.CheckInternet(getContext())) {
                            CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_information), getString(R.string.error_no_internet), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                            return;
                        }
                        if (mScreenMode == REQUEST_HOME_SCREEN) {
                            if(mIsSearchLocation) {
                                storeRequestBloodLocation();
                                updateLocationOnMap(mCurrentLocation, false, 12f, true,true);
                                UserDao userDao = new UserDao(getContext());
                                final UserDao.SingleUser singleUser = userDao.getRowById(null);
                                addDonorMarker(singleUser, mCurrentLocation.latitude, mCurrentLocation.longitude, mSearchBloodType.getSelectedItem().toString());
                            }
                            else {
                                updateCurrentLocation(12f);
                            }
                            changeMapScreen(REQUEST_BLOOD_CONFIRM_SCREEN);
                        } else if(mScreenMode == REQUEST_BLOOD_CONFIRM_SCREEN) {
                            //updateCurrentLocation(16f);
                            changeMapScreen(REQUEST_LOCATION_CONFIRM_SCREEN);
                        } else if(mScreenMode == REQUEST_LOCATION_CONFIRM_SCREEN) {
                            changeMapScreen(REQUEST_BLOOD_SEARCH_SCREEN);
                            sendBloodRequest();
                        } else if(mScreenMode == REQUEST_BLOOD_SEARCH_SCREEN) {
                        } else if(mScreenMode == REQUEST_ACCEPT_DONOR_SCREEN) {
                            SharedPreferenceHelper.setKeyValue(getContext(), CommonConstants.CHECK_REQUEST_COMPLETE, "0");
                            changeMapScreen(REQUEST_HOME_SCREEN);
                            updateCurrentLocation(12f);
                            if(mRequestId != null) {
                                RequestFireStore.updateCompleteRequest(mRequestId, CommonConstants.USER_REQUEST_RECEIVED_BLOOD);
                            }
                            if(mPlaceAutoCompleteFragment != null) {
                                mPlaceAutoCompleteFragment.setText("");
                            }
                        } else if(mScreenMode == DONOR_ACCEPT_SCREEN) {
                            //New Collection of Donate New entire of UserId, UserName, BloodType, TimeStamp, Distance, RequestId, RequestLocation, DonorLocation
                            UserDao userDao = new UserDao(getContext());
                            final UserDao.SingleUser singleUser = userDao.getRowById(null);
                            UserFireStore.updateDonorStatus(singleUser.userId, true);

//                            if(mPlaceAutoCompleteFragment != null) {
//                                mPlaceAutoCompleteFragment.setText("");
//                            }
                            updateCurrentLocation(12f);
                            changeMapScreen(REQUEST_HOME_SCREEN);
                        } else if(mScreenMode == HOSPITAL_MAIN_SCREEN) {
                            addHospitalMarker(mCurrentLocation.latitude, mCurrentLocation.longitude, mSearchBloodType.getSelectedItem().toString());
                            changeMapScreen(HOSPITAL_NEARBY_SCREEN);
                        } else if(mScreenMode == DOCTOR_MAIN_SCREEN) {
                            addDoctorMarker(mCurrentLocation.latitude, mCurrentLocation.longitude, mSearchBloodType.getSelectedItem().toString());
                            changeMapScreen(DOCTOR_NEARBY_SCREEN);
                        }
                    }
                });
            }

            if(mBackNavButton != null) {
                mBackNavButton.setBackgroundResource(R.drawable.ic_menu);
                mBackNavButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mScreenMode == REQUEST_LOCATION_CONFIRM_SCREEN) {
                            updateCurrentLocation(12f);
                            mIsSearchLocation = false;
                            changeMapScreen(REQUEST_BLOOD_CONFIRM_SCREEN);
                            mBackNavButton.setBackgroundResource(R.drawable.ic_arrow_back);
                        } else if(mScreenMode == REQUEST_BLOOD_CONFIRM_SCREEN){
                            updateCurrentLocation(12f);
                            mIsSearchLocation = false;
                            changeMapScreen(REQUEST_HOME_SCREEN);
                            mBackNavButton.setBackgroundResource(R.drawable.ic_menu);
                        } else if(mScreenMode == REQUEST_HOME_SCREEN) {
                            activity.openNavigationDrawer();
                        } else if(mScreenMode == HOSPITAL_MAIN_SCREEN) {
                            activity.openNavigationDrawer();
                        } else if(mScreenMode == HOSPITAL_NEARBY_SCREEN) {
                            updateCurrentLocation(12f);
                            mIsSearchLocation = false;
                            changeMapScreen(HOSPITAL_MAIN_SCREEN);
                            mBackNavButton.setBackgroundResource(R.drawable.ic_menu);
                        } else if(mScreenMode == DOCTOR_MAIN_SCREEN) {
                            activity.openNavigationDrawer();
                        } else if(mScreenMode == DOCTOR_NEARBY_SCREEN) {
                            updateCurrentLocation(12f);
                            mIsSearchLocation = false;
                            changeMapScreen(DOCTOR_MAIN_SCREEN);
                            mBackNavButton.setBackgroundResource(R.drawable.ic_menu);
                        }
                    }
                });
            }

            if(mSwitchSatelliteMap != null) {
                mSwitchSatelliteMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mMap != null) {
                            if(!mIsSatellite) {
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                mIsSatellite = true;
                            } else {
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                mIsSatellite = false;
                            }
                        }
                    }
                });
            }

            if(mRequestCancelTextView != null) {
                mRequestCancelTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mScreenMode == REQUEST_BLOOD_SEARCH_SCREEN) {
                            RequestFireStore.realTimeUpdateRequest(null, mRealTimeUpdateListener, null);
                            updateCurrentLocation(12f);
                            changeMapScreen(REQUEST_HOME_SCREEN);
                            mBloodRequestCanceled = true;
                            if(!isNullOrEmpty(mRequestId)) {
                                RequestFireStore.updateCompleteRequest(mRequestId, CommonConstants.USER_REQUEST_CANCELED);
                            }
                        } else if(mScreenMode == REQUEST_ACCEPT_DONOR_SCREEN) {
                            SharedPreferenceHelper.setKeyValue(getContext(), CommonConstants.CHECK_REQUEST_COMPLETE, "0");
                            changeMapScreen(REQUEST_HOME_SCREEN);
                            setDropDownAdapter(CommonConstants.REQUEST_HOME_SCREEN);
                            updateCurrentLocation(12f);
                            //add 3 value in completeRequest field in firebase
                            if(mPlaceAutoCompleteFragment != null) {
                                mPlaceAutoCompleteFragment.setText("");
                            }
                        } else if(mScreenMode == DONOR_ACCEPT_SCREEN) {
                            updateCurrentLocation(12f);
                            changeMapScreen(REQUEST_HOME_SCREEN);
                            UserDao userDao = new UserDao(getContext());
                            final UserDao.SingleUser singleUser = userDao.getRowById(null);
                            UserFireStore.updateDonorStatus(singleUser.userId, true);
                        }
                    }
                });
            }

            if(mDonorListBtn != null) {
                mDonorListBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDonorListFragment = new DonorListFragment();
                        changeMapScreen(REQUEST_ACCEPT_DONOR_LIST_FRAGMENT);
                        Bundle bundle = new Bundle();

                        bundle.putSerializable("donor", mDonorInfoList);
                        UserMapFragment.this.presentChildFragment(mDonorListFragment, getString(R.string.DonorListFragmentTag), false, bundle);
                    }
                });
            }

            if(mCurrentLocationMap != null) {
                mCurrentLocationMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mMap == null) {
                            return;
                        }
//                        Location loc = mMap.getMyLocation();
//                        if (loc != null) {
//                            LatLng latLang = new LatLng(loc.getLatitude(), loc.getLongitude());
//                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 17);
//                            mMap.animateCamera(cameraUpdate);
                        updateCurrentLocation(17);
//                        }
                    }
                });
            }


//            if(mSearchLocation != null) {
//                mSearchLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                    @Override
//                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                        if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
//                                || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                                || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
//
//                            geoLocate();
//                        }
//                        return false;
//                    }
//                });
//            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setDropDownAdapter(int spinnerType) {
        try {
            if(mSearchBloodType != null) {
                ArrayList<String> bloodType = new ArrayList<>();
                if(spinnerType == REQUEST_HOME_SCREEN) {
                    bloodType.add("Blood Group ?");
                    bloodType.add(CommonConstants.BLOOD_TYPE_A_POS);
                    bloodType.add(CommonConstants.BLOOD_TYPE_A_NEG);
                    bloodType.add(CommonConstants.BLOOD_TYPE_B_POS);
                    bloodType.add(CommonConstants.BLOOD_TYPE_B_NEG);
                    bloodType.add(CommonConstants.BLOOD_TYPE_O_POS);
                    bloodType.add(CommonConstants.BLOOD_TYPE_O_NEG);
                    bloodType.add(CommonConstants.BLOOD_TYPE_AB_POS);
                    bloodType.add(CommonConstants.BLOOD_TYPE_AB_NEG);
                } else if (spinnerType == HOSPITAL_MAIN_SCREEN) {
                    bloodType.add("Hospital Type");
                    bloodType.add(CommonConstants.HOSPITAL_TYPE_ALL_HOSPITAL);
                    bloodType.add(CommonConstants.HOSPITAL_TYPE_BLOOD_BANK);
                    bloodType.add(CommonConstants.HOSPITAL_TYPE_THALYSEMMIA);
                    bloodType.add(CommonConstants.HOSPITAL_TYPE_LUKEMIA);
                    bloodType.add(CommonConstants.HOSPITAL_TYPE_HAEMOPHILLIA);
                } else if (spinnerType == DOCTOR_MAIN_SCREEN) {
                    bloodType.add("Doctor Type");
                    bloodType.add(CommonConstants.DOC_TYPE_CARDIO);
                    bloodType.add(CommonConstants.DOC_TYPE_Physician_Gastroenterologist);
                    bloodType.add(CommonConstants.DOC_TYPE_DERMATOLOGISTS);
                    bloodType.add(CommonConstants.DOC_TYPE_Physiotherapist);
                    bloodType.add(CommonConstants.DOC_TYPE_GYNECOLOGIST);
                    bloodType.add(CommonConstants.DOC_TYPE_SPINAL_SURGEON);
                    bloodType.add(CommonConstants.DOC_TYPE_ORTHOPEDIC_SURGEON);
                    bloodType.add(CommonConstants.DOC_TYPE_PATHOLOGISTS);
                    bloodType.add(CommonConstants.DOC_TYPE_Hematologist);
                    bloodType.add(CommonConstants.DOC_TYPE_Neurophysician);
                    bloodType.add(CommonConstants.DOC_TYPE_PLASTIC_SURGEON);
                    bloodType.add(CommonConstants.DOC_TYPE_Neurosurgeon);
                    bloodType.add(CommonConstants.DOC_TYPE_PULMONOLOGISTS);
                    bloodType.add(CommonConstants.DOC_TYPE_RADIOLOGISTS);
                    bloodType.add(CommonConstants.DOC_TYPE_RHEUMATOLOGISTS);
                    bloodType.add(CommonConstants.DOC_TYPE_UROLOGISTS);
                    bloodType.add(CommonConstants.DOC_TYPE_GENERAL_SURGEON);
                    bloodType.add(CommonConstants.DOC_TYPE_Nephrologists);
                    bloodType.add(CommonConstants.DOC_TYPE_Nutritionist);
                    bloodType.add(CommonConstants.DOC_TYPE_Anesthesiologist);
                    bloodType.add(CommonConstants.DOC_TYPE_Cardiac_Surgeon);
                    bloodType.add(CommonConstants.DOC_TYPE_Cardiac_Electro_Physiologist);
                    bloodType.add(CommonConstants.DOC_TYPE_General_Physician);
                    bloodType.add(CommonConstants.DOC_TYPE_General_Vascular_Laparoscopic_Surgeon);
                    bloodType.add(CommonConstants.DOC_TYPE_Dental_Surgeon);
                    bloodType.add(CommonConstants.DOC_TYPE_Interventional_Cardiologist);
                    bloodType.add(CommonConstants.DOC_TYPE_Interventional_Neuroradiologist);
                    bloodType.add(CommonConstants.DOC_TYPE_Cosmetologist);
                    bloodType.add(CommonConstants.DOC_TYPE_Histopathologist);
                    bloodType.add(CommonConstants.DOC_TYPE_Trauma_Orthopedic_Surgeon);
                    bloodType.add(CommonConstants.DOC_TYPE_Endocrinologist);
                    bloodType.add(CommonConstants.DOC_TYPE_ENT_Surgeon);
                }

                final ArrayAdapter<String> bloodAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, bloodType){
                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(position == 0){
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        }
                        else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };

//                final ArrayAdapter<String> bloodAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, bloodType);
                mSearchBloodType.setAdapter(bloodAdapter);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeMapScreen(int screen) {
        if(mAddressContainer == null || mAddressTextField == null || mBackNavButton == null || mRequestBloodBtn == null || mSearchBloodType == null) {
            return;
        }
        try {
            if (screen == REQUEST_HOME_SCREEN) {
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.VISIBLE);
                mConfirmLocationView.setVisibility(View.GONE);
                mRequestCancelTextView.setVisibility(View.GONE);
                mSearchingDonorView.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mDonorFoundContainer.setVisibility(View.GONE);
                mFindingDonorsGif.setVisibility(View.GONE);
                mRequestBloodBtn.setVisibility(View.GONE);
                mRequestBloodBtn.setText(getString(R.string.request_blood_text));
                mSwitchSatelliteMap.setVisibility(View.VISIBLE);
                mCurrentLocationMap.setVisibility(View.VISIBLE);
                mSearchBloodType.setVisibility(View.VISIBLE);
                mSearchBloodTypeContainer.setVisibility(View.VISIBLE);
                mSearchIcon.setImageResource(R.mipmap.search_blood_icon);
//                mSearchLocation.setVisibility(View.VISIBLE);
                mSearchLocationContainer.setVisibility(View.VISIBLE);
                mBackNavButton.setBackgroundResource(R.drawable.ic_menu);
                mSearchBloodType.setSelection(0);           // newly added for showing :hint:
                mPlaceAutoCompleteFragment.setText("");     // newly added for showing :hint:
                mScreenMode = REQUEST_HOME_SCREEN;
                setAllMapGesture(true);
                clearMapPoint();
                setMapAttr(true);
                //updateLocationOnMap(new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude));
            } else if(screen == REQUEST_BLOOD_CONFIRM_SCREEN) {
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.VISIBLE);
                mConfirmLocationView.setVisibility(View.GONE);
                mRequestBloodBtn.setText(getString(R.string.confirm_blood_text));
                mRequestBloodBtn.setVisibility(View.VISIBLE);
                mSwitchSatelliteMap.setVisibility(View.VISIBLE);
                mCurrentLocationMap.setVisibility(View.VISIBLE);
                mSearchLocationContainer.setVisibility(View.GONE);
                mSearchingDonorView.setVisibility(View.GONE);
                mSearchBloodType.setVisibility(View.GONE);
                mSearchBloodTypeContainer.setVisibility(View.GONE);
//                mSearchLocation.setVisibility(View.GONE);
                mRequestCancelTextView.setVisibility(View.GONE);
                mDonorFoundContainer.setVisibility(View.GONE);
                mFindingDonorsGif.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mBackNavButton.setBackgroundResource(R.drawable.ic_arrow_back);
                mScreenMode = REQUEST_BLOOD_CONFIRM_SCREEN;
                setMapAttr(false);
                setAllMapGesture(true);
            } else if (screen == REQUEST_LOCATION_CONFIRM_SCREEN) {
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.VISIBLE);
                mConfirmLocationView.setVisibility(View.GONE);
                mRequestBloodBtn.setText(getString(R.string.confirm_location_text));
                mRequestBloodBtn.setVisibility(View.VISIBLE);
                mSwitchSatelliteMap.setVisibility(View.VISIBLE);
                mCurrentLocationMap.setVisibility(View.VISIBLE);
                mSearchLocationContainer.setVisibility(View.VISIBLE);
                mSearchingDonorView.setVisibility(View.GONE);
                mSearchBloodType.setVisibility(View.GONE);
                mSearchBloodTypeContainer.setVisibility(View.GONE);
//                mSearchLocation.setVisibility(View.GONE);
                mRequestCancelTextView.setVisibility(View.GONE);
                mDonorFoundContainer.setVisibility(View.GONE);
                mFindingDonorsGif.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mBackNavButton.setBackgroundResource(R.drawable.ic_arrow_back);
                mScreenMode = REQUEST_LOCATION_CONFIRM_SCREEN;
                setAllMapGesture(true);
                setMapAttr(true);

                //updateNewDragLocationOnMap();
                setUserLocationState(CommonConstants.MAP_CAMERA_STOP, true);
//                updateAddressOnScreen();
            } else if (screen == REQUEST_BLOOD_SEARCH_SCREEN) {
                mRequestBloodBtn.setVisibility(View.GONE);
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.GONE);
                mConfirmLocationView.setVisibility(View.GONE);
                mSearchBloodType.setVisibility(View.GONE);
                mCurrentLocationMap.setVisibility(View.GONE);
                mSearchBloodTypeContainer.setVisibility(View.GONE);
//                mSearchLocation.setVisibility(View.GONE);
                mSwitchSatelliteMap.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mSearchingDonorView.setVisibility(View.VISIBLE);
                //mFindingDonorsGif.setVisibility(View.VISIBLE);
                mRequestCancelTextView.setVisibility(View.VISIBLE);
                mSearchLocationContainer.setVisibility(View.GONE);
                mDonorFoundContainer.setVisibility(View.GONE);
                setMapAttr(false);
                setAllMapGesture(false);
                mScreenMode = REQUEST_BLOOD_SEARCH_SCREEN;
            } else if (screen == REQUEST_ACCEPT_DONOR_SCREEN) {
                mRequestBloodBtn.setVisibility(View.GONE);
//                mRequestBloodBtn.setText(getString(R.string.complete_request_text));
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.GONE);
                mConfirmLocationView.setVisibility(View.GONE);
                mSearchBloodType.setVisibility(View.GONE);
                mCurrentLocationMap.setVisibility(View.GONE);
                mSearchBloodTypeContainer.setVisibility(View.GONE);
                mFindingDonorsGif.setVisibility(View.GONE);
//                mSearchLocation.setVisibility(View.GONE);
                mSwitchSatelliteMap.setVisibility(View.VISIBLE);
                mDonorListBtn.setVisibility(View.VISIBLE);
//                mDonorFoundContainer.setVisibility(View.VISIBLE);
                mSearchingDonorView.setVisibility(View.GONE);
                mRequestCancelTextView.setVisibility(View.VISIBLE);
                mSearchLocationContainer.setVisibility(View.GONE);
                setAllMapGesture(true);
                mScreenMode = REQUEST_ACCEPT_DONOR_SCREEN;
            } else if (screen == REQUEST_ACCEPT_DONOR_LIST_FRAGMENT) {
                mRequestBloodBtn.setVisibility(View.GONE);
                mSwitchSatelliteMap.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
                mDonorFoundContainer.setVisibility(View.GONE);
                mFindingDonorsGif.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mRequestCancelTextView.setVisibility(View.GONE);
                mSearchLocationContainer.setVisibility(View.GONE);
                setAllMapGesture(false);
                mScreenMode = REQUEST_ACCEPT_DONOR_LIST_FRAGMENT;
            } else if (screen == REQUEST_ACCEPT_CUSTOM_DIALOG_BOX_FRAGMENT) {
                mRequestBloodBtn.setVisibility(View.GONE);
                mSwitchSatelliteMap.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
//                mFindingDonorsGif.setVisibility(View.GONE);
                mDonorFoundContainer.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mRequestCancelTextView.setVisibility(View.GONE);
                mSearchLocationContainer.setVisibility(View.GONE);
                setAllMapGesture(false);
                mScreenMode = REQUEST_ACCEPT_CUSTOM_DIALOG_BOX_FRAGMENT;
            } else if (screen == DONOR_ACCEPT_SCREEN) {
                //call direction
                //set DonorInfo new object
                mRequestBloodBtn.setVisibility(View.VISIBLE);
                mRequestBloodBtn.setText(getString(R.string.blood_donate_text));
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.GONE);
                mConfirmLocationView.setVisibility(View.GONE);
                mSearchBloodType.setVisibility(View.GONE);
                mSearchBloodTypeContainer.setVisibility(View.GONE);
                mFindingDonorsGif.setVisibility(View.GONE);
//                mSearchLocation.setVisibility(View.GONE);
                mSwitchSatelliteMap.setVisibility(View.VISIBLE);
                mDonorListBtn.setVisibility(View.GONE);
                mSearchingDonorView.setVisibility(View.GONE);
                mRequestCancelTextView.setVisibility(View.VISIBLE);
                mSearchLocationContainer.setVisibility(View.GONE);
                setAllMapGesture(true);
                mScreenMode = DONOR_ACCEPT_SCREEN;
            } else if (screen == HOSPITAL_MAIN_SCREEN) {
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.VISIBLE);
                mConfirmLocationView.setVisibility(View.GONE);
                mRequestCancelTextView.setVisibility(View.GONE);
                mSearchingDonorView.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mDonorFoundContainer.setVisibility(View.GONE);
                mFindingDonorsGif.setVisibility(View.GONE);
                mRequestBloodBtn.setVisibility(View.GONE);
                mRequestBloodBtn.setText(getString(R.string.find_nearest_blood_center_text));
                mSwitchSatelliteMap.setVisibility(View.VISIBLE);
                mCurrentLocationMap.setVisibility(View.VISIBLE);
                mSearchBloodType.setVisibility(View.VISIBLE);
                mSearchBloodTypeContainer.setVisibility(View.VISIBLE);
                mSearchIcon.setImageResource(R.mipmap.search_hospital_icon);
//                mSearchLocation.setVisibility(View.VISIBLE);
                mSearchLocationContainer.setVisibility(View.VISIBLE);
                mBackNavButton.setBackgroundResource(R.drawable.ic_menu);
                mSearchBloodType.setSelection(0);           // newly added for showing :hint:
                mPlaceAutoCompleteFragment.setText("");     // newly added for showing :hint:
                mScreenMode = HOSPITAL_MAIN_SCREEN;
                setAllMapGesture(true);
                clearMapPoint();
                setMapAttr(true);
            } else if (screen == HOSPITAL_NEARBY_SCREEN) {
                mSearchBloodType.setVisibility(View.GONE);
                mSearchBloodTypeContainer.setVisibility(View.GONE);
                mSearchLocationContainer.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.VISIBLE);
                mBackNavButton.setBackgroundResource(R.drawable.ic_arrow_back);
                mRequestBloodBtn.setVisibility(View.GONE);
                setAllMapGesture(true);
                mScreenMode = HOSPITAL_NEARBY_SCREEN;
            } else if (screen == DOCTOR_MAIN_SCREEN) {
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.VISIBLE);
                mConfirmLocationView.setVisibility(View.GONE);
                mRequestCancelTextView.setVisibility(View.GONE);
                mSearchingDonorView.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mDonorFoundContainer.setVisibility(View.GONE);
                mFindingDonorsGif.setVisibility(View.GONE);
                mRequestBloodBtn.setVisibility(View.GONE);
                mRequestBloodBtn.setText(getString(R.string.find_nearest_doctor_text));
                mSwitchSatelliteMap.setVisibility(View.VISIBLE);
                mSearchIcon.setImageResource(R.mipmap.search_doctor_icon);
                mCurrentLocationMap.setVisibility(View.VISIBLE);
                mSearchBloodType.setVisibility(View.VISIBLE);
                mSearchBloodTypeContainer.setVisibility(View.VISIBLE);
//                mSearchLocation.setVisibility(View.VISIBLE);
                mSearchLocationContainer.setVisibility(View.VISIBLE);
                mBackNavButton.setBackgroundResource(R.drawable.ic_menu);
                mSearchBloodType.setSelection(0);           // newly added for showing :hint:
                mPlaceAutoCompleteFragment.setText("");     // newly added for showing :hint:
                mScreenMode = DOCTOR_MAIN_SCREEN;
                setAllMapGesture(true);
                clearMapPoint();
                setMapAttr(true);
                updateCurrentLocation(12f);
            } else if (screen == DOCTOR_NEARBY_SCREEN) {
                mSearchBloodType.setVisibility(View.GONE);
                mSearchBloodTypeContainer.setVisibility(View.GONE);
                mSearchLocationContainer.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.VISIBLE);
                mBackNavButton.setBackgroundResource(R.drawable.ic_arrow_back);
                mRequestBloodBtn.setVisibility(View.GONE);
                setAllMapGesture(true);
                mScreenMode = DOCTOR_NEARBY_SCREEN;
            } else if (screen == HOSPITAL_DIALOG_SCREEN) {
                mRequestBloodBtn.setVisibility(View.GONE);
                mSwitchSatelliteMap.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
//                mFindingDonorsGif.setVisibility(View.GONE);
                mDonorFoundContainer.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mRequestCancelTextView.setVisibility(View.GONE);
                mSearchLocationContainer.setVisibility(View.GONE);
                setAllMapGesture(false);
                mScreenMode = HOSPITAL_DIALOG_SCREEN;
            } else if(screen == DOCTOR_DETAILS_SCREEN) {
                mRequestBloodBtn.setVisibility(View.GONE);
                mSwitchSatelliteMap.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mAddressContainer.setVisibility(View.GONE);
                mAddressTextField.setVisibility(View.GONE);
//                mFindingDonorsGif.setVisibility(View.GONE);
                mDonorFoundContainer.setVisibility(View.GONE);
                mBackNavButton.setVisibility(View.GONE);
                mDonorListBtn.setVisibility(View.GONE);
                mRequestCancelTextView.setVisibility(View.GONE);
                mSearchLocationContainer.setVisibility(View.GONE);
                setAllMapGesture(false);
                mScreenMode = DOCTOR_DETAILS_SCREEN;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Activity getMapActivity() {
        return getActivity();
    }

    public void storeRequestBloodLocation() {
        try {
            UserDao userDao = new UserDao(getContext());
            final UserDao.SingleUser singleUser = userDao.getRowById(null);
            UserFireStore.updateUserLocation(getContext(), singleUser.userId, new GeoPoint(mCurrentLocation.latitude, mCurrentLocation.longitude));        //.. NEED TO REMOVE THE COMMENT BEFORE RELEASE
            userDao.updateUserLocation(singleUser.userId, new GeoPoint(mCurrentLocation.latitude, mCurrentLocation.longitude));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAcceptedDonorData(String uId, final String requestId) {
        if(isNullOrEmpty(uId)) {
            return;
        }
        try {
            ArrayList<String> donorList = new ArrayList<>();
            donorList.add(uId);
            mDonorInfoList = new ArrayList<>();

            UserFireStore.getDonorsData(donorList, new BaseFireStore.FireStoreCallback() {
                @Override
                public void done(Exception fireBaseException, Object object) {
                    if (object != null) {
                        ArrayList<UserDao.SingleUser> singleUsers = (ArrayList<UserDao.SingleUser>) object;
                        for (UserDao.SingleUser user : singleUsers) {

                            DonorInfo donorInfo = new DonorInfo();
                            donorInfo.setRequestId(requestId);
                            donorInfo.setDonorUId(user.userId);
                            donorInfo.setRequesterName(user.name);
                            donorInfo.setDonorContact(user.cellNo);
                            donorInfo.setDonorLocation(new GeoPoint(user.latitude, user.longitude));
                            donorInfo.setDonorBloodType(user.bloodType);
                            mDonorInfoList.add(donorInfo);
                        }
                        changeMapScreen(DONOR_ACCEPT_SCREEN);
                        directionBetweenDonorRequester(mDonorInfoList.get(0));
                    }
                }
            });
            CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_fetching_requester_data), true);
        } catch (Exception e) {
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();
        }

    }

    public void directionBetweenDonorRequester(final DonorInfo donorInfo) {
        try {
            setMapAttr(false);
            if(mMarkerList != null) {
                mMarkerList.clear();
            } else {
                mMarkerList = new ArrayList<>();
            }
            clearMapPoint();

//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(new LatLng(donorInfo.donorLocation.getLatitude(), donorInfo.donorLocation.getLongitude()));
//
//            Marker marker = mMap.addMarker(markerOptions);
//            mMarkerList.add(null);
//            mMarkerList.set(0, marker);

            GoogleDirection.withServerKey(getString(R.string.google_directions_key))
                    .from(mCurrentLocation)
                    .to(new LatLng(donorInfo.donorLocation.getLatitude(), donorInfo.donorLocation.getLongitude()))
                    .avoid(AvoidType.FERRIES)
                    .avoid(AvoidType.HIGHWAYS)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if(direction.isOK()) {
                                Route route = direction.getRouteList().get(0);
                                String getLocationText = "";

                                // parsing json rawBody data to get Distance(KM) and Duration
                                try {
                                    String routes = new JSONObject(rawBody).getString("routes");
                                    String legs = new JSONArray(routes).getJSONObject(0).getString("legs");

                                    String distanceJson = new JSONArray(legs).getJSONObject(0).getString("distance");
                                    String durationJson = new JSONArray(legs).getJSONObject(0).getString("duration");

                                    String distance = new JSONObject(distanceJson).getString("text");
                                    String duration  = new JSONObject(durationJson).getString("text");

                                    mDonorInfoList.get(0).donorDistanceFUser = distance;
                                    getLocationText = String.format("%s", distance);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                DirectionsResult directionsResult = (DirectionsResult) direction.getRouteList();

                                //Bitmap bmp = CommonUtil.drawNumberInsideMarker(BitmapFactory.decodeResource(getResources(), R.mipmap.pin_drop_text), getLocationText);

                                mMap.addMarker(new MarkerOptions()
                                        .position(mCurrentLocation)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blood_donor_maker)));

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(donorInfo.donorLocation.getLatitude(), donorInfo.donorLocation.getLongitude()));
                                markerOptions.title(getLocationText);
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_drop));

                                Marker marker = mMap.addMarker(markerOptions);
                                marker.showInfoWindow();
                                mMarkerList.add(null);
                                mMarkerList.set(0, marker);

                                //mMap.addMarker(new MarkerOptions().position(new LatLng(donorInfo.donorLocation.getLatitude(), donorInfo.donorLocation.getLongitude())).title(getLocationText));

                                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 7, Color.DKGRAY));

                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 11);
                                mMap.moveCamera(cameraUpdate);
                                // create New Donate Entry in the Donate collection

                                UserDao userDao = new UserDao(getContext());
                                UserDao.SingleUser singleUser = new UserDao.SingleUser();
                                singleUser = userDao.getRowById(null);
                                if(singleUser == null) {
                                    return;
                                }
                                CommonUtil.showProgressDialog(getActivity(), null, false);
                                Map<String, Object> data = new HashMap<>();
                                data.put(DonateFireStore.COLUMN_REQUEST_ID, donorInfo.getRequestId());
                                data.put(DonateFireStore.COLUMN_USER_ID, singleUser.userId);
                                data.put(DonateFireStore.COLUMN_DONOR_NAME, singleUser.name);
                                data.put(DonateFireStore.COLUMN_REQUESTER_ID, donorInfo.getDonorUId());
                                data.put(DonateFireStore.COLUMN_REQUESTER_NAME, donorInfo.requesterName);
                                data.put(DonateFireStore.COLUMN_USER_BLOOD_TYPE, donorInfo.getDonorBloodType()); // change with user input bloodtype
                                data.put(DonateFireStore.COLUMN_DONOR_LOCATION, new GeoPoint(mCurrentLocation.latitude, mCurrentLocation.longitude));
                                data.put(DonateFireStore.COLUMN_REQUESTER_LOCATION, donorInfo.donorLocation);
                                data.put(DonateFireStore.COLUMN_CREATION_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
                                data.put(DonateFireStore.COLUMN_DISTANCE, mDonorInfoList.get(0).donorDistanceFUser);
                                data.put(DonateFireStore.COLUMN_DONATE_COMPLETED, CommonConstants.USER_DONATE_PENDING);

                                DonateFireStore.createDonateRequest(data, true, new BaseFireStore.FireStoreCallback() {
                                    @Override
                                    public void done(Exception fireBaseException, Object object) {
                                        if(object != null) {
                                            mDonateId = (String) object;
                                            takeMapSnapShot(DonateFireStore.COLLECTION_NAME, mDonateId, 350);
                                        }
                                    }
                                });

                            } else {
                                CommonUtil.showProgressDialog(getActivity(), null, false);
                                // Do something
                            }
                        }
                        @Override
                        public void onDirectionFailure(Throwable t) {
                            CommonUtil.showProgressDialog(getActivity(), null, false);
                            // Do something
                        }
                    });
        } catch (Exception e) {
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();
        }
    }

    private void updateNewDragLocationOnMap(boolean isAnimateCamera) {
        try {
            Projection projection = mMap.getProjection();
            Point pt = CommonUtil.getCenterLocationOfScreen(getActivity());
            LatLng latLng;
            if(isAnimateCamera) {
                latLng = mCurrentLocation;
            } else {
                latLng = projection.fromScreenLocation(pt);
                mCurrentLocation = latLng;
            }
            mCurrentLocation = latLng;
//                updateLocationOnMap(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            updateLocationOnMap(new LatLng(latLng.latitude, latLng.longitude), true, 18f, true, isAnimateCamera);
            //distanceBetweenNearbyDonors(mNearbyDonorsLocation);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUserLocationState(int state, boolean isAnimateCamera) {
        try {
            if(state == CommonConstants.MAP_CAMERA_ON_MOVE) {

            } else if(state == CommonConstants.MAP_CAMERA_STOP) {
                updateNewDragLocationOnMap(isAnimateCamera);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateAddressOnScreen();
                    }
                }).start();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAddressOnScreen() {
        if(mAddressTextField == null || mPlaceAutoCompleteFragment == null) {
            return;
        }
        final String currentAddress = getAddressFromLocation(getActivity(), mCurrentLocation);
        if(!isNullOrEmpty(currentAddress)) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAddressTextField.setText(currentAddress);
                    mPlaceAutoCompleteFragment.setText(currentAddress);
                }
            });
        }
    }

    private void takeMapSnapShot(final String folder, final String id, final int marginTop) {
        try {
            GoogleMap.SnapshotReadyCallback snapshotReadyCallback = new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    try {
                        Bitmap requestBitmap = CommonUtil.cropMapSnapShot(bitmap, 800, 500, marginTop);
                        byte[] requestStoreBytes = CommonUtil.getByteFromBitmap(requestBitmap);
                        BaseFireStore.saveMapSnapShot(folder, id, requestStoreBytes);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            mMap.snapshot(snapshotReadyCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendBloodRequest() {
        try {
            //mSearchBloodGEditText.setText("B+");
            //String bloodGroup = mSearchBloodGEditText.getText().toString();
            final String bloodGroup = mSearchBloodType.getSelectedItem().toString();
//                    String bloodGroup = CommonUtil.validateInputText(getActivity(), UserMapFragment.this, searchBloodGEditText, R.string.email_text, 1, true, false);
//                    if(bloodGroup == null) {
//                        return;
//                    }

            if (!ReachabilityTest.CheckInternet(getContext())) {
                return;
            }

            storeRequestBloodLocation();
            UserDao userDao = new UserDao(getContext());
            final UserDao.SingleUser singleUser = userDao.getRowById(null);
            RequestFireStore.getRequestToken(bloodGroup, singleUser.deviceToken, mCurrentLocation.latitude, mCurrentLocation.longitude, true, new BaseFireStore.FireStoreCallback() {
                @Override
                public void done(Exception fireBaseException, Object object) {

                    if (object != null) {
                        ArrayList<String> tokenArray = (ArrayList<String>) object;
                        Map<String, Object> data = new HashMap<>();
                        data.put(RequestDao.COLUMN_USER_ID, singleUser.userId);
                        data.put(RequestDao.COLUMN_USER_NAME, singleUser.name);
                        data.put(RequestDao.COLUMN_USER_BLOOD_TYPE, bloodGroup); // change with user input bloodtype
                        data.put(RequestDao.COLUMN_USER_DEVICE_TOKEN, singleUser.deviceToken);
                        data.put(RequestDao.COLUMN_CREATION_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
                        data.put(RequestDao.COLUMN_REQUEST_DEVICE_TOKEN, tokenArray);
                        data.put(RequestDao.COLUMN_REQUEST_COMPLETED, CommonConstants.USER_REQUEST_PENDING);
                        data.put(RequestDao.COLUMN_REQUESTER_LATITUDE, mCurrentLocation.latitude);
                        data.put(RequestDao.COLUMN_REQUESTER_LONGITUDE, mCurrentLocation.longitude);

                        mDonorIds = new ArrayList<>();
                        RequestFireStore.createRequest(data, true, new BaseFireStore.FireStoreCallback() {
                            @Override
                            public void done(Exception firebaseException, Object object) {
                                mRequestId = object.toString();
                                takeMapSnapShot(RequestDao.COLLECTION_NAME, mRequestId, 300);
                                mRealTimeUpdateListener = RequestFireStore.realTimeUpdateRequest(mRequestId, null, new BaseFireStore.FireStoreCallback() {
                                    @Override
                                    public void done(Exception fireBaseException, Object object) {
                                        if(object != null) {
                                            // gather all the accepted userId and store in data structure
                                            ArrayList<String> donorIds = (ArrayList<String>) object;
                                            for(String donorId : donorIds) {
                                                if(mDonorIds.size() > 0) {
                                                    if(!mDonorIds.contains(donorId)) {
                                                        mDonorIds.add(donorId);
                                                    }
                                                } else {
                                                    mDonorIds.add(donorId);
                                                }
                                            }

                                        }
                                    }
                                });

                            }
                        });
                    } else {

                    }
                }
            });
            String text = String.format("%s for %s %s", getString(R.string.dialog_message_searching_donors), getString(R.string.blood_type_text), bloodGroup);
            mFindingDonorsGif.setVisibility(View.VISIBLE);
            //CommonUtil.showProgressDialog(getActivity(), text, true);
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    timer.cancel();
                    RequestFireStore.realTimeUpdateRequest(null, mRealTimeUpdateListener, null);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSearchingDonorView.setVisibility(View.GONE);
                            mFindingDonorsGif.setVisibility(View.GONE);
                        }
                    });

                    // here retrieve the full detail about the donors and show location on the map
                    // donor required items: name, picture, bloodtype, contact, location

                    mDonorInfoList = new ArrayList<>();
                    if(mBloodRequestCanceled) {
                        mBloodRequestCanceled = false;
                        return;
                    }
                    if(mDonorIds.size() > 0) {
                        UserFireStore.getDonorsData(mDonorIds, new BaseFireStore.FireStoreCallback() {
                            @Override
                            public void done(Exception fireBaseException, Object object) {
                                if (object != null) {
                                    ArrayList<UserDao.SingleUser> singleUsers = (ArrayList<UserDao.SingleUser>) object;
                                    mDonorsFoundText.setText(String.valueOf(singleUsers.size()));
                                    removeDonorFoundView();
                                    for (UserDao.SingleUser user : singleUsers) {
//                                        if (user.name.equals("device_1")) {
//                                            user.latitude = 33.6843633;
//                                            user.longitude = 73.0188971;// hard code the location
//                                        } else {
//                                            user.latitude = 33.6882664;
//                                            user.longitude = 73.0376217;// hard code the location
//                                        }

                                        DonorInfo donorInfo = new DonorInfo();
                                        donorInfo.setDonorUId(user.userId);
                                        donorInfo.setRequesterName(user.name);
                                        donorInfo.setDonorContact(user.cellNo);
                                        donorInfo.setDonorLocation(new GeoPoint(user.latitude, user.longitude));
                                        donorInfo.setDonorBloodType(user.bloodType);
                                        mDonorInfoList.add(donorInfo);

                                    }
                                    changeMapScreen(REQUEST_ACCEPT_DONOR_SCREEN);
                                    addAcceptedDonorMarker();
                                }
//                                CommonUtil.showProgressDialog(getActivity(), null, false);
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!isNullOrEmpty(mRequestId)) {
                                    RequestFireStore.updateCompleteRequest(mRequestId, CommonConstants.USER_REQUEST_NO_DONOR_AVAILABLE);
                                }
                                CommonUtil.showDialog(getContext(), getString(R.string.dialog_title_information), getString(R.string.no_donor_available), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, new CommonUtil.AlertDialogOnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateCurrentLocation(12f);
                                        if(mPlaceAutoCompleteFragment != null) {
                                            mPlaceAutoCompleteFragment.setText("");
                                        }

                                    }
                                });
                                changeMapScreen(REQUEST_HOME_SCREEN);
                            }
                        });
                        CommonUtil.showProgressDialog(getActivity(), null, false);
                    }
                    // change screen mode to accepted donor list
                    // add marker and calculate distance between donor and requester

                }
            }, 30000, 1);   // to run blood request for 20 second
        } catch (Exception e) {
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();
        }
    }

    private void removeDonorFoundView() {
        try {
            mDonorFoundContainer.setVisibility(View.VISIBLE);
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    timer.cancel();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDonorFoundContainer.setVisibility(View.GONE);

//                            updateLocationOnMap(new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude), false, 22f);
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude)));
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude), 12f));
                        }
                    });
                }
            }, 10000, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAcceptedDonorMarker() {
        if(mMap == null) {
            return;
        }
        try {

            if(mMarkerList != null) {
                mMarkerList.clear();
            } else {
                mMarkerList = new ArrayList<>();
            }
//            int index = 0;
            setMapAttr(false);
            //mMap.addMarker(new MarkerOptions().position(mCurrentLocation));
            for(final DonorInfo donorInfo : mDonorInfoList) {
                // add markers and marker index into the array
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(new LatLng(donorInfo.donorLocation.getLatitude(), donorInfo.donorLocation.getLongitude()));
//                //markerOptions.title(donorInfo.donorName);
//
//                Marker marker = mMap.addMarker(markerOptions);
//                mMarkerList.add(null);
//                mMarkerList.set(index, marker);
//                index++;
//                final int dirIndex = index;
                GoogleDirection.withServerKey(getString(R.string.google_directions_key))
                    .from(mCurrentLocation)
                    .to(new LatLng(donorInfo.donorLocation.getLatitude(), donorInfo.donorLocation.getLongitude()))
                    .avoid(AvoidType.FERRIES)
                    .avoid(AvoidType.HIGHWAYS)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if(direction.isOK()) {
                                Route route = direction.getRouteList().get(0);
                                String getLocationText = "";

                                // parsing json rawBody data to get Distance(KM) and Duration
                                try {
                                    String routes = new JSONObject(rawBody).getString("routes");
                                    String legs = new JSONArray(routes).getJSONObject(0).getString("legs");

                                    String distanceJson = new JSONArray(legs).getJSONObject(0).getString("distance");
                                    String durationJson = new JSONArray(legs).getJSONObject(0).getString("duration");

                                    String distance = new JSONObject(distanceJson).getString("text");
                                    String duration  = new JSONObject(durationJson).getString("text");

                                    donorInfo.setDonorDistanceFUser(distance);

                                    getLocationText = String.format("%s", distance);
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }

//                                DirectionsResult directionsResult = (DirectionsResult) direction.getRouteList();
//                                mMap.addMarker(new MarkerOptions().position(mCurrentLocation));

                                Bitmap bmp = CommonUtil.drawNumberInsideMarker(BitmapFactory.decodeResource(getResources(), R.mipmap.pin_drop_text), getLocationText, 0xffffffff, 26.0f, 8, 100);

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(donorInfo.donorLocation.getLatitude(), donorInfo.donorLocation.getLongitude()));
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                                //markerOptions.title(getLocationText);

                                int index = mDonorInfoList.indexOf(donorInfo);
                                Marker marker = mMap.addMarker(markerOptions);
                                //marker.showInfoWindow();
                                mMarkerList.add(null);
                                mMarkerList.set(index, marker);

//                                mMap.addMarker(new MarkerOptions()
//                                        .position(new LatLng(donorInfo.donorLocation.getLatitude(), donorInfo.donorLocation.getLongitude()))
//                                        .title(getLocationText)
//                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blood_donor_maker)) //.. adding blood donor icon
//
//                                ).showInfoWindow();


                                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                if(index == 1) {
                                    mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 7, Color.DKGRAY));
                                } else {
                                    mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 7, Color.GRAY));
                                }
                                CommonUtil.showProgressDialog(getActivity(), null, false);

                            } else {
                                // Do something
                                CommonUtil.showProgressDialog(getActivity(), null, false);
                            }
                        }
                        @Override
                        public void onDirectionFailure(Throwable t) {
                            // Do something
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            CommonUtil.showProgressDialog(getActivity(), null, false);
                        }
                    });
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude), 12f));
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();
        }
    }

    private void addHospitalMarkerOnMap(ArrayList<HospitalInfo> hospitalInfoArrayList) {
        if(mMap == null || hospitalInfoArrayList == null) {
            return;
        }
        try {
            if(mMarkerList != null) {
                mMarkerList.clear();
            } else {
                mMarkerList = new ArrayList<>();
            }
            clearMapPoint();

            mMap.addMarker(new MarkerOptions()
                    .position(mCurrentLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_drop)))
                    .showInfoWindow();

            setMapAttr(false);
            for(final HospitalInfo hospitalInfo : hospitalInfoArrayList) {
                GoogleDirection.withServerKey(getString(R.string.google_directions_key))
                    .from(mCurrentLocation)
                    .to(new LatLng(hospitalInfo.getHospitalLocation().getLatitude(), hospitalInfo.getHospitalLocation().getLongitude()))
                    .avoid(AvoidType.FERRIES)
                    .avoid(AvoidType.HIGHWAYS)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if (direction.isOK()) {
                                Route route = direction.getRouteList().get(0);
                                String getLocationText = "";

                                // parsing json rawBody data to get Distance(KM) and Duration
                                try {
                                    String routes = new JSONObject(rawBody).getString("routes");
                                    String legs = new JSONArray(routes).getJSONObject(0).getString("legs");

                                    String distanceJson = new JSONArray(legs).getJSONObject(0).getString("distance");
                                    String durationJson = new JSONArray(legs).getJSONObject(0).getString("duration");

                                    String distance = new JSONObject(distanceJson).getString("text");
                                    String duration = new JSONObject(durationJson).getString("text");

                                    hospitalInfo.setHospitalDistance(distance);

                                    getLocationText = String.format("%s", distance);
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                                int res = -1;
                                if(hospitalInfo.getHospitalType().equals(CommonConstants.HOSPITAL_TYPE_BLOOD_BANK)) {
                                    res = R.mipmap.blood_bank_marker_text;
                                } else if(hospitalInfo.getHospitalType().equals(CommonConstants.HOSPITAL_TYPE_ALL_HOSPITAL)) {
                                    res = R.mipmap.hospital_marker_text;
                                } else if(hospitalInfo.getHospitalType().equals(CommonConstants.HOSPITAL_TYPE_HAEMOPHILLIA)) {
                                    res = R.mipmap.hemophilia_marker_text;
                                } else if(hospitalInfo.getHospitalType().equals(CommonConstants.HOSPITAL_TYPE_LUKEMIA)) {
                                    res = R.mipmap.leukemia_marker_text;
                                } else if(hospitalInfo.getHospitalType().equals(CommonConstants.HOSPITAL_TYPE_THALYSEMMIA)) {
                                    res = R.mipmap.thalassemia_marker_text;
                                }

                                Bitmap bmp = CommonUtil.drawNumberInsideMarker(BitmapFactory.decodeResource(getResources(), res), getLocationText, Color.BLACK, 33.0f, 12, 200);

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(hospitalInfo.getHospitalLocation().getLatitude(), hospitalInfo.getHospitalLocation().getLongitude()));
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                                //markerOptions.title(getLocationText);

                                int index = mHospitalInfoArrayList.indexOf(hospitalInfo);
                                Marker marker = mMap.addMarker(markerOptions);
                                //marker.showInfoWindow();
                                mMarkerList.add(null);
                                mMarkerList.set(index, marker);

                                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                if(index == 1) {
                                    mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 7, Color.DKGRAY));
                                } else {
                                    mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 7, Color.GRAY));
                                }
                                CommonUtil.showProgressDialog(getActivity(), null, false);

                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude), 12f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDoctorMarkerOnMap(ArrayList<DoctorInfo> docInfoArrayList) {
        if(mMap == null || docInfoArrayList == null) {
            return;
        }
        try {
            if(mMarkerList != null) {
                mMarkerList.clear();
            } else {
                mMarkerList = new ArrayList<>();
            }
            clearMapPoint();

            mMap.addMarker(new MarkerOptions()
                    .position(mCurrentLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_drop)))
                    .showInfoWindow();

            setMapAttr(false);
            for(final DoctorInfo doctorInfo : docInfoArrayList) {
                GoogleDirection.withServerKey(getString(R.string.google_directions_key))
                        .from(mCurrentLocation)
                        .to(new LatLng(doctorInfo.getDocLocation().getLatitude(), doctorInfo.getDocLocation().getLongitude()))
                        .avoid(AvoidType.FERRIES)
                        .avoid(AvoidType.HIGHWAYS)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                if (direction.isOK()) {
                                    Route route = direction.getRouteList().get(0);
                                    String getLocationText = "";

                                    // parsing json rawBody data to get Distance(KM) and Duration
                                    try {
                                        String routes = new JSONObject(rawBody).getString("routes");
                                        String legs = new JSONArray(routes).getJSONObject(0).getString("legs");

                                        String distanceJson = new JSONArray(legs).getJSONObject(0).getString("distance");
                                        String durationJson = new JSONArray(legs).getJSONObject(0).getString("duration");

                                        String distance = new JSONObject(distanceJson).getString("text");
                                        String duration = new JSONObject(durationJson).getString("text");

                                        doctorInfo.setDocDistance(distance);

                                        getLocationText = String.format("%s", distance);
                                    } catch (JSONException e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }

                                    Bitmap bmp = CommonUtil.drawNumberInsideMarker(BitmapFactory.decodeResource(getResources(), R.mipmap.doctor_marker_text), getLocationText, Color.BLACK, 33.0f, 12, 200);

                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(new LatLng(doctorInfo.getDocLocation().getLatitude(), doctorInfo.getDocLocation().getLongitude()));
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                                    //markerOptions.title(getLocationText);

                                    int index = mDoctorInfoArrayList.indexOf(doctorInfo);
                                    Marker marker = mMap.addMarker(markerOptions);
                                    //marker.showInfoWindow();
                                    mMarkerList.add(null);
                                    mMarkerList.set(index, marker);

                                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                    if(index == 1) {
                                        mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 7, Color.DKGRAY));
                                    } else {
                                        mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 7, Color.GRAY));
                                    }
                                    CommonUtil.showProgressDialog(getActivity(), null, false);

                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                CommonUtil.showProgressDialog(getActivity(), null, false);
                                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude), 12f));
        } catch (Exception e) {
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();
        }
    }

    List<Polyline> mNearbyDonorPolyLines = null;
    private int mNearbyDonorCounter = 0;
    private void distanceBetweenNearbyDonors(final ArrayList<GeoPoint> geoPointList) {
        if(geoPointList == null) {
            return;
        }
        try {
            if(geoPointList.size() > 0) {
                CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_finding_nearby_donors), true);
            }
            setMapAttr(false);
            clearMapPoint();
            if(mNearbyDonorPolyLines != null) {
                for(Polyline line : mNearbyDonorPolyLines)
                {
                    line.remove();
                }

                mNearbyDonorPolyLines.clear();
            } else {
                mNearbyDonorPolyLines = new ArrayList<>();
            }

            mMap.addMarker(new MarkerOptions()
                    .position(mCurrentLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_drop)))
                    .showInfoWindow();

            mNearbyDonorCounter = 0;
            for(final GeoPoint geoPoint : geoPointList) {
                GoogleDirection.withServerKey(getString(R.string.google_directions_key))
                    .from(mCurrentLocation)
                    .to(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
                    .avoid(AvoidType.FERRIES)
                    .avoid(AvoidType.HIGHWAYS)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if(direction.isOK()) {
                                mNearbyDonorCounter++;
                                Route route = direction.getRouteList().get(0);
                                String getLocationText = "";
                                try {
                                    String routes = new JSONObject(rawBody).getString("routes");
                                    String legs = new JSONArray(routes).getJSONObject(0).getString("legs");

                                    String distanceJson = new JSONArray(legs).getJSONObject(0).getString("distance");
                                    String distance = new JSONObject(distanceJson).getString("text");

                                    getLocationText = String.format("%s", distance);
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
//                                mMap.addMarker(new MarkerOptions()
//                                        .position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
//                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blood_donor_maker))
//                                        .title(getLocationText))
//                                        .showInfoWindow();
//
//                                MarkerOptions markerOptions = new MarkerOptions();
//                                markerOptions.position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
//                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.blood_donor_maker));
//                                markerOptions.title(getLocationText);
//                                mMap.addMarker(markerOptions).showInfoWindow();

                                Bitmap bmp = CommonUtil.drawNumberInsideMarker(BitmapFactory.decodeResource(getResources(), R.mipmap.pin_drop_text), getLocationText, 0xffffffff, 26.0f, 8, 100);

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));

                                Marker marker = mMap.addMarker(markerOptions);

                                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                mNearbyDonorPolyLines.add(mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 7, Color.DKGRAY)));

                                if(mNearbyDonorCounter == geoPointList.size()) {
                                    CommonUtil.showProgressDialog(getActivity(), null, false);
                                }

                            } else {
                                CommonUtil.showProgressDialog(getActivity(), null, false);
                            }

                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            CommonUtil.showProgressDialog(getActivity(), null, false);
                        }
                    });
            }
        } catch (Exception e) {
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();
        }
    }

    private void initializeGoogleApi() {
        if(getContext() == null) {
            return;
        }
        try {
            if(mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this).build();
                //.addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this).build();
                mGoogleApiClient.connect();
            }
            if(mLocationRequest == null) {
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(5 * 1000);
                mLocationRequest.setFastestInterval(2 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForGPSStatus() {
        if(mGoogleApiClient == null || mLocationRequest == null) {
            return;
        }
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    final Status status = result.getStatus();
//                final LocationSettingsStates state = result.getLocationSettingsStates();

                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            isGPSOn = true;
//                            if(mCurrentLocation == null) {
                                updateCurrentLocation(12f);
//                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
//                                mIsGpsPopUp = true;
                                status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_LOCATION) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                isGPSOn = true;
//                if(mCurrentLocation == null) {
                    updateCurrentLocation(12f);
//                }
            }
        }
    }

    public void updateCurrentLocation(final float cameraZoom) {
        try {
            if(getContext() != null) {
                if (ActivityCompat.checkSelfPermission((Activity) getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission((Activity) getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermission();
                    return;
                }
            }
            stopLocationUpdates();
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (mScreenMode == REQUEST_LOCATION_CONFIRM_SCREEN) {
                            updateLocationOnMap(new LatLng(location.getLatitude(), location.getLongitude()), true, cameraZoom, true, true);
                        } else {
                            updateLocationOnMap(new LatLng(location.getLatitude(), location.getLongitude()), false, cameraZoom, true, true);
                        }
                        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        updateAddressOnScreen();                    // updating address on location Search Bar
                        // update user location on firestore
                        UserDao userDao = new UserDao(getContext());
                        final UserDao.SingleUser singleUser = userDao.getRowById(null);
                        UserFireStore.updateUserLocation(getContext(), singleUser.userId, new GeoPoint(location.getLatitude(), location.getLongitude()));        //.. NEED TO REMOVE THE COMMENT BEFORE RELEASE
                        userDao.updateUserLocation(singleUser.userId, new GeoPoint(location.getLatitude(), location.getLongitude()));              //.. NEED TO REMOVE THE COMMENT BEFORE RELEASE
                        if (mScreenMode == REQUEST_HOME_SCREEN) {
                            addDonorMarker(singleUser, -1, -1, null);
                        } else if (mScreenMode == REQUEST_BLOOD_CONFIRM_SCREEN) {
                            addDonorMarker(singleUser, mCurrentLocation.latitude, mCurrentLocation.longitude, mSearchBloodType.getSelectedItem().toString());
                        }
                        if (mScreenMode == HOSPITAL_MAIN_SCREEN) {
                            addHospitalMarker(-1, -1, null);
                        }
                        if (mScreenMode == DOCTOR_MAIN_SCREEN) {
                            addDoctorMarker(-1, -1, null);
                        }
                    }
                    stopLocationUpdates();
                }
            };

//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                if(location != null)
//                    updateLocationOnMap(new LatLng(location.getLatitude(), location.getLongitude()));
//                stopLocationUpdates();
//            }
//        });

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            if (mFusedLocationClient == null) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            }
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

//        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if(location != null)
//            updateLocationOnMap(new LatLng(location.getLatitude(), location.getLongitude()));
//        mFusedLocationClient.getLastLocation()
//                .addOnCompleteListener(new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//
//                    }
//                })
//                .addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if(location != null)
//                    updateLocationOnMap(new LatLng(location.getLatitude(), location.getLongitude()));
//            }
//        });

//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
//        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopLocationUpdates() {
        if(mLocationCallback == null || mFusedLocationClient == null) {
            return;
        }
        try {
            if (mGoogleApiClient.isConnected()) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_FINE_LOCATION)) {
                Toast.makeText(getActivity(),
                        "Location permission are required", Toast.LENGTH_LONG).show();
            }
            if(shouldShowRequestPermissionRationale(PERMISSION_CALL_PHONE)) {
                Toast.makeText(getActivity(),
                        "Phone call permission are required", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{PERMISSION_COARSE_LOCATION, PERMISSION_FINE_LOCATION, PERMISSION_CALL_PHONE}, PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, final String[] permissions, final int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                updateCurrentLocation(12f);
            } else {
                requestPermission();
            }
        }
    }

    private void updateLocationOnMap(LatLng latLng, boolean showMarker, float cameraZoom, boolean isClearMap, boolean isAnimateCamera) {
        if (mMap == null || latLng == null) {
            return;
        }
        try {
            if(isClearMap) {
                mMap.clear();
            }
            mAnimateCamera = true;
            if(showMarker) {
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_drop)));       // show UBER designed marker HERE...

//                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Islamabad"));
            }
            mCurrentLocation = latLng;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            if(isAnimateCamera) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), cameraZoom));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDonorMarker(UserDao.SingleUser singleUser, double lat, double lng, final String bloodType) {
        if(singleUser == null) {
            return;
        }
        try {
            UserFireStore.loadDonorsLocation(singleUser.userId, lat, lng, bloodType, new BaseFireStore.FireStoreCallback() {
                @Override
                public void done(Exception fireBaseException, Object object) {
                    if(fireBaseException == null) {
                        ArrayList<GeoPoint> geoPoints = (ArrayList<GeoPoint>) object;
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for(GeoPoint geoPoint : geoPoints) {
                            LatLng donorLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                            mMap.addMarker(new MarkerOptions()
                                    .position(donorLatLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.blood_donor_maker))
                            );
                            builder.include(donorLatLng);
                        }
                        if(!isNullOrEmpty(bloodType)) {
                            mNearbyDonorsLocation = geoPoints;
                            distanceBetweenNearbyDonors(geoPoints);
                        }

                        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 100);
                        //mMap.animateCamera(cameraUpdate);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<HospitalInfo> mHospitalInfoArrayList = null;
    private void addHospitalMarker(double lat, double lng, final String hospitalType) {
        try {
            HospitalFireStore.loadNearbyHospitals(lat, lng, hospitalType, new BaseFireStore.FireStoreCallback() {
                @Override
                public void done(Exception fireBaseException, Object object) {
                    if(fireBaseException == null) {
                        Task<QuerySnapshot> hospitalsData = (Task<QuerySnapshot>) object;
                        if (!isNullOrEmpty(hospitalType)) {
                            mHospitalInfoArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : hospitalsData.getResult()) {
                                HospitalInfo hospitalInfo = new HospitalInfo();
                                if (document.get(HospitalFireStore.COLUMN_HOSPITAL_LOCATION) != null) {
                                    hospitalInfo.setHospitalLocation((GeoPoint) document.get(HospitalFireStore.COLUMN_HOSPITAL_LOCATION));
                                }
                                hospitalInfo.setHospitalName((String) document.get(HospitalFireStore.COLUMN_HOSPITAL_NAME));
                                hospitalInfo.setHospitalAddress((String) document.get(HospitalFireStore.COLUMN_HOSPITAL_ADDRESS));
                                hospitalInfo.setHospitalContact((String) document.get(HospitalFireStore.COLUMN_HOSPITAL_NUMBER));
                                hospitalInfo.setHospitalType((String) document.get(HospitalFireStore.COLUMN_HOSPITAL_TYPE));
                                mHospitalInfoArrayList.add(hospitalInfo);
                            }
                            if(mHospitalInfoArrayList.size() > 0) {
                                addHospitalMarkerOnMap(mHospitalInfoArrayList);
                            } else {
                                CommonUtil.showProgressDialog(getActivity(), null, false);
                                CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_information), getString(R.string.dialog_message_no_nearby_hospital_found), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                            }
                        } else {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (QueryDocumentSnapshot document : hospitalsData.getResult()) {
                                GeoPoint geoPoint = (GeoPoint) document.get(HospitalFireStore.COLUMN_HOSPITAL_LOCATION);
                                String hosType = (String) document.get(HospitalFireStore.COLUMN_HOSPITAL_TYPE);
                                if(geoPoint != null) {
                                    LatLng donorLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                    int res = -1;
                                    assert hosType != null;
                                    if(hosType.equals(CommonConstants.HOSPITAL_TYPE_BLOOD_BANK)) {
                                        res = R.mipmap.blood_bank_marker;
                                    } else if(hosType.equals(CommonConstants.HOSPITAL_TYPE_ALL_HOSPITAL)) {
                                        res = R.mipmap.hospital_marker;
                                    } else if(hosType.equals(CommonConstants.HOSPITAL_TYPE_HAEMOPHILLIA)) {
                                        res = R.mipmap.hemophilia_marker;
                                    } else if(hosType.equals(CommonConstants.HOSPITAL_TYPE_LUKEMIA)) {
                                        res = R.mipmap.leukemia_marker;
                                    } else if(hosType.equals(CommonConstants.HOSPITAL_TYPE_THALYSEMMIA)) {
                                        res = R.mipmap.thalassemia_marker;
                                    }

                                    mMap.addMarker(new MarkerOptions()
                                            .position(donorLatLng)
                                            .icon(BitmapDescriptorFactory.fromResource(res))
                                            .title(hosType)
                                    );

                                    builder.include(donorLatLng);
                                }
                            }
                            CommonUtil.showProgressDialog(getActivity(), null, false);
                        }
                    } else {
                        CommonUtil.showProgressDialog(getActivity(), null, false);
                    }

                }
            });
            String makeText = "";
            if(CommonUtil.isNullOrEmpty(hospitalType)) {
                makeText = String.format(getString(R.string.dialog_message_finding_nearby_hospitals), "Hospitals");
            } else {
                makeText = String.format(getString(R.string.dialog_message_finding_nearby_hospitals), hospitalType + " Centers");
            }
            CommonUtil.showProgressDialog(getActivity(), makeText, true);

        } catch (Exception e) {
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();
        }
    }

    ArrayList<DoctorInfo> mDoctorInfoArrayList = null;
    private void addDoctorMarker(double lat, double lng, final String docType) {
        try {
            DoctorFireStore.loadNearbyDoctors(lat, lng, docType, new BaseFireStore.FireStoreCallback() {
                @Override
                public void done(Exception fireBaseException, Object object) {
                    if(fireBaseException == null) {
                        Task<QuerySnapshot> doctorsData = (Task<QuerySnapshot>) object;
                        if(!CommonUtil.isNullOrEmpty(docType)) {
                            mDoctorInfoArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : doctorsData.getResult()) {
                                DoctorInfo doctorInfo = new DoctorInfo();
                                if (document.get(DoctorFireStore.COLUMN_DOCTOR_LOCATION) != null) {
                                    doctorInfo.setDocLocation((GeoPoint) document.get(DoctorFireStore.COLUMN_DOCTOR_LOCATION));
                                }
                                doctorInfo.setDocName((String) document.get(DoctorFireStore.COLUMN_DOCTOR_NAME));
                                doctorInfo.setDocAddress((String) document.get(DoctorFireStore.COLUMN_DOCTOR_ADDRESS));
                                doctorInfo.setDocContact((String) document.get(DoctorFireStore.COLUMN_DOCTOR_NUMBER));
                                doctorInfo.setDocType((String) document.get(DoctorFireStore.COLUMN_DOCTOR_TYPE));
                                mDoctorInfoArrayList.add(doctorInfo);
                            }
                            if(mDoctorInfoArrayList.size() > 0) {
                                addDoctorMarkerOnMap(mDoctorInfoArrayList);
                            } else {
                                CommonUtil.showProgressDialog(getActivity(), null, false);
                                CommonUtil.showDialog(getActivity(), getString(R.string.dialog_title_information), getString(R.string.dialog_message_no_nearby_doctor_found), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, new CommonUtil.AlertDialogOnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        changeMapScreen(DOCTOR_MAIN_SCREEN);
                                    }
                                });
                            }
                        } else {
                            for (QueryDocumentSnapshot document : doctorsData.getResult()) {
                                GeoPoint geoPoint = (GeoPoint) document.get(DoctorFireStore.COLUMN_DOCTOR_LOCATION);
                                String docType = (String) document.get(DoctorFireStore.COLUMN_DOCTOR_TYPE);
                                if(geoPoint != null) {
                                    LatLng donorLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                    mMap.addMarker(new MarkerOptions()
                                            .position(donorLatLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.doctor_marker))// replace with doctor marker
                                    );
                                }
                            }
                            CommonUtil.showProgressDialog(getActivity(), null, false);
                        }
                    } else {
                        CommonUtil.showProgressDialog(getActivity(), null, false);
                    }
                }
            });
            CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_finding_nearby_doctors), true);
        } catch (Exception e) {
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();
        }
    }

    private void setAllMapGesture(boolean isGesture) {
        if(mMap == null) {
            return;
        }
        if(isGesture) {
            mMap.getUiSettings().setAllGesturesEnabled(true);
        } else {
            mMap.getUiSettings().setAllGesturesEnabled(false);
        }
    }

    private void clearMapPoint() {
        if(mMap == null) {
            return;
        }
        mMap.clear();
    }

    public void setMapAttr(boolean isTrafficOn) {
        if(mMap == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }

        mMap.setTrafficEnabled(isTrafficOn);
        if(isTrafficOn) {
            mCurrentLocationMap.setVisibility(View.VISIBLE);
        } else {
            mCurrentLocationMap.setVisibility(View.GONE);
        }

        mMap.setMyLocationEnabled(isTrafficOn);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(getActivity() == null) {
            return;
        }
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }

        setMapAttr(true);
//        uiSettings.setMapToolbarEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.setOnCameraMoveStartedListener(this);
       // mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
//        mMap.setInfoWindowAdapter(this);
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

//        mMap.getUiSettings().setAllGesturesEnabled(false);

        mMap.setPadding(0, 0, 0, 150);//left, top, right and bottom

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mAnimateCamera = false;
                return false;
            }
        });

        mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {

            }
        });

//        mMap.setOnMyLocationClickListener(this);

        //update current location after map is initialize
        //if(isGPSOn) {
//            updatedLocationFromGPS();
        //}

    }

    public boolean removeDonorListFragment(int useFragmentManager, boolean animate) {
        if(mDonorListFragment != null) {
            dismissChildFragment(mDonorListFragment, useFragmentManager, false, animate);
            mDonorListFragment = null;
//            invalidateActivityOptionsMenu((MainActivity) getActivity());
            updateActionBar((MainActivity) getActivity());
//            mArrayAdapter.notifyDataSetChanged();
//            setCircularProgressBar();
            return true;
        }
        return false;
    }

    public boolean removeCustomDialogFragment(int useFragmentManager, boolean animate) {
        if(mCustomDialogBoxFragment != null) {
            dismissChildFragment(mCustomDialogBoxFragment, useFragmentManager, false, animate);
            mCustomDialogBoxFragment = null;
            updateActionBar((MainActivity) getActivity());
            return true;
        }
        return false;
    }


    public boolean removeHospitalDialogFragment(int useFragmentManager, boolean animate) {
        if(mHospitalDetailsViewFragment != null) {
            dismissChildFragment(mHospitalDetailsViewFragment, useFragmentManager, false, animate);
            mHospitalDetailsViewFragment = null;
            updateActionBar((MainActivity) getActivity());
            return true;
        }
        return false;
    }

    public boolean removeDoctorDialogFragment(int useFragmentManager, boolean animate) {
        if(mDoctorDetailsViewFragment != null) {
            dismissChildFragment(mDoctorDetailsViewFragment, useFragmentManager, false, animate);
            mDoctorDetailsViewFragment = null;
            updateActionBar((MainActivity) getActivity());
            return true;
        }
        return false;
    }

    @Override
    public boolean onBackButtonPress() {
        if(mScreenMode == REQUEST_BLOOD_CONFIRM_SCREEN) {
            updateCurrentLocation(12f);
            mIsSearchLocation = false;
            changeMapScreen(REQUEST_HOME_SCREEN);
            return true;
        } else if(mScreenMode == REQUEST_LOCATION_CONFIRM_SCREEN) {
            updateCurrentLocation(12f);
            mIsSearchLocation = false;
            changeMapScreen(REQUEST_BLOOD_CONFIRM_SCREEN);
            return true;
        } else if (mScreenMode == REQUEST_BLOOD_SEARCH_SCREEN) {
            return true;
        } else if(mScreenMode == REQUEST_ACCEPT_DONOR_LIST_FRAGMENT) {
            if(removeDonorListFragment(CommonConstants.USE_FRAGMENT_MANAGER_CHILD, true)) {
                changeMapScreen(REQUEST_ACCEPT_DONOR_SCREEN);
                return true;
            }
            return false;
        } else if(mScreenMode == REQUEST_ACCEPT_CUSTOM_DIALOG_BOX_FRAGMENT) {
            if(removeCustomDialogFragment(CommonConstants.USE_FRAGMENT_MANAGER_CHILD, true)) {
                setIsCallOrMessage(false);
                changeMapScreen(REQUEST_ACCEPT_DONOR_SCREEN);
                return true;
            }
            return false;
        } else if(mScreenMode == REQUEST_ACCEPT_DONOR_SCREEN) {
            SharedPreferenceHelper.setKeyValue(getContext(), CommonConstants.CHECK_REQUEST_COMPLETE, "1");
        } else if(mScreenMode == HOSPITAL_MAIN_SCREEN) {
            updateCurrentLocation(12f);
            mIsSearchLocation = false;
            changeMapScreen(REQUEST_HOME_SCREEN);
            setDropDownAdapter(REQUEST_HOME_SCREEN);
            return true;
        } else if(mScreenMode == HOSPITAL_NEARBY_SCREEN) {
            updateCurrentLocation(12f);
            changeMapScreen(HOSPITAL_MAIN_SCREEN);
            return true;
        } else if(mScreenMode == DOCTOR_MAIN_SCREEN) {
            updateCurrentLocation(12f);
            mIsSearchLocation = false;
            changeMapScreen(REQUEST_HOME_SCREEN);
            setDropDownAdapter(REQUEST_HOME_SCREEN);
            return true;
        } else if(mScreenMode == DOCTOR_NEARBY_SCREEN) {
            updateCurrentLocation(12f);
            changeMapScreen(DOCTOR_MAIN_SCREEN);
            return true;
        } else if(mScreenMode == HOSPITAL_DIALOG_SCREEN) {
            if(removeHospitalDialogFragment(CommonConstants.USE_FRAGMENT_MANAGER_CHILD, true)) {
                setIsCallOrMessage(false);
                changeMapScreen(HOSPITAL_NEARBY_SCREEN);
                //updateCurrentLocation(12f);
                return true;
            }
            return false;
        } else if(mScreenMode == DOCTOR_DETAILS_SCREEN) {
            if(removeDoctorDialogFragment(CommonConstants.USE_FRAGMENT_MANAGER_CHILD, true)) {
                setIsCallOrMessage(false);
                changeMapScreen(DOCTOR_NEARBY_SCREEN);
                return true;
            }
            return false;
        }

        //dismissFragment(false, getString(R.string.SignInFragmentTag));
        return false;
    }

    public void setIsCallOrMessage(boolean status) {
        mIsCallOrMessage = status;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mPlaceAutoCompleteFragment != null && getActivity() != null && !getActivity().isFinishing()) {
            getActivity().getFragmentManager().beginTransaction().remove(mPlaceAutoCompleteFragment).commit();
        }
    }

    @Override
    protected void onResumeFragment() {
        try {
//            if(!mIsGpsPopUp) {
            if(mScreenMode == REQUEST_HOME_SCREEN && !mIsSearchLocation) {
                initializeGoogleApi();

            }
//            else {
//                mIsSearchLocation = false;
//            }

            String uId = SharedPreferenceHelper.getKeyValue(getContext(), CommonConstants.DONOR_ACCEPT_REQUESTER_ID, "0");
            String requestId = SharedPreferenceHelper.getKeyValue(getContext(), CommonConstants.REQUEST_ID, "0");
            if(!uId.equals("0") ) {
                SharedPreferenceHelper.setKeyValue(getContext(), CommonConstants.DONOR_ACCEPT_REQUESTER_ID, "0");
                getAcceptedDonorData(uId, requestId);
            } else {
                if(!mIsSearchLocation && !mIsCallOrMessage) {
                    checkForGPSStatus();
                }
            }

//            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPauseFragment() {
        try {
            mIsGpsPopUp = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(getContext(), PERMISSION_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(getContext(), PERMISSION_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocationOnMap(new LatLng(location.getLatitude(), location.getLongitude()), false, 12f, true,true);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if(bundle != null)
            Log.d(TAG_GPS_STATUS, "Connected"+bundle.toString());
        else
            Log.d(TAG_GPS_STATUS, "Connected:Null");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG_GPS_STATUS, "Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG_GPS_STATUS, "ConnectionResult"+connectionResult.toString());
    }


    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        try {
            final Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                isGPSOn = true;
                updateCurrentLocation(12f);
                Log.i(TAG_GPS_STATUS, "All location settings are satisfied.");
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG_GPS_STATUS, "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings ");
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG_GPS_STATUS, "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created.");
                break;
            }
        } catch (IntentSender.SendIntentException e) {
            Log.i(TAG_GPS_STATUS, "PendingIntent unable to execute request.");
        }
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            //Toast.makeText(getActivity(), "The user gestured on the map.", Toast.LENGTH_SHORT).show();
            mAnimateCamera = false;
            if (mScreenMode == REQUEST_LOCATION_CONFIRM_SCREEN && !mAnimateCamera) {
                mConfirmLocationView.setVisibility(View.VISIBLE);
            }
        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
            //Toast.makeText(getActivity(), "The user tapped something on the map.", Toast.LENGTH_SHORT).show();
        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
            //Toast.makeText(getActivity(), "The app moved the camera.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCameraMoveCanceled() {
        //Toast.makeText(getActivity(), "Camera movement canceled.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraMove() {
        //Toast.makeText(getActivity(), "The camera is moving.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCameraIdle() {
        //Toast.makeText(getActivity(), "The camera has stopped moving.", Toast.LENGTH_SHORT).show();
        if(mScreenMode == REQUEST_LOCATION_CONFIRM_SCREEN && !mAnimateCamera) {
            setUserLocationState(CommonConstants.MAP_CAMERA_STOP, false);
            mConfirmLocationView.setVisibility(View.GONE);
            mAnimateCamera = true;
        }
    }

//    @Override
//    public View getInfoWindow(Marker marker) {
//        return null;
//    }
//
//    @Override
//    public View getInfoContents(Marker marker) {
//        View view = null;
//        try {
//            assert (getActivity()) != null;
//            view = (getActivity()).getLayoutInflater().inflate(R.layout.info_window_accepted_donor, null);
//
//            TextView donorName = view.findViewById(R.id.info_window_donor_name);
//
//            donorName.setText("FROM CODE!!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return view;
//    }


    boolean mCheckBloodReqStatus = false;
    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            final int index = mMarkerList.indexOf(marker);
            if (index <= -1) {
                return false;
            }
//            if(mScreenMode != DONOR_ACCEPT_SCREEN) {
//                changeMapScreen(REQUEST_ACCEPT_CUSTOM_DIALOG_BOX_FRAGMENT);
//            }

            if(mScreenMode == HOSPITAL_NEARBY_SCREEN) {
                mHospitalDetailsViewFragment = new HospitalDetailsViewFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("hospital", mHospitalInfoArrayList.get(index));
                bundle.putInt("screen", mScreenMode);
                changeMapScreen(HOSPITAL_DIALOG_SCREEN);
                UserMapFragment.this.presentChildFragment(mHospitalDetailsViewFragment, getString(R.string.HospitalDialogBoxFragmentTag), false, bundle);
            } else if(mScreenMode == DOCTOR_NEARBY_SCREEN) {
                mDoctorDetailsViewFragment = new DoctorDetailsViewFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("doctor", mDoctorInfoArrayList.get(index));
                bundle.putInt("screen", mScreenMode);
                changeMapScreen(DOCTOR_DETAILS_SCREEN);
                UserMapFragment.this.presentChildFragment(mDoctorDetailsViewFragment, getString(R.string.DoctorDetailsViewFragmentTag), false, bundle);
            } else {
                if(!mCheckBloodReqStatus) {
                    RequestFireStore.getCompleteRequestStatus(mDonorInfoList.get(index).requestId, new BaseFireStore.FireStoreCallback() {
                        @Override
                        public void done(Exception fireBaseException, Object object) {
                            if (object != null) {
                                try {
                                    Task<DocumentSnapshot> task = (Task<DocumentSnapshot>) object;
                                    if (task.getResult().getLong(RequestDao.COLUMN_REQUEST_COMPLETED) == CommonConstants.USER_REQUEST_CANCELED) {
                                        mCheckBloodReqStatus = false;
                                        changeMapScreen(CommonConstants.REQUEST_HOME_SCREEN);
                                        setDropDownAdapter(CommonConstants.REQUEST_HOME_SCREEN);
                                        updateCurrentLocation(12f);
                                        Toast.makeText(getContext(), getString(R.string.dialog_message_request_canceled), Toast.LENGTH_LONG).show();
                                    } else {
                                        mCheckBloodReqStatus = true;
                                        showCustomDialogFragment(index);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {

                            }
                        }
                    });
                } else {
                    showCustomDialogFragment(index);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showCustomDialogFragment(int index) {
        try {
            mCustomDialogBoxFragment = new CustomDialogBoxFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("donor", mDonorInfoList.get(index));
            bundle.putInt("screen", mScreenMode);
            changeMapScreen(REQUEST_ACCEPT_CUSTOM_DIALOG_BOX_FRAGMENT);
            UserMapFragment.this.presentChildFragment(mCustomDialogBoxFragment, getString(R.string.CustomDialogBoxFragmentTag), false, bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
