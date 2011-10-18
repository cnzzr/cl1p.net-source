 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.HtmlParam"%>
<%@ page import="com.diodesoftware.scb.clipboard.DisplayUrlLogic"%>
<%@ page import="com.diodesoftware.scb.filter.ClipFilter"%>
<%@include file="header.jsp" %>
<script language="javascript">
function printCl1p() {

   var s = document.aform.dummy_data.value;

   var regExp=/\n/gi;

   s = s.replace(regExp,'<br>');

   pWin = window.open('','pWin','location=yes, menubar=yes, toolbar=yes');

   pWin.document.open();

   pWin.document.write('<html><head></head><body>');

   pWin.document.write(s);

   pWin.document.write('</body></html>');

   pWin.print();

   pWin.document.close();

   pWin.close();

}
</script>
<form method="post" name="aform" action="<%= clip.getUri() %>" enctype="multipart/form-data" >
<center>

<table cellspacing="0" cellpadding="5" width="100%">
 <tr>
  <td valign="top" class="bboxl">
   <table width="100%">
    <tr>
     <td width="100%" height="80%">
<textarea name="dummy_data" cols="80" rows="<%= clip.getRows() %>" class="ohw"><%= clip.getValue() %></textarea>
     </td>
    </tr>
   </table>
  </td>
 </tr>
</table>
</center>

<table width="100%">
<tr>
<td>

</td>
<td>
<b><%= request.getAttribute("message")%></b>
</td>
<td>

</td></tr>
</table>

<table width="100%">
<tr>
<td align="right">
<table><tr><td>
<div style="border:solid 1px black;background:#eee;">
<table><tr><td colspan="2">
    <%
        String readOnlyLink = "";
        String link = "<input type=\"file\" name=\"uploadFile\"><input type=\"submit\" value=\"Upload\">";
       
    %>
<%= readOnlyLink%></td></tr>
<tr>
<tr>
 <td>Edit Password:</td>
    <%
 String password = clipRequest.getParameter(HtmlParam.PASSWORD);
        // When passwords don't match don't display the password again
        if (password != null && clipRequest.getAttribute(DisplayUrlLogic.DONT_DISPLAY_PASSWORD) != null)
            password = null;

        if (password == null)
            password = "";
    %>
 <td><input type="password" name="p1" maxlength="18" value="<%= password %>"></td>
 <td><input type="submit" value="Edit"><input type="button" value="Print" onclick="printCl1p();"></td>
</tr>
</table>
</div>
</td></tr></table></td></tr></table>



<input type="hidden" name="jump" value="<%= clip.getUri() %>">
 </form>
<%@include file="footer.jsp" %>