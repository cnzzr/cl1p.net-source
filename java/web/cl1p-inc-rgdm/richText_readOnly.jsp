 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
	ClipRequest clipRequest = ClipRequest.getClipRequest(request);
	Clip clip = clipRequest.getClip();
	RichTextType clipType = (RichTextType)clipRequest.getCl1pType();
	String data = clip.getValue();
	
%>

<%@page import="com.diodesoftware.scb.clipboard.DisplayUrlLogic"%>
<%@page import="com.diodesoftware.scb.filter.ClipFilter"%>
<%@page import="com.diodesoftware.scb.tables.Clip"%>


<%@page import="com.diodesoftware.scb.clipboard.RichTextType"%>
<%@page import="com.diodesoftware.scb.tables.RichText"%>
<%@ page import="com.diodesoftware.scb.tables.ClipS3Object" %>
<%@ page import="java.util.List" %>
<%@ page import="com.diodesoftware.scb.tables.DownloadCount" %>
<%@ page import="com.diodesoftware.scb.*" %>
<%@ page import="java.util.Iterator" %>
<table cellspacing="0" cellpadding="5" width="100%">
 <tr>
  <td valign="top" class="bboxl">
   <table width="100%">
    <tr>
     <td width="100%" height="80%">
          <%
            List urls =  ClipS3Object.listPictureUrls(clip, clipRequest.getCon());
            Iterator iter = urls.iterator();
            while(iter.hasNext()){
                PictureInfo pi = (PictureInfo)iter.next();
                %>
        <a href="<%= pi.getUrl() %>" target="_blank" onmouseover="Tip('Click to view full picture');">
        <img width="<%= pi.getSmallWidth() %>" height="<%= pi.getSmallHeight() %>" src="<%= pi.getUrl() %>" alt="<%= pi.getName()%>"/>
        </a><%
                %><input type='button' value='X' onmouseover="Tip('Delete this picture');" onclick='deleteFileNow( <%= pi.getNumber() %> );'/><%

            }
        %>
     <div  style="border:1px solid black;">
<%= data %>
</div>
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
                                    String link = "";
                                    List files = ClipS3Object.listFiles(clipRequest.getClip(), clipRequest.getCon());

    if (files.size() > 0) {

        for (int i = 0; i < files.size(); i++) {
            ClipS3Object cs3 = (ClipS3Object) files.get(i);
            String downloadUrl = "<br/><a href='"
                    +
                    UrlWriter.write(clip.getUri() + "?t=" +
                            DownloadTokenMgr.genToken(clip.getNumber())
                            + "&FILE=" + cs3.getNumber()
                            , "download", request);

            link += downloadUrl
                    + "'>Download "
                    + cs3.getName()
                    + "</a>"
                    + DownloadCount.getDownloadsLeft(clip, clipRequest
                    .getCon()) + " downloads left";
        }
    }
                                %> <%=link%></td></tr>
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
