package com.diodesoftware.scb.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Feb 2, 2008
 * Time: 8:26:06 PM
 */
public class UpgradeFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ServletContext context = request.getSession().getServletContext();
        RequestDispatcher dispatcher  =context.getRequestDispatcher("/upgrade.jsp");
        dispatcher.forward(request, response);
    }

    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
