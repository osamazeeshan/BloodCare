package com.bloodcare.fragment.history;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by osamazeeshan on 23/12/2018.
 */

public class RequestInfo implements Serializable {

    public transient String requesterId;
    public transient String userId;
    public transient String requesterName;
    public transient String requesterBloodType;
    public transient String requesterStatus;
    public transient String requesterDateTime;
    public transient Bitmap requesterMapSnapShot;
    public transient List<String> requesterAcceptedArray;

    public void setRequesterName(String name) {
        requesterName = name;
    }

    public void setRequesterBloodType(String bloodType) {
        requesterBloodType = bloodType;
    }

    public void setRequesterStatus(String status) {
        requesterStatus = status;
    }

    public void setRequesterDateTime(String dateTime) {
        requesterDateTime = dateTime;
    }

    public void setRequesterMapSnapShot(Bitmap mapSnapShot) {
        requesterMapSnapShot = mapSnapShot;
    }

    public void setRequesterId(String reqId) {
        requesterId = reqId;
    }

    public void setRequesterAcceptedArray(List<String> acceptedArray) {
        requesterAcceptedArray = acceptedArray;
    }

    public void setUserId(String userId1) {
        userId = userId1;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getRequesterAcceptedArray() {
        return requesterAcceptedArray;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getRequestBloodType() {
        return requesterBloodType;
    }

    public String getRequestStatus() {
        return requesterStatus;
    }

    public String getRequestDateTime() {
        return requesterDateTime;
    }

    public Bitmap getRequestMapSnapShot() {
        return requesterMapSnapShot;
    }

}
