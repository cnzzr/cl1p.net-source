<%@ page import="com.diodesoftware.scb.*"%>
<%@ page import="com.diodesoftware.scb.tables.Clip"%>
<%@ page import="com.diodesoftware.scb.tables.ClipS3Object" %>
<%@ page import="com.diodesoftware.scb.tables.DownloadCount" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="java.util.regex.Pattern" %>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
	ClipRequest clipRequest = ClipRequest.getClipRequest(request);
	ClipSession clipSession = ClipSession.getSession(request);
	Clip clip = Clip.getClip(request);
%>
<%
	String defaultValue = "Paste any text you want here.";

	String textAreaValue = ((clip.getValue().length() == 0) ? defaultValue
			: clip.getValue());
	//textAreaValue = textAreaValue.replaceAll("</\\s*textarea", "textarea");
	Pattern p = Pattern.compile("</\\s*textarea",
			Pattern.CASE_INSENSITIVE);
	Matcher m = p.matcher(textAreaValue);

	textAreaValue = m.replaceAll("textarea");
%>
<table cellspacing="0" cellpadding="0" width="100%">
<tr>
    <td valign="top" id="ctrlcv-container"class="textC">
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
        <textarea id="ctrlcv" name="ctrlcv" cols="80"
                                             onfocus="clearIfEmpty(this);"
                                             onkeypress="rgdm.stateMonitor.changed();" rows="<%=clip.getRows()%>"
			class="ohw"><%=textAreaValue%></textarea></td>
	</tr>

</table>
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
                    + "</a> <input type='button' value='Delete File' onclick='deleteFileNow(\"" + cs3.getNumber() + "\");'/> "
                    + DownloadCount.getDownloadsLeft(clip, clipRequest
                    .getCon()) + " downloads left";
        }
    }
                                %> <%=link%></div>

