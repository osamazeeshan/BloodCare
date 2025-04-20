package com.bloodcare.fragment.hospital;

import android.graphics.Bitmap;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

/**
 * Created by osamazeeshan on 29/12/2018.
 */

public class HospitalInfo implements Serializable {

    public transient String hospitalUId;
    public transient String hospitalName;
    public transient String hospitalContact;
    public transient String hospitalAddress;
    public transient String hospitalDistance;
    public transient GeoPoint hospitalLocation;
    public transient String hospitalType;


    public void setHospitalName(String name) {
        hospitalName = name;
    }

    public void setHospitalUId(String uId) {
        hospitalUId = uId;
    }

    public void setHospitalContact(String contact) {
        hospitalContact = contact;
    }

    public void setHospitalLocation(GeoPoint location) {
        hospitalLocation = location;
    }

    public void setHospitalType(String type) {
        hospitalType = type;
    }

    public void setHospitalAddress(String address) {
        hospitalAddress = address;
    }

    public void setHospitalDistance(String distance) {
        hospitalDistance = distance;
    }

    public String getHospitalContact() {
        return hospitalContact;
    }

    public String getHospitalUId() {
        return hospitalUId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public GeoPoint getHospitalLocation() {
        return hospitalLocation;
    }

    public String getHospitalType() {
        return hospitalType;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public String getHospitalDistance() {
        return hospitalDistance;
    }

}
