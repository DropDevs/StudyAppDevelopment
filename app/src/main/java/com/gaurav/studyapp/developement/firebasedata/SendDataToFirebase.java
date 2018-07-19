package com.gaurav.studyapp.developement.firebasedata;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SendDataToFirebase {
  public SendDataToFirebase() {
  }

  public void sendUserCollegeToFirebase(Context context, final Activity activity, final String savedCollegeName, final String savedCategory){
    UsersDetails usersDetails = new UsersDetails(context);
    final DatabaseReference userCollegeRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(usersDetails.getPersonId())
            .child("college_selected")
            .getRef();
    userCollegeRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        userCollegeRef.child("users_college").setValue(savedCollegeName);
        userCollegeRef.child("category_selected").setValue(savedCategory);
        Toast.makeText(activity, "Welcome to\n"+savedCollegeName, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }
}
