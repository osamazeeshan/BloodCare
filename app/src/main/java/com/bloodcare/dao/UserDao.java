package com.bloodcare.dao;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;

import com.bloodcare.cursor.CustomCursor;
import com.bloodcare.utility.CommonUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.LongToIntFunction;

public class UserDao extends BaseDao {

    public static final String COLLECTION_NAME = "users";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    //public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_CELL_NO = "cellNo";
//    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_IS_DONOR = "isDonor";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_ANY_DISEASE = "disease";
    public static final String COLUMN_BLOOD_TYPE = "bloodType";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_PHOTO_URL = "photoUrl";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_DEVICE_TOKEN = "deviceToken";
    public static final String COLUMN_CREATION_TIMESTAMP = "timestamp";
    public static final String COLUMN_BLOOD_DONATE_STATUS = "bloodDonateStatus";
    public static final String COLUMN_IS_ELIGIBLE_TO_DONATE = "isEligibleToDonate";

    public UserDao(Context context) { super(context); }

    @Override
    protected String getCollectionName() {
        return COLLECTION_NAME;
    }

    public static String getTableSchema() {
        try {
            ArrayList<String> columns = new ArrayList<>();
            columns.add(COLUMN_ID + " integer primary key");
            columns.add(COLUMN_USER_ID + " text not null");
            columns.add(COLUMN_NAME + " text not null");
            columns.add(COLUMN_EMAIL + " text not null");
            //columns.add(COLUMN_PASSWORD + " text not null");
//            columns.add(COLUMN_ANY_DISEASE + " text");
//            columns.add(COLUMN_CITY + "text");
            columns.add(COLUMN_GENDER + " text not null");
            columns.add(COLUMN_IS_DONOR + " integer not null");
//            columns.add(COLUMN_PHOTO_URL + " text");
            columns.add(COLUMN_CELL_NO + " text not null");
//            columns.add(COLUMN_ADDRESS + " text");
            columns.add(COLUMN_LATITUDE + " text");
            columns.add(COLUMN_LONGITUDE + " text");
            columns.add(COLUMN_DEVICE_TOKEN + " text not null");
            columns.add(COLUMN_BLOOD_TYPE + " text not null");
            columns.add(COLUMN_AGE + " text not null");
            columns.add(COLUMN_CREATION_TIMESTAMP + " text not null");

            return "CREATE TABLE IF NOT EXISTS " + COLLECTION_NAME + " (" + TextUtils.join(",", columns) + ");";
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class SingleUser {
        public long id;
        public String userId;
        public String name;
        public String email;
        //public String password;
        public String cellNo;
        public String address;
        public String age;
        public String deviceToken;
        public String bloodType;
        public double latitude;
        public double longitude;
        public String gender;
        public boolean isDonor;
        public String city;
        public String disease;
        public String photoUrl;
        public String creationTimeStamp;
    }

    public long createOrUpdate(SingleUser user, int operation, String userId) {
        if(user == null) {
            return 0;
        }
        try {
            ContentValues values = new ContentValues();

            values.put(COLUMN_USER_ID, user.userId);
            values.put(COLUMN_NAME, user.name);
            values.put(COLUMN_EMAIL, user.email);
            //values.put(COLUMN_PASSWORD, user.password);
            values.put(COLUMN_CELL_NO, user.cellNo);
            //values.put(COLUMN_ADDRESS, user.address);
            values.put(COLUMN_AGE, user.age);
            values.put(COLUMN_IS_DONOR, user.isDonor);
            values.put(COLUMN_BLOOD_TYPE, user.bloodType);
            values.put(COLUMN_DEVICE_TOKEN, user.deviceToken);
            values.put(COLUMN_GENDER, user.gender);
            values.put(COLUMN_CREATION_TIMESTAMP, user.creationTimeStamp);
            if(operation == SQL_INSERT) {
                return insert(values);
            } else {
                String clause = null;
                String[] clauseArg = null;
                if(userId != null) {
                    clause = COLUMN_USER_ID + " = ?";
                    clauseArg = new String[] {String.valueOf(userId)};
                }
                return updateByClause(values, clause, clauseArg);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public SingleUser getRowById(String user_id) {
        CustomCursor userCursor = null;
        try {
            String clause = null;
            String[] clauseArg = null;
            if(!CommonUtil.isNullOrEmpty(user_id)) {
                clause = COLUMN_USER_ID + " = ?";
                clauseArg = new String[] {user_id};
            }
            userCursor = findByClause(clause, clauseArg);
            return getUserFromCursor(userCursor);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(userCursor != null) {
                userCursor.close();
            }
        }
        return null;
    }

    public void updateDeviceToken(String userId, String token) {
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DEVICE_TOKEN, token);
            String clause = COLUMN_USER_ID + " = ?";
            String[] clauseArg = new String[] {userId};
            updateByClause(values, clause, clauseArg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserLocation(String userId, GeoPoint location) {
        if(location == null) {
            return;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_LATITUDE, location.getLatitude());
            values.put(COLUMN_LONGITUDE, location.getLongitude());
            String clause = COLUMN_USER_ID + " = ?";
            String[] clauseArg = new String[] {userId};
            updateByClause(values, clause, clauseArg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserDonorStatus(String userId, boolean isDonor) {
        if(CommonUtil.isNullOrEmpty(userId)) {
            return;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_IS_DONOR, isDonor);
            String clause = COLUMN_USER_ID + " = ?";
            String[] clauseArg = new String[] {userId};
            updateByClause(values, clause, clauseArg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SingleUser getUserFromCursor(CustomCursor userCursor) {
        if(userCursor == null) {
            return null;
        }
        try {
            if(userCursor.getCount() == 0) {
                return null;
            }
            userCursor.moveToFirst();
            SingleUser singleUser = new SingleUser();
            singleUser.id = userCursor.getCursorLongValue(COLUMN_ID);
            singleUser.userId = userCursor.getCursorStringValue(COLUMN_USER_ID);
            singleUser.name = userCursor.getCursorStringValue(COLUMN_NAME);
            singleUser.email = userCursor.getCursorStringValue(COLUMN_EMAIL);
            singleUser.gender = userCursor.getCursorStringValue(COLUMN_GENDER);
            singleUser.isDonor= userCursor.getCursorBooleanValue(COLUMN_IS_DONOR);
            singleUser.cellNo = userCursor.getCursorStringValue(COLUMN_CELL_NO);
//            singleUser.address = userCursor.getCursorStringValue(COLUMN_ADDRESS);
            singleUser.age = userCursor.getCursorStringValue(COLUMN_AGE);
            singleUser.bloodType = userCursor.getCursorStringValue(COLUMN_BLOOD_TYPE);
            singleUser.latitude = userCursor.getCursorDoubleValue(COLUMN_LATITUDE);
            singleUser.longitude = userCursor.getCursorDoubleValue(COLUMN_LONGITUDE);
            singleUser.deviceToken = userCursor.getCursorStringValue(COLUMN_DEVICE_TOKEN);
            singleUser.creationTimeStamp = userCursor.getCursorStringValue(COLUMN_CREATION_TIMESTAMP);
            return singleUser;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
