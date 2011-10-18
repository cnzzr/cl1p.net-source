package com.diodesoftware.scb;

import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.scb.tables.ClipS3Object;
import org.apache.log4j.Logger;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Mar 10, 2008
 * Time: 7:55:56 PM
 */
public class S3FileHandler {


    private static Logger log = Logger.getLogger(S3FileHandler.class);

    /* public static void main(String[] args) throws Exception {

       AWSCredentials awsCredentials = new AWSCredentials(awsAccessKeyId, awsSecretAccessKey);

       // To communicate with S3, create a class that implements an S3Service.
       // We will use the REST/HTTP implementation based on HttpClient, as this is the most
       // robust implementation provided with jets3t.

       S3Service s3Service = new RestS3Service(awsCredentials);
       S3Bucket testBucket = s3Service.createBucket("cl1p-file-backup");

       File fileData = new File("build.xml");
       S3Object fileObject = new S3Object(testBucket, fileData);
       //fileObject.setAcl(AccessControlList.REST_CANNED_PRIVATE);
       //fileObject.setOwner(new S3Owner());

       s3Service.putObject(testBucket, fileObject);

       S3Object objectComplete = s3Service.getObject(testBucket, "build.xml");
       System.out.println("S3Object, complete: " + objectComplete);

       // Read the data from the object's DataInputStream using a loop, and print it out.
       System.out.println("Greeting:");
       BufferedReader reader = new BufferedReader(
               new InputStreamReader(objectComplete.getDataInputStream()));
       String data = null;
       while ((data = reader.readLine()) != null) {
           System.out.println(data);
       }
   } */

    public static final String BUCKET_BASE = "cl1p-file-backup/cl1pupload";

    public void uploadObject(Clip clip, File file, int type, String info, Connection con) throws S3ServiceException {
        try {
            String bucketKey = buildBucketKey(clip);
            log.debug("Using Bucket Key [" + bucketKey + "]");
            AWSCredentials awsCredentials = new AWSCredentials(ClipConfig.AWS_ACCESS_KEY_ID, ClipConfig.AWS_SECRET_ACCESS_KEY);
            S3Service s3Service = new RestS3Service(awsCredentials);
            S3Bucket uploadBucket = s3Service.createBucket(bucketKey);
            if(type == ClipS3Object.TYPE_FILE)
            {
            	uploadBucket.addMetadata("content-type", "image/jpg");
            }
            log.debug("Bucket Created");
            S3Object fileObject = new S3Object(uploadBucket, file);
            s3Service.putObject(uploadBucket, fileObject);
            log.debug("Bucket Uploaded");
            ClipS3Object.createS3(clip, file.getName(), bucketKey, type, info, con);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void registerFile(int clipId, String uri, String fileName, Connection con)
    {
        int type = ClipS3Object.TYPE_FILE;
        String info = "";
        String bucketKey = buildBucketKey(uri);
        File f = new File(fileName);
        ClipS3Object.createS3(clipId, f.getName(), bucketKey, type, info, con);

    }


   
    public void deleteFile(Clip clip, String fileName) throws S3ServiceException {
        AWSCredentials awsCredentials = new AWSCredentials(ClipConfig.AWS_ACCESS_KEY_ID, ClipConfig.AWS_SECRET_ACCESS_KEY);
        String bucketKey = buildBucketKey(clip);
        S3Service s3Service = new RestS3Service(awsCredentials);
        S3Bucket uploadBucket = s3Service.createBucket(bucketKey);
        s3Service.deleteObject(uploadBucket, fileName);
        log.debug("Deleteing [" + fileName + "] from [" + uploadBucket.getName() + "]");
    }

    public void deleteBucket(Clip clip) throws S3ServiceException {
        AWSCredentials awsCredentials = new AWSCredentials(ClipConfig.AWS_ACCESS_KEY_ID, ClipConfig.AWS_SECRET_ACCESS_KEY);
        String bucketKey = buildBucketKey(clip);
        S3Service s3Service = new RestS3Service(awsCredentials);
        S3Bucket uploadBucket = s3Service.createBucket(bucketKey);
        s3Service.deleteBucket(uploadBucket);
    }


    public void returnFile(Clip clip, HttpServletResponse response, String fileName) throws S3ServiceException, IOException {
        OutputStream out = response.getOutputStream();
        AWSCredentials awsCredentials = new AWSCredentials(ClipConfig.AWS_ACCESS_KEY_ID, ClipConfig.AWS_SECRET_ACCESS_KEY);
        String bucketKey = buildBucketKey(clip);
        log.debug("returning key " + bucketKey);
        S3Service s3Service = new RestS3Service(awsCredentials);
        S3Bucket uploadBucket = s3Service.createBucket(bucketKey);
        response.setContentType("application/x-download");
        String downloadFileName = fileName;
        if(downloadFileName.startsWith("_"))downloadFileName = downloadFileName.substring(1);
        if(downloadFileName.startsWith("\\"))downloadFileName = downloadFileName.substring(1);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName.trim()+ "\"");
        S3Object objectComplete = s3Service.getObject(uploadBucket, fileName);
        InputStream is = objectComplete.getDataInputStream();

        final int chunkSize = 4096;
        byte[] buf = new byte[chunkSize];

        int count;
        int printWorking = 0;
        while ((count = is.read(buf)) != -1) {
            out.write(buf, 0, count);
            printWorking++;
            if (printWorking > 100) {
                printWorking = 0;
            }
        }
        out.flush();
        out.close();
    }

    public String openKey(Clip clip, String name) throws S3ServiceException
    {
        AWSCredentials awsCredentials = new AWSCredentials(ClipConfig.AWS_ACCESS_KEY_ID, ClipConfig.AWS_SECRET_ACCESS_KEY);
        String bucketKey = buildBucketKey(clip);
        S3Service s3Service = new RestS3Service(awsCredentials);
        S3Bucket uploadBucket = s3Service.createBucket(bucketKey);
        AccessControlList bucketAcl = s3Service.getBucketAcl(uploadBucket);
        bucketAcl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
        uploadBucket.addMetadata("content-type", "image");
        s3Service.putObjectAcl(bucketKey, name, bucketAcl);
        return bucketKey;
    }
    
    
    public void close(String bucketKey, String name) throws S3ServiceException
    {
       
        AWSCredentials awsCredentials = new AWSCredentials(ClipConfig.AWS_ACCESS_KEY_ID, ClipConfig.AWS_SECRET_ACCESS_KEY);
        S3Service s3Service = new RestS3Service(awsCredentials);
        S3Bucket uploadBucket = s3Service.createBucket(bucketKey);
        AccessControlList bucketAcl = s3Service.getBucketAcl(uploadBucket);
        bucketAcl.revokeAllPermissions(GroupGrantee.ALL_USERS);
        s3Service.putObjectAcl(bucketKey, name, bucketAcl); 
    }

   
    public static String stripPath(String path) {
        int lastFwd = path.lastIndexOf('\\');
        if (lastFwd != -1) {
            path = path.substring(lastFwd);
        }
        lastFwd = path.lastIndexOf('/');
        if (lastFwd != -1) {
            path = path.substring(lastFwd);
        }
        return path;
    }

    public static void indexExisting() {
        // TODO: For each exisitng S3 entry create an entry in the DB
    }

    private String buildBucketKey(Clip clip) {
        return buildBucketKey(clip.getUri());
    }

    private String buildBucketKey(String uri){
        String bucketKey = BUCKET_BASE + uri;
        if (bucketKey.endsWith("/"))
            bucketKey = bucketKey.substring(0, bucketKey.length() - 1);
        return bucketKey;
    }

}
