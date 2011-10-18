 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.diodesoftware.scb.tables.Ignoremail" %><%
    String go = request.getParameter("go");
    String email =  request.getParameter("e");
    if("YES".equals(go)){
        DBConnectionMgr dbMgr = new DBConnectionMgr();
        Connection con = dbMgr.getConnection();
        try{
            Ignoremail.add(email, con);
        }finally{
           dbMgr.returnConnection(con);
        }
        %>

<html>
<head></head>
<body>
<center>
<p>Thanks, it wont happen again.</p>
   </center>
</body>
</html>

<%

          return;

    }

%>

<html>
<head></head>
<body>
<center>
<form method="post">
    <input type="hidden" name="go" value="yes">
<input type="hidden" name="e" value="<%= email%>">
    <p>
        I’m really sorry for the inconvenience.  I added the share feature in hope that it would help improve cl1p.net,
        but in this case it has only annoyed. Again, my apologies.</p>

<p>Clicking the DON’T EVER DO IT AGAIN! will stop cl1p.net from ever sending you a message again. Again I’m sorry.
    </p>
    <input type="submit" value="DON’T EVER DO IT AGAIN!">
</form>
   </center>
</body>
</html>