package com.bloodcare.fragment.donor;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

/**
 * Created by osamazeeshan on 22/09/2018.
 */

public class DonorInfo implements Serializable {

    public transient String donorUId;
    public transient String donorName;
    public transient String requesterName;
    public transient String requestId;
    public transient String donorContact;
    public transient GeoPoint donorLocation;
    public transient String donorBloodType;
    public transient String donorDistanceFUser;
    public transient Bitmap donorPhotoBitmap;


    public void setDonorName(String name) {
        donorName = name;
    }

    public void setRequesterName(String name) {
        requesterName = name;
    }

    public void setRequestId(String reqId) {
        requestId = reqId;
    }

    public void setDonorPhotoBitmap(Bitmap bitmap) {
        donorPhotoBitmap = bitmap;
    }

    public void setDonorUId(String uId) {
        donorUId = uId;
    }

    public void setDonorContact(String contact) {
        donorContact = contact;
    }

    public void setDonorLocation(GeoPoint location) {
        donorLocation = location;
    }

    public void setDonorBloodType(String bloodType) {
        donorBloodType = bloodType;
    }

    public void setDonorDistanceFUser(String distanceFUser) {
        donorDistanceFUser = distanceFUser;
    }

    public String getDonorContact() {
        return donorContact;
    }

    public Bitmap getDonorPhotoBitmap() {
        return donorPhotoBitmap;
    }

    public String getDonorUId() {
        return donorUId;
    }

    public String getDonorName() {
        return donorName;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getDonorBloodType() {
        return donorBloodType;
    }

    public String getDonorDistanceFUser() {
        return donorDistanceFUser;
    }

}
