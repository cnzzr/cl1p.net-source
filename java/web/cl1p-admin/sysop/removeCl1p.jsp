<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%@ page import="java.sql.Connection"%>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr"%>
<%@ page import="org.apache.log4j.Logger"%>
<%@ page import="com.diodesoftware.scb.agents.UserAgent"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="com.diodesoftware.scb.tables.Clip"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.diodesoftware.scb.*" %>
<%@ page import="com.diodesoftware.R" %>

<%!

    private static Logger log  = Logger.getLogger("jsp.admin.menu");
%>
<%	
    int clipId = Integer.parseInt(request.getParameter("id"));

    ClipSession cl1pSession  = ClipSession.getSession(request);
    if(cl1pSession.isLoggedIn() == false){
        String redirectURL = "/cl1p-admin/create.jsp";
                                      %>
<jsp:forward page="<%= redirectURL%>"/>

<%
        return;
    }
    boolean go  = request.getParameter("go") != null;
    boolean deleted = false;
    if(go)
    {
    	DBConnectionMgr dbConnectionMgr = new DBConnectionMgr();
        Connection con = dbConnectionMgr.getConnection();
        try{
        cl1pSession.reloadUser(con);
		String sql = "Delete from UserCLip where UserId = ? and ClipId = ?";
		PreparedStatement prepStmt = con.prepareStatement(sql);
		prepStmt.setInt(1, cl1pSession.getUser().getNumber());
		prepStmt.setInt(2, clipId);
		prepStmt.executeUpdate();
		prepStmt.close();
		%>
		<jsp:forward page="menu.jsp"/><%
        }finally{
        	dbConnectionMgr.returnConnection(con);
        }
    }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="java.sql.PreparedStatement"%><html>
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
<%
    ClipSession clipSession = ClipSession.getSession(request);
%>

<div name="main">
User: <%= request.getAttribute("RGDM-USERNAME-RGDM")%>
    <br><br>
    <a href="/cl1p-admin/menu.jsp">Back</a>

</div><br>
</td></tr></table>
</center>
<h2>Are you sure you want to remove this cl1p from your user list?</h2>
<a href="removeClip.jsp?id=<%= clipId %>&go=true">Yes</a>&nbsp;&nbsp;<a href="menu.jsp">No</a>
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("UA-100555-2");
pageTracker._initData();
pageTracker._trackPageview();
</script>
</body>
</html>

