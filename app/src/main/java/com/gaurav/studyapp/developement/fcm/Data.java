package com.gaurav.studyapp.developement.fcm;

public class Data {


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String title;
    private String message;

    public Data(String title, String message, String image) {
        this.title = title;
        this.message = message;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Data{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    private String image;

    public Data(){

    }





}
