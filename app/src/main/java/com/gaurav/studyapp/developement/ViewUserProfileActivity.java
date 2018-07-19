package com.gaurav.studyapp.developement;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.gaurav.studyapp.developement.utils.CircularImage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * @author Gaurav
 */
public class ViewUserProfileActivity extends AppCompatActivity {

  TextView uploaderNameTextView, uploaderCollegeTextView, uploaderBioTextView, uploaderBranchTextView,
  uploaderDivTextView, uploaderRollNoTextView;
  ImageView uploaderProfileImageView;
  Button messageToUpoladerButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Uploader Profile");
    setContentView(R.layout.activity_view_user_profile);
    Toast.makeText(this, "In User Profile", Toast.LENGTH_SHORT).show();
    String uploderId = getIntent().getStringExtra("uploader_id");
    String cardViewKey = getIntent().getStringExtra("selected_cardview_key");
    uploaderNameTextView = findViewById(R.id.uploader_user_name_text_view);
    uploaderCollegeTextView = findViewById(R.id.uploader_college_name_text_view);
    uploaderBioTextView = findViewById(R.id.uploader_bio_text_view);
    uploaderBranchTextView = findViewById(R.id.uploader_branch_text_view);
    uploaderDivTextView = findViewById(R.id.uploader_div_text_view);
    uploaderRollNoTextView = findViewById(R.id.uploader_roll_no_text_view);
    uploaderProfileImageView = findViewById(R.id.uploader_image_view);
    messageToUpoladerButton = findViewById(R.id.message_to_uploader_button);

    messageToUpoladerButton.setVisibility(View.INVISIBLE);
    UsersDetails usersDetails = new UsersDetails(getApplicationContext());
    String currentUser = usersDetails.getPersonId();
    getUploaderName(uploderId);
    getUploaderProfile(uploderId);
    getUploaderCollegeName(uploderId);
    checkForUser(uploderId,usersDetails,currentUser,cardViewKey);

  }

  public void checkForUser(final String uploderId, UsersDetails usersDetails, String currentUser, final String cardViewKey){

    if (currentUser.equals(uploderId)){
      messageToUpoladerButton.setVisibility(View.INVISIBLE);
      messageToUpoladerButton.setClickable(false);
    }else if (!currentUser.equals(uploderId)){
      messageToUpoladerButton.setVisibility(View.VISIBLE);
      messageToUpoladerButton.setClickable(true);
    }

    messageToUpoladerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent toMessageActivity = new Intent(getApplicationContext(),SendMessageActivity.class);
        toMessageActivity.putExtra("message_to_key",uploderId);
        toMessageActivity.putExtra("card_view_key",cardViewKey);
        startActivity(toMessageActivity);
      }
    });
  }


  public void getUploaderName(String uploderId){
    final DatabaseReference uploderRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(uploderId)
            .getRef();
    uploderRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String uploderName = (String) dataSnapshot.child("name").getValue();
        String uploderImage = (String) dataSnapshot.child("image").getValue();
        uploaderNameTextView.setText(uploderName);
        Picasso.get().load(uploderImage).transform(new CircularImage()).into(uploaderProfileImageView);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public void getUploaderProfile(String uploaderId){
    final DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(uploaderId)
            .child("profile_info")
            .getRef();
    profileRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String userBio = (String) dataSnapshot.child("user_bio").getValue();
        String userBranch = (String) dataSnapshot.child("user_branch").getValue();
        String userDiv = (String) dataSnapshot.child("user_div").getValue();
        String userRollNo = (String) dataSnapshot.child("user_roll_no").getValue();
        uploaderBioTextView.setText(userBio);
        uploaderBranchTextView.setText(userBranch);
        uploaderDivTextView.setText(userDiv);
        uploaderRollNoTextView.setText(userRollNo);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

  }

  public void getUploaderCollegeName(String uploderId){
    final DatabaseReference collegeNameRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(uploderId)
            .child("college_selected")
            .getRef();
    collegeNameRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String collegeName = (String) dataSnapshot.child("users_college").getValue();
        uploaderCollegeTextView.setText(collegeName);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

}
