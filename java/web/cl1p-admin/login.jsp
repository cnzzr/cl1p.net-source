<%@ page import="com.diodesoftware.scb.admin.CreateAccountPage"%>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="com.diodesoftware.scb.agents.UserAgent"%>
<%@ page import="com.diodesoftware.scb.agents.CookieAgent"%>
<%@ page import="com.diodesoftware.scb.ClipUtil"%>
<%@ page import="com.diodesoftware.scb.ClipSession"%>
<%@ page import="com.diodesoftware.scb.admin.MenuPage"%>
<%@ page import="com.diodesoftware.scb.tables.User"%>
<%@ page import="com.diodesoftware.scb.email.EmailMgr"%>
<%@ page import="org.apache.log4j.Logger"%>
<%@ page import="com.diodesoftware.scb.ClipConfig" %>
<%@ page import="com.diodesoftware.R" %>
<%!

    private static Logger log  = Logger.getLogger("jsp.admin.create");
%>
<%
    DBConnectionMgr dbConnectionMgr = new DBConnectionMgr();
    Connection con = dbConnectionMgr.getConnection();
    ServletContext context = config.getServletContext();
    try {
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
                    MenuPage mp = new MenuPage();

                    String redirectURL = "/cl1p-admin/menu.jsp";
                    if (cl1pSession.getPurchaseUri() != null)
                        redirectURL = "/cl1p-admin/buy-url-2.jsp";
%>
<jsp:forward page="<%= redirectURL%>"/>
<%

                          return ;
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
                      if(request.getParameter("lcookie")!=null){
                          CookieAgent.getInstance().create(user.getNumber(), con, request, response);
                      }
                      cl1pSession.setUser(user);
                      ClipSession.getSession(request).setLoggedIn(true);
                      String redirectURL = "/cl1p-admin/menu.jsp";
                       if(cl1pSession.getPurchaseUri() != null)
                            redirectURL = "/cl1p-admin/buy-url-2.jsp";
                                                %>
<jsp:include page="<%= redirectURL%>"/>
<%
                return;
            }
        }
        String forgot = request.getParameter("forgot");
        if (forgot != null) {
            username = request.getParameter("username");
            User user = UserAgent.getInstance().load(username, con);
            if (user != null) {
                String emailTo = user.getEmail();
                if (emailTo != null) {
                    String newPassword = ClipUtil.genString(10);
                    user.setPassword(newPassword);
                    UserAgent.getInstance().save(user, con);
                    String subject = "cl1p.net account";
                    String message = "Your password has been reset to " + newPassword + ".";
                    try {
                        EmailMgr.getInstance().send("sys@cl1p.net", emailTo, subject, message, con);
                    } catch (Exception e) {
                        log.error("Error sending email", e);
                    }
                }
            }
        }
        //Page page = new Page(context.getRealPath(pagename));
        errorMsg = ClipUtil.blankNull(errorMsg);
        email = ClipUtil.blankNull(email);
        loginMsg = ClipUtil.blankNull(loginMsg);

        request.setAttribute("RGDM-USERNAME", username);
        request.setAttribute("RGDM-MESSAGE", errorMsg);
        request.setAttribute("RGDM-EMAIL", email);
        request.setAttribute("RGDM-LOGIN-MESSAGE", loginMsg);

    } finally {
        dbConnectionMgr.returnConnection(con);
    }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <title>cl1p.net The internet clipboard</title>
  <meta http-equiv="content-type"
 content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= R.T()%>">

</head>
<body>


<center>
    <table height="100%" width="850" cellspacing="0" cellpadding="5">
        <tr>
            <td valign="top">
                <img src="/cl1p-inc-rgdm/images/cl1p_logo_small.jpg" alt="cl1p the internet clipboard">
<br/><br/>
<div name="main">
	<table width="100%">
		<tr>
			<td valign="top">
				<div class="boxes" style="background-color: #9BD1FA;">
					<center>
<b class="rtop">
  <b class="r1"></b> <b class="r2"></b> <b class="r3"></b> <b class="r4"></b>
</b>  
    <table border="0" cellpadding="0" cellspacing="0">
    	<tr>
        	<td>
    		<h2>Existing users login.</h2>
            <p>Don't have an account. <a href="create.jsp">Create one now</a>!</p>
			<form method="post">
		    	<input type="hidden" name="login" value="yes">
    				<table>
    					<tr><td>Username:</td><td><input type="text" name="username"></td></tr>
       					<tr><td>Password:</td><td><input type="password" name="password"></td></tr>
        				<tr><td>Remember Me:</td><td><input type="checkbox" name="lcookie" value="yes"></td></tr>
        				<tr><td colspan="2"><input type="submit" id="loginButton" value="Login"></td></tr>
    				</table>
    				<%= request.getAttribute("RGDM-LOGIN-MESSAGE")%>
			</form>
   </td></tr></table>
<b class="rbottom">
  <b class="r4"></b> <b class="r3"></b> <b class="r2"></b> <b class="r1"></b>
</b> 
</center>
</div>
   </td><td valign="top">
<div class="boxes" style="background-color: #9BD1FA;">
<center>

<b class="rtop">
  <b class="r1"></b> <b class="r2"></b> <b class="r3"></b> <b class="r4"></b>
</b>  
   

    <table border="0" cellpadding="0" cellspacing="0">
    <tr>
        
        <td>
   <h2> Forgot your password?</h2>
<form method="post">
     <input type="hidden" name="forgot" value="yes">
    <table>
        <tr><td>Username:</td><td><input type="text" name="username"></td></tr>
        <tr><td colspan="2"><input type="submit" value="E-mail new password"></td></tr>
    </table>
    <%= request.getAttribute("RGDM-LOGIN-MESSAGE")%>
</form>
    </td></tr></table>
<b class="rbottom">
  <b class="r4"></b> <b class="r3"></b> <b class="r2"></b> <b class="r1"></b>
</b> 
</center>
</div>
</td></tr></table>
   </div>

<%
    if(ClipConfig.CL1P_SITE){
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

