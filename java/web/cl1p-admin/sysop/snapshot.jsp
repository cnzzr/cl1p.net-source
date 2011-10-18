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
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
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
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.text.DateFormat" %>
<html>
<head>
     <link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= com.diodesoftware.R.T()%>">
</head>
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
    try {
        if (startString != null && endString != null) {
            Calendar startCal = Calendar.getInstance();
            startCal.setTimeInMillis(Long.parseLong(startString));
            Calendar endCal = Calendar.getInstance();
            endCal.setTimeInMillis(Long.parseLong(endString));
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);

            String sql = "Select * From Clip where LastEdit between ? and ?";
            try {
                PreparedStatement prepStmt = con.prepareStatement(sql);
                prepStmt.setLong(1, SQLUtil.calendarToLong(startCal));
                prepStmt.setLong(2, SQLUtil.calendarToLong(endCal));
                ResultSet rs = prepStmt.executeQuery();
                while (rs.next()) {

                }
            } catch (SQLException e) {
                log.error("Error run ning SQL [" + sql + "]", e);
            }
            Iterator iter = null;
            List map = null;
            DateFormat df = DateFormat.getDateTimeInstance();
%>

    <br>
    Report run from <%= df.format(startCal.getTime())%> to <%= df.format(endCal.getTime())%> 
    <br/><h3>Hits</h3>
Hits:<%= hits(startCal, endCal, con) %><br>
    <%
        List list = referingSources(startCal, endCal, con);
        request.setAttribute("refs", list);
    %>
    <h3>Refering Sources</h3>
    <display:table name="refs" pagesize="5" class="clipTable" id="pair">
        <display:setProperty name="paging.banner.placement" value="bottom" />
        <display:column property="v" title="Count"/>
        <display:column property="s" title="Ref"/>
    </display:table>
    <%
        list = clips(startCal, endCal, con);
        request.setAttribute("clips", list);
    %>
    <h3>Active cl1ps</h3>
    <display:table name="clips" pagesize="5" class="clipTable" id="pair">
        <display:setProperty name="paging.banner.placement" value="bottom" />
        <display:column property="v" title="Count"/>
        <display:column title="cl1p">
            <% Pair p = (Pair)pageContext.getAttribute("pair");%>
            <a href="<%= p.s %>"><%= p.s %></a>
        </display:column>
    </display:table>
    <%
        list = ips(startCal, endCal, con);
        request.setAttribute("ips", list);
    %>
    <h3>Active IP'ss</h3>
    <display:table name="ips" pagesize="5" class="clipTable" id="pair">
        <display:setProperty name="paging.banner.placement" value="bottom" />
        <display:column property="v" title="Count"/>
        <display:column title="IP">
            <% Pair p = (Pair)pageContext.getAttribute("pair");%>
            <%= p.s %> <a href="http://ws.arin.net/cgi-bin/whois.pl?queryinput=<%= URLEncoder.encode(p.s, "utf-8")%>">IP Info</a>
        </display:column>
    </display:table>
			<%
		}
	}finally{
		dbMgr.returnConnection(con);
	}	
%>    


</table>
</form>
</body>
</html>    
<%!
static int hits(Calendar start, Calendar end, Connection con){
	int result = 0;	
	String sql = "Select sum(HitCount) from UserVisit where VisitStart between ? and ?";
	try{
		PreparedStatement prepStmt = con.prepareStatement(sql);
		populateStatement(prepStmt, start, end);
		ResultSet rs = prepStmt.executeQuery();
		while(rs.next()){
			result += rs.getInt(1);	
		}
		rs.close();
		prepStmt.close();
	}catch(SQLException e){
		log.error("Error running SQL [" + sql + "]",e);
	}
	return result;		
}

static List referingSources(Calendar start, Calendar end, Connection con){
	List result = new ArrayList();		
	String sql = "Select Ref, Count(Ref) from UserVisit where VisitStart between ? and ? group by Ref";
	try{
		PreparedStatement prepStmt = con.prepareStatement(sql);
		populateStatement(prepStmt, start, end);
		ResultSet rs = prepStmt.executeQuery();
		while(rs.next()){
			result.add(new Pair(rs.getString(1),rs.getInt(2)));
		}
		rs.close();
		prepStmt.close();
	}catch(SQLException e){
		log.error("Error running SQL [" + sql + "]",e);
	}
	Collections.sort(result, new PairCompare());
	return result;
}

static DBMapper dbMapper = DBMapper.getInstance();

static List clips(Calendar start, Calendar end, Connection con){
	List result = new ArrayList();		
	String sql = "Select c.Uri, Count(u.ClipId) from UserVisitHit u inner join Clip c on c.Number = u.ClipId where HitDate between ? and ? group by ClipId";
	try{
		PreparedStatement prepStmt = con.prepareStatement(sql);
		populateStatement(prepStmt, start, end);
		ResultSet rs = prepStmt.executeQuery();
		while(rs.next()){
			String clipUri = rs.getString(1);
			int count = rs.getInt(2);
			result.add(new Pair(clipUri, count));			
		}
		rs.close();
		prepStmt.close();
	}catch(SQLException e){
		log.error("Error running SQL [" + sql + "]",e);
	}
	Collections.sort(result, new PairCompare());
	return result;
}

static List ips(Calendar start, Calendar end, Connection con){
	List result = new ArrayList();		
	String sql = "Select Ip, Count(Ref) from UserVisit where VisitStart between ? and ? group by Ip";
	try{
		PreparedStatement prepStmt = con.prepareStatement(sql);
		populateStatement(prepStmt, start, end);
		ResultSet rs = prepStmt.executeQuery();
		while(rs.next()){
			result.add(new Pair(rs.getString(1), rs.getInt(2)));
		}
		rs.close();
		prepStmt.close();
	}catch(SQLException e){
		log.error("Error running SQL [" + sql + "]",e);
	}
	Collections.sort(result, new PairCompare());
	return result;
}

static void populateStatement(PreparedStatement prepStmt, Calendar start, Calendar end)
	throws SQLException
{
	prepStmt.setLong(1, SQLUtil.calendarToLong(start));
	prepStmt.setLong(2, SQLUtil.calendarToLong(end));
}


%>
