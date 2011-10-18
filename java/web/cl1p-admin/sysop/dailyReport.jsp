<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="com.diodesoftware.dbmapper.DBMapper" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.diodesoftware.scb.sysop.SysopSession" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="com.diodesoftware.dbmapper.SQLUtil" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%
    SysopSession sysopSession = SysopSession.getInstance(request);
    if (!sysopSession.isLoggedIn()) {
%>
<jsp:forward page="index.jsp"/>

<%
        return;
    }

    DBConnectionMgr dbCon = new DBConnectionMgr();
    DBMapper mapper = DBMapper.getInstance();
    Connection con = dbCon.getConnection();
    Calendar startCal = Calendar.getInstance();
    startCal.set(Calendar.HOUR_OF_DAY, 0);
    startCal.set(Calendar.MINUTE, 0);
    startCal.set(Calendar.SECOND, 0);
    Calendar endCal = Calendar.getInstance();
    endCal.set(Calendar.HOUR_OF_DAY, 23);
    endCal.set(Calendar.MINUTE, 59);
    endCal.set(Calendar.SECOND, 59);
    long start = SQLUtil.calendarToLong(startCal);
    long end = SQLUtil.calendarToLong(endCal);
    try {
%>

<%@page import="com.diodesoftware.scb.tables.Clip"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.diodesoftware.scb.tables.ClipS3Object"%><html>
<head>

</head>
<body>
<table>
    <tr><td valign="top">
Last 100 URIS
<table border="1">
    <tr>
 <td>Number</td>       
 <td>URI</td>
 <td>Created</td>
 <td>Type</td>
 <td>Password</td>      
    </tr>


    <%


        String sql = "select * from Clip order by Number desc limit 100";
        PreparedStatement prepStmt = con.prepareStatement(sql);
        ResultSet rs =prepStmt.executeQuery();
        DateFormat df = DateFormat.getDateTimeInstance();
        while(rs.next()){
        	Clip clip = (Clip)DBMapper.loadSingle(Clip.class, rs);
    %>
    <tr>
 <td><%=  clip.getNumber()%></td>
<td><a href="<%= clip.getUri() %>"><%= trim(clip.getUri()) %></a></td>
<td><%= df.format(clip.getCreated().getTime()) %></td>
<td><%= clip.getClipType() %></td>
<td><%= clip.getViewPassword()  %></td>       
    </tr>
    <%
        }
        rs.close();
        prepStmt.close();
    %>
</table>
</td>
<td valign="top">
S3 Usage
<table border="1">
    <tr>
 <td>Number</td>       
 <td>Clip id</td>
 <td>Created</td>
 <td>bucket</td>
 <td>Download count</td>      
    </tr>


    <%


        sql = "select * from ClipS3Object order by Number desc limit 100";
        prepStmt = con.prepareStatement(sql);
        rs =prepStmt.executeQuery();
        
        while(rs.next()){
        	ClipS3Object clip = (ClipS3Object)DBMapper.loadSingle(ClipS3Object.class, rs);
    %>
    <tr>
 <td><%=  clip.getNumber()%></td>
<td><%= clip.getClipId() %></td>
<td><%= df.format(clip.getCreated().getTime()) %></td>
<td><%= clip.getBucket() %></td>
<td><%= clip.getDownloadCount()  %></td>       
    </tr>
    <%
        }
        rs.close();
        prepStmt.close();
    %>
</table>

</td>
</tr></table>
</body>
</html>
<%
    } finally {
        dbCon.returnConnection(con);
    }
%>
<%!
	private String trim(String s)
	{
		if(s == null)return null;
		if(s.length() < 20)
			return s;
		return s.substring(0,20);
	}
%>
