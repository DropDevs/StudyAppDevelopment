package com.gaurav.studyapp.developement.server;

import android.util.Log;

public class ServerKeyData {

    private String server_key;

    public String getServer_key() {
        Log.d("server_key_message","In_get_server_key");
        Log.d("server_key_message","server_key"+server_key);
        return server_key;
    }

    public void setServer_key(String server_key) {
        Log.d("server_key_message","In_set_server_key");
        Log.d("server_key_message","server_key: "+server_key);
        this.server_key = server_key;
    }



    public ServerKeyData(){ }

}
