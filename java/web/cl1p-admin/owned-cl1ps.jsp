<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ page import="com.diodesoftware.scb.admin.CreateAccountPage"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr"%>
<%@ page import="org.apache.log4j.Logger"%>
<%@ page import="com.diodesoftware.scb.agents.UserAgent"%>
<%@ page import="com.diodesoftware.scb.SitePage.Page"%>
<%@ page import="org.apache.ecs.html.Table"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="org.apache.ecs.html.TR"%>
<%@ page import="org.apache.ecs.html.TD"%>
<%@ page import="com.diodesoftware.scb.tables.Clip"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.diodesoftware.scb.*" %>
<%@ page import="com.diodesoftware.scb.filter.ClipFilter" %>
<%@ page import="com.diodesoftware.R" %>
<%!

    private static Logger log  = Logger.getLogger("jsp.admin.menu");
%>
<%
    ClipSession cl1pSession  = ClipSession.getSession(request);
    if(cl1pSession.isLoggedIn() == false){
        String redirectURL = "/cl1p-admin/create.jsp";
                                      %>
<jsp:forward page="<%= redirectURL%>"/>

<%
        return;
    }
       DateFormat df = DateFormat.getDateTimeInstance();
    DBConnectionMgr dbConnectionMgr = new DBConnectionMgr();
    Connection con = dbConnectionMgr.getConnection();
    ServletContext context = config.getServletContext();
    Clip[] ownedClips = new Clip[0];
    Clip[] clips = new Clip[0];
    try {
        String errorMsg = null;
        cl1pSession.reloadUser(con);
        UserAgent userAgent = UserAgent.getInstance();



	    ownedClips = userAgent.loadOwnedClips(cl1pSession.getUser().getNumber(),con);





        request.setAttribute("RGDM-USERNAME-RGDM", cl1pSession.getUser().getUsername());


%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <title>cl1p.net The internet clipboard</title>
  <meta http-equiv="content-type"
 content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= R.T()%>">

</head>
<body>


<center>
<table height="100%" width="850" cellspacing="0" cellpadding="5">
<tr>
<td valign="top">
<img src="/cl1p-inc-rgdm/images/cl1p_logo.jpg" alt="cl1p the internet clipboard">
<%
    ClipSession clipSession = ClipSession.getSession(request);
%>

<div name="main">
User: <%= request.getAttribute("RGDM-USERNAME-RGDM")%>
    <br><br>

    <form method="post" name="logout">
        <input type="hidden" name="lusername" value="">
        <input type="hidden" name="lpassword" value="">
    </form>
      <a href="/cl1p-admin/menu.jsp">Home</a>
    &nbsp;<a href="/cl1p-admin/owned-cl1ps.jsp">Owned cl1ps</a>
    &nbsp;<a href="/cl1p-admin/accountEdit.jsp">Account Settings</a>
    &nbsp;<a href="logout.jsp">Logout</a><br><br>
    <%
if(ownedClips.length == 0){
%>
You don't own any cl1ps.
<%
}else{
%>
Owned cl1ps<br>
    <%

        List list = new ArrayList();
        for(Object o : ownedClips){
            list.add(o);
        }
        request.setAttribute("ownedlist", list);
    %>

<display:table name="ownedlist" id="ownedclip" pagesize="15" class="clipTable" >
    <display:setProperty name="paging.banner.placement" value="bottom" />
    <display:column property="title" title="Title"/>
        <display:column  title="Type">
            <%
                Clip c = (Clip)pageContext.getAttribute("ownedclip");
                String type = Clip.CLIP_TYPE_NAMES[c.getClipType()];

            %>
<%= type %>
        </display:column>
    <display:column title="URL" >
        <%
            Clip clip = (Clip)pageContext.getAttribute("ownedclip");
            String clipUri = clip.getUri();
        %>
            <a href="<%= UrlWriter.write(clipUri, null, request) %>"><%= UrlWriter.write(clipUri, null, request) %></a>
    </display:column>
    <display:column title="Password">
              <%
                  Clip c = (Clip) pageContext.getAttribute("ownedclip");
                  String checked = "";
                  if (!ClipUtil.isBlank(c.getPassword())){
                      checked = "CHECKED";
                  }

              %>
              <input type="checkbox" readonly="true" disabled="true" <%= checked %>/>
          </display:column>
          <display:column title="Restrict Views">
              <%
                  Clip c = (Clip) pageContext.getAttribute("ownedclip");
                  String checked = "";
                  if (c.isViewPassword()){
                      checked = "CHECKED";
                  }

              %>
              <input type="checkbox" readonly="true" disabled="true" <%= checked %>/>
          </display:column>
          <display:column title="File">
              <%
                  Clip c = (Clip) pageContext.getAttribute("ownedclip");
                  String checked = "";
                  if(com.diodesoftware.scb.filter.ClipFilter.hasFile(c, con)){
                      checked = "CHECKED";
                  }

              %>
              <input type="checkbox" readonly="true" disabled="true" <%= checked %>/>
          </display:column>
          
    <display:column title="Last Change" sortProperty="sortLastEdit" sortable="false">
            <%
                Clip c =(Clip)pageContext.getAttribute("ownedclip");

            %>
            <%= df.format(c.getLastEdit().getTime()) %>
        </display:column>
</display:table>
 <%
     }
 %>

</td></tr></table>
</center>
<%
    if(ClipConfig.CL1P_SITE){
%>
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("UA-100555-2");
pageTracker._initData();
pageTracker._trackPageview();
</script>
<%
    }
%>   
</body>
</html>
<%
     } finally {
        dbConnectionMgr.returnConnection(con);
    }
%>
