package com.gaurav.studyapp.developement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.menu.UserProfile;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * @author Gaurav
 */
public class EditUserProfileDetailsActivity extends AppCompatActivity {

  public EditText userBioEditText, userBranchEditText, userDivEditText, userRollNoEditText;
  private Button saveDetailsButton, resetDetailsButton;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_user_profile_details);

    userBioEditText = findViewById(R.id.edit_profile_user_bio_edit_text);
    userBranchEditText = findViewById(R.id.edit_profile_user_branch_edit_text);
    userDivEditText = findViewById(R.id.edit_profile_user_div_edit_text);
    userRollNoEditText = findViewById(R.id.edit_profile_user_roll_no_edit_text);
    saveDetailsButton = findViewById(R.id.edit_profile_save_profile_button);
    resetDetailsButton = findViewById(R.id.edit_profile_reset_profile_button);

    resetDetailsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        clearUserInputs();
      }
    });
    saveDetailsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d("TAG","save clicked!");
        Log.d("TAG","InEditDetailsActivity");
        final String userBio = userBioEditText.getText().toString().trim();
        final String userBranch = userBranchEditText.getText().toString().trim();
        final String userDivision = userDivEditText.getText().toString().trim();
        final String userRollNo = userRollNoEditText.getText().toString().trim();

        checkOne(userBio,userBranch,userDivision,userRollNo);
      }
    });

  }

  public void checkOne(String userBio, String userBranch, String userDivision, String userRollNo){
    if (TextUtils.isEmpty(userBio) && TextUtils.isEmpty(userBranch) && TextUtils.isEmpty(userDivision) && TextUtils.isEmpty(userRollNo) ||
            TextUtils.isEmpty(userBio) || TextUtils.isEmpty(userBranch) || TextUtils.isEmpty(userDivision) || TextUtils.isEmpty(userRollNo)){
      new AlertDialog.Builder(EditUserProfileDetailsActivity.this)
              .setMessage("Enter all fields to save!")
              .setTitle("Error")
              .setNeutralButton("Ok",null)
              .show();
    }else {
      checkTwo(userBio,userBranch,userDivision,userRollNo);
    }
  }

  public void checkTwo(final String userBio, final String userBranch, final String userDivision, final String userRollNo){
    new AlertDialog.Builder(EditUserProfileDetailsActivity.this)
            .setTitle("Confirmation")
            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                sendUserProfileData(userBio,userBranch,userDivision,userRollNo);
                Toast.makeText(EditUserProfileDetailsActivity.this, "Profile Saved!", Toast.LENGTH_SHORT).show();
              }
            })
            .setNegativeButton("Not confirm", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                clearUserInputs();
              }
            })
            .show();
  }

  public void sendUserProfileData(final String userBio, final String userBranch, final String userDivision, final String userRollNo){
    UsersDetails usersDetails = new UsersDetails(getApplicationContext());
    final DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(usersDetails.getPersonId())
            .child("profile_info");
    profileRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (userBio != null) {
          profileRef.child("user_bio").setValue(userBio);
        }
        if (userBranch != null) {
          profileRef.child("user_branch").setValue(userBranch);
        }
        if (userDivision != null) {
          profileRef.child("user_div").setValue(userDivision);
        }
        if (userRollNo != null) {
          profileRef.child("user_roll_no").setValue(userRollNo);
        }Log.d("SEND","Data send!");
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public void clearUserInputs(){
    userBioEditText.getText().clear();
    userBranchEditText.getText().clear();
    userDivEditText.getText().clear();
    userRollNoEditText.getText().clear();
    Toast.makeText(EditUserProfileDetailsActivity.this, "Data Cleared!", Toast.LENGTH_SHORT).show();
  }

}
