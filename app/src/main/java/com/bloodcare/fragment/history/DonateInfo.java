package com.bloodcare.fragment.history;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by osamazeeshan on 23/12/2018.
 */

public class DonateInfo implements Serializable {

    public transient String donorName;
    public transient String donateId;
    public transient String donateToName;
    public transient String donateBloodType;
    public transient String donateStatus;
    public transient String donateDateTime;
    public transient String donateDistance;
    public transient Bitmap donateMapSnapShot;

    public void setDonateToName(String name) {
        donateToName = name;
    }

    public void setDonateBloodType(String bloodType) {
        donateBloodType = bloodType;
    }

    public void setDonateStatus(String status) {
        donateStatus = status;
    }

    public void setDonateDateTime(String dateTime) {
        donateDateTime = dateTime;
    }

    public void setDonateDistance(String distance) {
        donateDistance = distance;
    }

    public void setRequesterMapSnapShot(Bitmap mapSnapShot) {
        donateMapSnapShot = mapSnapShot;
    }

    public void setDonorName(String name) {
        donorName = name;
    }

    public void setDonateId(String id) {
        donateId = id;
    }

    public String getDonorName() {
        return donorName;
    }

    public String getDonateId() {
        return donateId;
    }

    public String geDonateToName() {
        return donateToName;
    }

    public String getDonateBloodType() {
        return donateBloodType;
    }

    public String getDonateStatus() {
        return donateStatus;
    }

    public String getDonateDateTime() {
        return donateDateTime;
    }

    public String getDonateDistance() {
        return donateDistance;
    }

    public Bitmap getDonateMapSnapShot() {
        return donateMapSnapShot;
    }

}
