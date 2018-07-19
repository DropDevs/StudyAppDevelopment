package com.gaurav.studyapp.developement.fcmnotifications;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.gaurav.studyapp.developement.fcm.Data;
import com.gaurav.studyapp.developement.fcm.FirebaseCloudMessage;
import com.gaurav.studyapp.developement.firebasedata.CollegeNameGetterSetter;
import com.gaurav.studyapp.developement.saveddata.LocalServerKey;
import com.gaurav.studyapp.developement.server.ServerKeyData;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.gaurav.studyapp.developement.utils.FCM;
import com.squareup.okhttp.ResponseBody;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AssignmentNotification {
    LocalServerKey serverKey = new LocalServerKey();
    ServerKeyData serverKeyData = new ServerKeyData();
    FirebaseCloudMessage firebaseCloudMessage = new FirebaseCloudMessage();
    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public void sendNotifications(Context context, String userName, String branchName){
        String server_key = serverKeyData.getServer_key();
        UsersDetails usersDetails = new UsersDetails(context);

//        if (serverKeyData.getServer_key() != null){
//            server_key = serverKeyData.getServer_key();
//        }else if (serverKeyData.getServer_key() == null){
//            server_key = serverKey.getServerKeyFromLocalStorage(context);
//        }
        //---------------------From github ---------------
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
// add your other interceptors …

// add logging as last interceptor
        httpClient.addInterceptor(logging);
        //------------------------------------------------------------

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FCM fcmApi = retrofit.create(FCM.class);
        //attach the headers
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content_Type","application/json;charset=UTF-8");
        headers.put("Authorization","key=AAAAB4eBjxs:APA91bG51eCEiuYUbVSyrYVSidowMGb_HXjtJYbVk9vyRFTos7HjvatSYf153nXBNnQE9injE6wBsBcMXP-qGOkDOLRYwm9-2pdnVMJFtiVL3Bi7agR0zFErrFRINEPw04K3Q6mwEjIe");

        //send notification to the subscribed topic (notifications)
        sendNotificationToTopic(fcmApi, headers, userName, branchName,usersDetails);
    }

    public void sendNotificationToTopic(FCM fcmApi, HashMap<String, String> headers, String userName, String branchName,UsersDetails usersDetails){

        String sender_image = usersDetails.getPersonImage().toString();
        String collegeName = CollegeNameGetterSetter.getCollegeName();
        String remove_space = collegeName.replace(" ", "");
        String topic_name = remove_space.trim();
        final String sendToTopic = "/topics/"+topic_name;
        final Data data = new Data();
        data.setMessage(branchName);
        data.setTitle(userName);
        data.setImage(sender_image);
        firebaseCloudMessage.setData(data);
        firebaseCloudMessage.setTo(sendToTopic);
        Log.d("notification_topic","In_assignment_notifications_class");
        Log.d("notification_topic","topic_name: "+topic_name);

        Call<ResponseBody> call = fcmApi.send(headers, firebaseCloudMessage);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){

                }else if (!response.isSuccessful()){
                    // error case
                    switch (response.code()) {
                        case 404:
                            Log.d("fcm_notification","Error_404");
                            break;
                        case 500:
                            Log.d("fcm_notification","Error_500");
                            break;
                        default:
                            Log.d("fcm_notification","unknown_error");
                            break;
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.d("fcm_notification","onFailure: "+t.getMessage());
            }
        });

    }

}
