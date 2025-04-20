package com.bloodcare;

import com.bloodcare.dao.UserDao;
import com.bloodcare.firestore.UserFireStore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FireBaseInstanceService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        try {
            String updatedToken = FirebaseInstanceId.getInstance().getToken();

            UserDao userDao = new UserDao(getApplicationContext());
            UserDao.SingleUser singleUser = userDao.getRowById(null);

            UserFireStore.updateDeviceToken(singleUser.userId, updatedToken);

            userDao.updateDeviceToken(singleUser.userId, updatedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
