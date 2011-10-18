package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.ClipRequest;

import java.util.Calendar;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Apr 8, 2008
 * Time: 7:37:16 AM
 */
public class ClipS3Useage implements DatabaseEntry {

    private int number;
    private int clipId;
    private int userId;
    private int clipOwnerId;
    private int megaBytes;
    private Calendar transDate;
    private boolean upload;
    private int s3ObjectId;


    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("ClipId", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("UserId", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("ClipOwnerId", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("MegaBytes", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("TransDate", DatabaseColumnType.DATEEPOCH),
            new DatabaseColumn("Upload", DatabaseColumnType.BOOLEAN),
            new DatabaseColumn("S3ObjectId", DatabaseColumnType.DECIMAL)
    };

    public DatabaseColumn[] columns(){
        return columns;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getClipId() {
        return clipId;
    }

    public void setClipId(int clipId) {
        this.clipId = clipId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClipOwnerId() {
        return clipOwnerId;
    }

    public void setClipOwnerId(int clipOwnerId) {
        this.clipOwnerId = clipOwnerId;
    }

    public int getMegaBytes() {
        return megaBytes;
    }

    public void setMegaBytes(int megaBytes) {
        this.megaBytes = megaBytes;
    }

    public Calendar getTransDate() {
        return transDate;
    }

    public void setTransDate(Calendar transDate) {
        this.transDate = transDate;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public int getS3ObjectId() {
        return s3ObjectId;
    }

    public void setS3ObjectId(int s3ObjectId) {
        this.s3ObjectId = s3ObjectId;
    }

    public boolean readyForUpload(ClipRequest clipRequest, int clipS3Object, int megaBytes){
        int ownerId = clipRequest.getClip().getOwnerId();
        Owner owner = (Owner)DBMapper.load(Owner.class, ownerId, clipRequest.getCon());
        int clipOwnerId = -1;
        if(owner != null){
            clipOwnerId = owner.getUserId();
        }
        User user = clipRequest.getUser();
        int userId = -1;
        if(user != null){
            userId = user.getNumber();
        }
        int clipId = clipRequest.getClip().getNumber();
        //TODO Finish
        return false;
    }

    //TODO Track download
}
