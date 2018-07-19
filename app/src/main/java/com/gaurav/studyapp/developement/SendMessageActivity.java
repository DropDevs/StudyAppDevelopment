package com.gaurav.studyapp.developement;

import android.app.AlertDialog;
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
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * @author Gaurav
 */
public class SendMessageActivity extends AppCompatActivity {

  private EditText sendMessageEditText;
  private Button sendMessageButton, resetMessageButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_send_message);

    sendMessageEditText = findViewById(R.id.send_message_edit_text);
    sendMessageButton = findViewById(R.id.send_message_button);
    resetMessageButton = findViewById(R.id.send_message_reset_button);

    final String messageToKey = getIntent().getStringExtra("message_to_key");
    final String cardViewKey = getIntent().getStringExtra("card_view_key");
    final UsersDetails usersDetails = new UsersDetails(getApplicationContext());
    resetMessageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessageEditText.getText().clear();
        Toast.makeText(SendMessageActivity.this, "Message cleared!", Toast.LENGTH_SHORT).show();
      }
    });

    sendMessageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String messege = sendMessageEditText.getText().toString().trim();
        if (TextUtils.isEmpty(messege)){
          new AlertDialog.Builder(SendMessageActivity.this)
                  .setTitle("Error")
                  .setMessage("Please type a message to send!")
                  .setNeutralButton("Ok",null)
                  .show();
        }else if (!TextUtils.isEmpty(messege)){

//          sendMessageToUploader(usersDetails,messege,messageToKey,v,cardViewKey);
//          saveMessageInSendersData(usersDetails,cardViewKey,messageToKey,messege);
          setValues(usersDetails,messageToKey);
          privateMessages(usersDetails,messageToKey,messege);
        }
      }
    });

  }


  public void setValues(final UsersDetails usersDetails, final String messageToKey){
    Log.d("Track","setValuesCalled");
    final DatabaseReference setValuesRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(messageToKey)
            .getRef();
    setValuesRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (!dataSnapshot.hasChild("messages")){
          Log.d("Track","if called");
          final DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference()
                  .child("user_info")
                  .child(messageToKey)
                  .child("messages")
                  .push();
          msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              try {
                setForFirstTime(usersDetails,msgRef);
              }catch (Exception e){
                e.printStackTrace();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
          });
        }else if (dataSnapshot.hasChild("messages")){
          Log.d("Track","Else called");
          ifHasChildMessages(usersDetails,messageToKey);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public void setForFirstTime(UsersDetails usersDetails, DatabaseReference msgRef){
    msgRef.child("friend_id").setValue(usersDetails.getPersonId());
    msgRef.child("friend_name").setValue(usersDetails.getPersonName());
    msgRef.child("friend_image").setValue(usersDetails.getPersonImage().toString());
    exit();
    Log.d("Track","Exit called");
  }

  public void ifHasChildMessages(final UsersDetails usersDetails, final String messageToKey){
    Log.d("Track","hasChildMessages");
    final DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(messageToKey)
            .child("messages")
            .getRef();
    messageRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
          String friendsId = (String) snapshot.child("friend_id").getValue();
          if (friendsId.equals(usersDetails.getPersonId())){
            updateValues(messageToKey,snapshot.getKey(),usersDetails);
          }else if (!friendsId.equals(usersDetails.getPersonId())){
            final DatabaseReference setValueRef = messageRef.push();
            setValueRef.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                  setValueRef.child("friend_id").setValue(usersDetails.getPersonId());
                  setValueRef.child("friend_name").setValue(usersDetails.getPersonName());
                  setValueRef.child("friend_image").setValue(usersDetails.getPersonImage().toString());
                }catch (Exception e){
                  e.printStackTrace();
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
            });
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }


  public void exit(){
    Log.d("Track","Exit");
  }

  public void updateValues(String messageToKey, String key, final UsersDetails usersDetails){
    final DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(messageToKey)
            .child("messages")
            .child(key)
            .getRef();
    updateRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        try {
          updateRef.child("friend_id").setValue(usersDetails.getPersonId());
          updateRef.child("friend_image").setValue(usersDetails.getPersonImage().toString());
        }catch (Exception e){
          e.printStackTrace();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public void privateMessages(final UsersDetails usersDetails, String messageToKey, final String message){
    final DatabaseReference privateRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(messageToKey)
            .child("private_messages")
            .child(usersDetails.getPersonId())
            .push()
            .getRef();
    privateRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        try {
          privateRef.child("message").setValue(message);
          privateRef.child("sent_by_id").setValue(usersDetails.getPersonId());
          Toast.makeText(SendMessageActivity.this, "Message send!", Toast.LENGTH_SHORT).show();
          sendMessageEditText.getText().clear();
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
  protected void onResume() {
    super.onResume();
    sendMessageEditText.getText().clear();
  }
}
