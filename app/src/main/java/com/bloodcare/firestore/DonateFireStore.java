package com.bloodcare.firestore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bloodcare.dao.RequestDao;
import com.bloodcare.dao.UserDao;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bloodcare.utility.CommonUtil.isNullOrEmpty;
import static com.bloodcare.utility.LogTags.TAG_CREATE_REQUEST;

/**
 * Created by osamazeeshan on 29/10/2018.
 */

public class DonateFireStore extends BaseFireStore {

    public static final String COLLECTION_NAME = "donate";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_DONOR_NAME = "donorName";
    public static final String COLUMN_REQUESTER_ID = "requesterId";
    public static final String COLUMN_REQUEST_ID = "requestId";
    public static final String COLUMN_REQUESTER_NAME = "requesterName";
    public static final String COLUMN_USER_BLOOD_TYPE = "bloodType";
    public static final String COLUMN_DONOR_LOCATION = "donorLocation";
    public static final String COLUMN_REQUESTER_LOCATION = "requesterLocation";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_CREATION_TIMESTAMP = "timestamp";
    public static final String COLUMN_DONATE_COMPLETED = "donateCompleted";

    public static boolean createDonateRequest(Map<String, Object> data, boolean callAsync, final FireStoreCallback fireStoreCallback) {
        if(data == null) {
            return false;
        }
        try {

//            ArrayList<String> requestTokenList = getRequestToken("AB_Pos", false, null);
//            if(requestTokenList != null) {
//                data.put(RequestDao.COLUMN_REQUEST_TOKEN, requestTokenList);
//            }

            final FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            if (callAsync) {
                firebaseFirestore.collection(COLLECTION_NAME)
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                //documentReference.getId();
                                if(fireStoreCallback != null) {
                                    fireStoreCallback.done(null, documentReference.getId());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d(TAG_CREATE_REQUEST, exception.getMessage());
                            }
                        });

            } else {
                firebaseFirestore.collection(COLLECTION_NAME).document().set(data);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateDonateRequest(String donateId, long request) {
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            firebaseFirestore.collection(COLLECTION_NAME).document(donateId)
                    .update(COLUMN_DONATE_COMPLETED , request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getDonateHistory(String userId, final FireStoreCallback fireStoreCallback) {
        if(CommonUtil.isNullOrEmpty(userId)) {
            return;
        }
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();

            CollectionReference usersReference = firebaseFirestore.collection(COLLECTION_NAME);
            Query query = usersReference
                    .whereEqualTo(COLUMN_USER_ID, userId)
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

    public static void getAcceptedDonors(String requesterId, String acceptedDonor, String requestId, final FireStoreCallback fireStoreCallback) {
        if(CommonUtil.isNullOrEmpty(requesterId) || acceptedDonor == null) {
            return;
        }
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();

            CollectionReference usersReference = firebaseFirestore.collection(COLLECTION_NAME);
            Query query = usersReference
                    .whereEqualTo(COLUMN_USER_ID, acceptedDonor)
                    .whereEqualTo(COLUMN_REQUESTER_ID, requesterId)
                    .whereEqualTo(COLUMN_REQUEST_ID, requestId);

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

    public static void checkPendingDonateRequest(String userId, final FireStoreCallback fireStoreCallback) {
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            final ArrayList<String> requestDetail = new ArrayList<>();

            CollectionReference usersReference = firebaseFirestore.collection(COLLECTION_NAME);
            Query query = usersReference
                    .whereEqualTo(COLUMN_USER_ID, userId)
                    .whereEqualTo(COLUMN_DONATE_COMPLETED, CommonConstants.USER_DONATE_PENDING);
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
}
