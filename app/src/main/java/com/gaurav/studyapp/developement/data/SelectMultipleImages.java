package com.gaurav.studyapp.developement.data;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * @author Gaurav
 */
public class SelectMultipleImages {
  public int getSelectedImages() {
    Log.d("message","Getter called");
    Log.d("message",String.valueOf(selectedImages));
    return this.selectedImages;
  }

  public void setSelectedImages(int selectedImages) {
    this.selectedImages = selectedImages;
    Log.d("message","GETSET"+this.selectedImages);
    Log.d("message","Setter no"+selectedImages);
  }

   private int selectedImages = 0;

  public Intent getData() {
    return data;
  }

  public void setData(Intent data) {
    this.data = data;
  }

  public Uri getImageUri() {
    return imageUri;
  }

  public void setImageUri(Uri imageUri) {
    this.imageUri = imageUri;
  }

  private Uri imageUri = null;
   private Intent data = null;
  public SelectMultipleImages() {
  }
}
