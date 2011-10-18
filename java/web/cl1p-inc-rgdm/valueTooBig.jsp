 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.diodesoftware.scb.clipboard.ClipSaver"%>
<%@page import="org.apache.log4j.Logger"%><html>
<head>
<title>TOO BIG!</title>
<body>
<h1>
Whoa, Whoa!</h1>

<h2>Hold on there, the value you have tried to place is WAY TOO BIG!</h2>
<% NumberFormat nf = NumberFormat.getNumberInstance();%>
<p>The maximum size allowed is 	
	<%= nf.format(ClipSaver.MAX_VALUE_SIZE) %>.<p> 
<h3>Press the back button on your browser.</h3>
</body>
</html>