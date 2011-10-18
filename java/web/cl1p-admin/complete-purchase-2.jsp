<%@ page import="com.diodesoftware.scb.tables.UrlCredit" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.diodesoftware.scb.ClipSession" %>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.diodesoftware.scb.agents.UrlPurchaseAgent" %>
<%@ page import="com.diodesoftware.scb.ClipConfig" %>
<%@ page import="com.diodesoftware.R" %>
<%!
    private static Logger log = Logger.getLogger("jsp.complete-purchase-2");
%>
<%
    log.error("Porcessing complete-purchase-2.jsp");
    UrlCredit credit = UrlCredit.getCredit(request);
    if(credit == null){
        String msg = "cl1p-error 3. Token not found";
        %>
    <%= msg %>
<%
        log.error(msg);
        return;
    }
    ClipSession clipSession = ClipSession.getSession(request);
    if(!clipSession.isLoggedIn()){
        String msg = "cl1p-error 4. Not logged in";
        %>
<%= msg %>
<%
        log.error(msg);
        return;
    }
    DBConnectionMgr dbMgr = new DBConnectionMgr();
    Connection con = dbMgr.getConnection();
    try {
        UrlPurchaseAgent upa = new UrlPurchaseAgent();
        log.error("Sending to purchase agent");
        upa.completeCredit(credit, clipSession.getUser(), con);
%>
<html>
<head><title>Buy URL</title>
    <link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= R.T()%>">
</head>
<body>
<center>
    <table width="850" cellspacing="0" cellpadding="5">
        <tr>
            <td valign="top">

                <img src="/cl1p-inc-rgdm/images/cl1p_logo_small.jpg" alt="cl1p the internet clipboard"><br>


                <p class="buyLi">Puchase of <b>http://cl1p.net<%= credit.getUri() %></b> complete!</p>
                <p class="buyLi">
                    Thank you for your purchase.</p><br>
                    <a href="http://cl1p.net<%= credit.getUri() %>">http://cl1p.net<%= credit.getUri() %></a>

</td>
        </tr>
    </table>
    </center>

<%
    if (ClipConfig.CL1P_SITE) {
%>
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("UA-100555-2");
pageTracker._initData();
pageTracker._trackPageview();
</script>
<%
    }
%>
</body>
</html>

<%  }catch(Throwable t){
            log.error("Error in complete pruchase 2", t);
    }finally{
        dbMgr.returnConnection(con);
    }
%>