package com.gaurav.studyapp.developement.firebasedata;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gaurav.studyapp.developement.fcmnotifications.AssignmentNotification;
import com.gaurav.studyapp.developement.menu.Home;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

/**
 * @author Gaurav
 */
public class SendAssignmentData {

    AssignmentNotification notification = new AssignmentNotification();


  public SendAssignmentData() {
  }

  public void sendUserInputsWithId(final Context context, final String subjectName, final String branchName, final String divisionName, final String otherInfo, final Uri uri, final ProgressBar uploadProgressBar){
    final UsersDetails usersDetails = new UsersDetails(context);
    notification.sendNotifications(context,usersDetails.getPersonName(),branchName);
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    final StorageReference filepath = storageReference.child("assignments").child("pdf_files").child(uri.getLastPathSegment());
    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
          @Override
          public void onSuccess(Uri uri) {
            final Uri downloadUri = uri;
            final DatabaseReference collegeRef = FirebaseDatabase.getInstance().getReference()
                    .child("user_info")
                    .child(usersDetails.getPersonId())
                    .child("college_selected")
                    .getRef();
            collegeRef.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String collegeName = (String) dataSnapshot.child("users_college").getValue();
                final DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference()
                        .child("user_uploads_without_id")
                        .child("assignments")
                        .child(Objects.requireNonNull(collegeName))
                        .push();
                assignmentRef.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    assignmentRef.child("as_subject_name").setValue(subjectName);
                    assignmentRef.child("as_branch_name").setValue(branchName);
                    assignmentRef.child("as_div_name").setValue(divisionName);
                    assignmentRef.child("as_other_info").setValue(otherInfo);
                    assignmentRef.child("as_uploader_id").setValue(usersDetails.getPersonId());
                    assignmentRef.child("as_uploader_name").setValue(usersDetails.getPersonName());
                    assignmentRef.child("file_type").setValue("PDF");
                    assignmentRef.child("as_uploader_image").setValue((usersDetails.getPersonImage()).toString());
                    assignmentRef.child("pdf_file_uri").setValue(downloadUri.toString());


                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
                });
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
            });
          }
        });


      }
    })
    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        Toast.makeText(context, "Uploading...", Toast.LENGTH_SHORT).show();
        uploadProgressBar.setVisibility(View.VISIBLE);
      }
    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        uploadProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(context, "Upload Successful!", Toast.LENGTH_SHORT).show();
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        uploadProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(context, "Error Accured! Please try again later.", Toast.LENGTH_SHORT).show();
        Log.d("Upload Error",e.toString());
      }
    });


  }




}
