package com.gaurav.studyapp.developement.menu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.firebasedata.FriendsListFRA;
import com.gaurav.studyapp.developement.utils.CircularImage;
import com.squareup.picasso.Picasso;

/**
 * @author Gaurav
 */
public class UsersFriends extends Fragment{


  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_users_friend_list, container, false);
    RecyclerView friendsListRecyclerView;
    friendsListRecyclerView = view.findViewById(R.id.users_friends_list_recycler_view);
    friendsListRecyclerView.setHasFixedSize(true);
    friendsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    new FriendsListFRA(getContext(),friendsListRecyclerView);

    return view;

  }

  public static class FriendListViewHolder extends RecyclerView.ViewHolder {
    String student_name;
    String student_image;
    String student_id;
    ImageView friendsProfileImageView;
    TextView friendsNameTextView;
    public FriendListViewHolder(View itemView) {
      super(itemView);

      friendsNameTextView = itemView.findViewById(R.id.friends_list_friend_name_text_view);
      friendsProfileImageView = itemView.findViewById(R.id.users_friend_list_image_view);

    }

    public void setStudent_name(String student_name) {
      this.student_name = student_name;
      friendsNameTextView.setText(student_name);
    }

    public void setStudent_image(String student_image) {
      this.student_image = student_image;
      Picasso.get().load(student_image).transform(new CircularImage()).into(friendsProfileImageView);
    }

    public void setStudent_id(String student_id) {
      this.student_id = student_id;
    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getActivity().setTitle("Your Friends");
  }

}
