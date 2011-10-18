<%
    SysopSession sysopSession = SysopSession.getInstance(request);
    if (!sysopSession.isLoggedIn()) {
%>
<%@page import="com.diodesoftware.scb.sysop.SysopSession"%>
<jsp:forward page="index.jsp"/>
<%
        return;
    }
    %>
<%@page import="com.diodesoftware.scb.agents.UserAgent"%>
<%@page import="com.diodesoftware.scb.ClipRequest"%>
<%@page import="java.util.List"%>
<%@page import="com.diodesoftware.W"%>
<%@page import="com.diodesoftware.scb.tables.User"%>
<%@page import="com.diodesoftware.dbmapper.DBMapper"%>
<html>
  <head>
  <title><%= W.w("edit.user") %></title>
  <link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= com.diodesoftware.R.T()%>">
  </head>
  <body>
  <center>
<table height="100%" cellspacing="0" cellpadding="0" width="875">
<tr>
<td valign="top">
  <h2><%= W.w("edit.user") %></h2>


<%
	ClipRequest clipRequest = ClipRequest.getClipRequest(request);
	String errMsg = "";
	String userIdS = request.getParameter("userId");
	int userId = Integer.parseInt(userIdS);
	User user = (User)DBMapper.load(User.class, userId, clipRequest.getCon());

	String email = request.getParameter("email");
	String disabled = request.getParameter("disabled");
	if(email != null){
		user.setEmail(email);
		user.setDisabled("disabled".equals(disabled));
		DBMapper.save(user, clipRequest.getCon());
		errMsg = W.w("user.changes.saved");
	}		
%>
<form method="post">
<input type="hidden" name="id" value="">
<table>
<tr><td><%= W.w("username") %></td><td><%= user.getUsername() %></td></tr>
<tr><td><%= W.w("email") %></td><td><input type="text" name="email" value="<%= user.getEmail() %>"></td></tr>
<tr><td><%= W.w("disabled") %></td><td><input type="checkbox" name="disabled" value="disabled" <%= (user.isDisabled()?"":"Checked") %>></td></tr>
<tr><td colspan="2"><input type="submit" value="<%= W.w("submit") %>"></td></tr>
<tr><td colspan="2"><%= errMsg %></td></tr>
</table>
</form>
<a href="setPassword.jsp?userId=<%= user.getNumber() %>"><%= W.w("set.password") %></a><br/>

<a href="./index.jsp"><%= W.w("menu") %></a>
  </td></tr></table></center>
</body>
</html>  