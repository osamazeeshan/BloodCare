package com.bloodcare.firestore;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bloodcare.R;
import com.bloodcare.dao.RequestDao;
import com.bloodcare.dao.UserDao;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;
import com.bloodcare.utility.LogTags;
import com.bloodcare.utility.ReachabilityTest;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;
import static com.bloodcare.utility.CommonUtil.getUriFromFile;
import static com.bloodcare.utility.CommonUtil.isNullOrEmpty;
import static com.bloodcare.utility.LogTags.TAG_CREATE_USER;
import static com.bloodcare.utility.LogTags.TAG_GET_USER_DATA;
import static com.bloodcare.utility.LogTags.TAG_QUERY;
import static com.bloodcare.utility.LogTags.TAG_REAL_TIME_UPDATE_REQUEST;
import static com.bloodcare.utility.LogTags.TAG_UPDATE_DEVICE_TOKEN;
import static com.bloodcare.utility.LogTags.TAG_USER_AUTHENTICATE;
import static com.bloodcare.utility.LogTags.TAG_USER_REGISTRATION;

public class UserFireStore extends BaseFireStore {

    public static final String COLUMN_LOCATION = "location";

    private UserFireStore() {
    }

    public static boolean getUser(UserDao.SingleUser singleUser, DocumentSnapshot documentSnapshot) {
        if(singleUser == null || documentSnapshot == null) {
            return false;
        }
        try {
            singleUser.userId = documentSnapshot.getId();
            singleUser.name = documentSnapshot.getString(UserDao.COLUMN_NAME);
            singleUser.email = documentSnapshot.getString(UserDao.COLUMN_EMAIL);
            singleUser.gender = documentSnapshot.getString(UserDao.COLUMN_GENDER);
//            singleUser.address = documentSnapshot.getString(UserDao.COLUMN_ADDRESS);
            singleUser.age = documentSnapshot.getString(UserDao.COLUMN_AGE);
            singleUser.isDonor = documentSnapshot.getBoolean(UserDao.COLUMN_IS_DONOR);
            singleUser.cellNo = documentSnapshot.getString(UserDao.COLUMN_CELL_NO);

            GeoPoint geoPoint = documentSnapshot.getGeoPoint(UserFireStore.COLUMN_LOCATION);
            if(geoPoint != null) {
                singleUser.latitude = geoPoint.getLatitude();
                singleUser.longitude = geoPoint.getLongitude();
            }

            singleUser.bloodType = documentSnapshot.getString(UserDao.COLUMN_BLOOD_TYPE);
            singleUser.creationTimeStamp = documentSnapshot.getString(UserDao.COLUMN_CREATION_TIMESTAMP);
            singleUser.deviceToken = documentSnapshot.getString(UserDao.COLUMN_DEVICE_TOKEN);

            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void authenticateUser(String email, String password, Activity activity, final FireStoreCallback fireStoreCallback) {
        if(isNullOrEmpty(email) || isNullOrEmpty(password) || activity == null) {
            return;
        }
        try {
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG_USER_AUTHENTICATE, "signInWithEmail:success");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                    Log.d(TAG_USER_AUTHENTICATE, user.getEmail());
                                    if (fireStoreCallback != null) {
                                        fireStoreCallback.done(null, user.getUid());
                                    }
                                    //mUID = user.getUid();

                                    //CommonUtil.getReference("users").child("token").setValue(FirebaseInstanceId.getInstance().getToken());

                                }
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG_USER_AUTHENTICATE, "signInWithEmail:failure", task.getException());
                                fireStoreCallback.done(task.getException(), null);
                                //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                //   Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void firebaseAuthWithGoogle(GoogleSignInAccount account, Activity activity, final FireStoreCallback fireStoreCallback) {
        if(account == null) {
            return;
        }
        try {
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                    Log.d(TAG_USER_AUTHENTICATE, user.getEmail());
                                    if (fireStoreCallback != null) {
                                        fireStoreCallback.done(null, user);
                                    }
                                    //mUID = user.getUid();

                                    //CommonUtil.getReference("users").child("token").setValue(FirebaseInstanceId.getInstance().getToken());

                                }
                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                fireStoreCallback.done(task.getException(), null);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void firebaseAuthWithFacebook(AccessToken token, Activity activity, final FireStoreCallback fireStoreCallback) {
        if (token == null || activity == null) {
            return;
        }
        try {
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                    Log.d(TAG_USER_AUTHENTICATE, user.getEmail());
                                    if (fireStoreCallback != null) {
                                        fireStoreCallback.done(null, user);
                                    }
                                }

                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                fireStoreCallback.done(task.getException(), null);
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveUserPhoto(String uID, byte[] fileBytes, String fileName) {
        if(isNullOrEmpty(uID)) {
            return;
        }
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            StorageReference storageRef = storageReference.child("images/users/" + uID + "/" + fileName + ".jpg");
//            StorageReference storageRef = storageReference.child("images/users/abc.jpg");
//            Uri fileUri = getUriFromFile(fileName);
//            if(fileUri == null) {
//                return;
//            }
            storageRef.putBytes(fileBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri storageUri = taskSnapshot.getUploadSessionUri();
                    Log.d("save_photo", "Saved");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Failed", e.getMessage());

                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getUserPhoto(String uId, String fileName, final FireStoreCallback fireStoreCallback) {
        if(isNullOrEmpty(uId) || isNullOrEmpty(fileName)) {
            return;
        }
        try {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            StorageReference userImgRef = storageRef.child("images/users/" + uId + "/" + fileName + ".jpg");
//            StorageReference userImgRef = storageRef.child("images/users/abc.jpg");
            final long ONE_MEGABYTE = 1024 * 1024;
            userImgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    if(fireStoreCallback != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        fireStoreCallback.done(null, bitmap);
                    }
                    Log.d("save_photo", "bitmap loaded");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    fireStoreCallback.done(exception, null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getSingleUserData(String userId, Activity activity, final FireStoreCallback fireStoreCallback) {
        if(activity == null) {
            return;
        }
        try {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference docRef = firebaseFirestore.collection(UserDao.COLLECTION_NAME).document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        UserDao.SingleUser singleUser = new UserDao.SingleUser();
                        if (documentSnapshot.exists()) {
                            Log.d(TAG_GET_USER_DATA, "DocumentSnapshot data: " + documentSnapshot.getData());

                            getUser(singleUser, documentSnapshot);
                            fireStoreCallback.done(null, singleUser);

                        } else {
                            Log.d(TAG_GET_USER_DATA, "No such document");
                            fireStoreCallback.done(new Exception("No such document"), null);
                        }
                    } else {
                        Log.d(TAG_GET_USER_DATA, "get failed with ", task.getException());
                        fireStoreCallback.done(null, task.getException());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getDonorsData(final List<String> donorIds, final FireStoreCallback fireStoreCallback) {
        if(donorIds == null) {
            return;
        }
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            final ArrayList<UserDao.SingleUser> singleUserArrayList = new ArrayList<>();
            // Note: if add two or more query need to create a custom index in firestore console, by default firestore only create single index to query with one item
            Query queryDocumentSnapshot = firebaseFirestore.collection(UserDao.COLLECTION_NAME);
            queryDocumentSnapshot.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(donorIds.contains(document.getId())) {
                                        UserDao.SingleUser singleUser = new UserDao.SingleUser();
                                        getUser(singleUser, document);
                                        singleUserArrayList.add(singleUser);
                                        Log.d(TAG_QUERY, document.getId() + " => " + document.getData());
                                    }
                                }
                                fireStoreCallback.done(null, singleUserArrayList);
                            } else {
                                Log.d(TAG_QUERY, "Error getting documents: ", task.getException());
                                fireStoreCallback.done(new Exception("Error getting documents"), null);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void signUprWithEmailAndPassword(final String email, final String password, final Activity activity, final FireStoreCallback fireStoreCallback) {
        if(isNullOrEmpty(email) || isNullOrEmpty(password)) {
            return;
        }
        try {
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

//            new ReachabilityTest(activity.getApplicationContext(), REACHIBLITY_URL, REACHIBLITY_PORT, new ReachabilityTest.Callback() {
//                @Override
//                public void onReachabilityTestPassed() {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG_USER_REGISTRATION, "createUserWithEmail:success");
                                    FirebaseUser user = firebaseAuth.getCurrentUser();

                                    if (user != null) {
                                        if(fireStoreCallback != null) {
                                            fireStoreCallback.done(null, user);

                                        }
                                    }
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG_USER_REGISTRATION, "createUserWithEmail:failure", task.getException());
                                    fireStoreCallback.done(task.getException(), null);

                                }
                            }
                        });
//                }
//
//                @Override
//                public void onReachabilityTestFailed() {
//                    fireStoreCallback.done(new Exception(activity.getString(R.string.error_no_internet)), null);
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createUser(final Context context, final UserDao.SingleUser singleUser, final FireStoreCallback fireStoreCallback) {
        if(singleUser == null) {
            return;
        }
        try {
            final Map<String, Object> data = new HashMap<>();
            data.put(UserDao.COLUMN_USER_ID, singleUser.userId);
            data.put(UserDao.COLUMN_NAME, singleUser.name);
            data.put(UserDao.COLUMN_EMAIL, singleUser.email);
            data.put(UserDao.COLUMN_AGE, singleUser.age);
            data.put(UserDao.COLUMN_IS_DONOR, singleUser.isDonor);
            data.put(UserDao.COLUMN_BLOOD_TYPE, singleUser.bloodType);
//            data.put(UserDao.COLUMN_ADDRESS, singleUser.address);
            data.put(UserDao.COLUMN_GENDER, singleUser.gender);
            data.put(UserDao.COLUMN_CELL_NO, singleUser.cellNo);
            data.put(UserDao.COLUMN_DEVICE_TOKEN, singleUser.deviceToken);
            data.put(UserDao.COLUMN_CREATION_TIMESTAMP, singleUser.creationTimeStamp);


//        DatabaseReference reference = CommonUtil.getReference("users");
//        reference.setValue(data);

            final FirebaseFirestore firebaseFirestore = getFireStoreInstance();
//            new ReachabilityTest(context, REACHIBLITY_URL, REACHIBLITY_PORT, new ReachabilityTest.Callback() {
//                @Override
//                public void onReachabilityTestPassed() {
                    firebaseFirestore.collection(UserDao.COLLECTION_NAME).document(singleUser.userId)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG_CREATE_USER, "Successfully updated new record");
                                if(fireStoreCallback != null) {
                                    fireStoreCallback.done(null, "Success");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception error) {
                                Log.d(TAG_CREATE_USER, error.getMessage());
                                fireStoreCallback.done(error, null);
                            }
                        });
//                }
//
//                @Override
//                public void onReachabilityTestFailed() {
//                    fireStoreCallback.done(new Exception(context.getString(R.string.error_no_internet)), null);
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUserLocation(final Context context, final String userId, final GeoPoint geoPoint) {
        if (isNullOrEmpty(userId) || geoPoint == null || context == null) {
            return;
        }
        try {

            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            //GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            firebaseFirestore
                    .collection(UserDao.COLLECTION_NAME).document(userId)
                    .update(COLUMN_LOCATION, geoPoint)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(LogTags.TAG_UPDATE_USER_LOCATION, "Successfully updated user token");
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUserDonorStatus(String userId, boolean isDonor, final FireStoreCallback fireStoreCallback) {
        if (isNullOrEmpty(userId)) {
            return;
        }
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            //GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            firebaseFirestore
                    .collection(UserDao.COLLECTION_NAME).document(userId)
                    .update(UserDao.COLUMN_IS_DONOR, isDonor)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(LogTags.TAG_UPDATE_USER_LOCATION, "Successfully Registered as a Blood Donor");
                                fireStoreCallback.done(null, "Successfully Registered as a Blood Donor");
                            } else {
                                fireStoreCallback.done(new Exception(task.getException()), null);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadDonorsLocation(final String userId, double latitude, double longitude, String bloodType, final FireStoreCallback fireStoreCallback) {
        try {

//            if(listenerRegistration != null) {
//                listenerRegistration.remove();
//                return;
//            }
            final ArrayList<GeoPoint> locationArray = new ArrayList<>();
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            Query queryDocumentSnapshot;
            if(latitude != -1 && longitude != -1 && !isNullOrEmpty(bloodType)) {
                ArrayList<GeoPoint> locationRadius = CommonUtil.calculateNearbyLocation(5, latitude, longitude);
                queryDocumentSnapshot = firebaseFirestore.collection(UserDao.COLLECTION_NAME)
                        .whereEqualTo(UserDao.COLUMN_BLOOD_TYPE, bloodType)
                        .whereEqualTo(UserDao.COLUMN_IS_DONOR, true)
                        .whereGreaterThan(UserFireStore.COLUMN_LOCATION, locationRadius.get(0))
                        .whereLessThan(UserFireStore.COLUMN_LOCATION, locationRadius.get(1));
            }
            // Note: if add two or more query need to create a custom index in firestore console, by default firestore only create single index to query with one item
            else {
                queryDocumentSnapshot = firebaseFirestore.collection(UserDao.COLLECTION_NAME)
                        .whereEqualTo(UserDao.COLUMN_IS_DONOR, true);
            }

            queryDocumentSnapshot.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG_QUERY, document.getId() + " => " + document.getData());
                            if(!userId.equals(document.get(UserDao.COLUMN_USER_ID)) && document.get(COLUMN_LOCATION) != null) {
                                locationArray.add((GeoPoint) document.get(COLUMN_LOCATION));
                            }
                        }
                        fireStoreCallback.done(null, locationArray);
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

    public static void fetchAcceptedDonorsDetail() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logoutUser() {
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void updateDonorStatus(String donateId, boolean status) {
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            firebaseFirestore.collection(UserDao.COLLECTION_NAME).document(donateId)
                    .update(UserDao.COLUMN_BLOOD_DONATE_STATUS , status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateDeviceToken(String userId, String token) {
        if (isNullOrEmpty(userId) || isNullOrEmpty(token)) {
            return;
        }
        try {
            FirebaseFirestore firebaseFirestore = getFireStoreInstance();
            firebaseFirestore
                    .collection(UserDao.COLLECTION_NAME).document(userId)
                    .update(UserDao.COLUMN_DEVICE_TOKEN, token)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG_UPDATE_DEVICE_TOKEN, "Successfully updated device token");
                        }
                    });

//            getReference(UserDao.COLLECTION_NAME).child(UserDao.COLUMN_DEVICE_TOKEN).setValue(token);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
