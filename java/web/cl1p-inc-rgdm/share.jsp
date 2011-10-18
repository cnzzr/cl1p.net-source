 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.util.Tracker" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.net.URL" %>
<%@ page import="com.diodesoftware.scb.email.EmailMgr" %>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.Connection" %>
<%!
    Tracker tracker = new Tracker(1000*60);
    private Logger log = Logger.getLogger("jsp.share");
    private static final int MAX_PRE_MINUTE = 60;
%>
<%

    int count = tracker.count();
    if(count > MAX_PRE_MINUTE){
        log.error("Tracker count is " + count + " max is " + MAX_PRE_MINUTE);
        %>
{
    ok:false,
    error:"Sorry! System way too busy. Try again later!"
}
<%
        return;
    }
    String to = request.getParameter("shareEmail");
    String subject = request.getParameter("shareSubject");
    String msg = request.getParameter("shareMsg");
    DBConnectionMgr dbMgr = new DBConnectionMgr();
    Connection con = dbMgr.getConnection();
    try{
    	
        //EmailMgr.getInstance().send("cl1p@cl1p.net",to, subject,msg,con);
        %>
{
    ok:false,
    error:"Share this cl1p is disabled."
}
<%
        return;

    }catch(Exception e){
        log.error("Error Sending share email to " + to, e);
            %>
{
    ok:false,
    error:"Internal System error. Sorry!"
}
<%
    }finally{
        dbMgr.returnConnection(con);
    }


%>