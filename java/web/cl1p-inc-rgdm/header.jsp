 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.dbmapper.DBMapper"%>
<%
    Clip clip = Clip.getClip(request);
    String title = clip.getTitle();
	if(title == null){
	title = "cl1p.net";
}
    ClipRequest clipRequest = ClipRequest.getClipRequest(request);
    ClipSession clipSession = ClipSession.getSession(request);
    boolean editable = clipRequest.getViewMode() == ViewMode.EDIT;
    String msg = (String)request.getAttribute("login_message");
    if(msg != null && msg.trim().length() > 0 && !"&nbsp;".equals(msg)){
//    	request.setAttribute("errorMessage", msg);    	
    }
    int currentType = clip.getClipType();
%>
<%@page import="com.diodesoftware.scb.*"%>

<%@page import="com.diodesoftware.scb.clipboard.DisplayUrlLogic"%>
<%@ page import="com.diodesoftware.scb.clipboard.ViewMode" %>
<%@ page import="com.diodesoftware.scb.filter.ClipFilter" %>
<%@ page import="com.diodesoftware.scb.tables.Clip" %>
<%@ page import="com.diodesoftware.scb.tables.Owner" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Calendar" %>

<table width="100%">
 <tr>
  <td valign="top">


<input type="hidden" name="deleteFile" id="deleteFile" value="no"/>
<input type="hidden" name="deleteFileNumber" id="deleteFileNumber" value="0"/>
<script type="text/javascript">
function openOptions(){
    show("optionsPopup");
}

function closeOptions(){
    hide("optionsPopup");
}


window.onLoad(function(){

});
window.onLoad(function(){



});
</script>
<span style="">
      <a
            href="<%= UrlWriter.write(clip.getUri(), "d", request)%>"><%= UrlWriter.write(clip.getUri(), null, request)%></a></span>

  </td>
  <td>

      <b id="warnDiv"><%=request.getAttribute("errorMessage")%>
				</b>
  </td>
  <td align="right">
      <%= AdminController.getClipLoginText(clipRequest)%>

  </td>
  </tr>
 <tr>
<td >
    <%
           if( ViewMode.EDIT == clipRequest.getViewMode()){
           %>
 
    <%

              if(clipRequest.isOwned()){
          %>

          <%
              }else{
                  if(editable){
          %>
  
<span>This cl1p will expire in <%= clip.daysLeft()%> days. <a href="javascript:popup('keepForPopup')">Change</a></span>

<div id="keepForPopup" class="popup" style="width:200px;height:75px">

             Keep For:

          <%= JSPCode.renderKeepFor(clipRequest, clipSession)%>
     <%

             if(clipRequest.showAds()){
   


           } %>
    <div class="close-button-div">
        <input type="button" class="close-button" value="Close" onclick="closePopup('keepForPopup');"/>
    </div>
    </div>
                <%
                    }}
                %>
    

    <%    }
           if (clip.getOwnerId() > 0) {
               Owner owner = (Owner) DBMapper.load(Owner.class, clip.getOwnerId(), clipRequest.getCon());
               Calendar end = owner.getEnd();
               Calendar today = Calendar.getInstance();
               int daysLeft = -1;

               if (today.after(end)) {
                   // You have expired.
                   daysLeft = 0;
               } else {
                   // You are about to expire
                   daysLeft = (int) ((end.getTimeInMillis() - today.getTimeInMillis()) / (long) (24 * 60 * 60 * 1000));
               }
               if (daysLeft != -1 && daysLeft < 30) {
                   String renewMsg = "";
                   if (daysLeft == 0) {
                       renewMsg = "This cl1p has expired. Click here to renew.";
                   } else {
                       renewMsg = "This clip will expire in " + daysLeft + " days. Click here to renew.";
                   }
                   String href = "/cl1p-admin/renew.jsp";
                   if (clipRequest.getUser() != null) {
                       if (owner.getUserId() == clipRequest.getUser().getNumber()) {
                           href = "/cl1p-admin/buy-url-2.jsp";
                       }
                   }
                   %>
   <span style="border:1px solid black;background-color:yellow;padding:2px;">
   <a href="<%= href%>" border="0" class="buyLink">
<%= renewMsg %>
    </a></span>
    <%
               }
           }

     %>

</td><td>



</td>
     <td colspan="2" align="right">

            <span id="currentStatus"></span>
         <% if(editable){%>
            &nbsp;&nbsp;<input id="theSaveButton" type="submit" value="Saved">&nbsp;&nbsp;
         <%             
             }
         %>
     </td></tr>

     <%
    if(clipRequest.showAds()){
%>
<%
    String cl1pMsg = ClipMsg.msg;
    if (cl1pMsg != null && cl1pMsg.trim().length() > 0) {
%>
 <tr><td colspan="4">    
<%= cl1pMsg %></td></tr>
<%
}
%>

    <%
        }

    %>
</table>



<input type="hidden" name="viewMode" value="<%= clipRequest.getViewMode() %>"/>
<input type="hidden" id="currentURI" name="uri" value="<%= clip.getUri() %>">
<%
if(editable){
%>


<div id="optionsPopup" class="popup"
style="width:400px;height:250px;">
<table>
	<tr>
		<td colspan="2"><b>Options</b></td>
	</tr>
	<tr>
    	<td>Title:</td>
    	<td><input   type="text" maxsize="100" name="title" value="<%= title %>"></td>
	</tr>
</table>
        <%
            String password = clipRequest.getParameter(HtmlParam.PASSWORD);
            // When passwords don't match don't display the password again
            if (password != null && clipRequest.getAttribute(DisplayUrlLogic.DONT_DISPLAY_PASSWORD) != null)
                password = null;

            if (password == null)
                password = "";
            String email = clip.getEmail();
            email = ClipUtil.blankNull(email);
        %>
        <table>
            <tr><td colspan="2"><b>Security</b></td></tr>
            <tr>
                <td>Edit Password (Optional):</td>
                <td><input id="password1" onblur="passwordsMatch()"  type="password" name="p1" maxlength="18" value="<%= password %>">
                   </td>
            </tr>
            <tr>
                <td>Verify Password:</td>
                <td><input id="password2" onblur="passwordsMatch()"  type="password" name="p2" maxlength="18" value="<%= password %>">
                </td>
            </tr>
            <tr><td colspan="2"><span class="errMsg" id="passwordErr"></span></td></tr>
            <tr>
                <td>Remove Password:<input  type="checkbox" name="removePassword" value="yes">
                   </td>
                <td>Restrict
                    views:
                    <%
                        String restrictViews = "";

                        if (clip.getViewPassword()) {
                            restrictViews = " checked ";
                        }
                    %>
                    <input   type="checkbox" <%= restrictViews %> name="viewPassword"
                            >
                </td>
            </tr>
            <tr><td>Email for lost password:</td><td><input type="text" name="email" value="<%= email %>"></td></tr>
            <%
                if (clipSession.isPro()) {
                    String checkedSSL = "";
                    if (clip.isSecure()) checkedSSL = " checked ";
            %>
            <tr>
                <td coslpan='2'>Force SSL (https): <input  type="checkbox" name="forceSSL" value="YES" <%= checkedSSL %>>
                </td>
            </tr>
            <%
                }
            %>
        </table>

   <br/>

<table width="100%"><tr><td align="right">
    <input type="button" name="clsops" value="Close" onclick="closePopup('optionsPopup')">
    <input type="submit" name="save" value="Save" >
    </td></tr></table>

</div>




<%
}//End if editable
%>
