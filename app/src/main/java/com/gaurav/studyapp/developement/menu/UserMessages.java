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
import com.gaurav.studyapp.developement.firebasedata.MessagesFirebaseRecyclerClass;
import com.gaurav.studyapp.developement.utils.CircularImage;
import com.squareup.picasso.Picasso;

/**
 * @author Gaurav
 */
public class UserMessages extends Fragment{

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_user_messages, container, false);
    final RecyclerView messgesRecyclerView;
    messgesRecyclerView = view.findViewById(R.id.messages_recycler_view);
    messgesRecyclerView.setHasFixedSize(true);
    messgesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    MessagesFirebaseRecyclerClass firebaseRecyclerClass = new MessagesFirebaseRecyclerClass(getContext(),messgesRecyclerView);
    return view;

  }

  public static class MessagesViewHolder extends RecyclerView.ViewHolder {
    String friend_id;
    String friend_image;
    String friend_name;
    ImageView senderImageView;
    TextView senderName;
    public MessagesViewHolder(View itemView) {
      super(itemView);

      senderImageView = itemView.findViewById(R.id.message_user_image_view);
      senderName = itemView.findViewById(R.id.message_user_name_text_view);

    }

    public void setFriend_name(String friend_name) {
      this.friend_name = friend_name;
      senderName.setText(friend_name);
    }

    public void setFriend_image(String friend_image) {
      this.friend_image = friend_image;
      Picasso.get().load(friend_image).transform(new CircularImage()).into(senderImageView);
    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getActivity().setTitle("Messages");
  }

}
