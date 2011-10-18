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
<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="org.apache.ecs.xhtml.pre"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.diodesoftware.dbmapper.DBMapper"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.SortedMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>


<%@page import="com.diodesoftware.util.Pair"%>
<%@page import="com.diodesoftware.util.PairCompare"%>
<%@page import="com.diodesoftware.scb.agents.ClipAgent"%>
<%@page import="com.diodesoftware.scb.tables.Clip"%>
<%@page import="com.diodesoftware.scb.tables.User"%>
<html>
<body>
<form method="post">
URI <input type="text" name="uri">
<input type="hidden" name="getDetail" value="yes">
<input type="submit" value="Detail">
</form>
<%
	try{
		String msg = "";
		String uri = request.getParameter("uri");
		String getDeatil = request.getParameter("getDetail");
		String id = request.getParameter("i");
		Clip clip = null;
		User owner = null;
		if(getDeatil != null){
			clip = ClipAgent.getInstance().loadClip(uri, con);
			owner = (User)DBMapper.getInstance().load(User.class, clip.getOwnerId(),con);
		}
		if(id != null){
			clip = (Clip)DBMapper.getInstance().load(Clip.class, Integer.parseInt(id),con);
			owner = (User)DBMapper.getInstance().load(User.class, clip.getOwnerId(),con);
		}
		String removePassword = request.getParameter("removePassword");
		if(removePassword != null){
			int clipId = Integer.parseInt(removePassword);
			String sql = "Update Clip set Password = '' and ViewPassword = 'N' where Number = ?";
			try{
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, clipId);
				prepStmt.executeUpdate();
				prepStmt.close();
			}catch(SQLException e){
				%>
				<%= e.getMessage() %>
				<%
			}
		}
%>
<%= msg %><br/></br>
<%
		
		if(clip != null){
			%>
<br/>

Clip Details<br/>
Number: <%= clip.getNumber() %><br/>
Uri: <%= clip.getUri() %><br/>
Title: <%= clip.getTitle() %>
Type: <%= clip.getClipType() %>
Type Id: <%= clip.getClipTypeId() %>
Password: <%= clip.getPassword()!=null %>
View Password: <%= clip.getViewPassword() %> <a href="urlDetail.jsp?removePassword=<%= clip.getNumber() %>">Remove password</a><br/>
			<%
			
		}
		if(owner != null){
			%>
Owner<br/>
User Id: <%= owner.getNumber() %></br>
User Name: <%= owner.getUsername() %></br>
Email: <%= owner.getEmail() %></br>
			<%
		}
		
	}finally{
		dbMgr.returnConnection(con);
	}	
%>    



</body>
</html>    
<%!

%>
