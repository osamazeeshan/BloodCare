package com.bloodcare.fragment.doctor;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

/**
 * Created by osamazeeshan on 30/12/2018.
 */

public class DoctorInfo implements Serializable {

    public transient String docUId;
    public transient String docName;
    public transient String docContact;
    public transient String docAddress;
    public transient String docDistance;
    public transient GeoPoint docLocation;
    public transient String docType;


    public void setDocName(String name) {
        docName = name;
    }

    public void setDocUId(String uId) {
        docUId = uId;
    }

    public void setDocContact(String contact) {
        docContact = contact;
    }

    public void setDocLocation(GeoPoint location) {
        docLocation = location;
    }

    public void setDocType(String type) {
        docType = type;
    }

    public void setDocAddress(String address) {
        docAddress = address;
    }

    public void setDocDistance(String distance) {
        docDistance = distance;
    }

    public String getDocContact() {
        return docContact;
    }

    public String getDocUId() {
        return docUId;
    }

    public String getDocName() {
        return docName;
    }

    public GeoPoint getDocLocation() {
        return docLocation;
    }

    public String getDocType() {
        return docType;
    }

    public String getDocAddress() {
        return docAddress;
    }

    public String getDocDistance() {
        return docDistance;
    }
}