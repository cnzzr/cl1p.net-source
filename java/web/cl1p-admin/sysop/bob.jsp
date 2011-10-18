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
    startCal.add(Calendar.DAY_OF_MONTH, -1);
    Calendar endCal = Calendar.getInstance();
    long start = SQLUtil.calendarToLong(startCal);
    long end = SQLUtil.calendarToLong(endCal);
    try {
%>

<%@page import="com.diodesoftware.scb.tables.Clip"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.diodesoftware.scb.tables.ClipS3Object"%>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.diodesoftware.scb.tables.Bob" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<html>
<head>

</head>
<body>

    <%


        String sql = "select * from Bob where Visit > " +start + " and Visit < " + end  + " order by Uri";
        PreparedStatement prepStmt = con.prepareStatement(sql);
        //prepStmt.setLong(1, start);
        //prepStmt.setLong(2, end);
        ResultSet rs =prepStmt.executeQuery();
        DateFormat df = DateFormat.getDateTimeInstance();
        int hitCount = 0;
        int uriCount = 0;
        String lastUri = null;
        Set<String> ipSet = new HashSet<String>();
        Set<String> uaSet = new HashSet<String>();
        Set<String> sessionSet = new HashSet<String>();

        ResultCount ipAddress = new ResultCount();
        ResultCount userAgent = new ResultCount();
        ResultCount sessionID = new ResultCount();

        while(rs.next()){
        	Bob bob = (Bob)DBMapper.loadSingle(Bob.class, rs);
            if(!bob.getUri().equals(lastUri))
            {
                uriCount++;
                if(lastUri == null)
                {
                    lastUri = bob.getUri();
                }
                else
                {
                    ipAddress.increment(ipSet.size());
                    userAgent.increment(uaSet.size());
                    sessionID.increment(sessionSet.size());
                    ipSet.clear();
                    uaSet.clear();
                    sessionSet.clear();
                }
                lastUri = bob.getUri();
            }
            hitCount++;
            ipSet.add(bob.getIp());
            uaSet.add(bob.getUserAgent());
            sessionSet.add(bob.getSessionId());
        }
        ipAddress.increment(ipSet.size());
        userAgent.increment(uaSet.size());
        sessionID.increment(sessionSet.size());
        rs.close();
        prepStmt.close();
 %>
<body>
Hits <%= hitCount%><br/>
URI <%= uriCount %><br/>
<table>
    <tr><th>URI Count</th><th>IP address</th><th>User Agent</th><th>Session ID</th></tr>
    <tr><td>10+</td><td><%= ipAddress.tenPlus%></td><td><%= userAgent.tenPlus%></td><td><%= sessionID.tenPlus%></td></tr>
    <tr><td>5-9</td><td><%= ipAddress.fiveToNine%></td><td><%= userAgent.fiveToNine%></td><td><%= sessionID.fiveToNine%></td></tr>
    <tr><td>4</td><td><%= ipAddress.four%></td><td><%= userAgent.four%></td><td><%= sessionID.four%></td></tr>
    <tr><td>3</td><td><%= ipAddress.three%></td><td><%= userAgent.three%></td><td><%= sessionID.three%></td></tr>
    <tr><td>2</td><td><%= ipAddress.two%></td><td><%= userAgent.two%></td><td><%= sessionID.two%></td></tr>
    <tr><td>1</td><td><%= ipAddress.one%></td><td><%= userAgent.one%></td><td><%= sessionID.one%></td></tr>

</table>
</body>
</html>
<%
    } finally {
        dbCon.returnConnection(con);
    }
%>
<%!


    class ResultCount
    {
        int tenPlus;
        int fiveToNine;
        int four;
        int three;
        int two;
        int one;

        public void increment(int i)
        {
            if(i > 9)
            {
                tenPlus++;
            }else if(i >4)
            {
                fiveToNine++;
            }else if(i == 4)
            {
                four++;
            }else if(i == 3)
            {
                three++;
            }else if(i == 2)
            {
                two++;
            }else if(i == 1)
            {
                one++;
            }

        }
    }

	private String trim(String s)
	{
		if(s == null)return null;
		if(s.length() < 20)
			return s;
		return s.substring(0,20);
	}
%>
