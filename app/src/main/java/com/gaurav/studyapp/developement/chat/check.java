package com.gaurav.studyapp.developement.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * @author Gaurav
 */
public class check {
  public check(final Context context, final UsersDetails usersDetails, String friendsId, final ViewGroup parent) {
    final DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(usersDetails.getPersonId())
            .child("private_messages")
            .child(friendsId)
            .orderByKey()
            .limitToLast(1)
            .getRef();
    checkRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String sender_id = (String) dataSnapshot.child("send_by_id").getValue();
        if (usersDetails.getPersonId().equals(sender_id)){
          View viewOne;
          viewOne = LayoutInflater.from(context)
                  .inflate(R.layout.card_view_author_message,parent,false);
          SetView view1 = new SetView();
          view1.setView(viewOne);
        }else {
          View viewTwo;
          viewTwo = LayoutInflater.from(context)
                  .inflate(R.layout.card_view_private_message, parent, false);
          SetView view1 = new SetView();
          view1.setView(viewTwo);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }
}
