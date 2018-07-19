package com.gaurav.studyapp.developement.firebasedata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.data.FriendListData;
import com.gaurav.studyapp.developement.menu.UsersFriends;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static android.support.v7.widget.RecyclerView.*;

public class FriendsListFRA {

  public FriendsListFRA(final Context context, final RecyclerView friendsListRecyclerView) {

    CollegeNameGetterSetter getterSetter = new CollegeNameGetterSetter();
    String collegeName = CollegeNameGetterSetter.getCollegeName();

    final UsersDetails usersDetails = new UsersDetails(context);

    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
            .child("college_name_with_student_info")
            .child(collegeName)
            .getRef();

    final Query query = FirebaseDatabase.getInstance().getReference()
            .child("college_name_with_student_info")
            .child(collegeName)
            .getRef();

    FirebaseRecyclerOptions<FriendListData> options =
            new FirebaseRecyclerOptions.Builder<FriendListData>()
            .setQuery(query,FriendListData.class)
            .build();
    FirebaseRecyclerAdapter<FriendListData,UsersFriends.FriendListViewHolder> firebaseRecyclerAdapter =
            new FirebaseRecyclerAdapter<FriendListData, UsersFriends.FriendListViewHolder>(options) {
              @Override
              protected void onBindViewHolder(@NonNull UsersFriends.FriendListViewHolder holder, int position, @NonNull FriendListData model) {

                String friendsId = model.getStudent_id();
                if (friendsId.equalsIgnoreCase(usersDetails.getPersonId())){
                  LinearLayout.LayoutParams params = new LinearLayout.
                          LayoutParams(0,0);
                  holder.itemView.setVisibility(GONE);
                  holder.setIsRecyclable(false);
                  holder.itemView.setLayoutParams(params);
                }else if (!friendsId.equalsIgnoreCase(usersDetails.getPersonId())){
                  holder.setStudent_image(model.getStudent_image());
                  holder.setStudent_name(model.getStudent_name());
                }
              }

              @NonNull
              @Override
              public UsersFriends.FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.card_view_users_friends,parent,false);
                return new UsersFriends.FriendListViewHolder(view);
              }
            };
    firebaseRecyclerAdapter.startListening();
    friendsListRecyclerView.setAdapter(firebaseRecyclerAdapter);
    firebaseRecyclerAdapter.notifyDataSetChanged();

  }

}
