package com.diodesoftware.scb.sysop;

import com.diodesoftware.dbmapper.DBConnectionMgr;
import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.dbmapper.PasswordEncrypter;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;

public class SysopSession {

    public static SysopSession getInstance(HttpServletRequest request){
        SysopSession session = (SysopSession)request.getSession().getAttribute(SysopSession.class.getName());
        if(session == null){
            session = new SysopSession();
            request.getSession().setAttribute(SysopSession.class.getName(), session);
        }
        if(!session.isLoggedIn()){
            String password = request.getParameter("password");
            if(password != null){
                DBConnectionMgr dbMgr = new DBConnectionMgr();
                Connection con = dbMgr.getConnection();
                try{
                	if(login(password, con) != null){
                		session.loggedIn = true;
                	}
                }finally{
                    dbMgr.returnConnection(con);
                }
            }
        }
        return session;
    }
    
    public static SysopLogin login(String password, Connection con){
        DBMapper mapper = DBMapper.getInstance();
        SysopLogin login = (SysopLogin)mapper.load(SysopLogin.class, 1,con);
        String pswEnc = PasswordEncrypter.encrypt(password);
        if(pswEnc.equals(login.getPassword())){
            return login;
        }
        return null;
    }

    private boolean loggedIn = false;

    private SysopSession(){
        loggedIn = false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
