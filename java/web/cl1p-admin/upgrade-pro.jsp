<%@ page import="com.diodesoftware.scb.ClipSession"%>
<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_xclick">
<input type="hidden" name="business" value="cl1p@cl1p.net">
<input type="hidden" name="item_name" value="cl1p 1 year subscription">
<input type="hidden" name="item_number" value="cl1p-1y">
<input type="hidden" name="amount" value="19.95">
<input type="hidden" name="no_shipping" value="2">
<input type="hidden" name="return" value="http://javajax.org/cl1p-admin/paypal.jsp">
<input type="hidden" name="no_note" value="1">
<input type="hidden" name="currency_code" value="USD">
<input type="hidden" name="lc" value="US">
<input type="hidden" name="bn" value="PP-BuyNowBF">
<input type="hidden" name="custom" value="<%= ClipSession.getSession(request).getUser().getNumber() %>">
<input type="image" src="https://www.paypal.com/en_US/i/btn/x-click-butcc.gif" border="0" name="submit" alt="Make payments with PayPal - it's fast, free and secure!">
<img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
</form>


<%@ page import="com.diodesoftware.scb.agents.UserAgent"%>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="com.diodesoftware.scb.tables.User"%>
<%@ page import="com.diodesoftware.dbmapper.DBMapper"%>
<%
    String password = request.getParameter("password");
    if(!"fanda".equals(password)){
%>
<html>
<head></head>
<body>
<form method="post">
    <input type="password" name="password">
    <input type="submit" value="login">
</form>
</body>
</html>
<%
        return;
    }
    String msg = "";
    String username = request.getParameter("username");
    if(username != null){
        DBConnectionMgr dbConnectionMgr = new DBConnectionMgr();
        Connection con = dbConnectionMgr.getConnection();
        try{
            User user = UserAgent.getInstance().load(username, con);
            if(user == null){
                msg = "User [" + username + "] not found.";
            }else{
                user.setPro(true);
                DBMapper.getInstance().save(user,  con);
                msg = "User [" + username + "] upgraded to pro";
            }
        }finally{
            dbConnectionMgr.returnConnection(con);
        }
    }
%>
<html>
<head></head>
<body>
<form method="post">
    <input type="hidden" name="password" value="fanda">
    <table>
        <tr><td>Username:</td><td><input type="text" name="username"></td></tr>
        <tr><td colspan="2" align="right"><input type="submit" value="upgrade"></td></tr>
    </table>
    <%= msg %>
</form>
</body>
</html>
