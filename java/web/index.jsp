<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.diodesoftware.scb.ClipUtil"%>
<%
    String baseUrl = ClipUtil.baseUrl(request);
    request.getSession().setAttribute("fromIndex", true);
%>
<%@page import="com.diodesoftware.scb.filter.ClipFilter"%>
<%@ page import="com.diodesoftware.scb.filter.ClipFilter" %>
<%@ page import="com.diodesoftware.R" %>
<html>
<head>
<link rel="icon" href="/favicon.ico" type="image/x-icon">
<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon"> 
  <title>cl1p.net - The internet clipboard</title>
<META name="verify-v1" content="w6FYr0ObdgFYmM7VdO+Gmhr9bT5fmQoE4GgfwkUhSfw=" />
  <meta http-equiv="content-type"
 content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" media="screen, projection" href="./cl1p-inc-rgdm/style.css<%= R.T()%>">

<meta name="description" content="cl1p.net The online internet clipboard." />
<meta name="keywords" context="clipboard, internet, online, network, copy, paste"/>
</head>
<body onload="go()">
<%
    String rndUrl = ClipFilter.randomUrl(request);
%>
<center>
<table height="100%" cellspacing="0" cellpadding="0" width="875">
<tr>
<td valign="top">
<table width="100%"><tr><td valign="top">
<div style="float:left;">
<img src="./cl1p-inc-rgdm/images/cl1p_title.gif" alt="cl1p the internet clipboard">
</div>
<div>
<h2 class="bl">&nbsp;&nbsp;CL1P.net - The Internet Clipboard</h2>
</div>
</td>
<td align="right" valign="top">
</td></tr></table>
<tr>
<td valign="top">
<table><tr><td>
<br/>
<a href="/cl1p-inc-rgdm/theend.jsp"><H2>cl1p.net will be closing forever on Oct 31, 2011</H2></a>
<p class="pr">
<a href="/cl1p-admin/menu.jsp">login</a>&nbsp;&nbsp;&nbsp;<a href="http://twitter.com/cl1p">Twitter</a>
</p>
<p class="pr"></p>

</td></tr></table>
</td>
</tr></table>

<br/><br/>
<a href="/privacy.jsp">Privacy Policy</a>&nbsp;<a href="/terms_of_service.jsp">Terms of Service</a>

</center>
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
