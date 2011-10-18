
<%@page import="com.diodesoftware.scb.ClipConfig"%>
<%@page import="com.diodesoftware.scb.ClipContextListener"%>
<%

    String errMsg = "";
    String p1 = request.getParameter("p1");
    String p2 = request.getParameter("p2");
    try {
        if (p1 != null) {
            if (!p1.equals(p2)) {
                errMsg = "Passwords don't match";
            } else if (p1.trim().length() < 5) {
                errMsg = "Password must be longer then 5 characters";
            } else {
                ClipConfig.getInstance().loadProperties();
                ClipContextListener.init(session.getServletContext());
                com.diodesoftware.scb.filter.ClipFilter.init(session.getServletContext());
                p1 = p1.trim();
                p2 = p2.trim();

                DBConnectionMgr db = new DBConnectionMgr();
                Connection con = db.testConnection(ClipConfig.DB_URL, ClipConfig.DB_NAME, ClipConfig.DB_USERNAME, ClipConfig.DB_PASSWORD);
                try {

                    SysopLogin sysop = (SysopLogin) DBMapper.load(SysopLogin.class, 1, con);

                    if (sysop == null)
                        sysop = new SysopLogin();

                    sysop.setPassword(p1);

                    DBMapper.save(sysop, con);
                    errMsg = "Sysop Password Changed.<br><a href='/cl1p-admin/sysop/'>Click here to log in to Sysop</a>";
                } finally {
                    db.returnConnection(con);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        errMsg = e.getMessage();
    }
%>
<%@page import="com.diodesoftware.scb.ClipRequest"%>
<%@page import="com.diodesoftware.dbmapper.DBMapper"%>
<%@page import="com.diodesoftware.scb.sysop.SysopLogin"%>
<%@page import="com.diodesoftware.dbmapper.DBConnectionMgr"%>
<%@page import="java.sql.Connection"%>
<%@ page import="com.diodesoftware.scb.filter.ClipFilter" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" media="screen, projection" href="style.css">
</head>
<body>
<h2>Set Sysop Password</h2>
<form method="post">
<table>
<tr><td>Password</td><td><input type="password" name="p1"></td></tr>
<tr><td>Verify Password</td><td><input type="password" name="p2"></td></tr>
<tr><td colspan="2"><%= errMsg %></td></tr>
<tr><td colspan="2"><input type="submit" value="Set Root Password"></td></tr>
</table>
</form>

</body>
</html>