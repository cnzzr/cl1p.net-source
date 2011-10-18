package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Calendar;

/**
 * Copyright 2010 Rob Mayhew
 * User: rob
 * Date: Oct 15, 2010
 */
public class Bob implements DatabaseEntry
{
    private Calendar visit = Calendar.getInstance();
    private String uri;
    private String userAgent;
    private String sessionId;
    private String ip;
    private int number;

    private static Logger log = Logger.getLogger(Bob.class);

    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("Visit", DatabaseColumnType.DATE12),
            new DatabaseColumn("Uri", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("UserAgent", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("SessionId", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("Ip", DatabaseColumnType.CHAR_100),
    };


    public Bob()
    {
    }

    public Bob(HttpServletRequest request)
    {
        HttpSession httpSession = request.getSession();
        sessionId = trimTo(httpSession.getId(),200);
        ip = trimTo(request.getRemoteAddr(),100);
        userAgent = trimTo(request.getHeader("User-Agent"),200);
        uri = request.getRequestURI();

    }

    public DatabaseColumn[] columns()
    {
        return columns;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {

        this.ip = trimTo(ip,100);
    }


    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = trimTo(sessionId, 200);
    }

    public String getUserAgent()
    {
        return userAgent;
    }

    public void setUserAgent(String userAgent)
    {
        this.userAgent = trimTo(userAgent,200);
    }

    public Calendar getVisit()
    {
        return visit;
    }

    public void setVisit(Calendar visit)
    {
        this.visit = visit;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    private String trimTo(String s, int length)
    {
        if(s == null)return null;
        if(s.length() > length)
            return s.substring(0,length);
        return s;
    }
}
