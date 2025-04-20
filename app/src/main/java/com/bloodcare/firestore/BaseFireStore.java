package com.bloodcare.firestore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bloodcare.utility.CommonUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.bloodcare.utility.CommonUtil.isNullOrEmpty;

/**
 * Created by osamazeeshan on 28/08/2018.
 */

public class BaseFireStore {

    public static final String REACHIBLITY_URL = "www.google.com";
    public static final int REACHIBLITY_PORT = 80;

    protected static FirebaseFirestore getFireStoreInstance() {
        return FirebaseFirestore.getInstance();
    }

    public interface FireStoreCallback {
        public void done(Exception fireBaseException, Object object);
    }

    public static DatabaseReference getReference(String node) {
        return FirebaseDatabase.getInstance().getReference(node).push();
    }

    public static void saveMapSnapShot(String folder, String id, byte[] fileBytes) {
        if(isNullOrEmpty(id) || isNullOrEmpty(folder)) {
            return;
        }
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            StorageReference storageRef = storageReference.child("images/" + folder + "/" + id + ".jpg");
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

    public static void getMapSnapShot(String folder, String id, final FireStoreCallback fireStoreCallback) {
        if(isNullOrEmpty(id) || isNullOrEmpty(folder)) {
            return;
        }
        try {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            StorageReference userImgRef = storageRef.child("images/" + folder + "/" + id + ".jpg");
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

}
