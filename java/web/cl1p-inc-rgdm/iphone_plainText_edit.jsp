 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.clipboard.DisplayUrlLogic"%>
<%@ page import="java.util.regex.*"%>
<%@ page import="com.diodesoftware.scb.*"%>
<%@ page import="com.diodesoftware.scb.tables.*"%>
<%@ page import="com.diodesoftware.scb.agents.*"%>
<%@ page import="com.diodesoftware.scb.*"%>
<%@ page import="com.diodesoftware.scb.filter.ClipFilter" %>
<%@ page import="java.util.*" %>

<%
	ClipRequest clipRequest = ClipRequest.getClipRequest(request);
	ClipSession clipSession = ClipSession.getSession(request);
	Clip clip = Clip.getClip(request);
%>
<%
	String defaultValue = "";

	String textAreaValue = ((clip.getValue().length() == 0) ? defaultValue
			: clip.getValue());
	//textAreaValue = textAreaValue.replaceAll("</\\s*textarea", "textarea");
	Pattern p = Pattern.compile("</\\s*textarea",
			Pattern.CASE_INSENSITIVE);
	Matcher m = p.matcher(textAreaValue);
	List urls = new ArrayList();
	int start = textAreaValue.indexOf("http://");
	while(start != -1){
		int space = textAreaValue.indexOf(" ",start);
		if(space == -1){
			space = textAreaValue.indexOf("\n",start);
		}
		if(space != -1){
			urls.add(textAreaValue.substring(start,space));
			start = textAreaValue.indexOf("http://",++start);
		}else{
			urls.add(textAreaValue.substring(start));
			start = -1;
			
		}
		
	}
	textAreaValue = m.replaceAll("textarea");
    int rows = 100;
    int estimate = textAreaValue.length() / 46;
    if(estimate > rows)rows = estimate;
	Iterator urlIter = urls.iterator();
	while(urlIter.hasNext()){
		String u = (String)urlIter.next();
		%>
<a href="<%= u %>"><%= u %></a><br/>
<%
	}
%>

<textarea id="ctrlcv" name="ctrlcv" cols="80"
                                             onfocus="clearIfEmpty(this);"
                                             onkeypress="rgdm.stateMonitor.changed();" rows="<%=rows%>"
			class="ohw"><%=textAreaValue%></textarea>
</form>
