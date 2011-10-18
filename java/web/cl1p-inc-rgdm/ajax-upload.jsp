<%@ page import="org.apache.log4j.Logger" %>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.upload.UploadStatusMgr" %>
<%@ page import="com.diodesoftware.scb.upload.UploadStatus" %>
<%@ page import="com.diodesoftware.scb.ClipRequest" %>
<%@ page import="java.text.*" %>
<%!
    private static Logger log = Logger.getLogger("jsp.ajax.upload");
    private static Logger statusLog = Logger.getLogger("custom.status");
%>
<%

    response.setContentType("text/xml");
    response.setHeader("Cache-Control", "NO-CACHE");
    response.setDateHeader("Expires", 0);
    ClipRequest cliprequest = ClipRequest.getClipRequest(request);
            String uri = cliprequest.getUri();
    UploadStatusMgr uploadStatusMgr = UploadStatusMgr.getInstance();
    String key = request.getParameter("key");
    if(!key.endsWith("/")){
        key += "/";
    }
    if(statusLog.isDebugEnabled())
        statusLog.debug("Getting Status for ajax-uplaod URI [" + key + "]");

    UploadStatus status = uploadStatusMgr.getUploadStatus(key);
    boolean uploadStarted = status.isUploadStarted();

    float soFar = 0;
    float uploadedB = 0;
    float max = 0;
    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(2);
    try {

        int uploaded = status.getPercent();
        soFar = uploaded;
        uploadedB = (float)status.getUploaded() / 1048576f;
        max = (float)status.getSize() / 1048576f;
    } catch (Exception e) {
        e.printStackTrace();
    }
    boolean done = status.isComplete();
    if (!done) {
        if(status.getMsg() == null){
%>{percent:<%= soFar %>,uploaded:<%= nf.format(uploadedB) %>,total:<%= nf.format(max)  %>,askAgain:true}<%
    }else{
        %>{msg:'<%= status.getMsg() %>',askAgain:true}<%
    }
if(log.isDebugEnabled())
    log.debug("Ajax Upload [" + status.getPercent() + "] Uploaded [" + status.getUploaded() + "] of [" +status.getSize() + "]");

    }else{
       if(status.isError()){
       log.debug("Upload status Error");
           %>{askAgain:false,error:true,msg:"<%= status.getErrMsg() %>"}<%
       }else{
       log.debug("Upload status Done");
            %>{askAgain:false,error:false,msg:"Done"}<%
       }
   }
%>
