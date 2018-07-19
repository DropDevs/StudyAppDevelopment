package com.gaurav.studyapp.developement.menu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.AddImageAssignmentActivity;
import com.gaurav.studyapp.developement.AddPDFAssignmentActivity;

import com.google.firebase.storage.StorageReference;

public class AddAssignments extends Fragment {


  private EditText subjectNameEditText, branchNameEditText, divisionNameEditText, infoAboutAssignmentEditText;
  private Button proceedButton;
  private RadioGroup fileTypeRadioGroup;
  private String selectedFileType = "PDF";
  private StorageReference storageReference;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_add_assignments, container, false);
    fileTypeRadioGroup = (RadioGroup) view.findViewById(R.id.file_type_radio_group);
    subjectNameEditText = view.findViewById(R.id.as_sub_edit_text);
    branchNameEditText = view.findViewById(R.id.as_branch_edit_text);
    divisionNameEditText = view.findViewById(R.id.as_div_edit_text);
    infoAboutAssignmentEditText = view.findViewById(R.id.as_other_info_edit_text);
    proceedButton = view.findViewById(R.id.as_proceed_to_upload_selected_type_button);


    fileTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        try {
          switch (checkedId) {
            case R.id.pdf_file_radio_button:
              selectedFileType = "PDF";
              Toast.makeText(getContext(), "PDF File Selected!", Toast.LENGTH_SHORT).show();
              break;
            case R.id.image_file_radio_button:
              selectedFileType = "Image";
              Toast.makeText(getContext(), "Image File Selected!", Toast.LENGTH_SHORT).show();
              break;
            default:
              selectedFileType = "PDF";

          }
        }catch (Exception e){
          Toast.makeText(getContext(), "Select a file type!", Toast.LENGTH_SHORT).show();
          Log.d("Error", e.toString());
        }
      }
    });






    proceedButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String subjectName = subjectNameEditText.getText().toString().trim();
        final String branchName = branchNameEditText.getText().toString().trim();
        final String divisionName = divisionNameEditText.getText().toString().trim();
        final String infoAboutAssignment = infoAboutAssignmentEditText.getText().toString().trim();
        if (TextUtils.isEmpty(subjectName) && TextUtils.isEmpty(branchName) && TextUtils.isEmpty(divisionName) &&
                TextUtils.isEmpty(infoAboutAssignment) || TextUtils.isEmpty(subjectName) || TextUtils.isEmpty(branchName) || TextUtils.isEmpty(divisionName) || TextUtils.isEmpty(infoAboutAssignment)){
          new AlertDialog.Builder(getContext())
                  .setMessage("Enter all fields to upload")
                  .setNeutralButton("Ok",null)
                  .show();
        }else {
          try {
            if (selectedFileType.equalsIgnoreCase("PDF")) {
              Intent toAddPdfActivity = new Intent(getContext(), AddPDFAssignmentActivity.class);
              toAddPdfActivity.putExtra("subject_name", subjectName);
              toAddPdfActivity.putExtra("branch_name", branchName);
              toAddPdfActivity.putExtra("div_name", divisionName);
              toAddPdfActivity.putExtra("other_info", infoAboutAssignment);
              startActivity(toAddPdfActivity);
            } else if (selectedFileType.equalsIgnoreCase("Image")) {
              Intent toAddImageAssignmentActivity = new Intent(getContext(), AddImageAssignmentActivity.class);
              toAddImageAssignmentActivity.putExtra("subject_name", subjectName);
              toAddImageAssignmentActivity.putExtra("branch_name", branchName);
              toAddImageAssignmentActivity.putExtra("div_name", divisionName);
              toAddImageAssignmentActivity.putExtra("other_info", infoAboutAssignment);
              startActivity(toAddImageAssignmentActivity);
            } else if (selectedFileType.equals(null)) {
              Toast.makeText(getContext(), "Please select a file type!", Toast.LENGTH_SHORT).show();
            }
          }catch (Exception e){
            Toast.makeText(getContext(), "Please select a file type!", Toast.LENGTH_SHORT).show();
            Log.d("procced_button_click",e.toString());
          }
        }
      }
    });

    return view;
  }



  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getActivity().setTitle("Add Assignments");
  }
}
