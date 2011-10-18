package com.diodesoftware.scb.filter;

import com.diodesoftware.scb.sysop.SysopSession;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jun 23, 2007
 * Time: 1:18:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class SysopFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        SysopSession sysopSession = SysopSession.getInstance(request);
        if(sysopSession.isLoggedIn()){
          
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }else{

            HttpServletResponse response = (HttpServletResponse)servletResponse;
            response.setContentType("text/html");
            response.getWriter().println("<html><form method='post'>" +
                    "Sysop Password:<input type='password' name='password'><input type='submit' value='Login'></form></html>");
            response.getWriter().flush();
        }
    }

    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
