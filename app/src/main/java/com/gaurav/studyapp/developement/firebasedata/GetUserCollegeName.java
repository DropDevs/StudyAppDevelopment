package com.gaurav.studyapp.developement.firebasedata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GetUserCollegeName {


  public GetUserCollegeName() {
  }

  public void getCollegeName(Context context){
    Log.d("message","getCollegeNameCalled!");
    UsersDetails usersDetails = new UsersDetails(context);
    final DatabaseReference collegeNameRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(usersDetails.getPersonId())
            .child("college_selected");
    collegeNameRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        CollegeNameGetterSetter getterSetter = new CollegeNameGetterSetter();
        try {
          String collegeName = (String) dataSnapshot.child("users_college").getValue();
          getterSetter.setCollegeName((String) dataSnapshot.child("users_college").getValue());
          Log.d("message", collegeName);
        }catch (Exception e){
          Log.d("set_college_name",e.toString());
        }

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

}
