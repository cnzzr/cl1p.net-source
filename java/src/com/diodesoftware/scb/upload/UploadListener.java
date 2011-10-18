package com.diodesoftware.scb.upload;

import org.apache.commons.fileupload.UploadStatusListener;
import org.apache.log4j.Logger;

public class UploadListener implements UploadStatusListener {
    private UploadStatus status = null;


    public UploadListener(UploadStatus status, long size) {
        this.status = status;
        status.reset();
        status.setSize(size);
    }

    public void done(int i) {
      
      
        if (i == UploadStatusListener.STATUS_DONE) {
            status.setDone(true);
        }
        if (i == UploadStatusListener.STATUS_ERROR) {
            status.setError(true);
            status.setDone(true);
        }
    }

    public void uploaded(int i) {
        //if(log.isDebugEnabled()){
            //log.debug("Upload Called Value [" + i + "]");
        //}
        status.setUploaded(i);
        int percent = (int) ((float) i / (float) status.getSize() * 100f);
        if (i == status.getSize()) percent = 100;
        status.setPercent(percent);

    }
}
