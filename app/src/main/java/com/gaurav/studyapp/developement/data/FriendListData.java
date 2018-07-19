package com.gaurav.studyapp.developement.data;

public class FriendListData {
  public FriendListData(String student_image, String student_name) {
    this.student_image = student_image;
    this.student_name = student_name;
  }

  String student_image;

  public String getStudent_image() {
    return student_image;
  }

  public void setStudent_image(String student_image) {
    this.student_image = student_image;
  }

  public String getStudent_name() {
    return student_name;
  }

  public void setStudent_name(String student_name) {
    this.student_name = student_name;
  }

  String student_name;

  public FriendListData(String student_id) {
    this.student_id = student_id;
  }

  public String getStudent_id() {
    return student_id;
  }

  public void setStudent_id(String student_id) {
    this.student_id = student_id;
  }

  String student_id;
  public FriendListData() {
  }
}
