<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.diodesoftware.scb.ClipSession" %>
<%@ page import="com.diodesoftware.scb.email.EmailMgr" %>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.diodesoftware.scb.sysop.SysopSession" %>
<%!

    private static Logger log = Logger.getLogger("jsp.admin.email");
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
    String from = request.getParameter("from");
    String subject = request.getParameter("subject");
    String message = request.getParameter("content");
    boolean test = !"yes".equals(request.getParameter("all"));
    String userMsg = "";
    if (from != null) {


            EmailMgr.getInstance().sendToAll(from, test, subject, message);
           userMsg = "Mass Email Sent";
    }

%>

<html>
<head>

</head>
<body>
<%= userMsg %>
<form action="email.jsp" method="post">
<table>
    <tr><td>From</td><td><input type="text" name="from"></td></tr>
    <tr><td>Subject</td><td><input type="text" name='subject'></td></tr>
    <tr><td>Send to all</td><td><input type="checkbox" name="all" value='yes'></td></tr>
    <tr><td>Message</td><td><textarea name="content"rows="10" cols="60"></textarea></td></tr>
    <tr><td colspan="2" align="right"><input type="submit" value="Send"></td> </tr>
</table>
    </form>
</body>
</html>