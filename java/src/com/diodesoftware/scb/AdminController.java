package com.diodesoftware.scb;

import com.diodesoftware.scb.admin.*;
import com.diodesoftware.scb.tables.User;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.sql.Connection;
import java.io.IOException;

import org.apache.log4j.Logger;


public class AdminController {

    public static final String ADMIN_BASE_URI = "/cl1p-admin/";
    private static Logger log = Logger.getLogger(AdminController.class);

    public AdminController() {
    }

    public void doAdmin(ClipRequest request, HttpServletResponse response,
                        ServletContext context,
                        Connection con) {
        // Figure out reqested page

        ClipSession session = ClipSession.getSession(request.getRequest());
        // Logged in ?
        AdminPage page = null;
        if (session.isLoggedIn()) {
            // Yes go to requested page
            page = requestedPage(request);
        } else {
            // No Display sign-up / login page
            page = new CreateAccountPage();
        }
        String html = page.doPage(request.getRequest(), response, context, con);
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            response.getOutputStream().println(html);
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("Error writing afmin page", e);
        }
    }

    private static AdminPage requestedPage(ClipRequest request) {
        String uri = request.getUri();
        String pageUri = uri.substring(ADMIN_BASE_URI.length());
        AdminPage result = new MenuPage();
        if(pageUri.equals("list")){
            result = new ListClipPage();
        }else if(pageUri.equals("editAccount")){
            result = new EditAccountPage();
        }


        return result;
    }

    public static String getClipLoginText(ClipSession session, HttpServletRequest request){
        StringBuffer sb = new StringBuffer();
        if(session.isLoggedIn()){
            User user = session.getUser();
            sb.append("User: ").append(session.getUser().getUsername());
            sb.append("&nbsp;&nbsp;<a  href='/cl1p-admin/menu.jsp'>Menu</a> ");
            if(!session.isPro()){
                // Disabled for now
                //sb.append("<br><b><a href='/cl1p-admin/upgrade.jsp'>Upgrade to cl1p PRO</a></b><br>");
	        }
            sb.append("<a href='#' onclick='javascript:document.logout.submit()'>Logout</a>");
        }else{
            String msg = (String)request.getAttribute("login_message");


                sb.append("<div id='loginAsk' ");
                if(msg != null)sb.append("style='display:none;'");
                sb.append(">");
              //  sb.append("&nbsp;&nbsp;<a onclick=\"hide('loginAsk');show('loginWindow');\">Login</a>");
            sb.append("&nbsp;&nbsp;<a href=\"/cl1p-admin/login.jsp\">Login</a>");
                sb.append("&nbsp;<a href='").append(ADMIN_BASE_URI).append("create.jsp'>Create account</a>");
                sb.append("</div>");


            sb.append("<div id='loginWindow'");
            if(msg == null)sb.append("style='display:none;'");
                sb.append(">");
            sb.append("<table><tr>");
            sb.append("<td>Username</td><td><input type='text' name='").append(HtmlParam.LOGIN_USERNAME).append("'></td>");
            sb.append("<td>Remember Me</td><td><input  type='checkbox' name='").append(HtmlParam.LOGIN_COOKIE).append("' value='yes'></td>");
            sb.append("</tr><tr>");
            sb.append("<td>Password</td><td><input type='password' name='").append(HtmlParam.LOGIN_PASSWORD).append("'></td>");


            if(msg != null){
            	request.setAttribute("errorMessage", msg);
            }

            sb.append("<td><input  type='submit' value='login' id='loginButton'></td></tr>");
            if(msg != null)
                sb.append("<tr><td colspan='2'>").append(msg).append("</td></tr>");

            sb.append("</table></div>");
        }
        return sb.toString();
    }

    public static String getClipLoginText(ClipRequest request){
        ClipSession session = ClipSession.getSession(request.getRequest());
        return getClipLoginText(session, request.getRequest());

    }
}
