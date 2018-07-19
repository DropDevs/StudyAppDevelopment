package com.gaurav.studyapp.developement.data;

public class AssignmentData {

  public AssignmentData(String as_branch_name, String as_div_name, String as_other_info, String as_subject_name, String as_uploader_name, String as_uploader_image) {
    this.as_branch_name = as_branch_name;
    this.as_div_name = as_div_name;
    this.as_other_info = as_other_info;
    this.as_subject_name = as_subject_name;
    this.as_uploader_name = as_uploader_name;
    this.as_uploader_image = as_uploader_image;
  }

  public String getAs_branch_name() {
    return as_branch_name;
  }

  public void setAs_branch_name(String as_branch_name) {
    this.as_branch_name = as_branch_name;
  }

  public String getAs_div_name() {
    return as_div_name;
  }

  public void setAs_div_name(String as_div_name) {
    this.as_div_name = as_div_name;
  }

  public String getAs_other_info() {
    return as_other_info;
  }

  public void setAs_other_info(String as_other_info) {
    this.as_other_info = as_other_info;
  }

  public String getAs_subject_name() {
    return as_subject_name;
  }

  public void setAs_subject_name(String as_subject_name) {
    this.as_subject_name = as_subject_name;
  }

  public String getAs_uploader_name() {
    return as_uploader_name;
  }

  public void setAs_uploader_name(String as_uploader_name) {
    this.as_uploader_name = as_uploader_name;
  }

  public String getAs_uploader_image() {
    return as_uploader_image;
  }

  public void setAs_uploader_image(String as_uploader_image) {
    this.as_uploader_image = as_uploader_image;
  }

  private String as_branch_name;
  private String as_div_name;
  private String as_other_info;
  private String as_subject_name;
  private String as_uploader_name;
  private String as_uploader_image;

  public AssignmentData(String as_uploader_id) {
    this.as_uploader_id = as_uploader_id;
  }

  public String getAs_uploader_id() {
    return as_uploader_id;
  }

  public void setAs_uploader_id(String as_uploader_id) {
    this.as_uploader_id = as_uploader_id;
  }

  private String as_uploader_id;

  public AssignmentData() {
  }
}
