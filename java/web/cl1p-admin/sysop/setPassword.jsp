<%
    SysopSession sysopSession = SysopSession.getInstance(request);
    if (!sysopSession.isLoggedIn()) {
%>
<%@page import="com.diodesoftware.scb.sysop.SysopSession"%>
<%@page import="com.diodesoftware.W"%>
<jsp:forward page="index.jsp"/>
<%
        return;
    }
    %>
<%
	ClipRequest clipRequest = ClipRequest.getClipRequest(request);
	String userIdS = request.getParameter("userId");
	int userId = Integer.parseInt(userIdS);
	User user = (User)DBMapper.load(User.class, userId, clipRequest.getCon());
	String errMsg = "";
	String p1 = request.getParameter("p1");
	String p2 = request.getParameter("p2");
	if(p1 != null){
		if(!p1.equals(p2)){
			errMsg = W.w("passwords.dont.match");
		}else if(p1.trim().length() < 5){
			errMsg = W.w("password.must.be.5.characters");
		}else{
		
			user.setPassword(p1);
			DBMapper.save(user, clipRequest.getCon());
			errMsg =  W.w("password.changed.for.user") + " "+ user.getUsername();
		}
	}

%>    
<%@page import="com.diodesoftware.scb.agents.UserAgent"%>
<%@page import="com.diodesoftware.scb.ClipRequest"%>
<%@page import="java.util.List"%>
<%@page import="com.diodesoftware.scb.tables.User"%>
<%@page import="com.diodesoftware.dbmapper.DBMapper"%>
<html>
  <head>
  <title><%= W.w("reset.password") %></title>
  <link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= com.diodesoftware.R.T()%>">
  </head>
  <body>
<center>
<table height="100%" cellspacing="0" cellpadding="0" width="875">
<tr>
<td valign="top">
  <h2><%= W.w("reset.password") %>: <%= W.w("username") %> <%= user.getUsername() %></h2>
<form method="post">
<input type="hidden" name="userId" value="<%= user.getNumber() %>">
<table>
<tr><td><%= W.w("new.password") %></td><td><input type="password" name="p1"></td></tr>
<tr><td><%=W.w("new.password.verify")%></td><td><input type="password" name="p2"></td></tr>
<tr><td colspan="2"><input type="submit" value="<%=W.w("change.password")%>"></td></tr>
<tr><td colspan="2"><%= errMsg %></td></tr></table>
</form>
<a href="./userlist.jsp"><%=W.w("user.list")%></a>
  </td></tr></table></center>
</body>
</html>  