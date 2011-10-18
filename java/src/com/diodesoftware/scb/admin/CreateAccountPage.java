package com.diodesoftware.scb.admin;

import com.diodesoftware.scb.ClipUtil;
import com.diodesoftware.scb.ClipSession;
import com.diodesoftware.scb.email.EmailMgr;
import com.diodesoftware.scb.tables.User;
import com.diodesoftware.scb.agents.UserAgent;
import com.diodesoftware.scb.agents.CookieAgent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Jul 3, 2006
 * Time: 5:58:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateAccountPage extends AdminPage {

    protected String fileName;
    private static Logger log = Logger.getLogger(CreateAccountPage.class);

    public String doPage(HttpServletRequest request,
                         HttpServletResponse response,
                         ServletContext context,
                         Connection con) {
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
                    ClipSession session = ClipSession.getSession(request);
                    session.setUser(userAgent.load(userId, con));
                    ClipSession.getSession(request).setLoggedIn(true);
                    MenuPage mp = new MenuPage();
            String s =  mp.doPage(request, response, context, con);
            //String redirectURL = "http://cl1p.net/cl1p-admin/accountCreated";
            //session.setRedirect(redirectURL);
                    return s;
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
                ClipSession session = ClipSession.getSession(request);
                if(request.getParameter("lcookie")!=null){
                    CookieAgent.getInstance().create(user.getNumber(), con, request, response);
                }
                session.setUser(user);
                ClipSession.getSession(request).setLoggedIn(true);
                MenuPage mp = new MenuPage();
                return mp.doPage(request, response, context, con);
            }
        }
        String forgot = request.getParameter("forgot");
        if(forgot != null){
            username = request.getParameter("username");
            User user = UserAgent.getInstance().load(username, con);
            if(user != null){
                String emailTo = user.getEmail();
                if(emailTo != null){
                    String newPassword = ClipUtil.genString(10);
                    user.setPassword(newPassword);
                    UserAgent.getInstance().save(user,con);
                    String subject = "cl1p.net account";
                    String message = "Your password has been reset to " + newPassword + ".";
                    try{
                        EmailMgr.getInstance().send("sys@cl1p.net", emailTo, subject, message, con);
                    }catch(Exception e){
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

        return null;
    }
}
