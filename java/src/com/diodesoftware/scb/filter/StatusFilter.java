package com.diodesoftware.scb.filter;


import com.diodesoftware.dbmapper.DBConnectionMgr;
import com.diodesoftware.scb.agents.CleanerAgent;
import com.diodesoftware.scb.agents.ClipAgent;
import com.diodesoftware.scb.agents.S3AccessAgent;
import com.diodesoftware.scb.clipboard.ClipStatus;
import com.diodesoftware.scb.clipboard.LockMaster;
import com.diodesoftware.scb.tables.Clip;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;



public class StatusFilter implements Filter {

    private ClipStatus clipStatus = null;
    private LockMaster lockMaster = LockMaster.getInstnace();
    private final Object mutex = new Object();
    private boolean running = true;
    private static Logger log = Logger.getLogger(StatusFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {
        clipStatus = ClipStatus.getInstance();
        Runnable r = new Runnable(){

            public void run() {
                while(running){
                    try{
                        synchronized(mutex){
                            mutex.wait(60*1000);
                            clipStatus.cleanOld();
                        }
                    }catch(InterruptedException e){
                       break;
                    }
                }
                log.error("Status Thread Ended");
            }
        };
        S3AccessAgent.getInstance();// Start S3 access agent
        Thread t=  new Thread(r);
        t.setName("Cl1p Status Filter");
        t.start();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String rStatus = request.getParameter("clipStatus");
        if(rStatus == null)return;// Can't update status with the status param
        int status = Integer.parseInt(rStatus);
        String uri = request.getParameter("uri");
        String sessionId = request.getSession().getId();
        long lastEdit = Long.parseLong(request.getParameter("lastEdit"));
        boolean requestLock = Boolean.parseBoolean(request.getParameter("requestLock"));

        clipStatus.setStatus(sessionId, uri, status, 0);
        int[] currentStatus = clipStatus.getStatus(uri);
        long otherLastEdit = clipStatus.getLastEdit(uri);
        DBConnectionMgr dbMgr = new DBConnectionMgr();
        Connection con = dbMgr.getConnection();
        try{
            Clip clip = ClipAgent.getInstance().loadClip(uri,con);
            if(clip != null)
                otherLastEdit = clip.getLastEdit().getTimeInMillis();
        }finally{
            dbMgr.returnConnection(con);
        }

        int lockStatus = lockMaster.getStatus(sessionId, uri);
        if(requestLock){

            lockStatus = lockMaster.getLock(sessionId, uri);
             if(log.isDebugEnabled())
                log.debug("Requesting Lock. Status [" + lockStatus + "]");
        }else{
            //log.error("Not requiesting lock");
            // If i didn't ask for a lock then I don't need it
            // This can happen is Save button is pressed at the same
            // time the AJAX request is going.
            if(lockStatus == LockMaster.HAS_KEY){
                lockMaster.returnLock(sessionId,uri);
                lockStatus = LockMaster.UNLOCKED;
            }
        }
        boolean updateClip = otherLastEdit > lastEdit;


        String onetime = request.getParameter("onetime");

        response.setContentType("text");
        String out = "{" +
                "responseType:" + 1 + ","+
                "viewers:" + currentStatus[0] + ","+
                "editors:" + currentStatus[1] + ","+
                "onetime:" + onetime + "," +
                "lastEdit:" + otherLastEdit + "," +
                "lockStatus:" + lockStatus + "," +
                "lockRequested:" + requestLock + "," +
                "updateClip:" + updateClip + "" +
                "}";

        response.getOutputStream().println(out);
        response.getOutputStream().flush();
        response.getOutputStream().close();

    }

    public void destroy() {
        S3AccessAgent.getInstance().kill();
        running = false;
        synchronized(mutex){
            mutex.notifyAll();
        }
        CleanerAgent.getInstance().kill();
        log.error("Status Filter Destroyed");
    }
}
