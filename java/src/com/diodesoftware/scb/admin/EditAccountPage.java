package com.diodesoftware.scb.admin;

import com.diodesoftware.scb.agents.UserAgent;
import com.diodesoftware.scb.ClipSession;
import com.diodesoftware.scb.ClipUtil;
import com.diodesoftware.scb.tables.User;
import com.diodesoftware.scb.SitePage.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Jul 17, 2006
 * Time: 7:42:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditAccountPage extends AdminPage{

    protected String fileName;
    private static Logger log = Logger.getLogger(EditAccountPage.class);

    public String doPage(HttpServletRequest request,
                         HttpServletResponse response,
                         ServletContext context,
                         Connection con) {
        String errorMsg = null;

        UserAgent userAgent = UserAgent.getInstance();
        ClipSession session = ClipSession.getSession(request);
        String email = request.getParameter("email");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String newPasswordVerify = request.getParameter("newPasswordVerify");
        String goemail = request.getParameter("goemail");
        String gopassword = request.getParameter("gopassword");
        String emailMessage = null;
        String passwordMessage = null;
        if(goemail != null){
            User user = session.getUser();
            user.setEmail(email);
            userAgent.save(user, con);
            emailMessage = "E-mail changed to [" + email + "]";
        }
        if(gopassword  != null){
             if(ClipUtil.isBlank(oldPassword) || ClipUtil.isBlank(newPassword) || ClipUtil.isBlank(newPasswordVerify)){
                passwordMessage = "All fields are requeired";
             }
            User user = session.getUser();
            if(log.isDebugEnabled())log.debug("Loggin in as User ["+ user.getUsername() + "] Password [" + oldPassword + "]");
            if(userAgent.login(user.getUsername(), oldPassword, con) == null){
                passwordMessage = "Current password invalild";
            }
            if(passwordMessage == null){
                if(!newPassword.equals(newPasswordVerify)){
                    passwordMessage = "New Passwords don't match";
                }
            }
            if(passwordMessage == null){
                user.setPassword(newPassword);
                userAgent.save(user, con);
                passwordMessage = "Password changed";
            }
        }
        emailMessage = ClipUtil.blankNull(emailMessage);
        email = session.getUser().getEmail();
        email = ClipUtil.blankNull(email);
        passwordMessage = ClipUtil.blankNull(passwordMessage);

        Page page = new Page(context.getRealPath("accountEdit.html"));
        page.replace("RGDM-EMAIL-ADDRESS-RGDM",email);
        page.replace("RGDM-EMAIL-MESSAGE-RGDM", emailMessage);
        page.replace("RGDM-PASSWORD-MESSAGE-RGDM", passwordMessage);
        page.replace("RGDM-USERNAME-RGDM", session.getUser().getUsername());
        return page.toString();
    }
}
