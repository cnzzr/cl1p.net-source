package com.diodesoftware.scb.upload;

public class UploadStatus {

    private int percent;
    private long size;
    private long uploaded;
    private boolean done;
    private boolean error;
    private long cleanAt;
    private boolean complete;
    private boolean uploadStarted; // Because the update can beat the processing
    private String errMsg = "";
    private String msg;

    public UploadStatus() {

    }

    public void reset() {
        percent = 0;
        size = 0;
        uploaded = 0;
        done = false;
        error = false;
        cleanAt = 0;
        complete = false;
        uploadStarted = false;
        errMsg = "";
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getUploaded() {
        return uploaded;
    }

    public void setUploaded(long uploaded) {
        this.uploaded = uploaded;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        if (done) {
            this.cleanAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
        }
        this.done = done;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }


    public long getCleanAt() {
        return cleanAt;
    }


    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isUploadStarted() {
        return uploadStarted;
    }

    public void setUploadStarted(boolean b) {
        uploadStarted = b;
    }


    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return this.msg;
    }
}
