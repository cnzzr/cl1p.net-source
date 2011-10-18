 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.clipboard.DisplayUrlLogic" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.diodesoftware.scb.*" %>
<%@ page import="com.diodesoftware.scb.tables.*" %>
<%@ page import="com.diodesoftware.scb.agents.*" %>
<%@ page import="com.diodesoftware.scb.*" %>
<%
ClipRequest clipRequest = ClipRequest.getClipRequest(request);
ClipSession clipSession = ClipSession.getSession(request);
Clip clip = Clip.getClip(request);
%>
<table cellspacing="0" cellpadding="0" width="100%">
	<tr>
    	<td valign="top" >   	
			<div id="todoList">


			  <ul id="theTodoList">
    		<%
    			for(int i = 0; i < 50;i++){
    		%>
    		<li>
    		<table width="100%"><tr><td width="20">
    		<input type="checkbox" style="float:left;"></td><td width="100%"><input style="width:99%;" type="text" value="Todo Item <%= i %>"></td></tr></table></li>
    		<%
    			}
    		%>
    		</ul>
			</div>
        </td>
	</tr>
</table>
		
