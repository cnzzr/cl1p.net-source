 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@page import="com.diodesoftware.scb.ClipRequest"%>
<%@page import="com.diodesoftware.scb.clipboard.ViewMode"%>
<%@page import="com.diodesoftware.scb.clipboard.ClipType"%>
<%
    ClipRequest clipRequest = ClipRequest.getClipRequest(request);
    response.setCharacterEncoding("UTF-8");
    ClipType.load(clipRequest);
    ClipType clipType = clipRequest.getCl1pType();
    int viewMode = clipRequest.getViewMode();
    String contentJsp = "iphone_" + clipType.getJsp(viewMode);
    //System.err.println("Going to JSP [" + contentJsp + "]");
%>


<%@page import="java.net.URLEncoder"%>
<%@page import="com.diodesoftware.scb.UrlWriter"%>
<%@ page import="com.diodesoftware.scb.ClipConfig" %>
<%@ page import="com.diodesoftware.R" %>
<%@ page import="com.diodesoftware.scb.filter.ClipFilter" %>
<html>
<head>
<link rel="icon" href="<%= com.diodesoftware.Root.r() %>/favicon.ico" type="image/x-icon">
<link rel="shortcut icon" href="<%= com.diodesoftware.Root.r() %>/favicon.ico" type="image/x-icon">
<%   String defaultValue = "Paste any text you want here.";
 %>
<meta name="viewport" content="width=320; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />
<link rel="stylesheet" type="text/css" media="screen, projection" href="<%= com.diodesoftware.Root.r() %>/cl1p-inc-rgdm/style.css<%= com.diodesoftware.R.T()%>">
		<style type="text/css" media="screen">
			body {
				font: 14px/1.5em "Lucida Grande",sans-serif;
				margin: 0;
				padding: 10px;
				background: #ffffff;
		
			}
			h1, h2 {
				font-size: 1.2em;
				font-weight: normal;
			}
		</style>

 


<title><%= clipRequest.getClip().getTitle() %></title>
</head>
<body>
<jsp:include flush="true" page="<%= headerJsp(viewMode) %>"></jsp:include>
<jsp:include flush="true" page="<%= contentJsp %>"></jsp:include>

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("UA-100555-2");
pageTracker._initData();
pageTracker._trackPageview();
</script>

</body>
</html>
<%!

	private String headerJsp(int viewMode){
		switch(viewMode){
			case ViewMode.EDIT:
				return "iphone_header.jsp";
			case ViewMode.READ_ONLY:
				return "iphone_header.jsp";
			case ViewMode.PASSWORD_REQUIRED:
				return "iphone_header.jsp";
			default:
				return "iphone_header.jsp";
		}
	}

	private String controlsJsp(int viewMode){
		switch(viewMode){
			case ViewMode.EDIT:
				return "iphone_controls.jsp";
			case ViewMode.READ_ONLY:
				return "iphone_controls.jsp";
			case ViewMode.PASSWORD_REQUIRED:
				return "iphone_controls.jsp";
			default:
				return "iphone_controls.jsp";
		}			
	}
	
	private String defaultContent(int viewMode){
		return "iphone_default_content.jsp";	
	}
%>
