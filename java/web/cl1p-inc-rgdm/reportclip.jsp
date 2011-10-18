 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.tables.ReportedClip" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%--
  Created by IntelliJ IDEA.
  User: rob
  Date: Apr 5, 2008
  Time: 12:49:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String uri = request.getParameter("uri");
    String go = request.getParameter("go");
    if("yes".equals(go)){
        String reason = request.getParameter("reason");
        DBConnectionMgr dbMgr = new DBConnectionMgr(); 
        Connection con =dbMgr.getConnection();
        try{
            ReportedClip.report(uri, reason, con);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
           dbMgr.returnConnection(con);
        }
        %>
<html>
<body>
<center>
    Clip has been reported. Thank you.
</center>
</body>
</html>
<%
        return;
    }
%>
<html>
  <head><title>Report Cl1p</title></head>
  <body>
  Report Clip to the administrator. The clip will be reviewed and removed if needed</br>
  <form method="post">
      <input type="hidden" name="go" value="yes">
      <input type="hidden" value="<%= uri %>" name="uri">
      Reason:
      <textarea rows="20" cols="60" name="reason">

      </textarea>
      <input type="submit" value="Submit">
  </form>

  </body>
</html>