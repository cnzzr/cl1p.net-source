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
<%
    String defaultValue = "";
	RichTextType clipType = (RichTextType)clipRequest.getCl1pType();
	String data = clip.getValue();
    String textAreaValue = ((clip.getValue().length() == 0) ? defaultValue : data);
    //textAreaValue = textAreaValue.replaceAll("</\\s*textarea", "textarea");
    Pattern p = Pattern.compile("</\\s*textarea", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(textAreaValue);
    textAreaValue = m.replaceAll("textarea");
%>

	<%@page import="com.diodesoftware.scb.clipboard.RichTextType"%>
<p>Rich Text is not available for Safari Mobile</p>
 </form>