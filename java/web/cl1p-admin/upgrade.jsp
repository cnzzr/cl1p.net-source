<%@ page import="com.diodesoftware.scb.ClipSession"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr"%>
<%@ page import="com.diodesoftware.R" %>

<html>
<head>
  <title>cl1p.net The internet clipboard</title>
  <meta http-equiv="content-type"
 content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= R.T()%>">

</head>
<body>


<center>
<table height="100%" width="850" cellspacing="0" cellpadding="5">
<tr>
<td valign="top">
<img src="/cl1p-inc-rgdm/images/cl1p_logo.jpg" alt="cl1p the internet clipboard">
<br>
<%
    DBConnectionMgr dbConnectionMgr = new DBConnectionMgr();
    Connection con = dbConnectionMgr.getConnection();
    ClipSession clipSession = ClipSession.getSession(request);
    try{
    	clipSession.reloadUser(con);
    }finally{
	dbConnectionMgr.returnConnection(con);
    }
    if(clipSession.isLoggedIn()){
%>
<%@ include file="u_table.jsp"%>

<%
    }else{
        %>
You must first create an account and then upgrade.<br>
<a href="create.jsp">Login</a>
<%
    }
%>
</td></tr></table>
</body>
</html>


