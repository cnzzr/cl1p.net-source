<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.*" %>
<html>
<head>
</head>
<body>
<h4>Unused Tokens</h4>
<table>
<tr><th>Email</th><th>URL</th></tr>
<%
        DBConnectionMgr dbMgr = new DBConnectionMgr();
        String sql = "select Email, Uri from UrlCredit where Used = 'N'";
        Connection con = dbMgr.getConnection(); 
        try{
        	PreparedStatement prepStmt = con.prepareStatement(sql);
        	ResultSet rs = prepStmt.executeQuery();
        	while(rs.next()){
        		%>
<tr><td><%= rs.getString(1) %></td><td><%= rs.getString(2) %></td></tr>
<%
        	}
        	rs.close();
        	prepStmt.close();
        }catch(Exception e){
        	throw new RuntimeException(e);
        }finally{
        	dbMgr.returnConnection(con);
        }
        %>
</table>        
       
</body>
</html>