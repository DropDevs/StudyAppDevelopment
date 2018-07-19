package com.gaurav.studyapp.developement;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
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
import com.gaurav.studyapp.developement.firebasedata.SendAssignmentData;
import com.gaurav.studyapp.developement.pdf.GetPdfFilePath;
import com.gaurav.studyapp.developement.saveddata.DeleteSavedData;
import com.gaurav.studyapp.developement.saveddata.LocalData;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Gaurav
 */
public class AddPDFAssignmentActivity extends AppCompatActivity {

  private TextView selectedFileNameTextView;
  private Button selectFileFromStorageButton, uploadAssignmentButton;
  private ImageView selectedFileImageView;
  ProgressBar assignmentUploadProgressBar;
  private Uri uri;
  public static final int PICKFILE_REQUEST_CODE = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_pdfassignment);


    FirebaseMessaging.getInstance().subscribeToTopic("notifications");

    final String subjectName = getIntent().getStringExtra("subject_name");
    final String branchName = getIntent().getStringExtra("branch_name");
    final String divisionName = getIntent().getStringExtra("div_name");
    final String otherInfo = getIntent().getStringExtra("other_info");

    selectedFileNameTextView = findViewById(R.id.selected_file_name_text_view);
    selectFileFromStorageButton = findViewById(R.id.select_file_from_storage_button);
    uploadAssignmentButton = findViewById(R.id.upload_assignment_button);
    selectedFileImageView = findViewById(R.id.selected_file_image_view);
    assignmentUploadProgressBar = findViewById(R.id.assignment_upload_pro_bar);
    assignmentUploadProgressBar.setVisibility(View.INVISIBLE);
    final LocalData localData = new LocalData();

    selectFileFromStorageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        browseDocuments();
      }
    });

    uploadAssignmentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(AddPDFAssignmentActivity.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
        uploadAssignments(subjectName,branchName,divisionName,otherInfo);
        uploadAssignmentButton.setAlpha(1);
        uploadAssignmentButton.setClickable(false);
      }
    });



  }


  private void browseDocuments(){

    String[] mimeTypes =
            {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                    "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                    "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                    "text/plain",
                    "application/pdf",
                    "application/zip"};

    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
      if (mimeTypes.length > 0) {
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
      }
    } else {
      String mimeTypesStr = "";
      for (String mimeType : mimeTypes) {
        mimeTypesStr += mimeType + "|";
      }
      intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
    }
    startActivityForResult(Intent.createChooser(intent,"ChooseFile"), PICKFILE_REQUEST_CODE);

  }


  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    LocalData localData = new LocalData();
    switch (requestCode) {
      case PICKFILE_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          // Get the Uri of the selected file
          Uri uri = data.getData();
          String uriString = uri.toString();

          try {
            GetPdfFilePath filePath = new GetPdfFilePath();
          }catch (Exception e){
            e.printStackTrace();
          }

          localData.savePDFUri(getApplicationContext(),uri);
          File myFile = new File(uriString);
          String path = myFile.getAbsolutePath();
          String displayName = null;

          if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
              cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
              if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                selectedFileNameTextView.setText("Selected File\n"+displayName);
              }
            } finally {
              cursor.close();
            }
          } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
            selectedFileNameTextView.setText("Selected File\n"+displayName);
          }
        }
        break;
        default:
    }
    super.onActivityResult(requestCode, resultCode, data);
  }




  public void uploadAssignments(final String subName, final String branchName, final String divName, final String otherInfo){

    LocalData localData = new LocalData();
    SendAssignmentData sendAssignmentData = new SendAssignmentData();

    final String pdfUriString = localData.getPdfUri(getApplicationContext());
    final Uri  pdfUri = Uri.parse(pdfUriString);
    sendAssignmentData.sendUserInputsWithId(getApplicationContext(),subName,branchName,divName,otherInfo,pdfUri,assignmentUploadProgressBar);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    DeleteSavedData data = new DeleteSavedData();
    data.deletePdfUriData(getApplicationContext());
  }
}
