<%@page import="com.diodesoftware.scb.filter.ClipFilter"%>
<%@page import="com.diodesoftware.scb.ClipConfig"%>
<%@page import="com.diodesoftware.scb.ClipContextListener"%>
<%!

    private static Logger log = Logger.getLogger("jsp.admin.cl1p-list");
%>
<%
    SysopSession sysopSession = SysopSession.getInstance(request);
    if (!sysopSession.isLoggedIn()) {
%>
<jsp:forward page="index.jsp"/>
<%
        return;
    }
    %>
<%
ClipRequest clipRequest = ClipRequest.getClipRequest(request);
String errMsg = "";
String pc = request.getParameter("pc");
String p1 = request.getParameter("p1");
String p2 = request.getParameter("p2");
try{
if(p1 != null){
	if(!p1.equals(p2)){
		errMsg = W.w("password.dont.match");
	}else if(p1.trim().length() < 5){
		errMsg = W.w("password.must.be.5.characters");	
	}else if(SysopSession.login(pc, clipRequest.getCon()) == null){
		errMsg = W.w("current.password.is.invalid");
	}else{
		p1 = p1.trim();
		p2 = p2.trim();


        Connection con = clipRequest.getCon();


		SysopLogin sysop = (SysopLogin)DBMapper.load(SysopLogin.class, 1, con);

		if(sysop != null){
			

		sysop.setPassword(p1);

		DBMapper.save(sysop, con);
		errMsg = W.w("sysop.password.changed");
		}else{
			errMsg = W.w("cant.find.current.sysop.password");
		}
	}
}
}catch(Exception e){
	e.printStackTrace();
	errMsg = e.getMessage();
}
%>
<%@page import="com.diodesoftware.scb.ClipRequest"%>
<%@page import="com.diodesoftware.dbmapper.DBMapper"%>
<%@page import="com.diodesoftware.scb.sysop.SysopLogin"%>
<%@page import="com.diodesoftware.dbmapper.DBConnectionMgr"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.diodesoftware.scb.sysop.SysopSession"%>
<%@page import="com.diodesoftware.W"%>
<%@ page import="com.diodesoftware.R" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" media="screen, projection" href="/style.css<%= R.T()%>">
</head>
<body>
<center>
<table height="100%" cellspacing="0" cellpadding="0" width="875">
<tr>
<td valign="top">
<h2><%= W.w("set.sysop.password") %></h2>
<form method="post">
<table>
<tr><td><%= W.w("current.password") %></td><td><input type="password" name="pc"></td></tr>
<tr><td><%= W.w("password") %></td><td><input type="password" name="p1"></td></tr>
<tr><td><%= W.w("verify.password") %></td><td><input type="password" name="p2"></td></tr>
<tr><td colspan="2"><%= errMsg %></td></tr>
<tr><td colspan="2"><input type="submit" value="<%= W.w("set.sysop.password") %>"></td></tr>
</table>
</form>
<a href="./index.jsp"><%= W.w("menu") %></a>
</td></tr></table></center>
</body>
</html>