package com.gaurav.studyapp.developement;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.chat.SetView;
import com.gaurav.studyapp.developement.chat.check;
import com.gaurav.studyapp.developement.data.PrivateMessagesData;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * @author Gaurav
 */
public class PrivateMessages extends AppCompatActivity {

  private RecyclerView privateMessagesRecyclerView;
  private ImageView sendMessageImageView;
  private EditText typeMessageEditText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Private Chats");
    setContentView(R.layout.activity_private_messages);

    privateMessagesRecyclerView = findViewById(R.id.private_messages_recycler_view);
    typeMessageEditText = (EditText)findViewById(R.id.user_replay_message_edit_text);
    sendMessageImageView = findViewById(R.id.send_replay_message_image_view);
    privateMessagesRecyclerView.setHasFixedSize(true);
    privateMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    final String chat_key = getIntent().getStringExtra("chat_key");
    final UsersDetails usersDetails = new UsersDetails(getApplicationContext());
    final DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(usersDetails.getPersonId())
            .child("messages")
            .child(chat_key)
            .getRef();
    friendRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String friendId = (String) dataSnapshot.child("friend_id").getValue();
        firebaseRecyclerData(usersDetails,friendId);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    sendMessageImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String message = typeMessageEditText.getText().toString().trim();
        if (TextUtils.isEmpty(message)){
          new AlertDialog.Builder(PrivateMessages.this)
                  .setMessage("Type a message to send")
                  .setNeutralButton("Ok",null)
                  .show();
        }else if (!TextUtils.isEmpty(message)){
          sendMessage(usersDetails,message,chat_key);
        }
      }
    });

  }

  public void sendMessage(final UsersDetails usersDetails, final String message, String chat_key){
    final DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(usersDetails.getPersonId())
            .child("messages")
            .child(chat_key)
            .getRef();
    friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String friend_id = (String) dataSnapshot.child("friend_id").getValue();
        final DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference()
                .child("user_info")
                .child(usersDetails.getPersonId())
                .child("private_messages")
                .child(friend_id)
                .push()
                .getRef();
        messageRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try {
              messageRef.child("message").setValue(message);
              messageRef.child("send_by_id").setValue(usersDetails.getPersonId());
              Log.d("Track",message);
              Log.d("Track","Message send!");
            }catch (Exception e){
              e.printStackTrace();
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });


  }

  public void firebaseRecyclerData(final UsersDetails usersDetails, final String friendsId){
    Query query = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(usersDetails.getPersonId())
            .child("private_messages")
            .child(friendsId)
            .getRef();
    FirebaseRecyclerOptions<PrivateMessagesData> options =
            new FirebaseRecyclerOptions.Builder<PrivateMessagesData>()
            .setQuery(query,PrivateMessagesData.class)
            .build();
    FirebaseRecyclerAdapter<PrivateMessagesData,PrivateMessagesViewHolder> firebaseRecyclerAdapter =
            new FirebaseRecyclerAdapter<PrivateMessagesData, PrivateMessagesViewHolder>(options) {
              @Override
              protected void onBindViewHolder(@NonNull PrivateMessagesViewHolder holder, int position, @NonNull PrivateMessagesData model) {
               holder.setMessage(model.getMessage());
               int gravity = Gravity.LEFT;

              }


              @NonNull
              @Override
              public PrivateMessagesViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

                check check = new check(getApplicationContext(), usersDetails,friendsId,parent);


                return new PrivateMessagesViewHolder(null);
              }



            };

    firebaseRecyclerAdapter.startListening();
    privateMessagesRecyclerView.setAdapter(firebaseRecyclerAdapter);
    firebaseRecyclerAdapter.notifyDataSetChanged();

  }

  public static class PrivateMessagesViewHolder extends RecyclerView.ViewHolder {

    String message;
    TextView friendsMessageTextView;
    public PrivateMessagesViewHolder(View itemView) {
      super(itemView);

      friendsMessageTextView = itemView.findViewById(R.id.friends_message_text_view);


    }


    public void setMessage(String message) {
      this.message = message;
      friendsMessageTextView.setText(message);
    }
  }

}
