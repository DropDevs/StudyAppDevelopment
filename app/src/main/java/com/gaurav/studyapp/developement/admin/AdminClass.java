package com.gaurav.studyapp.developement.admin;

import android.content.Context;

import com.gaurav.studyapp.developement.usersdata.UsersDetails;

public class AdminClass  {

  public String getAdminName() {
    return adminName;
  }

  public void setAdminName(String adminName) {
    this.adminName = adminName;
  }

  public String getAdminEmail() {
    return adminEmail;
  }

  public void setAdminEmail(String adminEmail) {
    this.adminEmail = adminEmail;
  }

  public String getAdminId() {
    return adminId;
  }

  public void setAdminId(String adminId) {
    this.adminId = adminId;
  }

  private String adminName;
  private String adminEmail;
  private String adminId;

  public AdminClass(Context context) {
    UsersDetails adminDetails = new UsersDetails(context);
    this.adminName = adminDetails.getPersonName();
    this.adminEmail = adminDetails.getPersonEmail();
    this.adminId = adminDetails.getPersonId();
  }



}
