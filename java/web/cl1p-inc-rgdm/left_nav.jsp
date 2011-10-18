 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.*" %>
<%@ page import="com.diodesoftware.scb.agents.UserAgent" %>
<%@ page import="com.diodesoftware.scb.clipboard.ClipType" %>
<%@ page import="com.diodesoftware.scb.clipboard.ViewMode" %>
<%@ page import="com.diodesoftware.scb.filter.ClipFilter" %>
<%@ page import="com.diodesoftware.scb.tables.Clip" %>
<%@ page import="com.diodesoftware.scb.tables.ClipS3Object" %>
<%@ page import="com.diodesoftware.scb.tables.DownloadCount" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>
<%
    Clip clip = Clip.getClip(request);
    String title = clip.getTitle();
    if (title == null) {
        title = "cl1p.net";
    }
    ClipRequest clipRequest = ClipRequest.getClipRequest(request);
    ClipSession clipSession = ClipSession.getSession(request);
    boolean editable = clipRequest.getViewMode() == ViewMode.EDIT;
    String msg = (String) request.getAttribute("login_message");
    if (msg != null && msg.trim().length() > 0 && !"&nbsp;".equals(msg)) {
//    	request.setAttribute("errorMessage", msg);
    }
    int currentType = clip.getClipType();


%>
<%

    if (ViewMode.EDIT == clipRequest.getViewMode()) {
%>

<div style="border:1px solid black;background-color:#F0F0F0">


<input type="hidden" name="currentType" value="<%= currentType %>">
<a border="0" href="javascript:popup('typePopup');" onmouseover="Tip('Change the cl1p type to:<br/>Plain Text,Rich Text, or Message Board')"><img border="0"  src="/cl1p-inc-rgdm/images/wizard_48.gif" alt="Cl1p Type"/></a>


<div class="popup" id="typePopup" style="width:200px; height:50px;">
Cl1p Type:
<select name="changeType" id="changeType" onchange="changeTheType();">
    <%

        for (int i = 0; i < ClipConfig.MAX_TYPE; i++) {
            String name = Clip.CLIP_TYPE_NAMES[i];
            String selected = (i == currentType) ? "selected" : "";
    %>
    <option value="<%= i %>" <%= selected %>><%= name %>
    </option>
    <%
        }
    %>
</select>
    <div class="close-button-div">
        <input type="button" class="close-button" value="Close" onclick="closePopup('typePopup');"/>
    </div></div>
<br/>

<script language="javascript">
    function changeTheType() {
        var show = debug();
        if(!show){
           show = confirm("Changing types will clear all data from this cl1p. Are you sure?");
        }
        if (show) {
            $('changeTypeForm').changeType.value = document.aform.changeType.value
            $('changeTypeForm').submit();
        } else {
            var ops = document.aform.changeType.selectedIndex = document.aform.currentType.value;
        }
    }
</script>
<a border="0" onclick="javascript:popup('optionsPopup')" style="cursor:pointer;" id="optionsLink" onmouseover="Tip('Options. Click to change')">
     <img border="0"  src="/cl1p-inc-rgdm/images/gear_48.gif" alt="Options"/>
     </a><br/>
<%
					String max = GLOBAL.UPLOAD_MAX + "";
					if (clipRequest.isOwned()) {
						max = GLOBAL.UPLOAD_MAX_OWNED + "";
					}
					boolean maxReached = false;
					if (clipSession.isPro()) {
						max = GLOBAL.UPLOAD_MAX_PRO + "";

						if (UserAgent.getInstance().uploadLimitReached(
						clipSession.getUser(), config.getServletContext(),
						clipRequest.getCon())) {
							maxReached = true;
						}
					}
				%>
<%
	if(clip.getOwnerId() > 0)
	{
%>
<a border="0" href="javascript:showFileUpload();" onmouseover="Tip('Upload files');" onclick="showFileUpload();return false;"><img src="/cl1p-inc-rgdm/images/folder_up_48.gif" alt="Upload Files" border="0"/></a><br/>
<script type="text/javascript">
    function showFileUpload(){
        popup('fileUploadDiv');
    }
</script>
<div id="fileUploadDiv"  style="width:400px;height:150px;" class="popup">
                 <table>
                     <tr>
                         <%
                         if (!maxReached) {
                         %>
                         <td colspan="2">Upload a file (<%=max%> MB Max)</td>
                         <%
                         } else {
                         %>
                         <td colspan="2">You have uploaded over 100MB. You must
                         delete cl1ps or download files.</td>
                         <%
                         }
                         %>
                     </tr>
                     <tr>
                         <td colspan="2"><iframe id="UploadTarget"
                             name="UploadTarget" src=""
                             style="width:0px;height:0px;border:0"></iframe>
                         <div id="uploadBrowse">
                         <%
                             String link = " <input type='file' name='uploadFile'><input type='button' value='Upload' onclick='upload_file_start();'>";
                             if (ClipFilter.hasFile(clipRequest)) {
                                  List files = ClipS3Object.listFiles(clipRequest.getClip(), clipRequest.getCon());

                                 for(int i = 0; i < files.size(); i++){
                                     ClipS3Object cs3 = (ClipS3Object)files.get(i);
                                 String downloadUrl = "<br/><a href='"
                                                                                 +
                                         UrlWriter.write(clip.getUri() + "?t=" +
                                                 DownloadTokenMgr.genToken(clip.getNumber())
                                                 + "&FILE=" + cs3.getNumber()
                                                 , "download", request);

                                 link += downloadUrl
                                         + "'>Download "
                                         + cs3.getName()
                                         + "</a> <input type='button' value='Delete File' onclick='deleteFileNow(\"" + cs3.getNumber() +"\");'/> "
                                         + DownloadCount.getDownloadsLeft(clip, clipRequest
                                         .getCon()) + " downloads left";
                                 }
                             }
                         %> <%=link%></div>

                         <div id="uploadMsg"></div>
                           <input type="button" id="uploadMoreBtn" value="Add More Files" onclick="appendDownloaded();"/>
                         </td>
                     </tr>
                 </table>
    <div class="close-button-div">
        <input type="button" class="close-button" value="Close" onclick="closePopup('fileUploadDiv');"/>
    </div>
</div>

<%
	}
    } %>
    <%
    boolean share = false;
    if(share){
    %>
<a border="0" href="javascript:showShare();" onmouseover="Tip('Share this CL1P');" onclick="showShare();return false;"><img src="/cl1p-inc-rgdm/images/mail_fav_48.gif" alt="Share this CL1P" border="0"/></a><br/>
<script type="text/javascript">
    function showShare(){
        popup('sharePopup');
    }
</script>
<div class="popup" id="sharePopup" style="width:500px; height:200px;">
Share this Cl1P!<br/>
    E-mail: <input type="text" name="shareEmail" id="shareEmail"><br/>
    Subject: <input type="text" name="shareSubject" id="shareSubject" value="Check out http://cl1p.net<%= clip.getUri()%>"><br/>
    Message:
    <textarea rows="5" cols="60" name="shareMsg" id="shareMsg">
Check out http://cl1p.net<%= clip.getUri()%>

What's CL1P? CL1P is the revolutionary new way people are accessing their information online. Its like Copy and Paste only anywhere in the world. All you do is make your CL1P (example, http://www.CL1P.net/YOURNAME), paste text or upload a file, then from another computer navigate back to your CL1P.
Easy? Oh yeah. And it's all FREE!
    </textarea>  <br/>
     <span id="shareResponse"></span>
    <div class="close-button-div">
        <input type="button" class="close-button" value="Close" onclick="closePopup('sharePopup');"/> &nbsp; <input type="button" value="Send" onclick="sendShare();"/>
    </div></div>
<%
    }
    if(editable){
    if(currentType == Clip.CLIP_TYPE_PLAIN_TEXT){
%>


<a border="0" href="javascript:popup('sizePopup')" onmouseover="Tip('Change the number of rows displayed');"><img border="0" src="/cl1p-inc-rgdm/images/preview_zoom_48.gif" alt="Change Size"/></a>
<div class="popup" id="sizePopup" style="width:200px;height:50px">
Size: <select name="rows" onchange="$('aform').submit()">
					<%
						int[] rows = Clip.ROWS;
						for (int i = 0; i < rows.length; i++) {
							String selected = (rows[i] == clip.getRows()) ? "selected" : "";
					%>
					<option value="<%= rows[i] %>" <%= selected %>><%=rows[i]%>
					Rows</option>
					<%
					}
					%>
				</select>
    <div class="close-button-div">
        <input type="button" class="close-button" value="Close" onclick="closePopup('sizePopup');"/>
    </div>
</div>

<br/>
<span id="activateLinks"> <a border="0" href="javascript:toggleMode()" onmouseover="Tip('Click to toggle modes:<br/>  Edit mode for editing.<br/>  Link Mode for clickable links.')"><img border="0" id="modeImg" src="/cl1p-inc-rgdm/images/link_broken_48.gif" alt="Link Mode"/></a> </span><br/>
<a onclick="printCl1p();return false;" border="0" onmouseover="Tip('Print This cl1p')"> <img border="0"
					border="0" src="/cl1p-inc-rgdm/images/printer_48.gif" alt="Print"></a><br/>

<div id="extrasPopup" class="popup" style="width:300px;height:100px;">
	<b>Extras</b><br/><br/>
CL1P Button:
    <a border="0"
            onclick="alert('Drag this link to your bookmark toolbar.');return false;"
            onmouseover="Tip('The CL1P button is an easy way to append text to a cl1p.<br/> To install just drag the CL1P Button to your browsers link toolbar.<br/>Then on any other site select some text you want to save and click the CL1P button.<br/> The text will be appended to the bottom of this cl1p, along with the URL it was copied from.<br/>')"
            href="javascript:(function(){(window.open('http://cl1p.net<%= clipRequest.getUri() %>?nl=yes&A=' + encodeURIComponent((window.getSelection?window.getSelection():document.getSelection?document.getSelection:document.selection?document.selection.createRange().text:'') + ' : ' + document.location), 'cl1p.net'));}())" >CL1P</a>

			<div class="close-button-div">
		        <input type="button" class="close-button" value="Close" onclick="closePopup('extrasPopup');"/>
		    </div></div>
    <br/>

<a border="0"  href="javascript:popup('extrasPopup')" onmouseover="Tip('Extra utilites')"><img border="0"  src="/cl1p-inc-rgdm/images/lamp_48.gif" alt="Extras"/></a><br/>
 <%

        if(clipRequest.isOwner()){
            %>
<a border="0" href="javascript:document.location='/cl1p-admin/history.jsp?uri=<%= URLEncoder.encode(clip.getUri())%>'" onmouseover="Tip('View History')"><img border="0"  src="/cl1p-inc-rgdm/images/clock_48.gif" alt="View History"/></a>
    <%
        }%>
<%

    }

if(clip.getOwnerId() > 0)
{
%>

<a border="0"  href="javascript:popup('pickPopup')" onmouseover="Tip('Add a picture')"><img border="0"  src="/cl1p-inc-rgdm/images/photocamera_48.gif" alt="Pictures"/></a>
<div id="pickPopup" class="popup" style="width:300px;height:100px;">
	<b>Pictures</b><br/><br/>
Add a picture to this cl1p<br/>
      <%
                             String upload = " <input type='file' name='uploadPic'><input type='button' value='Upload' onclick='upload_file_start();'>";

                         %> <%=upload%> <br/>
    <span id="picUploadMsg"></span>
            <div class="close-button-div">
		        <input type="button" class="close-button" value="Close" onclick="closePopup('pickPopup');"/>
		    </div></div>



</div>
<%
}
    }
%>
