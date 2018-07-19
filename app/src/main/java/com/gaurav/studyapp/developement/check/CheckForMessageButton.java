package com.gaurav.studyapp.developement.check;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.gaurav.studyapp.developement.usersdata.UsersDetails;

public class CheckForMessageButton {

  public CheckForMessageButton(Context context, LinearLayout messageLayout, String userName) {
    UsersDetails usersDetails = new UsersDetails(context);
    String currentUserName = usersDetails.getPersonName();
    if (currentUserName.equals(userName)){
      messageLayout.setVisibility(View.INVISIBLE);
    }else if (!currentUserName.equals(userName)){
      messageLayout.setVisibility(View.VISIBLE);
    }

  }
}
