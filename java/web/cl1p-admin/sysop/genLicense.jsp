<%@ page import="com.diodesoftware.scb.licensegen.GenLicense" %>
<%
    String msg = "";
    if (request.getParameter("name") != null) {
        GenLicense.genEvalLicense(request.getParameter("email"),
                request.getParameter("name"),
                request.getParameter("companyName"), null);
        msg  = "Done";
    }
%>
<html>
<form>
    Name<input type="text" name="name"><br/>
    Email<input type="text" name="email"><br/>
    Comapany name<input type="text" name="comapnyName"><br/>
    <input type="submit" value="Gen"><br/>
        <%= msg %>
</form>
</html>