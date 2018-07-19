package com.gaurav.studyapp.developement.system;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SystemFunctions {
  public SystemFunctions() {
  }

  public void hideSoftKeyboard(Context context, Activity activity) {
    if(activity.getCurrentFocus()!=null) {
      InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
  }
}
