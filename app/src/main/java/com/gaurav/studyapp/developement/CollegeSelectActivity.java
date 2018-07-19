package com.gaurav.studyapp.developement;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.alertmessage.AlertMessage;
import com.gaurav.studyapp.developement.autocomplete.AutoCompleteData;
import com.gaurav.studyapp.developement.firebasedata.CollegeNameWithStudentInfo;
import com.gaurav.studyapp.developement.firebasedata.GetUserCollegeName;
import com.gaurav.studyapp.developement.firebasedata.SendDataToFirebase;
import com.gaurav.studyapp.developement.saveddata.DeleteSavedData;
import com.gaurav.studyapp.developement.saveddata.LocalData;
import com.gaurav.studyapp.developement.system.SystemFunctions;

/**
 * @author Gaurav
 */
public class CollegeSelectActivity extends AppCompatActivity {

  private AutoCompleteTextView categoryListAutoTextView, collegeListAutoTextView;
  private Button proceedButton;
  @SuppressLint("ClickableViewAccessibility")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_college_select);

    categoryListAutoTextView = findViewById(R.id.select_college_category_auto_text_view);
    collegeListAutoTextView = findViewById(R.id.select_college_auto_text_view);
    proceedButton = findViewById(R.id.proceed_button);


    AutoCompleteData categoryData = new AutoCompleteData();
    final LocalData localData = new LocalData();
    final AlertMessage alertMessage = new AlertMessage(getApplicationContext());
    final SystemFunctions system = new SystemFunctions();

    categoryData.collegeCategoryAutoComplete(getApplicationContext(),categoryListAutoTextView);

    categoryListAutoTextView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        categoryListAutoTextView.showDropDown();
        categoryListAutoTextView.requestFocus();
        return false;
      }
    });

    collegeListAutoTextView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        collegeListAutoTextView.showDropDown();
        collegeListAutoTextView.requestFocus();
        return false;
      }
    });

    categoryListAutoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String category = categoryListAutoTextView.getText().toString().trim();
        Toast.makeText(CollegeSelectActivity.this, category+" selected", Toast.LENGTH_SHORT).show();
          localData.saveSelectedCategory(getApplicationContext(), category);
          AutoCompleteData collegeList = new AutoCompleteData();
          collegeList.collegeNameAutoComplete(getApplicationContext(),CollegeSelectActivity.this,category,collegeListAutoTextView);
          system.hideSoftKeyboard(getApplicationContext(),CollegeSelectActivity.this);
      }
    });

    collegeListAutoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String collegeName = collegeListAutoTextView.getText().toString().trim();
        String category = localData.getSelectedCategory(getApplicationContext());
        if (TextUtils.isEmpty(category)){
          alertMessage.setAlertMessage(CollegeSelectActivity.this,"Select a category!","OK");
        }else if (!TextUtils.isEmpty(category)) {
          localData.saveSelectedCollege(getApplicationContext(),collegeName);
          Toast.makeText(CollegeSelectActivity.this, collegeName+" selected", Toast.LENGTH_SHORT).show();
          system.hideSoftKeyboard(getApplicationContext(),CollegeSelectActivity.this);
        }
      }
    });

    proceedButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String savedCategory = localData.getSelectedCategory(getApplicationContext());
        final String savedCollege = localData.getSelectedCollege(getApplicationContext());
        if (TextUtils.isEmpty(savedCategory) && TextUtils.isEmpty(savedCollege)){
                alertMessage.setAlertMessage(CollegeSelectActivity.this,"Enter all fields to proceed!","Ok");
        }else if (!TextUtils.isEmpty(savedCategory) && !TextUtils.isEmpty(savedCollege)){
          new AlertDialog.Builder(CollegeSelectActivity.this)
                  .setMessage(savedCollege)
                  .setPositiveButton("Sure!", new DialogInterface.OnClickListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      try {
                        SendDataToFirebase collegeData = new SendDataToFirebase();
                        collegeData.sendUserCollegeToFirebase(getApplicationContext(), CollegeSelectActivity.this, savedCollege, savedCategory);
                      }catch (Exception e){
                        Log.d("Error",e.toString());
                      }
                      try {
                        GetUserCollegeName collegeName = new GetUserCollegeName();
                        collegeName.getCollegeName(getApplicationContext());
                      }catch (Exception e){
                        Log.d("Error",e.toString());
                      }
                      try{
                        CollegeNameWithStudentInfo studentInfo = new CollegeNameWithStudentInfo(getApplicationContext(),savedCollege);
                      }catch (Exception e){
                        Log.d("Error",e.toString());
                      }
                      Intent toHomeActivity = new Intent(getApplicationContext(),HomeActivity.class);
                      toHomeActivity.putExtra("category",savedCategory);
                      toHomeActivity.putExtra("college",savedCollege);
                      startActivity(toHomeActivity);
                    }
                  })
                  .setNegativeButton("Not Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      collegeListAutoTextView.getText().clear();
                      Toast.makeText(CollegeSelectActivity.this, "Select your college", Toast.LENGTH_SHORT).show();
                    }
                  })
                  .show();

        }
      }
    });

  }

  @Override
  protected void onDestroy() {
    DeleteSavedData deleteSavedData = new DeleteSavedData();
    deleteSavedData.deleteCategoryAndCollegeName(getApplicationContext());
    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    Intent a = new Intent(Intent.ACTION_MAIN);
    a.addCategory(Intent.CATEGORY_HOME);
    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(a);
    super.onBackPressed();
  }

}
