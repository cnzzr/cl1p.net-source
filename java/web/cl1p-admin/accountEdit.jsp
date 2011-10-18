<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.diodesoftware.scb.admin.CreateAccountPage" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.diodesoftware.scb.agents.UserAgent" %>
<%@ page import="com.diodesoftware.scb.ClipSession" %>
<%@ page import="com.diodesoftware.scb.tables.User" %>
<%@ page import="com.diodesoftware.scb.ClipUtil" %>
<%@ page import="com.diodesoftware.scb.SitePage.Page" %>
<%@ page import="com.diodesoftware.scb.ClipConfig" %>
<%@ page import="com.diodesoftware.R" %>
<%!

    private static Logger log = Logger.getLogger("jsp.admin.accountEdit");
%>
<%
    ClipSession cl1pSession = ClipSession.getSession(request);
    if (cl1pSession.isLoggedIn() == false) {
        String redirectURL = "/cl1p-admin/create.jsp";
%>
<jsp:forward page="<%= redirectURL%>"/>

<%
        return;
    }
    DBConnectionMgr dbConnectionMgr = new DBConnectionMgr();
    Connection con = dbConnectionMgr.getConnection();
    ServletContext context = config.getServletContext();
    try {
        String errorMsg = null;

        UserAgent userAgent = UserAgent.getInstance();

        String email = request.getParameter("email");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String newPasswordVerify = request.getParameter("newPasswordVerify");
        String goemail = request.getParameter("goemail");
        String gopassword = request.getParameter("gopassword");
        String emailMessage = null;
        String passwordMessage = null;
        if (goemail != null) {
            User user = cl1pSession.getUser();
            user.setEmail(email);
            userAgent.save(user, con);
            emailMessage = "E-mail changed to [" + email + "]";
        }
        if (gopassword != null) {
            if (ClipUtil.isBlank(oldPassword) || ClipUtil.isBlank(newPassword) || ClipUtil.isBlank(newPasswordVerify)) {
                passwordMessage = "All fields are requeired";
            }
            User user = cl1pSession.getUser();
            if (log.isDebugEnabled())
                log.debug("Loggin in as User [" + user.getUsername() + "] Password [" + oldPassword + "]");
            if (userAgent.login(user.getUsername(), oldPassword, con) == null) {
                passwordMessage = "Current password invalild";
            }
            if (passwordMessage == null) {
                if (!newPassword.equals(newPasswordVerify)) {
                    passwordMessage = "New Passwords don't match";
                }
            }
            if (passwordMessage == null) {
                user.setPassword(newPassword);
                userAgent.save(user, con);
                passwordMessage = "Password changed";
            }
        }
        emailMessage = ClipUtil.blankNull(emailMessage);
        email = cl1pSession.getUser().getEmail();
        email = ClipUtil.blankNull(email);
        passwordMessage = ClipUtil.blankNull(passwordMessage);


        request.setAttribute("RGDM-EMAIL-ADDRESS-RGDM", email);
        request.setAttribute("RGDM-EMAIL-MESSAGE-RGDM", emailMessage);
        request.setAttribute("RGDM-PASSWORD-MESSAGE-RGDM", passwordMessage);
        request.setAttribute("RGDM-USERNAME-RGDM", cl1pSession.getUser().getUsername());


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
                <img src="/cl1p-inc-rgdm/images/cl1p_logo.jpg" alt="cl1p the internet clipboard">

                <div name="main">
                    Edit Account: <%= request.getAttribute("RGDM-USERNAME-RGDM")%>
                    <br>
                    <a href="/cl1p-admin/menu.jsp">menu</a><br><br>
                    <table>
                        <form method="post">
                            <input type="hidden" name="goemail" value="yes">

                            <tr>
                                <td colspan="2">Change your email address</td>
                            </tr>


                            <tr>
                                <td>E-Mail:</td>
                                <td><input type="text" name="email"
                                           value="<%= request.getAttribute("RGDM-EMAIL-ADDRESS-RGDM")%>">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" align="right"><input type="submit" value="Update E-Mail"></td>
                            </tr>

                            <tr>
                                <td colspan="2"><%= request.getAttribute("RGDM-EMAIL-MESSAGE-RGDM")%>
                                </td>
                            </tr>
                        </form>
                        <form method="post">
                            <input type="hidden" name="gopassword" value="yes">

                            <tr>
                                <td colspan="2">Change your password</td>
                            </tr>

                            <tr>
                                <td>Current Password</td>
                                <td><input type="password" name="oldPassword" maxlength="15">
                                </td>
                            </tr>
                            <tr>
                                <td>New Password</td>
                                <td><input type="password" name="newPassword" maxlength="15"></td>
                            </tr>
                            <tr>
                                <td>Verify Password</td>
                                <td><input type="password" name="newPasswordVerify"
                                           maxlength="15"></td>
                            </tr>
                            <tr>
                                <td colspan="2" align="right"><input type="submit" value="Change Password"></td>
                            </tr>

                            <tr>
                                <td colspan="2"><%= request.getAttribute("RGDM-PASSWORD-MESSAGE-RGDM")%>
                                </td>
                            </tr>
                        </form>
                    </table>
                </div>
            </td>
        </tr>
    </table>
    <%

        if(ClipConfig.CL1P_SITE){   %>

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
