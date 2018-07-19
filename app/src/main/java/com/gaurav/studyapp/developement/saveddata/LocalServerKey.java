package com.gaurav.studyapp.developement.saveddata;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalServerKey {

    public void saveServerKeyToLocalStorage(Context context, String server_key){
        SharedPreferences serverKeyPref = context.getSharedPreferences("ServerPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = serverKeyPref.edit();
        editor.putString("fcm_server_key",server_key);
        editor.apply();
    }

    public String getServerKeyFromLocalStorage(Context context){
        SharedPreferences serverKeyPref = context.getSharedPreferences("ServerPref",Context.MODE_PRIVATE);
        return serverKeyPref.getString("fcm_server_key",null);
    }

}
