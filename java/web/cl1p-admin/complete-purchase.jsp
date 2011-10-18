<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.diodesoftware.scb.tables.UrlCredit" %>
<%@ page import="com.diodesoftware.scb.agents.UrlPurchaseAgent" %>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.diodesoftware.scb.ClipSession" %>
<%@ page import="com.diodesoftware.scb.admin.MenuPage" %>
<%@ page import="com.diodesoftware.scb.agents.CookieAgent" %>
<%@ page import="com.diodesoftware.scb.tables.User" %>
<%@ page import="com.diodesoftware.scb.agents.UserAgent" %>
<%@ page import="com.diodesoftware.scb.ClipUtil" %>
<%@ page import="com.diodesoftware.scb.ClipConfig" %>
<%@ page import="com.diodesoftware.R" %>
<%!
    private Logger log = Logger.getLogger("jsp.complete-purchase");
%>
<%
    request.setAttribute("RGDM-MESSAGE","");
    request.setAttribute("RGDM-MESSAGE","");
    DBConnectionMgr dbMgr = new DBConnectionMgr();
    Connection con = dbMgr.getConnection();
    try {
        UrlCredit credit = UrlCredit.getCredit(request);
        if (credit == null) {
            log.debug("Checking for token");
            String token = request.getParameter("t");
            if (token == null || token.trim().length() == 0) {
                String msg = "Error cl1p-1. No token found.";
%>
<%= msg %>
<%
                log.warn(msg);
                return;
            }

            token = token.trim();
            UrlPurchaseAgent upa = new UrlPurchaseAgent();
            credit = upa.loadCredit(token, con);
            if (credit == null) {
                String msg = "Error cl1p-2. No token found";
%>
<%= msg %>
<%
            log.debug(msg);
            return;
        }
        UrlCredit.setCredit(request, credit);
        log.debug("Token found.");
    }
    log.debug("Has Token");
    // Login Logic
    String errorMsg = null;
    String run = request.getParameter("run");
    UserAgent userAgent = UserAgent.getInstance();
    CookieAgent cookieAgent = CookieAgent.getInstance();
    String username = null;
    String email = null;
    String pagename = "create.html";
    if (run != null) {
        username = request.getParameter("username");
        String password = request.getParameter("password");
        String passwordVerify = request.getParameter("passwordVerify");
        email = request.getParameter("email");
        if (ClipUtil.isBlank(username)) {
            errorMsg = "Username required";
        } else if (ClipUtil.isBlank(password)) {
            errorMsg = "Password is required";
        } else if (!password.equals(passwordVerify)) {
            errorMsg = "Passwords don't match";
        }
        if (errorMsg == null) {
            int userId = userAgent.createUser(username, password, email, con);
            if (userId == -1) {
                errorMsg = "Username is already in use";
            } else {
                ClipSession cl1pSession = ClipSession.getSession(request);
                cl1pSession.setUser(userAgent.load(userId, con));
                ClipSession.getSession(request).setLoggedIn(true);

                String redirectURL = "/cl1p-admin/complete-purchase-2.jsp";
                log.debug("Account Created Done. Reditecting to [" + redirectURL + "]");
%>
   <jsp:forward page="<%= redirectURL%>"/>
   <%

                   return;
               }
           }
       }
       String loginMsg = null;
       String login = request.getParameter("login");
       if (login != null) {
           User user = UserAgent.getInstance().login(request.getParameter("username"),
                   request.getParameter("password"), con);
           if (user == null) {
               loginMsg = "Invalid login";
           } else {
               ClipSession cl1pSession = ClipSession.getSession(request);
               if (request.getParameter("lcookie") != null) {
                   CookieAgent.getInstance().create(user.getNumber(), con, request, response);
               }
               cl1pSession.setUser(user);
               ClipSession.getSession(request).setLoggedIn(true);


               String redirectURL = "/cl1p-admin/complete-purchase-2.jsp";
               log.debug("Login Done. Reditecting to [" + redirectURL + "]");
   %>
   <jsp:include page="<%= redirectURL%>"/>
   <%
                return;
            }
        }
	if(errorMsg==null)errorMsg="";
	if(loginMsg==null)loginMsg="";
       log.debug("Diplaying login page");
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


                <p class="buyLi">Complete purchase of <b>http://cl1p.net<%= credit.getUri() %></p>
                <p class="buyLi">
                    To complete this purchase you must Create or Log into a cl1p account.
                    This account will be used to access the advanced features of your cl1p.
</p>
<table><tr><td valign="top">
<h3>Create an account</h3>

<form method="post">
    <input type="hidden" name="run" value="yes"/>
    <table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td class="signupTop"></td></tr><tr>
        <td class="signupMid" align="center">
    <table>
        <tr><td>Username</td><td><input type="text" name="username"></td></tr>
        <tr><td>Password</td><td><input type="password" name="password" maxlength="15"></td></tr>
        <tr><td>Password Verify</td><td><input type="password"  name="passwordVerify" maxlength="15"></td></tr>
        <tr><td>E-mail:</td><td><input type="text" name="email"></td></tr>
        <tr><td><input type="submit" value="Create"></td></tr>
    </table>
    <%= errorMsg %>
    </td></tr><tr>
    <td class="signupBottom"></td>
    </tr>
    </table>
</form>
<br>
</td><td valign="top">
    <h3>Existing users login</h3>
<form method="post">
    <input type="hidden" name="login" value="yes">
    <table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td class="signupTop"></td></tr><tr>
        <td class="signupMid" align="center">

    <table><tr><td>Username:</td><td><input type="text" name="username"></td></tr>
       <tr><td>Password:</td><td><input type="password" name="password"></td></tr>
        <tr><td>Remember Me:</td><td><input type="checkbox" name="lcookie" value="yes"></td></tr>
        <tr><td colspan="2"><input type="submit" value="Login"></td></tr>
    </table>
    <%= loginMsg %>
    </td></tr><tr>
    <td class="signupBottom"></td>
    </tr>
    </table>
</form>
</td></tr></table>
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
    }catch(Exception e){
        log.error("Error in complete purchase", e);
    } finally {
        dbMgr.returnConnection(con);
    }
%>
