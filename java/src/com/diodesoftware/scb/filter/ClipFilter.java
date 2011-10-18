package com.diodesoftware.scb.filter;

import com.diodesoftware.R;
import com.diodesoftware.Root;
import com.diodesoftware.dbmapper.DBConnectionMgr;
import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.*;
import com.diodesoftware.scb.agents.*;
import com.diodesoftware.scb.clipboard.*;
import com.diodesoftware.scb.email.EmailMgr;
import com.diodesoftware.scb.speed.SpeedRecorder;
import com.diodesoftware.scb.tables.*;
import com.diodesoftware.scb.upload.UploadListener;
import com.diodesoftware.scb.upload.UploadStatus;
import com.diodesoftware.scb.upload.UploadStatusMgr;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.log4j.Logger;
import org.jets3t.service.S3ServiceException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.SocketException;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.List;


public class ClipFilter implements Filter {

    private FilterConfig config = null;
    private static Logger log = Logger.getLogger(ClipFilter.class);
    private static Logger statusLog = Logger.getLogger("custom.status");

    private static Random rnd = new Random();
    private static Cache cache = null;
    


    public static ClipCounter clipCounter = null;
    private static WordPicker wordPicker;
    private static DBConnectionMgr dbConnectionMgr;
    private static List hitSkips = new ArrayList();
    private static boolean testMode = false;

    private S3FileHandler s3FileHandler = new S3FileHandler();
    public static final long start = System.currentTimeMillis();


    private ServletContext getServletContext() {
        return config.getServletContext();
    }

    public static void init(ServletContext context) {
        testMode = "true".equals(System.getProperty("cl1p.test.mode"));
        try {
            if (ClipConfig.LOG4J != null){
                org.apache.log4j.xml.DOMConfigurator.configureAndWatch(ClipConfig.LOG4J);
                
            }else{
                org.apache.log4j.BasicConfigurator.configure();
            }
            log.info("Startup SCB");
            log.info("Using logging [" + ClipConfig.LOG4J +"]");
            dbConnectionMgr = new DBConnectionMgr();
            DBMapper mapper = new DBMapper(dbConnectionMgr);
            ClipAgent.initalize(mapper);
            StatsAgent.initialize(mapper);
            
            Cache.initialize();
            cache = Cache.getInstance();
            Connection con = dbConnectionMgr.getConnection();
            ClipSystemSetup.setupTables(con, new StringBuffer());
            SystemSettingAgent.loadMap(con);
            R.refresh();
            CleanerAgent.initialize(mapper);
            CleanerAgent cleanerAgent = CleanerAgent.getInstance();
            cleanerAgent.startCleanerThread(context);
            EmailMgr.initialize("localhost", mapper);
            UploadStatusMgr.initialize();

            UserAgent.initalize(mapper);
            CookieAgent.initalize(mapper);

            ClipSystemSetup.createUploadDir(ClipConfig.UPLOADED_FILE_DIR, new StringBuffer(), context);

            WordPicker.init(context.getRealPath("WEB-INF/eng.dic"));
            wordPicker = WordPicker.getInstnace();
            if (clipCounter == null) clipCounter = new ClipCounter(dbConnectionMgr);


            for (int i = 64; i < 96; i++) { // Google
                hitSkips.add("66.249." + i);
            }

            dbConnectionMgr.returnConnection(con);
            log.error("cl1p Init Complete. Ready to go.");
        } catch (Throwable e) {
            e.printStackTrace();
            log.fatal("Failed to start cl1p", e);
        }

    }

    public void init(FilterConfig config) throws ServletException {

        try{
        this.config = config;
        if (ClipConfig.getInstance().configFileNotFound) {
            org.apache.log4j.BasicConfigurator.configure();
            return; // User will have to setup system first
        }
        init(config.getServletContext());
        }catch(Throwable t){
            t.printStackTrace();
        }
    }


    public void destroy() {
        try {
            config = null;
            CleanerAgent.getInstance().setRunning(false);
            if(clipCounter != null)clipCounter.stop();
            log.error("cl1p. Shutdown complete");
        } catch (Exception e) {
            log.error("cl1p. Shutdown Error", e);
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        SpeedRecorder.start();
        try{
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
       
   
        SpeedRecorder.checkpoint("Log HIT");
        Root.initialize(request);
        try {
            String uri = request.getRequestURI();
            if (alwaysAllowed(uri)) {
                try
                {
                	chain.doFilter(request, response);
                }catch(SocketException e)
                {
                	//Who cares
                }catch(Exception e)
                {
                	if(!e.getClass().getName().equals("org.apache.catalina.connector.ClientAbortException"))
                	{
                		log.error("Error in chain",e);
                	}
                	
                }
                return;
            }
         
            SpeedRecorder.checkpoint("Before First Db Connection");
            Connection con = dbConnectionMgr.getConnection();
            SpeedRecorder.checkpoint("Got DB Connection");
            try {
                ClipRequest crequest = new ClipRequest(request, con);
                if(crequest.getReferer()!=null&&crequest.getReferer().contains("thepiratebay.org"))
                {
                	response.sendError(500);
                	return;
                }
                SpeedRecorder.checkpoint("Clip Request");
                ClipSession.attemptLogin(request, response, con);
                ClipRequest.setClipRequest( request, crequest);
                request.setAttribute("errorMessage", "&nbsp;");
                String rurl = ( request).getRequestURL().toString();
                String www = "http://www.";
                if (rurl.startsWith(www)) {
                    rurl = "http://" + rurl.substring(www.length());
                    ( response).sendRedirect(rurl);
                    return;
                }






                SpeedRecorder.checkpoint("Process Start");

                process( request,  response);
                SpeedRecorder.checkpoint("Process end");


            } finally {
                 dbConnectionMgr.returnConnection(con);
            }
        }catch(SocketException e)
        {
        	
        } catch (Exception e) {
            log.error("Error in filter", e);
        }
        }finally{
            SpeedRecorder.end();
        }
    }



// From Main Servlet

    public void doGet(HttpServletRequest httpRequest, HttpServletResponse response)
            throws IOException, ServletException {
        process(httpRequest, response);
    }

    public void doPost(HttpServletRequest httpRequest, HttpServletResponse response)
            throws IOException, ServletException {

        process(httpRequest, response);
    }

    public void process(HttpServletRequest httpRequest, HttpServletResponse response)
            throws IOException, ServletException {

        String rurl = httpRequest.getRequestURL().toString();
        URL theURL = new URL(rurl);
        String host = theURL.getHost();
        boolean mobile = host.startsWith("m.");
        boolean api = host.startsWith("api.");
        if(api)
        {
            httpRequest.setAttribute(HtmlParam.API_ATTR, new Boolean(true));
            log.info("Request is for API");
        }
        
        if (mobile) {
            if (log.isDebugEnabled())
                log.debug("Request is mobile");

            httpRequest.setAttribute(HtmlParam.MOBILE_ATTR, new Boolean(true));
        } else {
            if (log.isDebugEnabled())
                log.debug("Request [" + rurl + "] not mobile");
        }
        if (log.isDebugEnabled()) log.debug("Processing URL [" + rurl + "]");


        Connection con = dbConnectionMgr.getConnection();
        try {
            try
            {
                Bob bob = new Bob(httpRequest);
                DBMapper.save(bob, con);
            }catch(Exception e)
            {
                log.error("Error saving stats (BOB)",e);
            }
            ClipRequest request = new ClipRequest(httpRequest, con);
            ClipSession.attemptLogin(request, response, con);
            Clip clip = cache.get(request);
            request.setClip(clip);
            if (clip.getOwnerId() != 0) {
                Owner owner = (Owner) DBMapper.getInstance().load(Owner.class, clip.getOwnerId(), con);
                request.setOwner(owner);
            }
            ClipSession cs = ClipSession.getSession(httpRequest);
            if (cs.isLoggedIn()) {
                request.setUser(cs.getUser());
            }

            if (httpRequest.isSecure()) {
                // SSL Request - You gota pay for that
                request.setSSL(true);
            }

            if ("yes".equals(httpRequest.getParameter("ajaxUpload"))) {
                ClipRequest.setClipRequest(request.getRequest(), request);
                RequestDispatcher dispatcher = config.getServletContext().getRequestDispatcher("/cl1p-inc-rgdm/ajax-upload.jsp");
                request.setAttribute("Name", "Irfan");
                request.setAttribute("Language", "Java");
                dispatcher.forward(httpRequest, response);
                return;
            }




            String mobieJump = request.getParameter("mobileJump");
            if (mobieJump != null) {
                response.sendRedirect("/" + mobieJump);
                return;
            }

            doClipboard(request, response);
        } finally {
            dbConnectionMgr.returnConnection(con);
        }
    }

    private void doClipboard(ClipRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String url = request.getUrl();
        ClipSession.getSession(request.getRequest()).setLastUri(request.getUri());
        if (log.isDebugEnabled()) log.debug("Processing URL [" + url + "]");
        if (url.startsWith("http://download.")) {
            
            log.debug("Returning file");
            returnFile(request, response);
            return;
        }
        ClipSession clipSession = ClipSession.getSession(request.getRequest());
        boolean pro = clipSession.isPro();
        int max = GLOBAL.UPLOAD_MAX + 10;

        if (request.isOwned()) {
            max = GLOBAL.UPLOAD_MAX_OWNED + 10;
        }
        if (clipSession.isPro()) {
            max = GLOBAL.UPLOAD_MAX_PRO + 10;

        }

        if (FileUpload.isMultipartContent(request.getRequest())) {
            UploadStatusMgr uplaodStatusMgr = UploadStatusMgr.getInstance();
            UploadStatus uploadStatus = uplaodStatusMgr.getUploadStatus(request.getUri());
            uploadStatus.reset();
            String contentLengthString = request.getRequest().getHeader("content-length");
            long contentLength = Long.parseLong(contentLengthString);

            if (contentLength > (1048576l * (long) max)) {
                request.setAttribute(DisplayUrlLogic.ERROR_MESSAGE, "File must be less then " + (max - 1) + "MB to upload");
                uploadStatus.setDone(true);
                uploadStatus.setComplete(true);
                uploadStatus.setError(true);
                int msgMax = GLOBAL.UPLOAD_MAX;
                if (pro) {
                    msgMax = GLOBAL.UPLOAD_MAX_PRO;
                }
                uploadStatus.setErrMsg("File must be less then " + msgMax + "MB");
                request.reInitParamererMap();
                clipboard(request, response, getServletContext());
            } else {
                request.reInitParamererMap();
                if(fileCount(request.getClip(), request.getCon()) <= 50){
                    uploadFile(request, response, contentLength);
                }
            }
        }else{
           request.reInitParamererMap();
           clipboard(request, response, getServletContext()); 
        }


    }

    public static void clipboard(ClipRequest request, HttpServletResponse response, ServletContext context)
           {
        try {
            String uri = request.getUri();
            String url = request.getUrl();
            if(ClipSaver.isValueToBig(request))
            {
            	 response.sendRedirect("/cl1p-inc-rgdm/valueTooBig.jsp");
            	return;
            }

            if (log.isDebugEnabled()) log.debug("Clipboard URI [" + uri + "] URL [" + url + "]");

            boolean dumpMode = url.startsWith("http://d.");
            boolean dumpModeBr= url.startsWith("http://dbr.");

            if (dumpModeBr) {
                dumpMode = true; // They are the same thing, just that BR inserts Br where the line breaks used to be.
            }

            Clip clip = request.getClip();

            int viewMode = ViewMode.getViewMode(request);

            request.setViewMode(viewMode);

            String value = clip.getValue();
            if (value == null)
                value = "";
            if (value.trim().length() > 0) {
                request.setAttribute("showAd", new Boolean(true));
            }
            String title = clip.getTitle();
            if (title == null) {
                title = "cl1p.net";
            }
            if (request.isSslAllowed() && !request.isSSL()) {
                // Force SSL Clips to be SSL
                String rurl = request.getRequest().getRequestURL().toString();
                int i = rurl.indexOf("://");
                i = i + 3;
                rurl = "https://" + rurl.substring(i);
                response.sendRedirect(rurl);
                return;
            }




            if (viewMode == ViewMode.EDIT && request.isSSL()) {
                // Trying to edit an SSL page requires a pro user
//                if (!request.isSslAllowed()) {
//                    // Ok, redirect to login page, and explain why
//                    RequestDispatcher dispatcher = context.getRequestDispatcher("/cl1p-admin/buy-url-2.jsp");
//                    request.setAttribute("Name", "Irfan");
//                    request.setAttribute("Language", "Java");
//                    dispatcher.forward(request.getRequest(), response);
//                    return;
//                }
            }
            ClipStatus clipStatus = ClipStatus.getInstance();
            if (viewMode == ViewMode.EDIT) {
                ClipSaver.saveTODb(request);
                clipStatus.grantAccess(request.getSession().getId(), clip.getUri());
            } else if (viewMode == ViewMode.READ_ONLY) {
                ClipType.load(request);// Just in case, load it
                request.getCl1pType().save(request);
                request.setClipType(null);
                ClipType.load(request);// Load again to relect recent changes
                clipStatus.grantAccess(request.getSession().getId(), clip.getUri());
            }
            Calendar lastEdit = clip.getLastEdit();
            long lastEditEpoch = System.currentTimeMillis();
            if (lastEdit != null) lastEditEpoch = lastEdit.getTimeInMillis();

            String message = (String) request.getAttribute(DisplayUrlLogic.ERROR_MESSAGE);
            if (message == null)
                message = "&nbsp;";

            request.getRequest().setAttribute("message", message);
            String password = request.getParameter(HtmlParam.PASSWORD);
            // When passwords don't match don't display the password again
            if (password != null && request.getAttribute(DisplayUrlLogic.DONT_DISPLAY_PASSWORD) != null)
                password = null;

            if (password == null)
                password = "";

            if (dumpMode) {
            	response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");              
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                if (dumpModeBr)
                    value = value.replace("\n", "<br/>\n\b");
                if (clip.getViewPassword()) {
                    PasswordMgr pwdMgr = PasswordMgr.getInstance(request);
                    if (pwdMgr.hasKey(clip.getUri())) {
                        response.getOutputStream().write(value.getBytes("UTF-8"));
                    } else {
                        response.getOutputStream().println("Can't download restricted cl1p.");
                    }
                } else {
                    response.getOutputStream().write(value.getBytes("UTF-8"));
                }
                response.getOutputStream().flush();
                return;
            }

            Clip.setClip(request.getRequest(), clip);
            String clipType = request.getParameter("changeType");
            String currentType = request.getParameter("currentType");
            if (clipType != null && !clipType.equals(currentType)) {
                int typeId = Integer.parseInt(clipType);
                ClipAgent.getInstance().changeClipType(clip, request.getCon(), typeId);
            }
            ClipRequest.setClipRequest(request.getRequest(), request);



            String userAgent = request.getRequest().getHeader("user-agent");
            RequestDispatcher dispatcher = null;
            if(request.getRequest().getAttribute(HtmlParam.API_ATTR) != null)
            {
            	System.err.println("API - GOING");
            	dispatcher = context.getRequestDispatcher("/cl1p-inc-rgdm/api_master.jsp");
            }else{ 
            	if((userAgent != null && userAgent.indexOf("Safari") != -1 && userAgent.indexOf("Mobile/") != -1 && userAgent.indexOf("iPad") == -1) || (userAgent != null && userAgent.indexOf("Nokia") != -1))
            		dispatcher = context.getRequestDispatcher("/cl1p-inc-rgdm/iphone_master.jsp");
            	else
            		dispatcher = context.getRequestDispatcher("/cl1p-inc-rgdm/master.jsp");
            }
            request.setAttribute("Name", "Irfan");
            request.setAttribute("Language", "Java");
            request.setAttribute("viewRestricted", new Boolean(viewMode == ViewMode.PASSWORD_REQUIRED));
            long start = System.currentTimeMillis();

            dispatcher.forward(request.getRequest(), response);
            long spend = System.currentTimeMillis() - start;
            //log.error("Spend [" + spend + "]ms on JSP");
        } catch (Exception e) {
            log.error("Error in clipboard", e);
        }
    }

    private void uploadFile(ClipRequest request, HttpServletResponse response, long contentLength)
            throws IOException, ServletException {
        // Create a factory for disk-based file items
        DiskFileUpload diskFileUpload = new DiskFileUpload();
        int max = GLOBAL.UPLOAD_MAX + 10;
        int absMax = max + 4;
        ClipSession clipSession = ClipSession.getSession(request.getRequest());
        if (clipSession.isPro()) {
            max = GLOBAL.UPLOAD_MAX_PRO + 10;
            absMax = max + 10;
        } else {
            if (request.isOwned()) {
                max = GLOBAL.UPLOAD_MAX_OWNED + 10;
                absMax = max + 10;
            }
        }
        // Set factory constraints

        diskFileUpload.setSizeMax(1048576 * absMax);


        diskFileUpload.setRepositoryPath(getServletContext().getRealPath(ClipSystemSetup.UPLOAD_TEMP));
        // Create a new file upload handler

        // Set overall request size constraint

        UploadStatusMgr statusMgr = UploadStatusMgr.getInstance();
        if (statusLog.isDebugEnabled())
            statusLog.debug("Getting status for cl1pFilter URI [" + request.getUri() + "]");
        UploadStatus status = statusMgr.getUploadStatus(request.getUri());

        try {
            // Parse the request
            status.setUploadStarted(true);
            status.setComplete(false);
            UploadListener listener = new UploadListener(status, contentLength);
            diskFileUpload.setUploadStatusListener(listener);
            if (statusLog.isDebugEnabled())
                statusLog.debug("Before Parsing Request");
            List items = diskFileUpload.parseRequest(request.getRequest());
            if (statusLog.isDebugEnabled())
                statusLog.debug("Done Parsing Request");
            Iterator iter = items.iterator();

            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                log.debug("Processing " + item.getFieldName());
                if (item.isFormField()) {
         
                    request.setParameter(item.getFieldName(), item.getString("UTF-8"));
                } else {
                    if (item.getSize() > 0) {
                        if (log.isDebugEnabled())
                            log.debug("Uploading file size [" + item.getSize() + "]");
                        if(request.getClip().getOwnerId() > 0)
                        {
                        String fileName = item.getName();
                        fileName = stripPath(fileName);
                        File uploadDir = new File(uploadDirName(request));
                        uploadDir.mkdirs();
                        String name = uploadDirName(request) + File.separator + fileName;
                        item.write(new File(name));
                        // upload to S3
                        String info = "";
                        try{
                            int type = ClipS3Object.TYPE_FILE;
                            if("uploadPic".equals(item.getFieldName())){
                                log.debug("Uploading picture");
                                type = ClipS3Object.TYPE_PICTURE;
                                int pictureMax = 2;
                                if(request.isOwned()){
                                    pictureMax = 20;
                                }

                                int uploadPics = ClipS3Object.listPictureUrls(request.getClip(), request.getCon()).size();
                                if(uploadPics >= pictureMax){
                                    status.setError(true);

                                    String msg = "Only " + pictureMax + " pictures can be uploaded.";
                                    status.setErrMsg(msg);
                                    status.setMsg(msg);
                                    File f = new File(name);
                                                                        f.delete();
                                    return;
                                }
                                Image originalImage = new ImageIcon(name).getImage();
                                int imageWidth = originalImage.getWidth(null);
                                int imageHeight = originalImage.getHeight(null);
                                log.debug("Image width [" + imageWidth + "] Image Height [" + imageHeight + "]");
                                info = "" + imageWidth + "," + imageHeight;

                            }else{
                                log.debug("Uploading file " + item.getFieldName());
                            }
                            status.setMsg("Processing");
                            s3FileHandler.uploadObject(request.getClip(), new File(name), type, info, request.getCon());
                            // Upload WOrked. Delete the temp file
                            File f = new File(name);
                            f.delete();
                        
                        }catch(Exception e){
                            log.error("Error uploading to S3", e);
                        }

                        DownloadCount.resetCount(request.getClip(), request.getCon());
                        }
                    }
                }
            }
        } catch (FileUploadBase.SizeLimitExceededException e) {
            log.debug("FileSize was too Big", e);
            status.setError(true);

            String msg = "File must be less then " + max + "MB to upload";
            status.setErrMsg(msg);
            request.setAttribute(DisplayUrlLogic.ERROR_MESSAGE, msg);
        } catch (Exception e) {
            log.debug("File Upload Exception", e);
            status.setError(true);
            status.setErrMsg("File Upload Error");
        }
        status.setComplete(true);
        clipboard(request, response, getServletContext());
    }

    public static String uploadDirName(ClipRequest request) {
        String key = Cache.getKey(request);
        //key = key.replace('/', '-');
        return uploadDirName(key);
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

    public static String uploadDirName(String uri,ServletContext cx) {
         String base = ClipConfig.UPLOADED_FILE_DIR;
         return base + File.separator + uri;
     }


    public static String uploadDirName(String uri) {
         String base = ClipConfig.UPLOADED_FILE_DIR;
         return base + File.separator + uri;
     }




    public static boolean hasFile(ClipRequest request) {
       return hasFile(request.getClip(), request.getCon());
    }

    public static boolean hasFile(Clip clip, Connection con) {
        return fileCount(clip,con) > 0;
    }

    public static int fileCount(Clip clip, Connection con){
        return ClipS3Object.listFiles(clip, con).size();
    }



    public static String[] getFileName(ClipRequest request, ServletContext context) {
        return getFileName(request.getClip().getUri());
    }

    public static String[] getFileName(String uri ){
        String filename = uploadDirName( uri);
        File f = new File(filename);
        ArrayList names = new ArrayList();
        if (f.exists()) {
            File[] files = f.listFiles();
            for (File file : files) {
                String n = file.getName();
                if (!n.equals(".") && !n.equals("..")) {
                    names.add(n);
                }
            }
        }
        String[] result = new String[names.size()];
        names.toArray(result);
        return result;
    }


    public void returnFile(ClipRequest request, HttpServletResponse res)
            throws IOException {
        if (request.getClip().isViewPassword() && request.getClip().getPassword() != null) {
           /* String token = request.getParameter("t");
            if (token != null) {
                int tokenClipId = DownloadTokenMgr.getClipId(token);
                if (tokenClipId != request.getClip().getNumber()) {
                    log.debug("Token is invalid");
                    return;
                }
            } else {
                log.debug("No Token found");
                return;
            }*/
        }
        /*String dirName = uploadDirName(request, getServletContext());
        if (log.isDebugEnabled())
            log.debug("Downloading File for dir [" + dirName + "]");
        File dir = new File(dirName);
        if (!dir.exists()) {
            if (log.isDebugEnabled())
                log.debug("Dir [" + dirName + "] does not exist.");
            return;
        } */
        int fileNumber = 0;
        try{
            fileNumber = Integer.parseInt(request.getParameter("FILE"));
        }catch(NumberFormatException e){
            log.debug("File Number is not a number");
        }
        /*File[] list = dir.listFiles();
        File file = null;

        if(fileNumber < list.length){
             file = list[fileNumber];
        }
        if (file == null) {
            if (log.isDebugEnabled())
                log.debug("No file found in dir [" + dirName + "]");
            return;
        } */
        //if (log.isDebugEnabled()) log.debug("Downloading file [" + file.getPath() + "]");
        // Set the headers.
        /*res.setContentType("application/x-download");
        //String codedfilename = URLEncoder.encode(file.getName(), "UTF8");   //Didn't work
        res.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        // Send the file. // From S3
        OutputStream out = res.getOutputStream();
        DownloadCount.addCount(request.getClip(), request.getCon());
        returnFile(file, out, request.getClip(), request.getCon());  // Shown earlier in the chapter
        out.close();
        if (log.isDebugEnabled()) log.debug("Downloading file [" + file.getPath() + "] complete"); */
        try{
            ClipS3Object s3 = ClipS3Object.retreiveFile(request.getClip(),fileNumber, request.getCon());
            if(s3 == null)
            {
                log.debug("No cl1pS3Record found.");
                return; // No record no download
            }
            String fileName = s3.getName();
            try
            {
            	s3FileHandler.returnFile(request.getClip(), res, fileName);
            }catch(S3ServiceException s3e)
            {
            	if(s3e.getS3ErrorCode().equals("NoSuchKey"))
            	{
            		log.warn("No Such file for:" + fileName + " clip URI:" + request.getUri());
            	}else{
            		log.error("S3 Exception",s3e);
            	}
            }
        }catch(Exception e){
            log.error("Error returning file from S3",e);
        }
    }


    public static void returnFile(File file, OutputStream out, Clip clip, Connection con)
            throws IOException {
        InputStream in = null;
        boolean success = false;
        if (file.exists() && !file.isDirectory()) {
            try {
                in = new BufferedInputStream(new FileInputStream(file));
                byte[] buf = new byte[4 * 1024];  // 4K buffer
                int bytesRead;
                while ((bytesRead = in.read(buf)) != -1) {
                    out.write(buf, 0, bytesRead);
                }

                success = true;
            }
            finally {
                if (in != null) in.close();

                if (success) {

                    int left = DownloadCount.getDownloadsLeft(clip, con);
                    if (left <= 0)
                        file.delete();
                }
            }
        }
    }

    private static char nextChar() {
        int val = 97 + rnd.nextInt(26);
        return (char) val;
    }

    private static String rndString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(nextChar());
        }
        return sb.toString();
    }

    public static String randomUrl(HttpServletRequest request) {
        String s = rndKey();
        String url =com.diodesoftware.Root.R(request) + s;
        return url;
    }

    public static String rndKey() {
        StringBuffer sb = new StringBuffer("/");
        sb.append(wordPicker.pickWord());
        sb.append("/");

        return sb.toString();
    }





    public static String getBlankUri(Connection con) {
        String s = "/" + rndString(rnd.nextInt(15) + 5);
        Clip c = ClipAgent.getInstance().loadClip(s, con);
        if (c == null)
            return s;
        return getBlankUri(con);
    }


  
    private boolean alwaysAllowed(String uri) {
        if(uri.equals("/"))return true;
        if(testMode)if(uri.startsWith("/selenium-server"))return true;
        if(uri.equals("/index.jsp"))return true;
        int i = uri.lastIndexOf("/");
        if(i == 0 && uri.endsWith(".jsp"))return true;
        if(uri.startsWith("/cl1p-inc-rgdm/"))return true;
        if(uri.startsWith("/cl1p-admin/"))return true;
        if(uri.equals("/sitemap.xml"))return true;
        if(uri.startsWith("/favicon.ico"))return true;
        if (uri.endsWith("/robots.txt")) return true;
        if (uri.endsWith("/rss.xml")) return true;

        return false;
    }


}



