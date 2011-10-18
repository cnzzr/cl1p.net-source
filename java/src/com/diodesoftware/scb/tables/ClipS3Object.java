package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.scb.PictureInfo;
import com.diodesoftware.scb.S3FileHandler;
import com.diodesoftware.scb.agents.S3AccessAgent;
import org.apache.log4j.Logger;
import org.jets3t.service.S3ServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Mar 13, 2008
 * Time: 9:14:09 PM
 */
public class ClipS3Object implements DatabaseEntry {

    private int number;
    private int clipId;
    private int type;
    private String name;
    private String bucket;
    private Calendar created;
    private Calendar lastDownloaded;
    private int downloadCount;
    private String info;

    public static final int TYPE_FILE = 1;
    public static final int TYPE_PICTURE = 2;
    private static Logger log = Logger.getLogger(ClipS3Object.class);

    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("ClipId", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("Type", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("Name", DatabaseColumnType.TEXT),
            new DatabaseColumn("Bucket", DatabaseColumnType.TEXT),
            new DatabaseColumn("Created", DatabaseColumnType.DATEEPOCH),
            new DatabaseColumn("LastDownloaded", DatabaseColumnType.DATEEPOCH),
            new DatabaseColumn("DownloadCount", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("info", DatabaseColumnType.TEXT)
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }


    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public Calendar getLastDownloaded() {
        return lastDownloaded;
    }

    public void setLastDownloaded(Calendar lastDownloaded) {
        this.lastDownloaded = lastDownloaded;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    // When uploaded create an entry
    public static void createFile(Clip clip, String fileName, String bucket, Connection con){
        createS3(clip, fileName, bucket, ClipS3Object.TYPE_FILE, "", con);
    }

     public static void createS3(Clip clip, String fileName, String bucket, int type, String info, Connection con){
         if(clip.getNumber() == 0){
                   DBMapper.save(clip, con);// Get a number if not already here
               }
         createS3(clip.getNumber(), fileName, bucket, type, info, con);
     }

     public static void createS3(int clipId, String fileName, String bucket, int type, String info, Connection con){
        ClipS3Object cs3 = new ClipS3Object();
        cs3.clipId = clipId;
        cs3.created = Calendar.getInstance();
        cs3.downloadCount = 0;
        cs3.lastDownloaded = Calendar.getInstance();
        cs3.bucket = bucket;
        cs3.name = fileName;
        cs3.type = type;
        cs3.info = info;
        DBMapper.save(cs3,con);
    }

    public static void deleteFiles(Clip clip, Connection con)
    {
        String sql = "delete from ClipS3Object where ClipId = " + clip.getNumber();
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            //prepStmt.setInt(1, clip.getNumber());
            prepStmt.executeUpdate(sql);
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error deleting file sql[" + sql + "]", e);
        }
    }



    // List object FILES for a clip
    public static List listFiles(Clip clip, Connection con){
        String sql = "Select * from ClipS3Object where ClipId = ? and Type = " + TYPE_FILE;
        List result = new ArrayList();
        if(clip.getNumber() == 0)return result;
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setInt(1, clip.getNumber());
            ResultSet rs = prepStmt.executeQuery();
            while(rs.next()){
                result.add(DBMapper.loadSingle(ClipS3Object.class, rs));
            }
            rs.close();
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error running SQL [" + sql + "]",e);
        }
        return result;
    }

    // When downloading on clip id and file Id
    public static ClipS3Object retreiveFile(Clip clip, int objectId, Connection con){
        String sql = "Select * from ClipS3Object where ClipId = ? and Number = ? and Type = " + TYPE_FILE;
        ClipS3Object result = null;
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setInt(1, clip.getNumber());
            prepStmt.setInt(2, objectId);
            ResultSet rs = prepStmt.executeQuery();
            while(rs.next()){
                result = (ClipS3Object)DBMapper.loadSingle(ClipS3Object.class, rs);
            }
            rs.close();
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error running SQL [" + sql + "]",e);
        }
        return result;
    }

    public static List listPictureUrls(Clip clip, Connection con){
        List result = new ArrayList();
        if(clip.getNumber() == 0)return result;
        String sql = "Select * from ClipS3Object where ClipId = ? and Type = " + TYPE_PICTURE;
        try{
            S3FileHandler s3fh = new S3FileHandler();
            String location = null;

            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setInt(1, clip.getNumber());
            List objects = new ArrayList();
            ResultSet rs = prepStmt.executeQuery();
            while(rs.next()){
                ClipS3Object cs3o = (ClipS3Object)DBMapper.loadSingle(ClipS3Object.class, rs);
                location = S3AccessAgent.getInstance().open(clip, cs3o, con);
                //location = s3fh.openKey(clip, cs3o.getName());
                String url = "http://s3.amazonaws.com/" + location + "/" + cs3o.getName();
                log.debug("Got pic URL [" + url + "]");
                String info = cs3o.getInfo();
                int width = 200;
                int height = 200;

                StringTokenizer st = new StringTokenizer(info, ",");
                if(st.hasMoreTokens())
                    width = Integer.parseInt(st.nextToken());
                if(st.hasMoreTokens())
                    height = Integer.parseInt(st.nextToken());
                PictureInfo pi = new PictureInfo(cs3o.getNumber(), url, width, height, cs3o.getName());
                result.add(pi);

            }


            rs.close();
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error running SQL [" + sql + "]",e);
        }

        return result;
    }
}
