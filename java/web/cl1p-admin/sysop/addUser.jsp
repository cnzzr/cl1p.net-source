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
<%
    ClipRequest clipRequest = ClipRequest.getClipRequest(request);
    String errMsg = "";
    String username = request.getParameter("username");
    String email = request.getParameter("email");
    String p1 = request.getParameter("p1");
    String p2 = request.getParameter("p2");
    if (username != null) {
        if (ClipUtil.isBlank(username)) {
            errMsg = Word.w("username.required");
        } else if (ClipUtil.isBlank(p1)) {
            errMsg = Word.w("password.required");
        } else if (!p1.equals(p2)) {
            errMsg = Word.w("password.dont.match");
        } else {
            int userId = UserAgent.getInstance().createUser(username, p1, email, clipRequest.getCon());
            if (userId == -1) {
                errMsg = Word.w("username.already.in.use");
                ;
            } else {
                errMsg = Word.w("username") + " [" + username + "] " + W.w("created");
                username = null;
                email = null;
                p1 = null;
                p2 = null;
            }
        }
    }
    email = ClipUtil.blankNull(email);
    username = ClipUtil.blankNull(username);
%>    
<%@page import="com.diodesoftware.scb.agents.UserAgent"%>
<%@page import="com.diodesoftware.scb.ClipUtil"%>
<%@page import="com.diodesoftware.scb.ClipRequest"%>
<%@page import="com.diodesoftware.Word"%>
<%@page import="com.diodesoftware.W"%>
<%@ page import="com.diodesoftware.R" %>
<html>
  <head>
  <title><%= W.w("add.user") %></title>
  <link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= R.T()%>">
  </head>
  <body>
  <center>
<table height="100%" cellspacing="0" cellpadding="0" width="875">
<tr>
<td valign="top">
  <h2><%= W.w("users") %></h2>
  <form method="post">
  <table>
  <tr><td><%= W.w("username") %></td><td><input type="text" name="username" value="<%= username %>"></td></tr>
  <tr><td><%= W.w("email") %></td><td><input type="text" name="email" value="<%= email %>"></td></tr>
  <tr><td><%= W.w("password") %></td><td><input type="password" name="p1"></td></tr>
  <tr><td><%= W.w("password.verify") %></td><td><input type="password" name="p2"></td></tr>
  <tr><td align="right"><input type="submit" value="<%= W.w("add") %>"></td></tr>  
  <tr><td><%= errMsg %></td></tr>  
  </table>
  </form>  
<a href="./userlist.jsp"><%= W.w("user.list") %></a>
  </td></tr></table></center>
</body>
</html>       