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
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<script type="text/javascript" src="/cl1p-inc-rgdm/tiny_mce/tiny_mce_src.js"></script>
<script language="javascript" type="text/javascript">
	// Notice: The simple theme does not use all options some of them are limited to the advanced theme
	tinyMCE.init({
               mode : "textareas",
            elements : "ctrlcv",
        width:"100%",
        height:"500px",
        theme : "advanced",
        plugins : "print",
        theme_advanced_toolbar_align : "left",
        theme_advanced_toolbar_location : "top",
        theme_advanced_buttons1 : "bold,italic,underline,strikethrough,bullist,numlist,outdent,indent,cut,copy,paste,undo, redo, link, unlin\
k,image,forecolor,backcolor,charmap",
            theme_advanced_buttons2 : "formatselect,fontselect,fontsizeselect,justifyleft,justifycenter,justifyright,justifyfull",
        theme_advanced_buttons3_add : "print",
        handle_event_callback : "dude",
        init_instance_callback : "mce_now_loaded"

    });
    function dude(){
        rgdm.stateMonitor.changed();

    }
</script>




<table cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td valign="top" style="border:solid black 1px;height:500px;">
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



            <textarea onkeypress="rgdm.stateMonitor.changed();"   id="ctrlcv"  width="100%" height="100%"                    
                      name="ctrlcv" cols="80"
                      onfocus="clearIfEmpty(this);" rows="18"
                      class="richTextStyle"><%= textAreaValue %></textarea>
    	</td>
	</tr>
	<tr>
	<td>
		<table width="100%">
    	<tr>
        	<td>
            <b id="warnDiv"><%= request.getAttribute("message") %>
            </b>
        	</td>
        	<td align="right" valign="top">
            	<input type="submit" value="Save">
        	</td>
    	</tr>
        <tr><td>
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
                                %> <%=link%>
        </td></tr>
    </table>
	</td>
	</tr>
	</table>
<input type="hidden" id="ctrlcv_rich" name="ctrlcv_rich"/>


<script type="text/javascript">
    var editor = null;
    function mce_now_loaded(mce){
        editor = mce;
        
    }

function cl1p_beforeSave(){

        var value = tinyMCE.getContent("ctrlcv");
        $("ctrlcv_rich").value = value;  
    
}


    function cl1p_setValue(newValue){
            $("ctrlcv").value = newValue;
            if(editor)
                editor.getBody().innerHTML = newValue;
    }

</script>





	
