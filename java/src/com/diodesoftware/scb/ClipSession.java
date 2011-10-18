package com.diodesoftware.scb;

import com.diodesoftware.scb.tables.*;
import com.diodesoftware.scb.agents.*;
import com.diodesoftware.dbmapper.DBMapper;

import javax.servlet.http.*;
import javax.servlet.http.HttpSession;
import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Jun 20, 2006
 * Time: 6:40:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClipSession {

    private static final String CLIP_SESSION_KEY = "ClipSession";
    private static Logger log = Logger.getLogger(ClipSession.class);


    public static ClipSession getSession(HttpServletRequest request) {
        HttpSession session = (HttpSession) request.getSession();
        ClipSession result = (ClipSession) session.getAttribute(CLIP_SESSION_KEY);
        if (result == null) {
            result = new ClipSession();
            session.setAttribute(CLIP_SESSION_KEY, result);
        }
        return result;
    }

    public static void clearSession(HttpServletRequest request) {
        HttpSession session = (HttpSession) request.getSession();
        session.removeAttribute(CLIP_SESSION_KEY);
    }

    public static void attemptLogin(HttpServletRequest request, HttpServletResponse response, Connection con) {
        ClipRequest cr = new ClipRequest(request, con);
        attemptLogin(cr, response, con);
    }


    private static boolean cookieLogin(ClipRequest request, Connection con) {
        CookieAgent cookieAgent = CookieAgent.getInstance();
        ClipCookie cc = cookieAgent.load(request.getRequest(), con);
        if (cc == null) return false;
        int userId = cc.getUserId();
        DBMapper dbMapper = DBMapper.getInstance();
        User user = (User) dbMapper.load(User.class, userId, con);
        if (user == null) return false;
        ClipSession session = getSession(request.getRequest());
        session.setLoggedIn(true);
        session.setUser(user);
        return true;
    }


    public static void attemptLogin(ClipRequest request, HttpServletResponse response, Connection con) {
        if (log.isDebugEnabled())
            log.debug("Attempting Login");
        String username = request.getParameter(HtmlParam.LOGIN_USERNAME);
        String password = request.getParameter(HtmlParam.LOGIN_PASSWORD);

        /*if (FileUpload.isMultipartContent(request.getRequest())) {
            DiskFileUpload diskFileUpload = new DiskFileUpload();
            try {
                List items = diskFileUpload.parseRequest(request.getRequest());

                Iterator iter = items.iterator();

                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (item.isFormField()) {
                        request.setParameter(item.getFieldName(), item.getString());
                    }
                }
            } catch (Exception e) {
                log.error("Error parsing multipart reqest",e);
            }
        }
        request.reInitParamererMap();
        */
        //request.reInitParamererMap();
        if (log.isDebugEnabled())
            log.debug("Attempting login with username[" + username + "] password [" + password + "]");
        User user = null;
        ClipSession session = getSession(request.getRequest());
        if (username != null && password != null) {
            session.setLoggedIn(false);
            session.setUser(null);
            CookieAgent.getInstance().clearCookie(request.getRequest(), response, con);
            user = UserAgent.getInstance().login(username, password, con);
            if(log.isDebugEnabled()){
               if(user == null){
                   log.debug("Login for User [" + username + "] password [" + password + "] Failed");
               }else{
                    log.debug("Login for User [" + username + "] password [" + password + "] Sucsessful");                   
               }
            }
            if(username.trim().length() != 0 && password.trim().length() !=0){
                request.getRequest().setAttribute("login_message", "Invalid login");
            }
        }
        if (user == null) {
            ClipCookie cc = CookieAgent.getInstance().load(request.getRequest(), con);
            if (log.isDebugEnabled())
                log.debug("Loaded cookie " + cc);
            if (cc != null){
                user = UserAgent.getInstance().load(cc.getUserId(), con);
                if(user != null && user.isDisabled()){
                	user = null;
                }
            }
        }
        if (user != null) {
            if (log.isDebugEnabled())
                log.debug("Login Succressfull");
            session.setLoggedIn(true);
            session.setUser(user);
            String cookie = request.getParameter(HtmlParam.LOGIN_COOKIE);
            if (cookie != null) {
                if (log.isDebugEnabled())
                    log.debug("Setting cookie");
                CookieAgent.getInstance().create(user.getNumber(),
                        con,
                        request.getRequest(),
                        response);
            }
        } else {
            if (log.isDebugEnabled())
                log.debug("Login Failed");
        }
        if (cookieLogin(request, con)) {
            if (log.isDebugEnabled())
                log.debug("Cookie login sucessful, not logging in");
            return;
        }

    }

    private ClipSession() {

    }

    private boolean loggedIn = false;
    private User user;
    private String redirectURL = null;
    private String lastUri = null;
    private String purchaseUri = null;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRedirect(String s) {
        redirectURL = s;
    }

    public String getRedirect() {
        String s = redirectURL;
        redirectURL = null;
        return s;
    }

    public String getLastUri() {
        return lastUri;
    }

    public void setLastUri(String lastUri) {
        this.lastUri = lastUri;
    }

    public boolean isPro() {
        if (user != null && user.isPro()) return true;
        return false;
    }

    public void reloadUser(Connection con) {
        if (user != null) {
            int id = user.getNumber();
            user = (User) DBMapper.getInstance().load(User.class, id, con);
        }
    }


    public String getPurchaseUri() {
        return purchaseUri;
    }

    public void setPurchaseUri(String purchaseURI) {
        this.purchaseUri = purchaseURI;
    }

    public String toString(){
	return "LoggedIn [" + loggedIn + "] User [" + user + "] redirectURL [" + redirectURL + "] lastUri [" + lastUri + "] purchaseURI [" + purchaseUri + "]";
    }
}
