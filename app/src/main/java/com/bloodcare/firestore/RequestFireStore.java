package com.bloodcare.firestore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bloodcare.dao.RequestDao;
import com.bloodcare.dao.UserDao;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.bloodcare.utility.CommonUtil.isNullOrEmpty;
import static com.bloodcare.utility.LogTags.TAG_CREATE_REQUEST;
import static com.bloodcare.utility.LogTags.TAG_DELETE_ALL_REQUEST;
import static com.bloodcare.utility.LogTags.TAG_QUERY;
import static com.bloodcare.utility.LogTags.TAG_REAL_TIME_UPDATE_REQUEST;

/**
 * Created by osamazeeshan on 28/08/2018.
 */

public class RequestFireStore extends BaseFireStore {

    private RequestFireStore() {
    }

    public static boolean createRequest(Map<String, Object> data, boolean callAsync, final FireStoreCallback fireStoreCallback) {
        if(data == null) {
            return false;
        }
        try {

//            ArrayList<String> requestTokenList = getRequestToken("AB_Pos", false, null);
//            if(requestTokenList != null) {
//                data.put(RequestDao.COLUMN_REQUEST_TOKEN, requestTokenList);
//            }

            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            if (callAsync) {
                firebaseFirestore.collection(RequestDao.COLLECTION_NAME)
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                //documentReference.getId();
                                fireStoreCallback.done(null, documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d(TAG_CREATE_REQUEST, exception.getMessage());
                            }
                        });

            } else {
                firebaseFirestore.collection(RequestDao.COLLECTION_NAME).document().set(data);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public static void updateCompleteRequest(String requestId, int request) {
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            firebaseFirestore.collection(RequestDao.COLLECTION_NAME).document(requestId)
                    .update(RequestDao.COLUMN_REQUEST_COMPLETED , request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getCompleteRequestStatus(String requestId, final FireStoreCallback fireStoreCallback) {
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();

            DocumentReference documentReference = firebaseFirestore.collection(RequestDao.COLLECTION_NAME).document(requestId);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (fireStoreCallback != null) {
                        fireStoreCallback.done(null, task);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void updateRequest(String requestId, String field, String val) {
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();

//            firebaseFirestore.collection(RequestDao.COLLECTION_NAME).document(requestId)
//                    .update(
//                            "acceptRequest." + val, val
//                    );

            firebaseFirestore.collection(RequestDao.COLLECTION_NAME).document(requestId)
                    .update("acceptRequest" , FieldValue.arrayUnion(val));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ListenerRegistration realTimeUpdateRequest(String requestId, ListenerRegistration listenerRegistration, final FireStoreCallback fireStoreCallback) {
        try {
            if(listenerRegistration != null) {
                listenerRegistration.remove();
                return null;
            }
            final FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            final DocumentReference docRef = firebaseFirestore.collection(RequestDao.COLLECTION_NAME).document(requestId);
            listenerRegistration =
                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG_REAL_TIME_UPDATE_REQUEST, "Listen failed.", e);
                                return;
                            }

                            if (snapshot != null && snapshot.exists()) {
                                Log.d(TAG_REAL_TIME_UPDATE_REQUEST, "Current data: " + snapshot.get("acceptRequest"));
                                if(fireStoreCallback != null) {
                                    fireStoreCallback.done(null, snapshot.get("acceptRequest"));

                                }
                            } else {
                                Log.d(TAG_REAL_TIME_UPDATE_REQUEST, "Current data: null");
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listenerRegistration;
    }

    public static ArrayList<String> getRequestToken(String bloodType, final String currentDeviceToken, double latitude, double longitude, boolean callAsync, final FireStoreCallback fireStoreCallback) {
        if(CommonUtil.isNullOrEmpty(bloodType) || CommonUtil.isNullOrEmpty(currentDeviceToken)) {
            return null;
        }
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            final ArrayList<String> tokenArrayList = new ArrayList<>();

            ArrayList<GeoPoint> locationRadius = CommonUtil.calculateNearbyLocation(5, latitude, longitude);   //.. roughly 8 Km radius
            CollectionReference usersReference = firebaseFirestore.collection(UserDao.COLLECTION_NAME);
            Query query = usersReference
                    .whereEqualTo(UserDao.COLUMN_BLOOD_TYPE, bloodType)
                    .whereEqualTo(UserDao.COLUMN_IS_DONOR, true)
                    .whereGreaterThan(UserFireStore.COLUMN_LOCATION, locationRadius.get(0))
                    .whereLessThan(UserFireStore.COLUMN_LOCATION, locationRadius.get(1))
                    .whereEqualTo(UserDao.COLUMN_BLOOD_DONATE_STATUS, true);

            // before release add
            // * include isEligible check
            // * last three month check
            // * age > 15

            if(callAsync) {
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (fireStoreCallback != null) {
                            for (QueryDocumentSnapshot querySnapshot : task.getResult()) {
                                Log.d(TAG_QUERY, querySnapshot.getString(UserDao.COLUMN_DEVICE_TOKEN));
                                if(!currentDeviceToken.equals(querySnapshot.getString(UserDao.COLUMN_DEVICE_TOKEN))) {
                                    tokenArrayList.add(querySnapshot.getString(UserDao.COLUMN_DEVICE_TOKEN));
                                }
                            }
                            fireStoreCallback.done(null, tokenArrayList);
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception fireStoreException) {
                        fireStoreCallback.done(fireStoreException, null);
                    }
                });

            } else {

                query.get().continueWithTask(new Continuation<QuerySnapshot, Task<QuerySnapshot>>() {
                    @Override
                    public Task<QuerySnapshot> then(@NonNull Task<QuerySnapshot> task) throws Exception {

                        return task;
                    }
                });
//                for (QueryDocumentSnapshot querySnapshot : task.getResult()) {
//                    tokenArrayList.add(querySnapshot.getString(UserDao.COLUMN_USER_TOKEN));
//                }
                return tokenArrayList;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void checkPendingRequest(String userId, final FireStoreCallback fireStoreCallback) {
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            final ArrayList<String> requestDetail = new ArrayList<>();

            CollectionReference usersReference = firebaseFirestore.collection(RequestDao.COLLECTION_NAME);
            Query query = usersReference
                    .whereEqualTo(UserDao.COLUMN_USER_ID, userId)
                    .whereEqualTo(RequestDao.COLUMN_REQUEST_COMPLETED, CommonConstants.USER_REQUEST_PENDING);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (fireStoreCallback != null) {
                        for (QueryDocumentSnapshot querySnapshot : task.getResult()) {
                            requestDetail.add(querySnapshot.getString(UserDao.COLUMN_BLOOD_TYPE));
                            requestDetail.add(querySnapshot.getString(UserDao.COLUMN_CREATION_TIMESTAMP));
                            requestDetail.add(querySnapshot.getId());
                        }
                        fireStoreCallback.done(null, requestDetail);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getRequestHistory(String userId, final FireStoreCallback fireStoreCallback) {
        if(CommonUtil.isNullOrEmpty(userId)) {
            return;
        }
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            final ArrayList<String> tokenArrayList = new ArrayList<>();

            CollectionReference usersReference = firebaseFirestore.collection(RequestDao.COLLECTION_NAME);

            Query query = usersReference
                    .whereEqualTo(RequestDao.COLUMN_USER_ID, userId)
                    .orderBy(RequestDao.COLUMN_CREATION_TIMESTAMP, Query.Direction.DESCENDING);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (fireStoreCallback != null) {
                            fireStoreCallback.done(null, task);
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception fireStoreException) {
                        fireStoreCallback.done(fireStoreException, null);
                    }
                });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllRequests() {
        FirebaseFirestore firebaseFirestore = getFireStoreInstance();
        firebaseFirestore.collection("cities").document("DC")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG_DELETE_ALL_REQUEST, "All request successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG_DELETE_ALL_REQUEST, "Error deleting request document: ", e);
                    }
                });
    }

}
