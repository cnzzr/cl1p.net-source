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
<%@page import="com.diodesoftware.scb.tables.EvalCustomer"%>
<html>
<head>
     <link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= com.diodesoftware.R.T()%>">
</head>
<body>
<%
DateFormat df = DateFormat.getDateTimeInstance();
	try{
		ArrayList<EvalCustomer> downloads = downloads(con);
		request.setAttribute("downloads",downloads);
%>




    <display:table name="downloads" pagesize="20" class="clipTable" id="pair">
        <display:setProperty name="paging.banner.placement" value="bottom" />
            <display:column title="Date">
            <% EvalCustomer p = (EvalCustomer)pageContext.getAttribute("pair");%>
            <%=  df.format(p.getEvalDate().getTime())%>
        </display:column>
                    <display:column title="Number">
            <% EvalCustomer p = (EvalCustomer)pageContext.getAttribute("pair");%>
            <%=  p.getNumber() %>
        </display:column>
                            <display:column title="Email">
            <% EvalCustomer p = (EvalCustomer)pageContext.getAttribute("pair");%>
            <%=  p.getEmail() %>
        </display:column>
                                    <display:column title="Name">
            <% EvalCustomer p = (EvalCustomer)pageContext.getAttribute("pair");%>
            <%=  p.getName() %>
        </display:column>
    </display:table> 
<%    
	}finally{
		dbMgr.returnConnection(con);
	}	
%>    



</body>
</html>    
<%!

private ArrayList<EvalCustomer> downloads(Connection con) throws SQLException{
	ArrayList<EvalCustomer> result = new ArrayList<EvalCustomer>();
	String sql = "Select * from EvalCustomer order by EvalDate";
	PreparedStatement prepStmt = con.prepareStatement(sql);
	ResultSet rs = prepStmt.executeQuery();
	while(rs.next()){
		result.add((EvalCustomer)DBMapper.loadSingle(EvalCustomer.class, rs));
	}
	rs.close();
	prepStmt.close();
	return result;
}


%>
