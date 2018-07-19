package com.gaurav.studyapp.developement.imagecompression;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import java.io.ByteArrayOutputStream;

/**
 * @author Gaurav
 */
public class CompressImage {
  public Bitmap CompressBitmapImage(Bitmap bitmap) {
    Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    Canvas canvas = new Canvas(newBitmap);
    canvas.drawColor(Color.WHITE);
    canvas.drawBitmap(bitmap, 0, 0, null);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
   newBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
   return newBitmap;
  }
}
