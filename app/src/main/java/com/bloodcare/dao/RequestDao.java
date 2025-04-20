package com.bloodcare.dao;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by osamazeeshan on 29/08/2018.
 */

public class RequestDao extends BaseDao {

    public static final String COLLECTION_NAME = "requests";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_BLOOD_TYPE = "bloodType";
    public static final String COLUMN_USER_DEVICE_TOKEN = "userToken";
    public static final String COLUMN_REQUEST_DEVICE_TOKEN = "requestToken";
    public static final String COLUMN_CREATION_TIMESTAMP = "timestamp";
    public static final String COLUMN_REQUEST_COMPLETED = "requestCompleted";
    public static final String COLUMN_REQUESTER_LATITUDE = "requesterLatitude";
    public static final String COLUMN_REQUESTER_LONGITUDE = "requesterLongitude";
    public static final String COLUMN_ACCEPT_REQUESTS = "acceptRequest";

    public RequestDao(Context context) {
        super(context);
    }

    @Override
    protected String getCollectionName() {
        return COLLECTION_NAME;
    }

    public static String getTableSchema() {
        try {
            ArrayList<String> columns = new ArrayList<>();
            columns.add(COLUMN_ID + " integer primary key");
            columns.add(COLUMN_USER_ID + " text not null");
            columns.add(COLUMN_USER_NAME + " text not null");
            columns.add(COLUMN_USER_BLOOD_TYPE + " text not null");
            columns.add(COLUMN_USER_DEVICE_TOKEN + " text not null");
            columns.add(COLUMN_REQUEST_DEVICE_TOKEN + " text not null");
            columns.add(COLUMN_CREATION_TIMESTAMP + " text not null");
            columns.add(COLUMN_REQUEST_COMPLETED + " text not null");

            return "CREATE TABLE IF NOT EXISTS " + COLLECTION_NAME + " (" + TextUtils.join(",", columns) + ");";
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class SingleRequest {
        public long id;
        public String userId;
        public String userName;
        public String userBloodType;
        public String userDeviceToken;
        public String requestDeviceToken;
        public String creationTimeStamp;
        public int requestCompleted;
    }

    public long createOrUpdate(SingleRequest singleRequest, int operation, long rowId) {
        if(singleRequest == null) {
            return 0;
        }
        try {
            ContentValues values = new ContentValues();

            values.put(COLUMN_USER_ID, singleRequest.userId);
            values.put(COLUMN_USER_NAME, singleRequest.userName);
            values.put(COLUMN_USER_BLOOD_TYPE, singleRequest.userBloodType);
            values.put(COLUMN_USER_DEVICE_TOKEN, singleRequest.userDeviceToken);
            values.put(COLUMN_REQUEST_DEVICE_TOKEN, singleRequest.requestDeviceToken);
            values.put(COLUMN_CREATION_TIMESTAMP, singleRequest.creationTimeStamp);
            values.put(COLUMN_REQUEST_COMPLETED, singleRequest.requestCompleted);
            if(operation == SQL_INSERT) {
                return insert(values);
            } else {
                String clause = null;
                String[] clauseArg = null;
                if(rowId > 0) {
                    clause = COLUMN_ID + " = ?";
                    clauseArg = new String[] {String.valueOf(rowId)};
                }
                return updateByClause(values, clause, clauseArg);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
