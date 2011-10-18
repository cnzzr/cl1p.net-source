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
    String handle = clipRequest.getParameter("handle");
    if(handle != null){
        session.setAttribute("handle", handle);
    }
    handle = (String)session.getAttribute("handle");
    if(handle == null)handle = "N/A";
%>


<%@page import="java.text.DateFormat"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<table cellspacing="0" cellpadding="0" width="100%">
	<tr>
    	<td valign="top" >
        	<table width="100%" cellpadding="0" cellspacing="0">
                <tr>
             

                <td  class="textC">
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
<%
	boolean canDelete = clipRequest.getViewMode() == ViewMode.EDIT;
%>                
<table border="1" width="100%" style="border-collapse: collapse;border:1px solid black;">


<tr><td  style="border:1px solid black;background-color:#eeeeee;" colspan="<%= (canDelete?3:2) %>"  align="center"><%= clip.getTitle() %></td></tr>
<%
	String odd = "#FFFFFF";
	String even = "#EEEEFF";
	String bgColor = odd;
	boolean oddRow = true;
	if(comments.length == 0){
%>		
<tr><td style="width:100%;height:400px;font-size:16px;" valign="top" bgcolor="<%= bgColor %>">		
Enter in your comment below.<br/>
Set a password in <i>options</i> to restrict comment editing.<br/>
Set a title using the <i>options</i> button.<br/>
</td></tr>
<%
	}
	for(int i = 0; i < comments.length;i++){
		ForumComment comment = comments[i];				
%>                
<tr>
<%
if(canDelete){
%>
<td bgcolor="<%= bgColor %>">Delete?&nbsp;<input type="checkbox" value="yes" name="<%= ForumType.DELETE_COMMENT + comment.getNumber() %>"></td>
<%
}
%>
<td style="width:100%;font-size:16px;" bgcolor="<%= bgColor %>">
<table width="100%"><tr><td valign="top">
<i><script type="text/javascript">$dt(<%= comment.getCommentDate().getTime().getTime() %>);</script></i>&nbsp;&nbsp;</td><td valign="top"><b><i><%= comment.getHandle() %>:</i></b></td><td width="100%"  valign="top" style="font-size:16px;">
<%= comment.getComment() %></td></td></tr></table>
</tr>
<%
		oddRow = !oddRow;
		if(oddRow)
			bgColor = odd;
		else
			bgColor = even;
	}
%>
</table>
                </td>
             
            	</tr>
	        </table>
    	</td>
	</tr>
	<tr>
		<td style="border:1px solid black;background-color:#eeeeee;">
<table width="100%">
<tr><td width="100%">	
<%= AvoidRepost.getHiddenField(request,"forum") %>
Handle:<input type="text" maxlength="100" size="20" name="handle" value="<%= handle %>"><br/>
<textarea name="<%= ForumType.ADD_COMMENT %>" id="msgTextarea" rows="<%=clip.getRows()%>" class="ohw">
</textarea><br/>
<script type="text/javascript">
    function updateRowSize(select){
        var r = select.value;
        $('msgTextarea').rows = r;
    }
</script>
 Size: <select name="rows" onchange="updateRowSize(this)">
					<%
						int[] rows = Clip.ROWS;
						for (int i = 0; i < rows.length; i++) {
							String selected = (rows[i] == clip.getRows()) ? "selected" : "";
					%>
					<option value="<%= rows[i] %>" <%= selected %>><%=rows[i]%>
					Rows</option>
					<%
					}
					%>  </select>Plain text. No HTML allowed.
</td></tr>

<%
if(canDelete){
%>
<tr><td>
Last Post First <input type="checkbox" value="yes" name="<%= ForumType.LAST_POST_FIRST %>" <%= (forum.isLastCommentFirst()?"checked":"") %>>
</td></tr>
<%
}else{
%>
<tr><td>
Admin Password:<input type="password" name="p1" maxlength="18"></td></tr>
<%
}
%>
<tr><td align="right">
    <input type="button" value="Refresh" onclick="document.location='<%= clip.getUri() %>'">
    <input type="submit" value="Save">

</td>
</tr>
</table>
		</td>
	</tr>
</table>
<div>

<%
	if(clipRequest.showAds()){
%>

<%
	}

%>
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
                    + "</a> ";

            if(canDelete){
                   link += "<input type='button' value='Delete File' onclick='deleteFileNow(\"" + cs3.getNumber() + "\");'/> ";
            }
            link        += DownloadCount.getDownloadsLeft(clip, clipRequest
                    .getCon()) + " downloads left";
        }
    }
                                %> <%=link%></div>

<%
if(canDelete){
%>
	<script language="javascript">
window.onLoad(function(){

$("theSaveButton").value = "Save";});
</script>
<%
}
%>
