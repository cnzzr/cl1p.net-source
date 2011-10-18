 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@page import="com.diodesoftware.scb.ClipRequest"%>
<%@page import="com.diodesoftware.scb.clipboard.ViewMode"%>
<%@page import="com.diodesoftware.scb.clipboard.ClipType"%>
<%
    ClipRequest clipRequest = ClipRequest.getClipRequest(request);
Clip clip = Clip.getClip(request);
    response.setCharacterEncoding("UTF-8");
    ClipType.load(clipRequest);
    ClipType clipType = clipRequest.getCl1pType();
    int viewMode = clipRequest.getViewMode();
    String contentJsp = clipType.getJsp(viewMode);
    //System.err.println("Going to JSP [" + contentJsp + "]");
%>


<%@page import="java.net.URLEncoder"%>
<%@page import="com.diodesoftware.scb.UrlWriter"%>
<%@ page import="com.diodesoftware.scb.ClipConfig" %>
<%@ page import="com.diodesoftware.R" %>
<%@ page import="com.diodesoftware.scb.filter.ClipFilter" %>

<%@page import="com.diodesoftware.scb.tables.Clip"%><html>
<head>
    <script language="javascript" type="text/javascript"
            src="<%= com.diodesoftware.Root.r() %>/cl1p-inc-rgdm/firebug/firebug.js"></script>
<link rel="icon" href="<%= com.diodesoftware.Root.r() %>/favicon.ico" type="image/x-icon">
<link rel="shortcut icon" href="<%= com.diodesoftware.Root.r() %>/favicon.ico" type="image/x-icon">
<%   String defaultValue = "Paste any text you want here.";
 %>

<script type="text/javascript">

                                                      


var cl1pConfig = {
	uri: '<%= clipRequest.getClip().getUri() %>',
	defaultValue : '<%= defaultValue %>',
	encodedUri : '<%= URLEncoder.encode(clipRequest.getUri()) %>',
	downloadUrl : '<%= UrlWriter.write(clipRequest.getClip().getUri(), "download", request)%>',
        fileCount: <%= ClipFilter.fileCount(clipRequest.getClip(), clipRequest.getCon()) %>
};
	</script>
    <script type="text/javascript" src="/cl1p-inc-rgdm/prototype.js<%= com.diodesoftware.R.T()%>"></script>
    <script type="text/javascript" src="/cl1p-inc-rgdm/cl1p.js<%= com.diodesoftware.R.T()%>"></script>

 

<link rel="stylesheet" type="text/css" media="screen, projection" href="<%= com.diodesoftware.Root.r() %>/cl1p-inc-rgdm/style.css<%= com.diodesoftware.R.T()%>">
<title><%= clipRequest.getClip().getTitle() %></title>
</head>
<body>
<a href="/cl1p-inc-rgdm/theend.jsp"><h2>Cl1p.net will shutdown forever Oct 31, 2011</h2></a>
<script type="text/javascript" src="/cl1p-inc-rgdm/wz_tooltip.js<%= com.diodesoftware.R.T()%>"></script>
<div style="display:none;">
    <form method="post" name="logout">
        <input type="hidden" name="lusername" value="">
        <input type="hidden" name="lpassword" value="">
     </form>
    <form method="post" name="changeType" id="changeTypeForm">
    <input type="hidden" name="changeType">
        <%
             int currentType =clipRequest.getClip().getClipType();
        %>
    <input type="hidden" name="currentType" value="<%= currentType %>">
     <input type="hidden" name="DEBUG" value="false" id="DEBUGELE"/>
</form>
</div>
<form method="post" id="aform" name="aform" action="<%= clip.getUri() %>" enctype="multipart/form-data" accept-charset="UTF-8">
<table border="0" cellspacing="2" cellpadding="0" width="100%">
	<tr><td valign="top" width="61">
		<table><tr><td >
			<a href="/"><img alt="cl1p.net" src="/cl1p-inc-rgdm/images/logo-small.gif" border="0"/></a></td></tr></table>
	</td><td  colspan="<%= showHelp(request)?2:1 %>" valign="top">
   <jsp:include flush="true" page="<%= headerJsp(viewMode) %>"></jsp:include>
   </td></tr>
    </table>
<table border="0" cellspacing="2" cellpadding="0" width="100%">
<tr><td  valign="top" width="50px">
                   <jsp:include flush="true" page="left_nav.jsp"/>
</td><td  valign="top">
<jsp:include flush="true" page="<%= contentJsp %>"></jsp:include>
</td>
    <% if(showHelp(request) ){

         %>
   <td width="125px"  valign="top">
<p>You have created a cl1p!</p>
       <p>Paste in any text you want into the textarea and click save.</p>
       <p>Then on any other computer or mobile device enter in http://cl1p.net<%= clipRequest.getUri()%></p>
   </td>
        <%

    }
    %>
</tr></table>
</form>
<a href="/cl1p-admin/kill.jsp?url=<%= URLEncoder.encode(clipRequest.getUri(),"UTF-8")%>">Logout</a>&nbsp;


<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("UA-100555-2");
pageTracker._initData();
pageTracker._trackPageview();
</script>
<iframe src="/cl1p-inc-rgdm/blank.html" id="popup-click-blocker" style="display:none;"></iframe>
  <% session.setAttribute("fromIndex",false);%>
</body>
</html>
<%!

	private String headerJsp(int viewMode){
		switch(viewMode){
			case ViewMode.EDIT:
				return "header.jsp";
			case ViewMode.READ_ONLY:
				return "header.jsp";
			case ViewMode.PASSWORD_REQUIRED:
				return "header.jsp";
			default:
				return "header.jsp";
		}
	}

	private String controlsJsp(int viewMode){
		switch(viewMode){
			case ViewMode.EDIT:
				return "controls.jsp";
			case ViewMode.READ_ONLY:
				return "controls.jsp";
			case ViewMode.PASSWORD_REQUIRED:
				return "controls.jsp";
			default:
				return "controls.jsp";
		}			
	}
	
	private String defaultContent(int viewMode){
		return "default_content.jsp";	
	}

    private boolean showHelp(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        if(session.getAttribute("fromIndex") != null)
        {
            boolean b = (Boolean)session.getAttribute("fromIndex");
            
            return b;
        }
        return false;
    }
%>
