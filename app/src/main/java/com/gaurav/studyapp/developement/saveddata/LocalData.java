package com.gaurav.studyapp.developement.saveddata;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;

public class LocalData {
  public LocalData() {

  }

  public void saveSelectedCategory(Context context, String selectedCategory){
    SharedPreferences catergoryPref = context.getSharedPreferences("CategoryPref",Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = catergoryPref.edit();
    editor.putString("temp_save_category",selectedCategory);
    editor.apply();
  }

  public String getSelectedCategory(Context context){
    SharedPreferences catergoryPref = context.getSharedPreferences("CategoryPref",Context.MODE_PRIVATE);
    return catergoryPref.getString("temp_save_category",null);
  }


  public void saveSelectedCollege(Context context, String collegeNameselected){
    SharedPreferences Pref = context.getSharedPreferences("CollegeNamePref",Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = Pref.edit();
    editor.putString("save_selected_college_name",collegeNameselected);
    editor.apply();
  }

  public String getSelectedCollege(Context context){
    SharedPreferences Pref = context.getSharedPreferences("CollegeNamePref",Context.MODE_PRIVATE);
    return Pref.getString("save_selected_college_name",null);
  }

  public void savePDFUri(Context context, Uri pdfUri){
    String Uri = pdfUri.toString();
    SharedPreferences Pref = context.getSharedPreferences("PdfPref",Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = Pref.edit();
    editor.putString("pdf_uri_string",Uri);
    editor.apply();
    Log.d("MESSAGE","In save pdf method");
  }

  public String getPdfUri (Context context){
    SharedPreferences Pref = context.getSharedPreferences("PdfPref",Context.MODE_PRIVATE);
    Log.d("MESSAGE","Pdf uri returned!");
    return Pref.getString("pdf_uri_string",null);
  }



}
