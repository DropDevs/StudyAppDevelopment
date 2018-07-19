package com.gaurav.studyapp.developement.firebasedata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CollegeNameWithStudentInfo {

  public CollegeNameWithStudentInfo(final Context context, final String collegeName) {
    final UsersDetails usersDetails = new UsersDetails(context);
    final DatabaseReference clgWithStudentRef = FirebaseDatabase.getInstance().getReference()
            .child("college_name_with_student_info")
            .child(collegeName)
            .push()
            .getRef();
    clgWithStudentRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        clgWithStudentRef.child("student_name").setValue(usersDetails.getPersonName());
        clgWithStudentRef.child("student_image").setValue(usersDetails.getPersonImage().toString());
        clgWithStudentRef.child("student_id").setValue(usersDetails.getPersonId()).addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {
            Toast.makeText(context, "Data send successfully!", Toast.LENGTH_SHORT).show();

          }
        }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.d("Error",e.toString());
          }
        });
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

}
