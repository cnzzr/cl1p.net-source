<%@ page import="com.diodesoftware.scb.ClipConfig" %>
<html>
<head>

</head>
<body>
Menu: RGDM-USERNAME-RGDM
<br>
<a href="/cl1p-admin/menu">menu</a><br>
RGDM-CLIPS-RGDM

<%
    if (ClipConfig.CL1P_SITE) {
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