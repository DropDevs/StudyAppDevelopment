package com.gaurav.studyapp.developement.data;

public class MessagesViewData {
  String friend_id;

  public MessagesViewData(String friend_name) {
    this.friend_name = friend_name;
  }

  public String getFriend_name() {
    return friend_name;
  }

  public void setFriend_name(String friend_name) {
    this.friend_name = friend_name;
  }

  String friend_name;

  public MessagesViewData(String friend_id, String friend_image) {
    this.friend_id = friend_id;
    this.friend_image = friend_image;
  }

  public String getFriend_id() {
    return friend_id;
  }

  public void setFriend_id(String friend_id) {
    this.friend_id = friend_id;
  }

  public String getFriend_image() {
    return friend_image;
  }

  public void setFriend_image(String friend_image) {
    this.friend_image = friend_image;
  }

  String friend_image;
  public MessagesViewData() {
  }
}
