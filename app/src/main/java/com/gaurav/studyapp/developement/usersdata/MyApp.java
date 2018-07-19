package com.gaurav.studyapp.developement.usersdata;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.gaurav.studyapp.developement.usersdata.MyApp;

/**
 * @author Gaurav
 */
public class MyApp extends Application {

  private static Context mContext;


  @Override
  public void onCreate() {
    super.onCreate();
    mContext = this.getApplicationContext();
  }

  public static Context getAppContext(){
    return mContext;
  }

}


