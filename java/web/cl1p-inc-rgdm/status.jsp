 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.sysop.SysopSession" %>
<%@page import="com.diodesoftware.W"%>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.diodesoftware.scb.agents.SystemStatus" %>
<%@ page import="java.text.DateFormat" %>
<%
    try{
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title><%=W.w("cl1p.sysop.menu") %></title>
      <link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= com.diodesoftware.R.T()%>">
  </head>
  <body>
  <center>
      <table>
          <tr>
              <td>
                  <a href="../cl1p-admin/sysop/index.jsp">Back to menu</a>

                  <h1>System Status</h1>
                  <%!
                      DateFormat df = DateFormat.getDateTimeInstance();
                  %>
<%
    if (SystemStatus.getInstance().isERROR()){
        %>
SYSTEM_ERROR<br/>
                  Error Count: <%= SystemStatus.getInstance().getErrorCount() %>   <br/>
                  <%
                      if(SystemStatus.getInstance().getLastError()!=null){
                  %>
                  Last Error: <%= df.format(SystemStatus.getInstance().getLastError())%>
                  <%
                      }
                  %>
                  <%
    }else{
        %>
SYSTEM_OK                  
                  <%
    }
%>

                  <h2>JVM Status</h2>
                  <%
          int processors = Runtime.getRuntime().availableProcessors();
          long freeMemory = Runtime.getRuntime().freeMemory();
          long maxMemory = Runtime.getRuntime().maxMemory();
          long usedMemory = maxMemory - freeMemory;
          int activeThreads = Thread.activeCount();
          NumberFormat nf = NumberFormat.getNumberInstance();
      %>
      Processors: <%= nf.format(processors) %><br/>
      Used Memory: <%= nf.format(usedMemory) %><br/>
      Free Memory: <%= nf.format(freeMemory) %><br/>
      Active Threads: <%= nf.format(activeThreads) %><br/>
      </td></tr></table>
      Threads
      <table><tr><td>Name</td><td>State</td><td>Stack Trace</td></tr>
      <%
          Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
          Iterator<Thread> iterator = map.keySet().iterator();
          while(iterator.hasNext()){
              Thread t = iterator.next();
%>
<tr><td><%= t.getName() %></td><td><%= t.getState()%></td><td><%= t.getStackTrace()[0].getClassName()%></td></tr>
      <%
          }
      %>
          </table>
  </center>
  </body>
</html>
<%
    }catch(Exception e){
    	%>
<%= e.getMessage() %>
    	<%
    }
%>

