package com.gaurav.studyapp.developement.server;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseServerKey {

    public void setServerKey(){
        final ServerKeyData serverKeyData = new ServerKeyData();
        final DatabaseReference serverKeyRef = FirebaseDatabase.getInstance().getReference()
                .child("server_data")
                .getRef();
        serverKeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String server_key = (String) dataSnapshot.child("server_key").getValue();
                    serverKeyData.setServer_key(server_key);
                }catch (Exception e){
                    Log.d("server_key_message","Exception: "+e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
