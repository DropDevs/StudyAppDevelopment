package com.gaurav.studyapp.developement.firebasedata;

import android.content.Context;
import android.util.Log;

/**
 * @author Gaurav
 */
public class CollegeNameGetterSetter {

  private static String collegeName;


  public static String getCollegeName() {
      Log.d("message", "getCollegeNameCalled!Two");
      return collegeName;
  }

  public void setCollegeName(String collegeName) {
    try {
      CollegeNameGetterSetter.collegeName = collegeName;
      Log.d("message", "setCollegeNameCalled!");
    }catch (Exception e){
      Log.d("Error",e.toString());
    }
  }





}
