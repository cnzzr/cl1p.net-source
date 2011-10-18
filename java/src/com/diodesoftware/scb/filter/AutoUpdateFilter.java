package com.diodesoftware.scb.filter;

import com.diodesoftware.scb.clipboard.ClipStatus;
import com.diodesoftware.scb.agents.ClipAgent;
import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.dbmapper.DBConnectionMgr;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jun 28, 2007
 * Time: 9:00:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class AutoUpdateFilter implements Filter {

    private ClipStatus clipStatus = null;
    private DBConnectionMgr dbCon = new DBConnectionMgr();

    public void init(FilterConfig filterConfig) throws ServletException {
        clipStatus = ClipStatus.getInstance();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getParameter("uri");
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        String rLastEdit =  request.getParameter("lastEdit");
        if(rLastEdit == null)return;
        long userLastEdit = Long.parseLong(rLastEdit);
        if (clipStatus.hasAccess(request.getSession().getId(), uri)) {
            Connection con = dbCon.getConnection();
            try {
                Clip clip = ClipAgent.getInstance().loadClip(uri, con);
                if (clip != null) {
                    DateFormat df = DateFormat.getDateTimeInstance();
                      String textAreaValue = clip.getValue();
                        Pattern p = Pattern.compile("</\\s*textarea",
                                Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(textAreaValue);

                        textAreaValue = m.replaceAll("textarea");
                        response.getOutputStream().write(textAreaValue.getBytes("UTF-8"));
                    
                }
            } finally {
                dbCon.returnConnection(con);
            }
        } else {
         
            response.getOutputStream().print("");
        }
        response.getOutputStream().flush();
    }

    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
