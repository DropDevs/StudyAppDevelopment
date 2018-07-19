package com.gaurav.studyapp.developement.download;

import android.util.Log;

/**
 * @author Gaurav
 */
public class ImagesDownload {

  public String getUri_name() {
    return uri_name;
  }

  public void setUri_name(String uri_name) {
    Log.d("message","In set uri name");
    Log.d("message","set_uri_name: "+uri_name);
    this.uri_name = uri_name;
  }

  private String uri_name;

  public ImagesDownload(){

  }

}
