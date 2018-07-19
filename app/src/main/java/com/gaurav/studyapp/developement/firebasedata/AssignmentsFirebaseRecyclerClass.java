package com.gaurav.studyapp.developement.firebasedata;

import android.app.DownloadManager;
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
import com.gaurav.studyapp.developement.AssignmentMoreDetailsActivity;
import com.gaurav.studyapp.developement.data.AssignmentData;
import com.gaurav.studyapp.developement.menu.Home;
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
public class AssignmentsFirebaseRecyclerClass {


  public AssignmentsFirebaseRecyclerClass(final Context context, final RecyclerView userUploadsRecyclerView, final String nameOfCollege) {
    Log.d("message","In FirebaseRecyclerViewAdapter");
    Query assignmentQuery = FirebaseDatabase.getInstance().getReference()
            .child("user_uploads_without_id")
            .child("assignments")
            .child(nameOfCollege)
            .getRef();
    FirebaseRecyclerOptions<AssignmentData> options =
            new FirebaseRecyclerOptions.Builder<AssignmentData>()
            .setQuery(assignmentQuery,AssignmentData.class)
            .build();
    FirebaseRecyclerAdapter<AssignmentData,Home.UserUploadsViewHolder> firebaseRecyclerAdapter =
            new FirebaseRecyclerAdapter<AssignmentData, Home.UserUploadsViewHolder>(options) {
              @Override
              protected void onBindViewHolder(@NonNull Home.UserUploadsViewHolder holder, int position, @NonNull AssignmentData model) {
                holder.setAs_uploader_name(model.getAs_uploader_name());
                holder.setAs_uploader_image(context,model.getAs_uploader_image());
                holder.setAs_branch_name(model.getAs_branch_name());
                holder.setAs_subject_name(model.getAs_subject_name());
                holder.setAs_div_name(model.getAs_div_name());
                final String selectedCardViewKey = getRef(position).getKey();
                holder.itemView.setTag(selectedCardViewKey);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    Intent toAsMoreDetails = new Intent(context,AssignmentMoreDetailsActivity.class);
                    toAsMoreDetails.putExtra("as_selected_card_view_key",selectedCardViewKey);
                    context.startActivity(toAsMoreDetails);
                  }
                });

              }

              @NonNull
              @Override
              public Home.UserUploadsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_view_user_uploads, parent, false);
                return new Home.UserUploadsViewHolder(view);
              }
            };
    firebaseRecyclerAdapter.startListening();
    userUploadsRecyclerView.setAdapter(firebaseRecyclerAdapter);
    firebaseRecyclerAdapter.notifyDataSetChanged();

  }



}
