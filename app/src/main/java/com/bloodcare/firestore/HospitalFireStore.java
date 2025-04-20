package com.bloodcare.firestore;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bloodcare.dao.UserDao;
import com.bloodcare.utility.CommonUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.bloodcare.utility.CommonUtil.isNullOrEmpty;
import static com.bloodcare.utility.LogTags.TAG_QUERY;

/**
 * Created by osamazeeshan on 26/12/2018.
 */

public class HospitalFireStore extends BaseFireStore {


    public static final String COLLECTION_NAME = "hospitals";
    public static final String COLUMN_HOSPITAL_NAME = "hospitalName";
    public static final String COLUMN_HOSPITAL_ID = "hospitalId";
    public static final String COLUMN_HOSPITAL_ADDRESS = "hospitalAddress";
    public static final String COLUMN_HOSPITAL_TYPE = "hospitalType";
    public static final String COLUMN_HOSPITAL_LOCATION = "hospitalLocation";
    public static final String COLUMN_HOSPITAL_NUMBER = "hospitalNumber";

    public static void loadNearbyHospitals(double latitude, double longitude, String hospitalType, final FireStoreCallback fireStoreCallback) {
        try {
            final ArrayList<GeoPoint> locationArray = new ArrayList<>();
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            Query queryDocumentSnapshot;
            if(latitude != -1 && longitude != -1 && !isNullOrEmpty(hospitalType)) {
                ArrayList<GeoPoint> locationRadius = CommonUtil.calculateNearbyLocation(5, latitude, longitude);
                queryDocumentSnapshot = firebaseFirestore.collection(COLLECTION_NAME)
                        .whereGreaterThan(COLUMN_HOSPITAL_LOCATION, locationRadius.get(0))
                        .whereEqualTo(COLUMN_HOSPITAL_TYPE, hospitalType)
                        .whereLessThan(COLUMN_HOSPITAL_LOCATION, locationRadius.get(1));
            }
            // Note: if add two or more query need to create a custom index in firestore console, by default firestore only create single index to query with one item
            else {
                queryDocumentSnapshot = firebaseFirestore.collection(COLLECTION_NAME);
            }

            queryDocumentSnapshot.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
//
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Log.d(TAG_QUERY, document.getId() + " => " + document.getData());
//                            if(document.get(COLUMN_HOSPITAL_LOCATION) != null) {
//                                locationArray.add((GeoPoint) document.get(COLUMN_HOSPITAL_LOCATION)); // store all information
//                            }
//                        }
                        fireStoreCallback.done(null, task);
                    } else {
                        Log.d(TAG_QUERY, "Error getting documents: ", task.getException());
                        fireStoreCallback.done(new Exception("Error getting documents"), null);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
