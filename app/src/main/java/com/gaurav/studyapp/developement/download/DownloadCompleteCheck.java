package com.gaurav.studyapp.developement.download;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadCompleteCheck {

  public DownloadCompleteCheck() {
  }
  public boolean downloadComplete(Context context, long downloadId){
    DownloadManager dMgr = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
    Cursor c= dMgr.query(new DownloadManager.Query().setFilterById(downloadId));

    if(c.moveToFirst()){
      int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

      if(status == DownloadManager.STATUS_SUCCESSFUL){
        return true;
      }else{
        int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
        Log.d("message","Download not correct, status [" + status + "] reason [" + reason + "]");
        return false;
      }
    }
    return false;
  }
}
