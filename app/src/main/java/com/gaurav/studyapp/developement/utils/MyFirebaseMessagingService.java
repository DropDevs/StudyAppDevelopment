package com.gaurav.studyapp.developement.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.HomeActivity;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import retrofit2.http.Url;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    UsersDetails usersDetails = new UsersDetails(getApplicationContext());

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("message","In_onMessageReceived_method");
        String notificationBody = "";
        String notificationTitle = "";
        String notificationData = "";

        try{
            notificationTitle = remoteMessage.getData().get("title");
            notificationBody = remoteMessage.getData().get("message");
            notificationData = remoteMessage.getData().toString();

//            showNotification(remoteMessage);
            sendNotification(remoteMessage);
        }catch (Exception e){
            Log.d("message","Exception: "+e.toString());
        }

        Log.d("received_message","notificationTitle: "+notificationTitle);
        Log.d("received_message","notificationBody: "+notificationBody);
        Log.d("received_message","notificationData: "+notificationData);
    }

    public void sendNotification(RemoteMessage remoteMessage){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

//        Uri user_uri = usersDetails.getPersonImage();
//        InputStream image_stream = getContentResolver().
//        Bitmap bitmap= BitmapFactory.decodeStream(image_stream);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications",NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        URL sender_image = null;
        try {
            sender_image = new URL(remoteMessage.getData().get("image"));
            Bitmap image = BitmapFactory.decodeStream(sender_image.openConnection().getInputStream());
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    //     .setPriority(Notification.PRIORITY_MAX)
//                    .setLargeIcon(image)
                    .setSmallIcon(R.drawable.fcmlogo)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("message"));

            notificationManager.notify(/*notification id*/1, notificationBuilder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
