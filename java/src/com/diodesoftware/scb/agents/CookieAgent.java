package com.diodesoftware.scb.agents;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.tables.ClipCookie;
import com.diodesoftware.scb.ClipUtil;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Jul 4, 2006
 * Time: 9:17:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class CookieAgent {
    private static CookieAgent instance = null;

    private DBMapper dbMapper;
    private Logger log = Logger.getLogger(CookieAgent.class);
    public static final String COOKIE_NAME = "cl1pcookie";

    private CookieAgent(DBMapper mapper) {
        this.dbMapper = mapper;
    }

    public static synchronized void initalize(DBMapper dbMapper) {
        instance = new CookieAgent(dbMapper);
    }

    public static CookieAgent getInstance() {
        return instance;
    }

    public ClipCookie load(HttpServletRequest request, Connection con) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String value = null;
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (COOKIE_NAME.equals(cookie.getName())) {
                    value = cookie.getValue();
                    return load(value, con);
                } else {
                    if (log.isDebugEnabled())
                        log.debug("Not my Cookie [" + cookie.getName() + "] != [" + COOKIE_NAME + "]");
                }
            }

        }
        return null;
    }

    public ClipCookie load(String value, Connection con) {
        if (log.isDebugEnabled())
            log.debug("Looking for cookied with value [" + value + "]");
        ClipCookie result = null;
        String sql = "Select * from ClipCookie where Value = ?";
        try {
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1, value);
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next()) {

                result = (ClipCookie) dbMapper.loadSingle(ClipCookie.class, rs);
            }
            rs.close();
            prepStmt.close();
        } catch (SQLException e) {
            log.error("Error running SQL [" + sql + "]", e);
        }
        return result;
    }

    public void clearCookie(HttpServletRequest request,
                            HttpServletResponse response,
                            Connection con) {

        ClipCookie cc = load(request, con);
        if (cc != null) {
            cc.setUserId(-1);
            cc.setValue("REMOVE");
            dbMapper.save(cc, con);
        }
        Cookie cookie = new Cookie(COOKIE_NAME, "done");
        cookie.setMaxAge(-1);
        cookie.setPath("/");

        response.addCookie(cookie);

    }

    public ClipCookie create(int userId, Connection con,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        ClipCookie clipCookie;
        String value;
        do {
            value = ClipUtil.genString(50);
            clipCookie = load(value, con);
        } while (clipCookie != null);
        clipCookie = new ClipCookie();
        clipCookie.setValue(value);
        clipCookie.setUserId(userId);
	
        dbMapper.save(clipCookie, con);
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setMaxAge(120 * 24 * 60 * 60);
	cookie.setPath("/");
        response.addCookie(cookie);
        if (log.isDebugEnabled())
            log.debug("Added cookie [" + cookie.getName() + "] v[" + cookie.getValue() + "]");
        return clipCookie;
    }


}

