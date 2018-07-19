package com.gaurav.studyapp.developement;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.data.SelectMultipleImages;
import com.gaurav.studyapp.developement.firebasedata.CollegeNameGetterSetter;
import com.gaurav.studyapp.developement.firebasedata.ImagesKeyRef;
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

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Gaurav
 */
public class AddImageAssignmentActivity extends AppCompatActivity {

  private ImageView selectedImagesImgView;
  private Button selecteImagesButton, uploadImagesButton;
  private static final int SELECT_IMAGES_CODE = 1;
  SelectMultipleImages images = new SelectMultipleImages();
  ImagesKeyRef keyRef = new ImagesKeyRef();
  private ProgressBar asUploadImagesProBar;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_image_assignment);

    selectedImagesImgView = findViewById(R.id.images_selected_images_image_view);
    selecteImagesButton = findViewById(R.id.images_select_images_button);
    uploadImagesButton = findViewById(R.id.image_upload_images_button);
    asUploadImagesProBar = findViewById(R.id.as_upload_images_pro_bar);
    asUploadImagesProBar.setVisibility(View.INVISIBLE);

    final String subjectName = getIntent().getStringExtra("subject_name");
    final String div_name = getIntent().getStringExtra("div_name");
    final String branch_name = getIntent().getStringExtra("branch_name");
    final String other_info = getIntent().getStringExtra("other_info");

    selecteImagesButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_IMAGES_CODE);
      }
    });


    uploadImagesButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          uploadImages(subjectName,branch_name,div_name,other_info);
          Toast.makeText(AddImageAssignmentActivity.this, "Uploading please wait...", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
      Log.d("message", "On Activity Result called!");
      if (data.getData() != null) {

        Uri mImageUri = data.getData();
        keyRef.setSingleImageUri(mImageUri);
        Log.d("message","Single Image Selected!");

      } else {
        if (data.getClipData() != null) {
          int totalItemSelected = data.getClipData().getItemCount();
          Log.d("message","No. of images"+totalItemSelected);
          images.setSelectedImages(totalItemSelected);
          images.setData(data);
        }

      }

    }
  }

  public void singleImageSelected(Uri imageUri, StorageReference asImageRef,
                                  final String subjectName, String branch_name, final String div_name, final String other_info){
    final UsersDetails usersDetails = new UsersDetails(getApplicationContext());
    final StorageReference filePath = asImageRef.child("assignments").child("image_files").child(imageUri.getLastPathSegment());


    filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        Log.d("message", "IN FilePath");
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
          @Override
          public void onSuccess(Uri uri) {
            final Uri downloadUri = uri;
            Log.d("message", downloadUri.toString());
            keyRef.setImageUri(downloadUri);
            final DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference()
                    .child("user_uploads_without_id")
                    .child("assignments")
                    .child(CollegeNameGetterSetter.getCollegeName())
                    .push();
            assignmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assignmentRef.child("as_branch_name").setValue(subjectName);
                assignmentRef.child("as_div_name").setValue(div_name);
                assignmentRef.child("as_subject_name").setValue(subjectName);
                assignmentRef.child("as_other_info").setValue(other_info);
                assignmentRef.child("file_type").setValue("Image");
                assignmentRef.child("no_of_files").setValue(String.valueOf(1));
                assignmentRef.child("as_uploader_id").setValue(usersDetails.getPersonId());
                assignmentRef.child("as_uploader_name").setValue(usersDetails.getPersonName());
                assignmentRef.child("image_uri").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AddImageAssignmentActivity.this, "Upload Complete!", Toast.LENGTH_SHORT).show();
                    asUploadImagesProBar.setVisibility(View.INVISIBLE);
                    Log.d("message", "Single Image task complete!");
                  }
                });

              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
            });

          }
        }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.d("message", "Uri Error" + e.toString());
          }
        });
      }
    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        asUploadImagesProBar.setVisibility(View.VISIBLE);
      }
    });
  }


    public void uploadImages (final String subjectName, String branch_name, final String div_name, final String other_info) throws InterruptedException {
      StorageReference asImageRef = FirebaseStorage.getInstance().getReference();
    uploadImagesButton.setClickable(false);
    if (images.getSelectedImages() == 0){

      singleImageSelected(keyRef.getSingleImageUri(),asImageRef,subjectName,branch_name,div_name,other_info);

    }else if (images.getSelectedImages() != 0){
      final UsersDetails usersDetails = new UsersDetails(getApplicationContext());
      int j = 0;
      Uri imageUri = Objects.requireNonNull(images.getData().getClipData()).getItemAt(j).getUri();
      Log.d("message", "Else called");

      final StorageReference filePath = asImageRef.child("assignments").child("image_files").child(imageUri.getLastPathSegment());


      filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
          Log.d("message", "IN FilePath");
          filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
              final Uri downloadUri = uri;
              Log.d("message", downloadUri.toString());
              keyRef.setImageUri(downloadUri);
              final DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference()
                      .child("user_uploads_without_id")
                      .child("assignments")
                      .child(CollegeNameGetterSetter.getCollegeName())
                      .push();
              assignmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  keyRef.setRefKey(dataSnapshot.getKey().toString());
                  Log.d("message", "REFKEY" + dataSnapshot.getKey().toString());
                  assignmentRef.child("as_branch_name").setValue(subjectName);
                  assignmentRef.child("as_div_name").setValue(div_name);
                  assignmentRef.child("as_subject_name").setValue(subjectName);
                  assignmentRef.child("as_other_info").setValue(other_info);
                  try {
                    assignmentRef.child("no_of_files").setValue(String.valueOf(images.getSelectedImages()));
                  }catch (Exception e){
                    Log.d("message","Exception_Error"+e.toString());
                  }
                  assignmentRef.child("as_uploader_id").setValue(usersDetails.getPersonId());
                  assignmentRef.child("file_type").setValue("Image");
                  assignmentRef.child("as_uploader_name").setValue(usersDetails.getPersonName());
                  try {
                    assignmentRef.child("as_uploader_image").setValue(usersDetails.getPersonImage().toString());
                  }catch (Exception e){
                    Log.d("message","Exception_Error"+e.toString());
                  }
                  assignmentRef.child("image_uri").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      Log.d("message", "If Exit");
                      methodTwo();
                    }
                  });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
              });

            }
          }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Log.d("message", "Uri Error" + e.toString());
            }
          });
        }
      }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
          asUploadImagesProBar.setVisibility(View.VISIBLE);
        }
      });
    }
  }

  public void methodTwo(){
    Log.d("message","MethodTwoCalled");
    final int size = images.getSelectedImages();
    Log.d("message","Size "+size);
    final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    for (int i = 1; i < size; i++) {
      Log.d("message","In for loop");
      Log.d("message", "value of i" + String.valueOf(i));
      Uri imageUri = Objects.requireNonNull(images.getData().getClipData()).getItemAt(i).getUri();
      keyRef.setImageUri(imageUri);
      final StorageReference filePath = storageReference.child("assignments").child("image_files").child(imageUri.getLastPathSegment());
      Log.d("message", imageUri.toString());
      final int finalI = i;
      Log.d("message","Final I"+finalI);
      final int finalI1 = i;
      filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
         filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
           @Override
           public void onSuccess(Uri uri) {
             Uri downloadUri = uri;
             Log.d("message","In getDownloadUri");
             Log.d("message","DownloadUri"+downloadUri.toString());
             Log.d("message","Key_Ref"+keyRef.getRefKey().toString());
             sendData(finalI,downloadUri);
           }
         });
        }
      }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
          asUploadImagesProBar.setVisibility(View.VISIBLE);
          Toast.makeText(AddImageAssignmentActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
        }
      }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
          if (finalI == (size-1)){
            asUploadImagesProBar.setVisibility(View.INVISIBLE);
            Toast.makeText(AddImageAssignmentActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
          }
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Log.d("message","ExceptionError"+e.toString());
        }
      });
      Log.d("message","I"+i);
    }

  }

  public void sendData(final int finalI,final Uri downloadUri){
    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
            .child("user_uploads_without_id")
            .child("assignments")
            .child(CollegeNameGetterSetter.getCollegeName())
            .child(keyRef.getRefKey());
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        reference.child("image_uri"+String.valueOf(finalI)).setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            Log.d("message","Else successful!");
          }
        }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.d("ExceptionError","In else"+e.toString());
          }
        });
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }






}
