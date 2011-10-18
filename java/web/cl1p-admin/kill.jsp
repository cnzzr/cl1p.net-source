<%
	session.invalidate();
	if(request.getParameter("url") != null){
		response.sendRedirect(request.getParameter("url"));
	}
%>