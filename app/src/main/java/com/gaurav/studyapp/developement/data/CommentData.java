package com.gaurav.studyapp.developement.data;

public class CommentData {
  public CommentData(String comment) {
    this.comment = comment;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  String comment;
  String commentor_name;

  public String getCommentor_name() {
    return commentor_name;
  }

  public void setCommentor_name(String commentor_name) {
    this.commentor_name = commentor_name;
  }

  public String getCommentor_image() {
    return commentor_image;
  }

  public void setCommentor_image(String commentor_image) {
    this.commentor_image = commentor_image;
  }

  public CommentData(String commentor_name, String commentor_image) {
    this.commentor_name = commentor_name;
    this.commentor_image = commentor_image;
  }

  String commentor_image;
  public CommentData() {
  }
}
