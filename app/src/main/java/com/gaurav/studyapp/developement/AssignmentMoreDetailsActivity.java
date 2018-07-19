package com.gaurav.studyapp.developement;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.download.DownloadCompleteCheck;
import com.gaurav.studyapp.developement.download.ImagesDownload;
import com.gaurav.studyapp.developement.firebasedata.CollegeNameGetterSetter;
import com.gaurav.studyapp.developement.utils.CircularImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * @author Gaurav
 */
public class AssignmentMoreDetailsActivity extends AppCompatActivity {

  TextView branchTextView, userNameTextView, divisionTextView, subjectTextView,
  aboutAssignmentTextView, attachmentTextView;
  ImageView userImageView;
  Button downloadButton;
  ProgressBar downloadProgressBar;
  ImagesDownload download = new ImagesDownload();
  DownloadCompleteCheck check = new DownloadCompleteCheck();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_assignment_more_details);
    Toast.makeText(this, "In Assignment", Toast.LENGTH_SHORT).show();

    branchTextView = findViewById(R.id.more_details_as_branch);
    userNameTextView = findViewById(R.id.more_details_as_user_name);
    divisionTextView = findViewById(R.id.more_details_as_div_name);
    subjectTextView = findViewById(R.id.more_details_as_subject_name);
    aboutAssignmentTextView = findViewById(R.id.more_details_as_about_text_view);
    attachmentTextView = findViewById(R.id.more_details_as_file_type);
    userImageView = findViewById(R.id.more_details_as_user_image);
    downloadButton = findViewById(R.id.more_details_as_download_button);
    downloadProgressBar = findViewById(R.id.as_down_pro_bar);
    downloadProgressBar.setVisibility(View.INVISIBLE);

    final String cardViewKey = getIntent().getStringExtra("as_selected_card_view_key");
    final DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference()
            .child("user_uploads_without_id")
            .child("assignments")
            .child(CollegeNameGetterSetter.getCollegeName())
            .child(cardViewKey)
            .getRef();
    assignmentRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        try {
          String user_name = (String) dataSnapshot.child("as_uploader_name").getValue();
          String user_image = (String) dataSnapshot.child("as_uploader_image").getValue();
          String user_division = (String) dataSnapshot.child("as_div_name").getValue();
          String subject_name = (String) dataSnapshot.child("as_subject_name").getValue();
          String branch_name = (String) dataSnapshot.child("as_branch_name").getValue();
          String other_info = (String) dataSnapshot.child("as_other_info").getValue();

          userNameTextView.setText(user_name);
          divisionTextView.setText(user_division);
          subjectTextView.setText(subject_name);
          branchTextView.setText(branch_name);
          aboutAssignmentTextView.setText(other_info);
          Picasso.get().load(user_image).transform(new CircularImage()).into(userImageView);
        }catch (Exception e){
          Log.d("Error",e.toString());
        }
        try {
          String file_type = (String) dataSnapshot.child("file_type").getValue();
          attachmentTextView.setText(file_type);
        }catch (Exception e){
          Log.d("Error",e.toString());
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    downloadButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
      final DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference()
              .child("user_uploads_without_id")
              .child("assignments")
              .child(CollegeNameGetterSetter.getCollegeName())
              .child(cardViewKey)
              .getRef();
      checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          String file_type = (String) dataSnapshot.child("file_type").getValue();
          chechFileType(file_type,cardViewKey);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
      }
    });

  }

  public void chechFileType(String file_type, String card_view_key){
    if ("PDF".equalsIgnoreCase(file_type)){
      pdfFileType(card_view_key);
    }else if ("Image".equalsIgnoreCase(file_type)){
      imageFileType(card_view_key);
    }
  }

  public void imageFileType(final String card_view_key){
    final DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference()
            .child("user_uploads_without_id")
            .child("assignments")
            .child(CollegeNameGetterSetter.getCollegeName())
            .child(card_view_key)
            .getRef();
    imagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChild("no_of_files")) {
          String noOfImages = (String) dataSnapshot.child("no_of_files").getValue();
          getImageUris(card_view_key, noOfImages);
        }else{
          Log.d("message","Child number of files not found!");
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }



  public void getImageUris(final String card_view_key, String no_of_images){
    final int imagesCount = Integer.parseInt(no_of_images);
    Log.d("message","Images_Count"+imagesCount);
    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
             .child("user_uploads_without_id")
             .child("assignments")
             .child(CollegeNameGetterSetter.getCollegeName())
             .child(card_view_key)
             .getRef();
     reference.addValueEventListener(new ValueEventListener() {
       @Override
       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         if (imagesCount == 1){
           Log.d("message","Only one image to download");
           String uri_0 = (String) dataSnapshot.child("image_uri").getValue();
           try {
             downloadImageFile(card_view_key,uri_0);
           } catch (IOException e) {
             e.printStackTrace();
           }
         }
       else if (imagesCount > 1){
           Log.d("message","More than one images to download");
         final String uri_zero = (String) dataSnapshot.child("image_uri").getValue();
         for (int i = 1; i < imagesCount; i++){
           Log.d("message","In for loop");
             String integer = String.valueOf(i);
             String  strName = (String) dataSnapshot.child("image_uri"+integer).getValue();
             String child = "image_uri"+integer;
             Log.d("message","Child: "+child);
             Log.d("message","images_uri: "+strName);
//             download.setUri_name(strName);
             try{
               downloadMutlipleImages(strName,uri_zero,imagesCount);
             }catch (Exception e){
               Log.d("message","Multiple img down error: "+e.toString());
             }

           }
         }
       }
       @Override
       public void onCancelled(@NonNull DatabaseError databaseError) {

       }
     });
  }

  public void downloadMutlipleImages(String uri, String uri_0,int imageCount){
    Log.d("message",String.valueOf(imageCount));
    Log.d("message","In multiple images download method");
    File direct = new File(Environment.getExternalStorageDirectory()+"StudyAppDownloads");

    if (!direct.exists()) {
      direct.mkdirs();
    }

    DownloadManager mgr = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
    for (int i = 1; i< imageCount; i++) {
      Uri downloadUri = Uri.parse(uri);
      DownloadManager.Request request1 = new DownloadManager.Request(downloadUri);
      long downIdTwo = mgr.enqueue(request1);
      request1.setAllowedNetworkTypes(
              DownloadManager.Request.NETWORK_WIFI
                      | DownloadManager.Request.NETWORK_MOBILE)
              .setAllowedOverRoaming(true)
              .setDescription("Downloading please wait...")
              .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename.jpg");
        Log.d("message",String.valueOf(i));
        boolean lastCheck = check.downloadComplete(getApplicationContext(),downIdTwo);
        if (lastCheck){
          Log.d("message","Last Download Completed");
          Toast.makeText(this, "Download Complete...", Toast.LENGTH_SHORT).show();
          startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
        }

    }

    Uri downZerothUri = Uri.parse(uri_0);
    DownloadManager.Request request = new DownloadManager.Request(
            downZerothUri);
    long downloadId = Objects.requireNonNull(mgr).enqueue(request);
    request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI
                    | DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(true)
            .setDescription("Downloading please wait...")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename.jpg");
   /* try {
      boolean isComplete = check.downloadComplete(getApplicationContext(),downloadId);
      if (isComplete){
        Log.d("message","Uri_0_downloaded");
      }else{
        Log.d("message","Downloading zeroth image uri...");
        Toast.makeText(this, "Downloading please wait...", Toast.LENGTH_SHORT).show();
      }
    }catch (Exception e){
      Log.d("message","Exception_Error: "+e.toString());
    } */

  }

  public void downloadImageFile(String card_view_key, String uri_0) throws IOException{
    Log.d("message","In download image file");
    File direct = new File(Environment.getExternalStorageDirectory()+"StudyAppDownloads");

    if (!direct.exists()) {
      direct.mkdirs();
    }

    DownloadManager mgr = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
    Uri downloadUri = Uri.parse(uri_0);
    DownloadManager.Request request = new DownloadManager.Request(
            downloadUri);
    long downloadId = Objects.requireNonNull(mgr).enqueue(request);
    request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI
                    | DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(true)
            .setDescription("Downloading please wait...")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename.jpg");

    try {
      boolean isComplete = check.downloadComplete(getApplicationContext(),downloadId);
      if (isComplete){
        Log.d("message","Download complete!");
        Toast.makeText(AssignmentMoreDetailsActivity.this, "Download complete!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
      }else{
        Log.d("message","Downloading...");
        Toast.makeText(this, "Downloading please wait...", Toast.LENGTH_SHORT).show();
      }
    }catch (Exception e){
      Log.d("message","Exception_Error: "+e.toString());
    }
  }


  public void pdfFileType(String cardViewKey){
    final DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference()
            .child("user_uploads_without_id")
            .child("assignments")
            .child(CollegeNameGetterSetter.getCollegeName())
            .child(cardViewKey)
            .getRef();
    assignmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String pdf_string_uri = (String) dataSnapshot.child("pdf_file_uri").getValue();
        downloadPdfUri(pdf_string_uri);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

public void downloadPdfUri(String url){
  Intent intent = new Intent(Intent.ACTION_VIEW);
  intent.setDataAndType(Uri.parse(url), "application/pdf");
  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
  Intent newIntent = Intent.createChooser(intent, "Open File");
  try {
    startActivity(newIntent);
  } catch (ActivityNotFoundException e) {
    Toast.makeText(this, "Download a pdf reader", Toast.LENGTH_SHORT).show();
    Log.d("ExceptionError",e.toString());
    // Instruct the user to install a PDF reader here, or something
  }
}


}
