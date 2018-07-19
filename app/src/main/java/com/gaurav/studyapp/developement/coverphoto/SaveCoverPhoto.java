package com.gaurav.studyapp.developement.coverphoto;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class SaveCoverPhoto {

  public void saveCoverPhotoUri(final Context context, final Uri coverPhotoUri, final ProgressBar coverPhotoProBar,
                                final byte[] compressedBitmapData){
    final UsersDetails usersDetails = new UsersDetails(context);
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    final StorageReference filePath = storageReference.child("user_info").child("cover_photos").child(coverPhotoUri.getLastPathSegment());

    filePath.putBytes(compressedBitmapData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
          @Override
          public void onSuccess(Uri uri) {
           final Uri downloadUri = uri;
           final DatabaseReference coverPhotoRef = FirebaseDatabase.getInstance().getReference()
                   .child("user_info")
                   .child(usersDetails.getPersonId())
                   .child("profile_info")
                   .getRef();
           coverPhotoRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               coverPhotoRef.child("cover_photo_uri").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                   Toast.makeText(context, "Cover Photo added", Toast.LENGTH_SHORT).show();
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
    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        coverPhotoProBar.setVisibility(View.VISIBLE);
      }
    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
        coverPhotoProBar.setVisibility(View.GONE);
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        coverPhotoProBar.setVisibility(View.GONE);
      }
    });

  }

}
