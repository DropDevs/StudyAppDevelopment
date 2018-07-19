package com.gaurav.studyapp.developement.autocomplete;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.gaurav.studyapp.developement.alertmessage.AlertMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AutoCompleteData {
  public AutoCompleteData() {
  }

  public void collegeCategoryAutoComplete(Context context, AutoCompleteTextView categroyAutoTextView){
    final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1);
    final DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference()
            .child("college_category");
    categoryRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot category: dataSnapshot.getChildren()){
          String categoryList = (String) category.child("category_name").getValue();
          categoryAdapter.add(categoryList);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
    categroyAutoTextView.setAdapter(categoryAdapter);
  }


  public void collegeNameAutoComplete(Context context, Activity activity, String category, AutoCompleteTextView collegeNameAutoTextView){
    final ArrayAdapter<String> collegeNameAdapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1);
    if (TextUtils.isEmpty(category)){
      AlertMessage alertMessage = new AlertMessage(activity);
      alertMessage.setAlertMessage(activity,"Select a category first!","Ok");
    }else if (!TextUtils.isEmpty(category)){
      final DatabaseReference collegeNameRef = FirebaseDatabase.getInstance().getReference()
              .child("college_name_list")
              .child(category)
              .getRef();
      collegeNameRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          for (DataSnapshot snapshot: dataSnapshot.getChildren()){
            String collegeNameList =  (String) snapshot.child("college_name").getValue();
            collegeNameAdapter.add(collegeNameList);
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });collegeNameAutoTextView.setAdapter(collegeNameAdapter);
    }
  }

}
