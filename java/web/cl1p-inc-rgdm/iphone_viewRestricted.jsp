 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
	ClipRequest clipRequest = ClipRequest.getClipRequest(request);
    Clip clip = clipRequest.getClip();
%>

<%@page import="com.diodesoftware.scb.ClipRequest"%>
<%@page import="com.diodesoftware.scb.tables.Clip"%>
<%@ page import="com.diodesoftware.scb.email.EmailMgr" %>
<script type="text/javascript">
    function emailPasswordNow(){
        var f = $('aform');
        var e = $('emailPassword');
        e.value='yes';
        f.submit();

    }
</script>
<form method="post" id="aform" name="aform" action="<%= clip.getUri()%>" enctype="multipart/form-data" >

<center>

<table cellspacing="0" cellpadding="5" width="100%" height="200px">
 <tr>
  <td valign="top" class="bboxl">
   <table width="100%">
    <tr>
     <td width="100%">
Password: <input type="password" name="p1"  maxlength="18" value=""><input type="submit" value="View">

         <br>
<%
    boolean emailSent = "yes".equals(request.getParameter("emailPassword"));
    if (emailSent) {
        EmailMgr.getInstance().send("cl1p@cl1p.net",
                clip.getEmail(), "Lost password for cl1p.net" + clip.getUri(),
                "The password is " + clip.getEmailPassword() + ".",
                clipRequest.getCon());
%>
E-mail has been sent to <%= clip.getEmail() %>.         
         <%
    }else{
    if(clip.getEmail() != null && clip.getEmail().length() > 0){
        %>
Forgot password? <input type="button" value="E-mail the password" onclick="emailPasswordNow()">
         <%
    }
             }
%>
<P>This page requires a password to view.</p>
<p>If you wish to create a new cl1p try another URL that starts with http://cl1p.net</p>
     </td>
    </tr>
   </table>
  </td>
 </tr>
</table>
</center>




<input type="hidden" name="emailPassword" id="emailPassword" value="no">
<input type="hidden" name="jump" value="<%= clip.getUri() %>">
 </form>



