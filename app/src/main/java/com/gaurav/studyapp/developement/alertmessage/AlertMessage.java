package com.gaurav.studyapp.developement.alertmessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

/**
 * @author Gaurav
 */
public class AlertMessage {

  public AlertMessage(Context context) {

  }

  public void setAlertMessage(Activity activity, String message, String neutralmessage){
    new AlertDialog.Builder(activity)
            .setMessage(message)
            .setNeutralButton(neutralmessage,null)
            .show();
  }
}
