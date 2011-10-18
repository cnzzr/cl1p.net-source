<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.diodesoftware.scb.ClipSession" %>
<%@ page import="com.diodesoftware.scb.email.EmailMgr" %>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@page import="com.diodesoftware.scb.ClipUtil"%>
<%@page import="com.diodesoftware.dbmapper.SQLUtil"%>
<%@ page import="java.sql.*" %>
<%@page import="com.diodesoftware.scb.DateTimeTag"%>
<%@page import="java.util.Calendar"%>
<%@ page import="com.diodesoftware.scb.sysop.SysopSession" %>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
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
	DBConnectionMgr dbMgr = new DBConnectionMgr();
	Connection con = dbMgr.getConnection();
	String startString = request.getParameter("start");
	String endString = request.getParameter("end");
%>
<html>
<body>
<form method="post">
<table>
<tr><td>
<app:DateTime epoch="start"></app:DateTime>
</td><td>
<app:DateTime epoch="end"></app:DateTime>
</td></tr></table>
<input type="submit" value="run">
<table>

<%
	try{
		if(startString != null && endString != null){
			Calendar startCal = Calendar.getInstance();
			startCal.setTimeInMillis(Long.parseLong(startString));
			Calendar endCal = Calendar.getInstance();
			endCal.setTimeInMillis(Long.parseLong(endString));
			startCal.set(Calendar.HOUR_OF_DAY,0);
			startCal.set(Calendar.MINUTE, 0);
			endCal.set(Calendar.HOUR_OF_DAY,23);
			endCal.set(Calendar.MINUTE, 59);
			
			String sql = "Select * From Clip where LastEdit between ? and ?";
			try{
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setLong(1,SQLUtil.calendarToLong(startCal));
				prepStmt.setLong(2,SQLUtil.calendarToLong(endCal));
				ResultSet rs = prepStmt.executeQuery();
				while(rs.next()){
					%>
<tr><td><a href="http://cl1p.net<%= rs.getString("Uri") %>"><%= rs.getString("Uri") %></a></td></tr>
					<%
				}
			}catch(SQLException e){
				log.error("Error run ning SQL [" + sql +  "]",e);
			}
		}
	}finally{
		dbMgr.returnConnection(con);
	}	
%>    


</table>
</form>
</body>
</html>    