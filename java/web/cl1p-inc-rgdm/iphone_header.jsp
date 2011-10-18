 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.tables.Clip"%>
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
<%@page import="java.net.URLEncoder"%>

<%@page import="com.diodesoftware.scb.clipboard.ViewMode"%>
<%@ page import="com.diodesoftware.scb.*" %>
<%@ page import="com.diodesoftware.scb.filter.ClipFilter" %>
<%@ page import="com.diodesoftware.scb.clipboard.DisplayUrlLogic" %>
<%@ page import="com.diodesoftware.R" %>
<%@ page import="com.diodesoftware.Root" %>

<%@page import="com.diodesoftware.scb.tables.Owner"%>
<%@page import="com.diodesoftware.dbmapper.DBMapper"%>
<%@page import="java.util.Calendar"%><div style="display:none;">
<form method="post" name="changeType">
    <input type="hidden" name="changeType">  <input type="hidden" name="currentType" value="<%= currentType %>">
</form>
</div>
<form method="post" id="aform" name="aform" action="<%= clip.getUri() %>" enctype="multipart/form-data" accept-charset="UTF-8">
<a href="/">cl<font color="red">1</font>p.net</a>
<table width="100%">
 <tr>
  <td valign="top">
      <%
          if(clipRequest.isOwned()){
      %>

      <%
          }else{
              %>

      <%
              if(editable){
      %>

         Keep For:
          <%= JSPCode.renderKeepFor(clipRequest, clipSession)%>
      <%
              }
      }
      %>


  </td>
  <td>
      <%
                  if(ViewMode.EDIT == clipRequest.getViewMode()){
                      String password = clipRequest.getParameter(HtmlParam.PASSWORD);
                             // When passwords don't match don't display the password again
                             if (password != null && clipRequest.getAttribute(DisplayUrlLogic.DONT_DISPLAY_PASSWORD) != null)
                                 password = null;

                             if (password == null)
                                 password = "";

                  %>
      <input id="theSaveButton" type="submit" value="Save">
</td></tr>
 <tr><td>Password: <input id="password1"  type="password" name="p1" maxlength="18" value="<%= password %>"></td>
     <td>Verify: <input id="password2" onblur="passwordsMatch()"  type="password" name="p2" maxlength="18" value="<%= password %>"></td></tr>
      <input type="hidden" name="currentType" value="<%= currentType %>">
            <input type="hidden" name="changeType" value="<%= currentType %>">
 <tr><td>Remove Password:<input  type="checkbox" name="removePassword" value="yes"></td><td>    Restrict
                views:
                <%
                    String restrictViews = "";

                    if (clip.getViewPassword()) {
                        restrictViews = " checked ";
                    }
                %>
                <input   type="checkbox" <%= restrictViews %> name="viewPassword"
                    <%
                        }
                    %>
</td></tr>
<tr><td colspan="2">
<%
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

    <%
       String cl1pMsg = ClipMsg.msg;
       if (cl1pMsg != null && cl1pMsg.trim().length() > 0) {
   %>
   <%= cl1pMsg %></td></tr>
   <%
   }
   %>


</td></tr>
 </table>














<input type="hidden" name="viewMode" value="<%= clipRequest.getViewMode() %>"/>
<input type="hidden" id="currentURI" name="uri" value="<%= clip.getUri() %>">


