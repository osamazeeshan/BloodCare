package com.bloodcare.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bloodcare.R;
import com.bloodcare.activity.MainActivity;
import com.bloodcare.activity.NotificationActivity;
import com.bloodcare.dao.RequestDao;
import com.bloodcare.firestore.BaseFireStore;
import com.bloodcare.firestore.DonateFireStore;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.fragment.startup.SignInFragment;
import com.bloodcare.utility.CommonConstants;
import com.bloodcare.utility.CommonUtil;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by osamazeeshan on 24/08/2018.
 */

public class FireBaseMessaging extends FirebaseMessagingService {

    public  FireBaseMessaging() {

    }

    String TAG = "notification";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String click_action = remoteMessage.getNotification().getClickAction();

            String requestId = remoteMessage.getData().get(CommonConstants.REQUEST_ID_KEY);
            String requesterId = remoteMessage.getData().get(CommonConstants.REQUESTER_ID_KEY);
            String requesterBloodType = remoteMessage.getData().get(CommonConstants.REQUESTER_BLOOD_TYPE_KEY);
            String requesterName = remoteMessage.getData().get(CommonConstants.REQUESTER_NAME_KEY);
            String timeStamp = remoteMessage.getData().get(CommonConstants.REQUEST_TIME_STAMP);
            String requesterLat = remoteMessage.getData().get(RequestDao.COLUMN_REQUESTER_LATITUDE);
            String requesterLong = remoteMessage.getData().get(RequestDao.COLUMN_REQUESTER_LONGITUDE);

            if(title != null && body != null) {
                Log.d(TAG, title);
                Log.d(TAG, body);
            }

            //sendNotification(title, body, requestId, click_action);
//            sendMessageNotification(title, body, "afw4r34");
            startActivity(requestId, requesterId, requesterBloodType, requesterName, timeStamp);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void startActivity(String requestId, String requesterId, String requesterBloodType, String requesterName, String timeStamp) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra(CommonConstants.REQUEST_ID_KEY, requestId);
        intent.putExtra(CommonConstants.REQUESTER_ID_KEY, requesterId);
        intent.putExtra(CommonConstants.REQUESTER_BLOOD_TYPE_KEY, requesterBloodType);
        intent.putExtra(CommonConstants.REQUESTER_NAME_KEY, requesterName);
        intent.putExtra(CommonConstants.REQUEST_TIME_STAMP, timeStamp);
        intent.putExtra("playSound", true);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendNotification(String title, String body, String requestId, String click_action) {
        Intent intent = new Intent(click_action);
        intent.putExtra("title", title);
        intent.putExtra("body", body);
        intent.putExtra("requestId", requestId);
        intent.putExtra("playSound", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendMessageNotification(String title, String message, String messageId){
        Log.d(TAG, "sendChatmessageNotification: building a chatmessage notification");

        //get the notification id
        int notificationId = buildNotificationId(messageId);

        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                getString(R.string.NotificationId));
        // Creates an Intent for the Activity
        Intent pendingIntent = new Intent(this, NotificationActivity.class);
        // Sets the Activity to start in a new, empty task
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        pendingIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //add properties to the builder
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.ic_launcher_foreground))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setColor(getColor(R.color.colorPrimaryDark))
                .setAutoCancel(true)
                //.setSubText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setOnlyAlertOnce(true);

        builder.setContentIntent(notifyPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationId, builder.build());

    }


    private int buildNotificationId(String id){
        Log.d(TAG, "buildNotificationId: building a notification id.");

        int notificationId = 0;
        for(int i = 0; i < 9; i++){
            notificationId = notificationId + id.charAt(0);
        }
        Log.d(TAG, "buildNotificationId: id: " + id);
        Log.d(TAG, "buildNotificationId: notification id:" + notificationId);
        return notificationId;
    }

}
