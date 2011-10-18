 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.clipboard.DisplayUrlLogic" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.diodesoftware.scb.*" %>
<%@ page import="com.diodesoftware.scb.tables.*" %>
<%@ page import="com.diodesoftware.scb.agents.*" %>
<%@ page import="com.diodesoftware.scb.*" %>
<%@page import="com.diodesoftware.scb.clipboard.ViewMode"%>
<%@page import="com.diodesoftware.scb.clipboard.ForumType"%>
<%
ClipRequest clipRequest = ClipRequest.getClipRequest(request);
ClipSession clipSession = ClipSession.getSession(request);
Clip clip = Clip.getClip(request);
%>
<%
	DateFormat dateFormat = DateFormat.getDateInstance();
	ForumType forumType =(ForumType)clipRequest.getCl1pType();        
	Forum forum = forumType.getForum();
	ForumComment[] comments = forumType.getComments();
%>


<%@page import="java.text.DateFormat"%>
<p>Message Board is not available for Safari Mobile</p>
</form>