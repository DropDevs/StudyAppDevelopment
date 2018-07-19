package com.gaurav.studyapp.developement.saveddata;

import android.content.Context;
import android.content.SharedPreferences;

public class DeleteSavedData {
  public DeleteSavedData() {

  }

  public void deleteCategoryAndCollegeName(Context context){
    SharedPreferences catergoryPref = context.getSharedPreferences("CategoryPref",Context.MODE_PRIVATE);
    SharedPreferences.Editor categoryEditor = catergoryPref.edit();
    categoryEditor.clear();
    categoryEditor.apply();
    SharedPreferences Pref = context.getSharedPreferences("CollegeNamePref",Context.MODE_PRIVATE);
    SharedPreferences.Editor collegeNameEditor = Pref.edit();
    collegeNameEditor.clear();
    collegeNameEditor.apply();
  }

  public void deletePdfUriData(Context context){
    SharedPreferences Pref = context.getSharedPreferences("PdfPref",Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = Pref.edit();
    editor.clear();
    editor.apply();
  }


}
