 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
	ClipRequest clipRequest = ClipRequest.getClipRequest(request);
	Clip clip = clipRequest.getClip();
	RichTextType clipType = (RichTextType)clipRequest.getCl1pType();
	String data = ((RichText)clipType.getData(clipRequest.getViewMode())).getValue();
	
%>

<%@page import="com.diodesoftware.scb.ClipRequest"%>
<%@page import="com.diodesoftware.scb.HtmlParam"%>
<%@page import="com.diodesoftware.scb.clipboard.DisplayUrlLogic"%>
<%@page import="com.diodesoftware.scb.filter.ClipFilter"%>
<%@page import="com.diodesoftware.scb.tables.Clip"%>


<%@page import="com.diodesoftware.scb.clipboard.RichTextType"%>
<%@page import="com.diodesoftware.scb.tables.RichText"%>
<p>Rich Text is not available for Safari Mobile</p>
 </form>