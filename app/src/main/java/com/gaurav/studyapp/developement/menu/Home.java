package com.gaurav.studyapp.developement.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.UsersCommentsActivity;
import com.gaurav.studyapp.developement.ViewUserProfileActivity;
import com.gaurav.studyapp.developement.data.AssignmentData;
import com.gaurav.studyapp.developement.firebasedata.AssignmentsFirebaseRecyclerClass;
import com.gaurav.studyapp.developement.firebasedata.CollegeNameGetterSetter;
import com.gaurav.studyapp.developement.usersdata.MyApp;
import com.gaurav.studyapp.developement.utils.CircularImage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * @author Gaurav
 */
public class Home extends Fragment {


  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    RecyclerView userUploadsRecyclerView;
    userUploadsRecyclerView = view.findViewById(R.id.user_uploads_recycler_view);
    userUploadsRecyclerView.setHasFixedSize(true);
    userUploadsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    String nameOfCollege = CollegeNameGetterSetter.getCollegeName();
    Log.d("message","message");
    Log.d("message",nameOfCollege);
    new AssignmentsFirebaseRecyclerClass(getContext(),userUploadsRecyclerView,nameOfCollege);

    return view;
  }

  public static class UserUploadsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    String as_branch_name;
    String as_div_name;
    String as_other_info;
    String as_subject_name;
    String as_uploader_name;
    String as_uploader_image;
    String as_uploader_id;

    TextView userNameTextView;
    TextView BranchNameTextView;
    TextView divisionTextView;
    TextView subNameTextView;
    ImageView uploaderImageView;
    CardView selectedCardView;
    TextView commentTextView;
    ImageView likeImageView;
    public UserUploadsViewHolder(View itemView) {
      super(itemView);

      userNameTextView = itemView.findViewById(R.id.as_uploader_name_text_view);
      BranchNameTextView = itemView.findViewById(R.id.as_branch_name_text_view);
      divisionTextView = itemView.findViewById(R.id.as_division_name_text_view);
      subNameTextView = itemView.findViewById(R.id.as_subject_name_text_view);
      uploaderImageView = itemView.findViewById(R.id.as_uploader_image_view);
      selectedCardView = itemView.findViewById(R.id.user_uploads_as_card_view);
      likeImageView = itemView.findViewById(R.id.home_like_image_view);
      commentTextView = itemView.findViewById(R.id.home_comment_text_view);


      commentTextView.setOnClickListener(this);

    }

    public void setAs_uploader_name(String as_uploader_name) {
      this.as_uploader_name = as_uploader_name;
      userNameTextView.setText(as_uploader_name);
    }

    public void setAs_branch_name(String as_branch_name) {
      this.as_branch_name = as_branch_name;
      BranchNameTextView.setText(as_branch_name);
    }

    public void setAs_uploader_image(Context context, String as_uploader_image) {
      this.as_uploader_image = as_uploader_image;
      Picasso.get().load(as_uploader_image).transform(new CircularImage()).into(uploaderImageView);
    }

    public void setAs_subject_name(String as_subject_name) {
      this.as_subject_name = as_subject_name;
      subNameTextView.setText(as_subject_name);
    }

    public void setAs_div_name(String as_div_name) {
      this.as_div_name = as_div_name;
      divisionTextView.setText(as_div_name);
    }

    public void setAs_uploader_id(String as_uploader_id) {
      this.as_uploader_id = as_uploader_id;
    }

    @Override
    public void onClick(View v) {

      int id = v.getId();
      switch (id){
        case R.id.home_comment_text_view:
          String cardViewKey = selectedCardView.getTag().toString();
          Intent toCommentActivity = new Intent(v.getContext(), UsersCommentsActivity.class);
          toCommentActivity.putExtra("card_view_key",cardViewKey);
          v.getContext().startActivity(toCommentActivity);
          break;
        default:
      }

    }
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getActivity().setTitle("Home");


  }


}
