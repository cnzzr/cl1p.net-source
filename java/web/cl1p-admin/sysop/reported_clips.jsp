
<%@ page import="java.util.List" %>
<%@ page import="com.diodesoftware.scb.tables.ReportedClip" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.text.DateFormat" %><%

    DateFormat df = DateFormat.getDateTimeInstance();
    String handle = request.getParameter("handle");
    List reportedClips = new ArrayList();
    DBConnectionMgr dbMgr = new DBConnectionMgr();
    Connection con = dbMgr.getConnection();
    try{
        if(handle != null){
            ReportedClip.handle(Integer.parseInt(handle),con);
        }
        reportedClips = ReportedClip.listUnhandled(con);
    }finally{
        dbMgr.returnConnection(con);
    }
%>
<html>
<body>
<%
    for(int i = 0; i < reportedClips.size(); i++){
        ReportedClip rc = (ReportedClip)reportedClips.get(i);
        %>
<%= df.format(rc.getCreated().getTime())%><a href="<%= rc.getUri()%>"><%= rc.getUri()%></a>&nbsp;<a href="reported_clips.jsp?handle=<%= rc.getNumber()%>">handle</a><br/>
<%
    }
%>
</body>
</html>