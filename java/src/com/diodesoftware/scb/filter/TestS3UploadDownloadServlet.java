package com.diodesoftware.scb.filter;

import com.amazon.s3.AWSAuthConnection;

import com.diodesoftware.scb.ClipConfig;
import com.diodesoftware.scb.ClipRequest;
import com.diodesoftware.scb.ClipSystemSetup;
import com.diodesoftware.scb.GLOBAL;
import com.diodesoftware.scb.clipboard.Cache;
import com.diodesoftware.scb.upload.UploadStatus;
import com.diodesoftware.scb.upload.UploadStatusMgr;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.S3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Mar 10, 2008
 * Time: 8:03:06 PM
 */
public class TestS3UploadDownloadServlet extends HttpServlet {
    private static final String TEST_URI = "/rgdm/test/s32";

   

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String upload = request.getParameter("upload");
        if ("YES".equalsIgnoreCase(upload)) {
            upload(request, response);
        } else {
            download(request, response);
        }

    }


    private void upload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Create a factory for disk-based file items
        DiskFileUpload diskFileUpload = new DiskFileUpload();
        int max = GLOBAL.UPLOAD_MAX + 10;
        int absMax = max + 4;
        // Set factory constraints

        diskFileUpload.setSizeMax(1048576 * absMax);


        diskFileUpload.setRepositoryPath(getServletContext().getRealPath(ClipSystemSetup.UPLOAD_TEMP));
        // Create a new file upload handler

        // Set overall request size constraint

        UploadStatusMgr statusMgr = UploadStatusMgr.getInstance();
        UploadStatus status = statusMgr.getUploadStatus(TEST_URI);

        try {
            // Parse the request
            status.setUploadStarted(true);
            status.setComplete(false);
            //UploadListener listener = new UploadListener(status, contentLength);
            //diskFileUpload.setUploadStatusListener(listener);
            //if (statusLog.isDebugEnabled())
            //    statusLog.debug("Before Parsing Request");
            List items = diskFileUpload.parseRequest(request);
            // if (statusLog.isDebugEnabled())
            //     statusLog.debug("Done Parsing Request");
            Iterator iter = items.iterator();

            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                } else {
                    if (item.getSize() > 0) {
                        String fileName = item.getName();
                        fileName = stripPath(fileName);
                        File uploadDir = new File(uploadDirName(TEST_URI, getServletContext()));
                        uploadDir.mkdirs();
                        String name = uploadDirName(TEST_URI, getServletContext()) + File.separator + fileName;
                        item.write(new File(name));
                        // upload to S3
                        //  AWSAuthConnection conn =
                        //          new AWSAuthConnection(awsAccessKeyId, awsSecretAccessKey);
                        String key = TEST_URI + "/";

                        try {

                            AWSCredentials awsCredentials = new AWSCredentials("","");

                            // To communicate with S3, create a class that implements an S3Service.
                            // We will use the REST/HTTP implementation based on HttpClient, as this is the most
                            // robust implementation provided with jets3t.

                            S3Service s3Service = new RestS3Service(awsCredentials);
                            S3Bucket testBucket = s3Service.createBucket("cl1p-file-backup" + key);

                            File fileData = new File(name);
                            org.jets3t.service.model.S3Object fileObject = new org.jets3t.service.model.S3Object(testBucket, fileData);
                            //fileObject.setAcl(AccessControlList.REST_CANNED_PRIVATE);
                            //fileObject.setOwner(new S3Owner());

                            s3Service.putObject(testBucket, fileObject);
                            org.jets3t.service.model.S3Object objectComplete = s3Service.getObject(testBucket, fileName);
                            System.out.println("S3Object, complete: " + objectComplete);

                            // Read the data from the object's DataInputStream using a loop, and print it out.
                            System.out.println("Greeting:");
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(objectComplete.getDataInputStream()));
                            String data = null;
                            while ((data = reader.readLine()) != null) {
                                System.out.println(data);
                            }

                            //                   String s3Key =  URLEncoder.encode(key, "UTF-8");
                            //HttpURLConnection s3request =
                            //        conn.makeRequest("PUT", "cl1p-file-backup", key, null, null, null);

                            /*s3request.setDoOutput(true);
                          BufferedReader testReader = new BufferedReader(new FileReader(name));
                              while(testReader.ready()){
                                  System.err.println(testReader.readLine());
                              }
                              testReader.close();
                          FileInputStream fr = new FileInputStream(name);

                          final int chunkSize = 4096;
                          byte[] buf = new byte[chunkSize];
                          OutputStream os = s3request.getOutputStream();
                          int count;
                          while ((count = fr.read(buf)) != -1) {
                              os.write(buf, 0, count);
                          }
                          os.flush();
                          os.close();  */

                            //ListBucketResponse bucketResponse = conn.listBucket("cl1p-file-backup", TEST_URI + "/",null,null,null);
                            //    System.err.println(bucketResponse.name);
                        } catch (Exception e) {
                            //TODO if this fails, do I retry?
                            e.printStackTrace();
                        }

                    }
                }
            }
        } catch (FileUploadBase.SizeLimitExceededException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        status.setComplete(true);

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

    public static String uploadDirName(String uri) {
        String base = ClipConfig.UPLOADED_FILE_DIR;
        return base + File.separator + uri;
    }


    public static String uploadDirName(ClipRequest request) {
        String key = Cache.getKey(request);
        //key = key.replace('/', '-');
        return uploadDirName(key);
    }

    public static String uploadDirName(String uri, ServletContext context) {
        String base = ClipConfig.UPLOADED_FILE_DIR;
        return base + File.separator + uri;
    }

    private void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AWSAuthConnection conn =
                new AWSAuthConnection("", "");

        String fileName = "build.xml";
        //System.out.println(conn.listAllMyBuckets(null).entries);
        //System.out.println("----- listing bucket -----");
        //System.out.println(conn.listBucket("cl1p-db-backup", null, null, null, null).entries);
        String key = TEST_URI + "/";


        response.setContentType("application/x-download");
        //String codedfilename = URLEncoder.encode(file.getName(), "UTF8");   //Didn't work
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Send the file. // From S3
        OutputStream out = response.getOutputStream();
        try {
            AWSCredentials awsCredentials = new AWSCredentials("", "");

            // To communicate with S3, create a class that implements an S3Service.
            // We will use the REST/HTTP implementation based on HttpClient, as this is the most
            // robust implementation provided with jets3t.

            S3Service s3Service = new RestS3Service(awsCredentials);
            S3Bucket testBucket = s3Service.createBucket("cl1p-file-backup" + key);

            org.jets3t.service.model.S3Object objectComplete = s3Service.getObject(testBucket, "build.xml");
            System.out.println("S3Object, complete: " + objectComplete);

            // Read the data from the object's DataInputStream using a loop, and print it out.
            System.out.println("Greeting:");
           /* BufferedReader reader = new BufferedReader(
                    new InputStreamReader(objectComplete.getDataInputStream()));
            String data = null;
            while ((data = reader.readLine()) != null) {
                System.out.println(data);
            } */
            InputStream is = objectComplete.getDataInputStream();

            //File f = new File("cl1p.backup." + dateString + ".tar");
            //FileOutputStream fw = new FileOutputStream(f);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
