<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%
    
    SysopSession sysopSession = SysopSession.getInstance(request);
    if (!sysopSession.isLoggedIn()) {
%>
<%@page import="com.diodesoftware.scb.sysop.SysopSession"%>
<jsp:forward page="index.jsp"/>
<%
        return;
    }
    %>
<%@page import="com.diodesoftware.scb.agents.UserAgent"%>
<%@page import="com.diodesoftware.scb.ClipRequest"%>
<%@page import="java.util.List"%>
<%@page import="com.diodesoftware.W"%>
<%@page import="com.diodesoftware.scb.tables.User"%>
<%@ page import="java.text.DateFormat" %>
<%@ page import="org.displaytag.util.ParamEncoder" %>
<%@ page import="org.displaytag.tags.TableTagParameters" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="com.diodesoftware.scb.tag.DatePickerTag" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.diodesoftware.scb.ClipUtil" %>
<html>
  <head>
  <title><%= W.w("users") %></title>
  <link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= com.diodesoftware.R.T()%>">
    <!-- calendar stylesheet -->
  <link rel="stylesheet" type="text/css" media="all" href="/cl1p-inc-rgdm/calendar/calendar-win2k-cold-1.css" title="win2k-cold-1" />

  <!-- main calendar program -->
  <script type="text/javascript" src="/cl1p-inc-rgdm/calendar/calendar.js"></script>

  <!-- language for the calendar -->
  <script type="text/javascript" src="/cl1p-inc-rgdm/calendar/lang/calendar-en.js"></script>

  <!-- the following script defines the Calendar.setup helper function, which makes
       adding a calendar a matter of 1 or 2 lines of code. -->
  <script type="text/javascript" src="/cl1p-inc-rgdm/calendar/calendar-setup.js"></script>  
  </head>
  <body>
<center>
<table height="100%" cellspacing="0" cellpadding="0" width="875">
<tr>
<td valign="top">
  <h2><%= W.w("users") %></h2>
<a href="addUser.jsp"><%= W.w("add.new.user") %></a>
  <form method="post" action="userlist.jsp">
  <table>
      <tr><td>User Id</td><td><input type="text" name="userId"></td><td>Username</td><td><input type="text" name="username"></td></tr>
      <tr><td>Email</td><td><input type="text" name="email"></td><td></td><td></td></tr>
      <tr><td>Created From:</td><td><app:DatePicker parameterName="from"/></td>
          <td>To:</td><td><app:DatePicker parameterName="to"/></td></tr>
      <tr><td colspan="4" align="right"><input type="submit" value="Search"></td></tr>
  </table>
  </form>

  <%
      ClipRequest clipRequest = ClipRequest.getClipRequest(request);

      String userIdP = request.getParameter("userId");
      String emailP = request.getParameter("email");
      String fromP = request.getParameter("from");
      String toP = request.getParameter("to");
      String usernameP = request.getParameter("username");
      String errMsg = "";
      List<User> list = new ArrayList<User>();


      int userId = 0;
      if (userIdP != null) 
      {
          
          if(ClipUtil.isBlank(userIdP))
          {
            try {
                  userId = Integer.parseInt("userId");
            } catch (NumberFormatException e) {
              errMsg = W.w("userid.must.be.a.number");
            }
          }
      
          
      Calendar from = null;
          
      try {
              
    	  Date date = DatePickerTag.dateFormat.parse(fromP);
              
    	  from = Calendar.getInstance();              
    	  from.setTimeInMillis(date.getTime());
 		  from.set(Calendar.HOUR_OF_DAY, 0);
          from.set(Calendar.MINUTE, 0);
          from.set(Calendar.SECOND, 0);
          from.set(Calendar.MILLISECOND, 0);
   		} catch (Exception e) {
              errMsg = W.w("from.invalid.date.format");
     	}
        Calendar to = null;
       	try {
              Date date = DatePickerTag.dateFormat.parse(toP);
              to = Calendar.getInstance();
              to.setTimeInMillis(date.getTime());
              to.set(Calendar.HOUR_OF_DAY, 23);
              to.set(Calendar.MINUTE, 59);
              to.set(Calendar.SECOND, 59);
              to.set(Calendar.MILLISECOND, 999);
          } catch (Exception e) {
              errMsg = W.w("to.invalid.date.format");
          }
          String email = null;
          if (!ClipUtil.isBlank(emailP))
              email = emailP;
          String username = null;
          if(!ClipUtil.isBlank(usernameP))
            username = usernameP;
          list = UserAgent.getInstance().search(userId, username, email, from, to, clipRequest.getCon());
      


      request.setAttribute("resultSize", list.size());
      String column = request.getParameter("d-448696-s");
      String stringAsc = request.getParameter("d-448696-o");

      boolean asc = false;
      if (stringAsc != null) {
          asc = Integer.parseInt(stringAsc) == 1;
      }
      if (column != null)
          UserAgent.getInstance().sort(column, asc, list);

      request.setAttribute("list", list);
      DateFormat df = DateFormat.getDateInstance();
      try {
  %>
  <%=errMsg %>
 <display:table name="list" id="userid"  class="clipTable" sort="external" defaultsort="1" pagesize="20"  partialList="true" size="resultSize">
     <display:setProperty name="paging.banner.placement" value="bottom" />
     <display:column sortable="true" sortName="number" property="number" title="ID" />
     <display:column sortable="true" sortName="username" property="username" title="<%= W.w("username") %>" />
     <display:column sortable="true" sortName="email" property="email" title="<%= W.w("email") %>" />
     <display:column sortable="true" sortName="disabled" property="disabled" title="<%= W.w("disabled") %>" />
     <display:column sortable="true" sortName="created" title="<%= W.w("created") %>" >
        <%= df.format(((User)pageContext.getAttribute("userid")).getCreated().getTime())%>         
     </display:column>
     <display:column title="">
     <a href="editUser.jsp?userId=<%=((User)pageContext.getAttribute("userid")).getNumber()%>">Edit</a>
     <a href="setPassword.jsp?userId=<%=((User)pageContext.getAttribute("userid")).getNumber()%>"><%= W.w("set.password") %></a>
    </display:column>
</display:table>
  <%
      }catch(Exception e){
          e.printStackTrace();
      }
      }
  %>
<a href="./index.jsp"><%= W.w("menu") %></a>
  </td></tr></table></center>
</body>
</html>
