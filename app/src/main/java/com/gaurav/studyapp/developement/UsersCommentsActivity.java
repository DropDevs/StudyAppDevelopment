package com.gaurav.studyapp.developement;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.data.CommentData;
import com.gaurav.studyapp.developement.firebasedata.CollegeNameGetterSetter;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.gaurav.studyapp.developement.utils.CircularImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * @author Gaurav
 */
public class UsersCommentsActivity extends AppCompatActivity {

  EditText userCommentEditText;
  TextView numberOfLikesTextView;
  ImageView sendCommentImageView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_users_comments);

    final RecyclerView commetsRecyclerView;
    commetsRecyclerView = findViewById(R.id.user_comments_recycler_view);
    userCommentEditText = findViewById(R.id.user_comment_edit_text);
//    numberOfLikesTextView = findViewById(R.id.user_number_of_likes_text_view);
    sendCommentImageView = findViewById(R.id.send_user_comment_image_view);
    commetsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    final String cardViewKey = getIntent().getStringExtra("card_view_key");
    CollegeNameGetterSetter nameGetterSetter = new CollegeNameGetterSetter();
    final String collegeName = CollegeNameGetterSetter.getCollegeName();
    firebaseRecyclerData(cardViewKey, collegeName, commetsRecyclerView);
    sendCommentImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String comment = userCommentEditText.getText().toString().trim();
        if (TextUtils.isEmpty(comment)){
          new AlertDialog.Builder(UsersCommentsActivity.this)
                  .setMessage("Enter a comment")
                  .setNeutralButton("Ok",null)
                  .show();
        }else if (!TextUtils.isEmpty(comment)){
          final UsersDetails usersDetails = new UsersDetails(getApplicationContext());
          final DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference()
                  .child("comments_on_post")
                  .child("assignments")
                  .child(collegeName)
                  .child(cardViewKey)
                  .push()
                  .getRef();
          commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              commentsRef.child("commentor_name").setValue(usersDetails.getPersonName());
              commentsRef.child("commentor_image").setValue(usersDetails.getPersonImage().toString());
              commentsRef.child("comment").setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                  Log.d("message","Comment added");
                  Toast.makeText(UsersCommentsActivity.this, "Commented", Toast.LENGTH_SHORT).show();
                }
              }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Log.d("Error",e.toString());
                }
              });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
          });
          userCommentEditText.getText().clear();
          commetsRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
              try {
                commetsRecyclerView.smoothScrollToPosition(commetsRecyclerView.getAdapter().getItemCount() - 1);
              }catch (Exception e){
                Log.d("Error",e.toString());
              }
            }
          },500);

        }
      }
    });
    commetsRecyclerView.postDelayed(new Runnable() {
      @Override
      public void run() {
        try {
          commetsRecyclerView.smoothScrollToPosition(commetsRecyclerView.getAdapter().getItemCount() - 1);
        }catch (Exception e){
          Log.d("Error",e.toString());
        }
      }
    },1000);

  }

  public void firebaseRecyclerData(String cardViewKey, String collegeName, RecyclerView commentsRecyclerView){
    final Query query = FirebaseDatabase.getInstance().getReference()
    .child("comments_on_post")
    .child("assignments")
    .child(collegeName)
    .child(cardViewKey)
    .getRef();

    FirebaseRecyclerOptions<CommentData> options =
            new FirebaseRecyclerOptions.Builder<CommentData>()
            .setQuery(query,CommentData.class)
            .build();
    FirebaseRecyclerAdapter<CommentData, CommentViewHolder> firebaseRecyclerAdapter =
            new FirebaseRecyclerAdapter<CommentData, CommentViewHolder>(options) {
              @Override
              protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull CommentData model) {
                holder.setComment(model.getComment());
                holder.setCommentor_image(model.getCommentor_image());
                holder.setCommentor_name(model.getCommentor_name());
              }

              @NonNull
              @Override
              public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.card_view_user_comments,parent,false);
                return new CommentViewHolder(view);
              }
            };
    firebaseRecyclerAdapter.startListening();
    commentsRecyclerView.setAdapter(firebaseRecyclerAdapter);
    firebaseRecyclerAdapter.notifyDataSetChanged();

  }

  public static class CommentViewHolder extends RecyclerView.ViewHolder {
    String comment;
    String commentor_name;
    String commentor_image;
    TextView commentTextView;
    TextView commentUserNameTextView;
    ImageView userImageView;
    public CommentViewHolder(View itemView) {
      super(itemView);
      commentTextView = itemView.findViewById(R.id.card_comment_user_comment_text_view);
      commentUserNameTextView = itemView.findViewById(R.id.card_comment_user_name_text_view);
      userImageView = itemView.findViewById(R.id.card_comment_user_image_view);
    }

    public void setComment(String comment) {
      this.comment = comment;
      commentTextView.setText(comment);
    }

    public void setCommentor_name(String commentor_name) {
      this.commentor_name = commentor_name;
      commentUserNameTextView.setText(commentor_name);
    }

    public void setCommentor_image(String commentor_image) {
      this.commentor_image = commentor_image;
      Picasso.get().load(commentor_image).transform(new CircularImage()).into(userImageView);
    }
  }

}
