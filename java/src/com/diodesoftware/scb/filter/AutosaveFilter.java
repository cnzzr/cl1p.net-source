package com.diodesoftware.scb.filter;

import com.diodesoftware.scb.ClipRequest;
import com.diodesoftware.scb.clipboard.Cache;
import com.diodesoftware.scb.clipboard.ClipSaver;
import com.diodesoftware.scb.clipboard.ClipStatus;
import com.diodesoftware.scb.clipboard.LockMaster;
import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.dbmapper.DBConnectionMgr;
import com.diodesoftware.dbmapper.DBMapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;

import org.apache.log4j.Logger;


public class AutosaveFilter implements Filter {


    private DBConnectionMgr dbMgr = new DBConnectionMgr();
    private ClipStatus clipStatus = null;
    private static Logger log = Logger.getLogger(AutosaveFilter.class);
    private LockMaster lockMaster = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        filterConfig.getServletContext();
        this.clipStatus = ClipStatus.getInstance();
        this.lockMaster = LockMaster.getInstnace();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
       // log.error("Started AUtosave filter");
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            request.setCharacterEncoding("UTF-8");
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            Connection con = dbMgr.getConnection();
            try {
                ClipRequest clipRequest = new ClipRequest(request, con);
                String uri = clipRequest.getParameter("uri");
                String sessionID = request.getSession().getId();
                if(log.isDebugEnabled()){
                    log.debug("Autosave Filter URI [" + uri + "] Value [" + clipRequest.getParameter("ctrlcv") + "]Session ID [" + sessionID + "]");
                }
                if (clipStatus.hasAccess(sessionID, uri)) {
                    // Pass off processing to main system
                    clipRequest.setUri(uri);
                    Cache cache = Cache.getInstance();
                    Clip clip = cache.get(uri, con);
                    String value = clipRequest.getParameter("ctrlcv");
                    if(ClipSaver.isValueToBig(clipRequest))
                    {
                    	return;
                    }
                    if(value != null)
                        clip.setValue(value);
                    String ctrlRich = request.getParameter("ctrlcv_rich");
                    if(ctrlRich != null)
                        clip.setValue(ctrlRich);
                    clip.setLastEdit(Calendar.getInstance());
                    DBMapper.save(clip, con);
                    lockMaster.returnLock(sessionID, uri);
                    response.getOutputStream().println("{lastEdit:" + clip.getLastEdit().getTimeInMillis() + "}");

                    log.debug("Clip auto saved Value [" + clip.getValue() + "]");
                } else {
                    log.debug("Clip not saved. No access");
                    response.getOutputStream().println("{lastEdit:0,abortLoop:true}");
                }
            } finally {
                dbMgr.returnConnection(con);
            }
        } catch (Exception e) {
            log.error("Error in Autosave filter", e);
        }
    }

    public void destroy() {

    }
}
