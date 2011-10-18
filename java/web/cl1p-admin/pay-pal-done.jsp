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

    DBConnectionMgr dbMgr = new DBConnectionMgr();
    Connection con = dbMgr.getConnection();
    try {
       ClipSession clipSession = ClipSession.getSession(request);
       String uri = clipSession.getPurchaseUri();
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


                <p class="buyLi">Thank you for buying <b>http://cl1p.net<%= uri %></b>. </p>
                <p class="buyLi">You will receive an email shortly with additional instructions. 
                   </p>

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

<%
    }finally{
        dbMgr.returnConnection(con);
    }
%>