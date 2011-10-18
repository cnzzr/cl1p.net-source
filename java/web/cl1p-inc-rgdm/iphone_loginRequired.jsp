 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.ClipRequest" %>
<%@ page import="com.diodesoftware.scb.ClipSession" %>
<%@ page import="com.diodesoftware.scb.AdminController" %>
<%@ page import="com.diodesoftware.W" %>
<%
    ClipRequest clipRequest = ClipRequest.getClipRequest(request);
    ClipSession clipSession = ClipSession.getSession(request);
%>
<center>
<h2><%= W.w("login.required")%></h2>
<p><%= W.w("must.be.logged.in")%></p>
    <form method="post">
<div style="border:1px solid black; width:350px;height:50px;padding:5px;background-color:#dddddd;">
    <%= AdminController.getClipLoginText(clipSession, request)%>
</div>
        </form>
</center>