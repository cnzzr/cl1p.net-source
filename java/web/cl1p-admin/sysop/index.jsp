<%@ page import="com.diodesoftware.scb.sysop.SysopSession" %>
<%@page import="com.diodesoftware.W"%>
<%
    try{
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title><%=W.w("cl1p.sysop.menu") %></title>
  <link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= com.diodesoftware.R.T()%>">
  </head>
  <body>
  <center>
<table height="100%" cellspacing="0" cellpadding="0" width="875">
<tr>
<td valign="top">
  <%
      SysopSession sysopSession = SysopSession.getInstance(request);
      if(!sysopSession.isLoggedIn()){
  %>
  <h2><%=W.w("cl1p.sysop.menu") %></h2><br/>
  <%=W.w("login") %>
  <form method="post">
      <%=W.w("password") %>:<input type="password" name="password"><input type="submit" value="<%=W.w("login") %>">
  </form>
  <%
      }else{
         %>
  <h2><%=W.w("cl1p.sysop.menu") %></h2><br/>
    <a href="bob.jsp">Bob</a><br/>  
  <a href="settings.jsp"><%=W.w("settings") %></a><br/>
  <a href="changeRootPassword.jsp"><%=W.w("change.root.password") %></a><br/>
  <a href="userlist.jsp"><%=W.w("user.list") %></a><br/>
  <a href="genLicense.jsp">Gen License</a><br/>
    <a href="buyUrl.jsp">Buy URL</a><br>
  <a href="dailyReport.jsp">Daily Report</a><br>
  <a href="email.jsp">Mass Email</a><br>
  <a href="buyUrlToken.jsp">Buy Url Token</a><br>
  <a href="snapshot.jsp">Snapshot</a><br>
  <a href="urlDetail.jsp">URI Detail</a><br>
  <a href="unusedTokens.jsp">Unused Tokens</a><br>
  <a href="config.jsp">Config</a><br/>
    <a href="reported_clips.jsp">Reported</a><br/>
        <a href="imageClips.jsp">Images</a><br/>
  <%
      }
  %>
      </td></tr></table>
  </center>
  </body>
</html>
<%
    }catch(Exception e){
    	%>
<%= e.getMessage() %>    	
    	<%
    }
%>

