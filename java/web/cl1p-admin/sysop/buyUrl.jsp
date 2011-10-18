<%@ page import="com.diodesoftware.scb.sysop.SysopSession" %>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.diodesoftware.scb.sysop.SysopAgent" %>
<%@ page import="com.diodesoftware.scb.agents.FormException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    SysopSession sysopSession = SysopSession.getInstance(request);
    if (!sysopSession.isLoggedIn()) {
%>
<jsp:forward page="index.jsp"/>
<%
        return;
    }

    String error = "";
    String checkoutId = "";
    String completeMsg = "";
    String item = request.getParameter("item");
    String username = request.getParameter("username");
    String uri = request.getParameter("uri");
    String checkoutValue = request.getParameter("checkoutId");
    DBConnectionMgr dbMgr = new DBConnectionMgr();
    Connection con = dbMgr.getConnection();
    try {
        try {
            SysopAgent sa = new SysopAgent();
            if (username != null) {

                checkoutId = "" + sa.queueUrlToBuy(uri, username, con);

            }
            if(checkoutValue != null){
                sa.completeUrlPurchase(Integer.parseInt(checkoutValue), item, con, config.getServletContext());
            }
        } catch (FormException e) {
            error = e.getMsg();
        }
    } finally {
        dbMgr.returnConnection(con);
    }

%>
<html>
<head><title>BUY URL</title></head>
<body>
<a href="index.jsp">Menu</a><br>
<%= error %><br>

<form method="post">
    Get Checkout ID first<br>
    Username <input type="text" name="username"><br>
    URI cl1p.net<input type="text" name="uri"><br>
    <input type="submit" value="Get ID"><br>
    Checkout ID <%= checkoutId %><br>
</form>
<br>

<form method="post">
    Complete Purchase<br>
    Checkout ID <input type="text" name="checkoutId"><br>
    <select name="item">
        <option value="URL-1YR">URL-1YR</option>
        <option value="URL-5YR">URL-5YR</option>
        <option value="URL-10YR">URL-10YR</option>
    </select>
    <input type="submit" value="Complete transaction"></br>
    <%= completeMsg %>

</form>


</body>
</html>