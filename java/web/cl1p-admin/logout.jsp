<%@ page import="com.diodesoftware.scb.ClipSession"%>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="com.diodesoftware.scb.agents.CookieAgent"%>
<%
    DBConnectionMgr dbConnectionMgr = new DBConnectionMgr();
    Connection con = dbConnectionMgr.getConnection();
    try{
        ClipSession.clearSession(request);
        CookieAgent.getInstance().clearCookie(request, response, con);
    }finally{
        dbConnectionMgr.returnConnection(con);
    }
%>
<jsp:forward page="login.jsp"/>