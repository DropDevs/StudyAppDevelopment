package com.gaurav.studyapp.developement.firebasedata;

import android.net.Uri;
import android.util.Log;

public class ImagesKeyRef {

  public Uri getSingleImageUri() {
    return singleImageUri;
  }

  public void setSingleImageUri(Uri singleImageUri) {
    this.singleImageUri = singleImageUri;
  }

  private Uri singleImageUri;

  public String getRefKey() {
    Log.d("message","keyRef"+refKey);
    Log.d("message","GetKeyRefCalled");
    return refKey;

  }

  public void setRefKey(String refKey) {
    this.refKey = refKey;
    Log.d("message","keyRef"+refKey);
    Log.d("message","KeyRef"+this.refKey);
    Log.d("message","setKeyRefCalled");
  }

//  public ImagesKeyRef(String refKey) {
//    this.refKey = refKey;
//  }

  private String refKey;

  public Uri getImageUri() {
    return imageUri;
  }

  public void setImageUri(Uri imageUri) {
    this.imageUri = imageUri;
  }

  private Uri imageUri;
  }
