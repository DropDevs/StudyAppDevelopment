package com.gaurav.studyapp.developement.firebasedata;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gaurav.studyapp.R;

import com.gaurav.studyapp.developement.PrivateMessages;
import com.gaurav.studyapp.developement.data.MessagesViewData;

import com.gaurav.studyapp.developement.menu.Home;
import com.gaurav.studyapp.developement.menu.UserMessages;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * @author Gaurav
 */
public class MessagesFirebaseRecyclerClass {

  public MessagesFirebaseRecyclerClass(final Context context, final RecyclerView messagesRecyclerView) {

    UsersDetails usersDetails = new UsersDetails(context);
    final Query messagesQuery = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(usersDetails.getPersonId())
            .child("messages")
            .getRef();
    FirebaseRecyclerOptions<MessagesViewData> options =
            new FirebaseRecyclerOptions.Builder<MessagesViewData>()
            .setQuery(messagesQuery,MessagesViewData.class)
            .build();
    FirebaseRecyclerAdapter<MessagesViewData,UserMessages.MessagesViewHolder> firebaseRecyclerAdapter =
            new FirebaseRecyclerAdapter<MessagesViewData, UserMessages.MessagesViewHolder>(options) {
              @Override
              protected void onBindViewHolder(@NonNull UserMessages.MessagesViewHolder holder, int position, @NonNull MessagesViewData model) {
                holder.setFriend_name(model.getFriend_name());
                holder.setFriend_image(model.getFriend_image());
                final String key = getRef(position).getKey();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    Intent toPrivateMsgActivity = new Intent(v.getContext(),PrivateMessages.class);
                    toPrivateMsgActivity.putExtra("chat_key",key);
                    Log.d("chat_key",key);
                    context.startActivity(toPrivateMsgActivity);
                  }
                });
              }

              @NonNull
              @Override
              public UserMessages.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_view_user_message,parent,false);
                return new UserMessages.MessagesViewHolder(v);
              }
            };
    firebaseRecyclerAdapter.startListening();
    messagesRecyclerView.setAdapter(firebaseRecyclerAdapter);
    firebaseRecyclerAdapter.notifyDataSetChanged();
  }


}
