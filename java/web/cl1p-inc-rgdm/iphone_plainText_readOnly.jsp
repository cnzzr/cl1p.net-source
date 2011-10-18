 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
	ClipRequest clipRequest = ClipRequest.getClipRequest(request);
	Clip clip = clipRequest.getClip();

String defaultValue = "";

	String textAreaValue = ((clip.getValue().length() == 0) ? defaultValue
			: clip.getValue());
	//textAreaValue = textAreaValue.replaceAll("</\\s*textarea", "textarea");
	Pattern p = Pattern.compile("</\\s*textarea",
			Pattern.CASE_INSENSITIVE);
	Matcher m = p.matcher(textAreaValue);

	textAreaValue = m.replaceAll("textarea");
    int rows = 100;
    int estimate = textAreaValue.length() / 46;
    if(estimate > rows)rows = estimate;
%>

<%@page import="com.diodesoftware.scb.ClipRequest"%>
<%@page import="com.diodesoftware.scb.HtmlParam"%>
<%@page import="com.diodesoftware.scb.clipboard.DisplayUrlLogic"%>
<%@page import="com.diodesoftware.scb.filter.ClipFilter"%>
<%@page import="com.diodesoftware.scb.tables.Clip"%>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="java.util.regex.Pattern" %>


<table cellspacing="0" cellpadding="5" width="100%">
 <tr>
  <td valign="top" class="bboxl">
   <table width="100%">
    <tr>
     <td width="100%" height="80%">
<textarea name="ctrlcv" cols="80" rows="<%= rows %>" class="ohw"><%= clip.getValue() %></textarea>
     </td>
    </tr>
   </table>
  </td>
 </tr>
</table>
</center>
<%
	if(clipRequest.showAds()){
%>
<table cellspacing="0" cellpadding="0" width="100%">
 <tr>
  <td bgcolor="#FFFADB">


</td></tr></table>
<%
}
%>
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
        if (ClipFilter.hasFile(clipRequest)) {
            String downloadUrl = "<a href='http://download.cl1p.net" + clip.getUri();
            link = downloadUrl + "'>Download File</a>";
            readOnlyLink = link;
        }
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
</td></tr></table>

</td></tr></table>


<input type="hidden" name="jump" value="<%= clip.getUri() %>">
 </form>
