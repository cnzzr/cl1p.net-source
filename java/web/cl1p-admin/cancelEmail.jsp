<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.diodesoftware.scb.tables.CancelEmail" %>
<%@ page import="com.diodesoftware.dbmapper.DBMapper" %>
<%@ page import="com.diodesoftware.scb.tables.User" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.sql.SQLException" %>
<%
    Logger emailLog = Logger.getLogger("email");
%>
<%

    boolean canceled = false;
    String value = request.getParameter("v");
    emailLog.info("Canceling email Value [" + value + "]");
    DBConnectionMgr dbCon = new DBConnectionMgr();
    Connection con = dbCon.getConnection();
    DBMapper mapper = DBMapper.getInstance();
    if("go".equals(request.getParameter("go"))){
    try {
        String sql = "Select * from CancelEmail where Value = ?";
        try {
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1, value);
            ResultSet rs = prepStmt.executeQuery();
            CancelEmail cancelEmail = null;
            while (rs.next()) {
                cancelEmail = (CancelEmail) mapper.loadSingle(CancelEmail.class, rs);
            }
            rs.close();
            prepStmt.close();
            if (cancelEmail != null) {
                emailLog.info("Cancling meial for User [" + cancelEmail.getUserId() + "]");
                User u = (User) mapper.load(User.class, cancelEmail.getUserId(), con);
                if (u != null) {
                    u.setNoEmail(true);
                    mapper.save(u, con);
                    emailLog.info("Email canceled");
                }
            }
            canceled = true;
        } catch (SQLException e){
            emailLog.error("Error running SQL [" + sql + "]",e);
        }
    } finally {
        dbCon.returnConnection(con);
    }
    }
%>
<html>
<head></head>
<body>
<%
  if(canceled){
%>
<center>
    <p>Your e-mail has been removed from our newsletter. You will not receive any more emails from us.</p>
</center>
<%
    }else{
        %>
Click the button below to remove your e-mail from my newsletter.
<form action="cancelEmail.jsp" method="post">
    <input type="hidden" value="<%= request.getParameter("v")%>" name="v">
    <input type="hidden" name="go" value="go">
    <input type="submit" value="Don't send me another newsletter"/>

</form>
<%

    }
%>
</body>
</html>