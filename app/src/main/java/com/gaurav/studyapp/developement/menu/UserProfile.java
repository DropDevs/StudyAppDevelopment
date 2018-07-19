package com.gaurav.studyapp.developement.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.EditUserProfileDetailsActivity;
import com.gaurav.studyapp.developement.coverphoto.SaveCoverPhoto;
import com.gaurav.studyapp.developement.imagecompression.CompressImage;
import com.gaurav.studyapp.developement.imagecompression.UriToBitmap;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.gaurav.studyapp.developement.utils.CircularImage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

/**
 * @author Gaurav
 */
public class UserProfile extends android.support.v4.app.Fragment {

  private TextView userNameTV, clgNameTV, userBioTV, userBranchTV, userDivTV, userRollTV;
  private Button messageToUserButton, editDetailsButton;
  private ImageView userProfileImageView, userCoverPhoto, editCoverPhoto;
  private LinearLayout messageLayout, editLayout;
  private ProgressBar coverPhotoProBar;
  private static final int PICK_IMAGE = 1;
  SaveCoverPhoto saveCoverPhoto = new SaveCoverPhoto();
  UriToBitmap uriToBitmap = new UriToBitmap();
  CompressImage compressImage = new CompressImage();


  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
    userProfileImageView = view.findViewById(R.id.profile_user_image_imgView);
    userNameTV = view.findViewById(R.id.profile_user_name_text_view);
    clgNameTV = view.findViewById(R.id.profile_user_college_name_text_view);
    userBioTV = view.findViewById(R.id.profile_user_bio_text_view);
    userBranchTV = view.findViewById(R.id.profile_user_branch_text_view);
    userDivTV = view.findViewById(R.id.profile_user_div_text_view);
    userRollTV = view.findViewById(R.id.profile_user_roll_no_text_view);
//    messageToUserButton = view.findViewById(R.id.profile_message_to_user_button);
    editDetailsButton = view.findViewById(R.id.profile_edit_user_details_button);
    messageLayout = view.findViewById(R.id.profile_message_linear_layout);
    editLayout = view.findViewById(R.id.profile_edit_details_layout);
    userCoverPhoto = view.findViewById(R.id.profile_cover_photo_image_view);
    editCoverPhoto = view.findViewById(R.id.profile_edit_cover_image_view);
    coverPhotoProBar = view.findViewById(R.id.profile_cover_photo_pro_bar);

    coverPhotoProBar.setVisibility(View.GONE);

    editCoverPhoto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
      }
    });

    UsersDetails usersDetails = new UsersDetails(getContext());
    userNameTV.setText(usersDetails.getPersonName());
    Picasso.get().load(usersDetails.getPersonImage()).transform(new CircularImage()).into(userProfileImageView);
    setCollegeName(usersDetails);
//    CheckForMessageButton messageButton = new CheckForMessageButton(getContext(),messageLayout,usersDetails.getPersonName());
    editDetailsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent toEditDetailsActivity = new Intent(getActivity(), EditUserProfileDetailsActivity.class);
        Log.d("TAG","toEditDetailsActivity");
        startActivity(toEditDetailsActivity);
      }
    });

    setUserProfile(usersDetails);
    setUserCoverPhoto(usersDetails);
    return view;
  }


  public void setUserCoverPhoto(UsersDetails usersDetails){
    final DatabaseReference getCoverPhotoRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(usersDetails.getPersonId())
            .child("profile_info")
            .getRef();
    getCoverPhotoRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          String coverPhotoUri = (String) dataSnapshot.child("cover_photo_uri").getValue();
          if (coverPhotoUri != null){
            Uri coverPhoto = Uri.parse(coverPhotoUri);
            Picasso.get().load(coverPhotoUri).into(userCoverPhoto);
          }else if (coverPhotoUri.equals(null)){
            userCoverPhoto.setImageResource(R.drawable.mylogo);
          }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
      if (data != null){
        Uri coverPhotoUri = data.getData();
        try {
          userCoverPhoto.setImageURI(coverPhotoUri);

            String uriStr = coverPhotoUri.toString();
            Bitmap coverPhotoBitmap = uriToBitmap.toBitmap(uriStr);
            Bitmap compressedBitmap = compressImage.CompressBitmapImage(coverPhotoBitmap);
            Uri sendUri = getImageUri(compressedBitmap);
            byte[] compressedByteData = getByteData(compressedBitmap);

          saveCoverPhoto.saveCoverPhotoUri(getContext(), coverPhotoUri, coverPhotoProBar, compressedByteData);
        }catch (Exception e){
          Log.d("cover_photo","Exception_error: "+e.toString());
        }
      }

    }
  }

  public byte[] getByteData(Bitmap bitmap){
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] data = baos.toByteArray();
    return  data;
  }

  public Uri getImageUri(Bitmap inImage) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), inImage, "Title", null);
    return Uri.parse(path);
  }



  public void setUserProfile(UsersDetails usersDetails){

      final DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference()
              .child("user_info")
              .child(usersDetails.getPersonId())
              .child("profile_info")
              .getRef();
      profileRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          if ((String) dataSnapshot.child("user_bio").getValue() != null){
            userBioTV.setText((String) dataSnapshot.child("user_bio").getValue());
          }
          if ((String) dataSnapshot.child("user_branch").getValue() != null){
            userBranchTV.setText("Branch: "+(String) dataSnapshot.child("user_branch").getValue().toString());
          }
          if ((String) dataSnapshot.child("user_div").getValue() != null){
            userDivTV.setText("Division: "+(String) dataSnapshot.child("user_div").getValue().toString());
          }
          if ((String) dataSnapshot.child("user_roll_no").getValue() != null){
            userRollTV.setText("Roll No: "+(String) dataSnapshot.child("user_roll_no").getValue().toString());
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
  }

  public void setCollegeName(UsersDetails usersDetails){
    final DatabaseReference collegeNameRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(usersDetails.getPersonId())
            .child("college_selected")
            .getRef();
    collegeNameRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String collegeName = (String) dataSnapshot.child("users_college").getValue();
        clgNameTV.setText(collegeName);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getActivity().setTitle("Profile");
  }


}
