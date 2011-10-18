 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.diodesoftware.scb.clipboard.DisplayUrlLogic" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.diodesoftware.scb.*" %>
<%@ page import="com.diodesoftware.scb.tables.*" %>

<%@ page import="com.diodesoftware.scb.agents.UserAgent" %>
<%@ page import="java.net.URLEncoder" %>
<%@ include file="header.jsp" %>

<script language="javascript" src="/cl1p-inc-rgdm/ajax-engine.js"></script> 
<%   String defaultValue = "Paste any text you want here.";
 %>
<script language="javascript">
    function clearIfEmpty(field) {
        if (field.value == '<%= defaultValue %>') {
            field.value = "";
        }
    }

    function upload_file_start(){
         $('uploadBrowse').style.display='none';
                    $('uploadMsg').innerHTML = '<p>Uploading</p>';
        var f = document.aform;
        f.target = "UploadTarget";
        setTimeout('upload_update()', 1000);
        f.submit();
    }

    function upload_update(){
        var url = '<%= clip.getUri() %>?ajaxUpload=yes&key=<%= URLEncoder.encode(clipRequest.getUri())%>';
        http( "GET" , url ,ajax_upload_update );
     }

    function ajax_upload_update(text) {
         var myObject =  eval('(' + text + ')'); 
        if (myObject.msg && myObject.error) {

            $('uploadMsg').innerHTML = '<p>' + myObject.msg + '</p>';
            $('uploadBrowse').style.display='block';
        } else {
            $('uploadMsg').innerHTML = '<p>' + myObject.uploaded + 'MB of ' +
                                       myObject.total + 'MB ' + myObject.percent + '%</p>';
        }
        
        if (myObject.askAgain){
            setTimeout('upload_update()', 1000);
        }else if(!myObject.error){
            var form = document.aform;
            form.target=null;
	    var msg = "<a href='<%= UrlWriter.write(clip.getUri(), "download", request)%>'>Download File</a>";
            $('uploadMsg').innerHTML = '<p>' + msg + '</p>';
        }
    }
    


function printCl1p() {

   var s = document.aform.ctrlcv.value;

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
<%
    String header = request.getHeader("user-agent");
    boolean IE = false;
    if(header != null){
        header = header.toLowerCase();
        IE = (header.indexOf("msie")!=-1);
    }
    IE = false;//disable for now
%>

<form method="post" name="aform" action="<%= clip.getUri() %>" enctype="multipart/form-data">
<input type="hidden" name="deleteFile" id="deleteFile" value="no"/>
<center>
<%
	String jspFile = clip.getIncludeJSP();
    jspFile += "_edit.jsp";
%>
<jsp:include flush="true" page="<%=jspFile %>"></jsp:include>


<table>

<tr>
<td valign="top">
<table border="0" cellspacing='0' cellpadding='0'>
<tr><td class="leftCell"></td>
<td class="midCell" valign="top">
<center>
<br>
<table>
<tr><td colspan="2"><b>Options</b></td></tr>
<tr>
    <td>Title:</td>
    <td><input type="text" maxsize="100" name="title" value="<%= title %>"></td>
</tr>
<%
    if(clipRequest.isOwned()){
        %>
<tr><td colspan="2">
     Permanent cl1p
<input type="hidden" name="keepfor" value="-1">
</td></tr>
<%
    }else{
%>
<tr>

<td>Keep for:</td>
<td>
    <select name="keepfor">
        <%

            if (clipSession.isPro()) {

            }
            int[] keepForValues = {60, 120, 480, 1440, 2880, 10080, 20160, 
            440 * 31,
                        1440 * 31 * 2,
                        1440 * 31 * 3,
                        1440 * 31 * 6,
            525600};
            String[] lables = {
                    "1 Hour",
                    "2 Hours",
                    "8 Hours",
                    "1 Day",
                    "2 Days",
                    "1 Week",
                    "2 Weeks",
                    "1 Month",
                        "2 Months",
                        "3 Months",
                        "6 Months",
                    "As long as possible"
            };

            if (clipSession.isPro()) {
                keepForValues = new int[]{60,
                        120,
                        480,
                        1440,
                        1440 * 2,
                        1440 * 7,
                        1440 * 14,
                        1440 * 31,
                        1440 * 31 * 2,
                        1440 * 31 * 3,
                        1440 * 31 * 6,
                        525600,
                        -1
                };
                lables = new String[]{
                        "1 Hour",
                        "2 Hours",
                        "8 Hours",
                        "1 Day",
                        "2 Days",
                        "1 Week",
                        "2 Weeks",
                        "1 Month",
                        "2 Months",
                        "3 Months",
                        "6 Months",
                        "1 Year",
                        "Until I delete it"
                };
            }

            boolean foundOption = false;
            for (int i = 0; i < keepForValues.length; i++) {
                int val = keepForValues[i];
                String selected = "";

                if (val == clip.getKeepFor()) {
                    selected = " Selected ";
                    foundOption = true;
                }
        %>
        <option value="<%= val%>" <%= selected%>><%= lables[i] %>
        </option>
        <%
            }
            if (!foundOption) {
                keepForValues = new int[]{

                        1440 * 31,
                        1440 * 31 * 2,
                        1440 * 31 * 3,
                        1440 * 31 * 6,
                        1440 * 31 * 12,
                        -1
                };
                lables = new String[]{
                        "1 Month",
                        "2 Months",
                        "3 Months",
                        "6 Months",
                        "1 Year",
                        "Until I delete it"
                };
                for (int i = 0; i < keepForValues.length; i++) {
                    int val = keepForValues[i];
                    String selected = "";
                    if (val == clip.getKeepFor()) {
                        selected = " Selected ";
                        foundOption = true;
                    }
        %>
        <option value="<%= val%>" <%= selected%>><%= lables[i] %>
        </option>
        <%
                }

		if(!foundOption){
 %>
        <option value="<%= clip.getKeepFor() %>" SELECTED >As long as possible
        </option>
        <%
		}
            }
        %>
    </select>

</td>

</tr>
<%
    }
%>
</table>
</center>

</td>
<td class="rightCell">

</td></tr></table>
<%

%>
</td>



    <td valign="top">
<table border="0" cellspacing='0' cellpadding='0'>
<tr><td class="leftCell"></td>
<td class="midCell" valign="top">
        <%
            String password = clipRequest.getParameter(HtmlParam.PASSWORD);
            // When passwords don't match don't display the password again
            if (password != null && clipRequest.getAttribute(DisplayUrlLogic.DONT_DISPLAY_PASSWORD) != null)
                password = null;

            if (password == null)
                password = "";
        %>
        <center>
            <br/>
        <table>
            <tr><td colspan="2"><b>Security</b></td></tr>
            <tr>
                <td>Edit Password (Optional):</td>
                <td><input type="password" name="p1" maxlength="18" value="<%= password %>">
                   </td>
            </tr>
            <tr>
                <td>Verify Password:</td>
                <td><input type="password" name="p2" maxlength="18" value="<%= password %>">
                </td>
            </tr>
            <tr>
                <td>Remove Password:<input type="checkbox" name="removePassword" value="yes">
                   </td>
                <td>Restrict
                    views:
                    <%
                        String restrictViews = "";

                        if (clip.getViewPassword()) {
                            restrictViews = " checked ";
                        }
                    %>
                    <input type="checkbox" <%= restrictViews %> name="viewPassword"
                            >
                </td>
            </tr>
            <%
                if (clipSession.isPro()) {
                    String checkedSSL = "";
                    if (clip.isSecure()) checkedSSL = " checked ";
            %>
            <tr>
                <td coslpan='2'>Force SSL (https): <input type="checkbox" name="forceSSL" value="YES" <%= checkedSSL %>>
                </td>
            </tr>
            <%
                }
            %>
        </table>
            </center>
</td>
<td class="rightCell">

</td></tr></table>
    </td>
<td valign="top">
<table border="0" cellspacing='0' cellpadding='0'>
<tr><td class="leftCell"></td>
<td class="midCell" valign="top">
   <br/> <B>Account</b><br/>
    <%= AdminController.getClipLoginText(clipRequest)%>
 </td>
<td class="rightCell">

</td></tr></table>

</td>
<td valign="top">
    <%
        if (clipRequest.isSellable()) {
    %>
 <table border="0" cellspacing='0' cellpadding='0'>
<tr>
<td class="buyUrlCell" valign="top">
    <table border="0">
        <tr><td colspan="2" class="buyUrlTitle"><a href="/cl1p-admin/buy-url-2.jsp" class="buyUrlTitle">Buy this cl1p!</a></td></tr>
        <tr><td>
            <ul><li class="buyUrlText">cl1p never erased</li>
<li class="buyUrlText">30Mb upload</li>
<li class="buyUrlText">No ads</li>
              <li class="buyUrlText"> <a href="/cl1p-admin/buy-url-2.jsp" class="buyUrlText">and more</a></li>
                </ul>
</td>
            <td valign="bottom"><a href="/cl1p-admin/buy-url-2.jsp"><img src="/cl1p-inc-rgdm/images/buy-urls-star.gif" border="0" alt="Buy this cl1p only 99� a year!"/></a>
</td></tr>
    </table>

</td>
 
</tr></table>


    <%
        }
        if(clipRequest.isOwner()){
            %>


<a href="/cl1p-admin/history.jsp?uri=<%= URLEncoder.encode(clip.getUri())%>">View History</a>
    <%
        }else{
    %>

    <%
        }
    %><br>

</td>

    </tr><table>
   <a href="/guide.html">User Guide</a> <br>
<br>



</td>

</tr>
</table>
<%
    if(clipRequest.showAds()){
%>
<%
    }
%>
</center>

<input type="hidden" name="removeLink" id="dl" value="">
<input type="hidden" name="jump" value="<%= clip.getUri() %>">
</form>

<form method="post" name="logout">

    <input type="hidden" name="lusername" value="">
    <input type="hidden" name="lpassword" value="">
</form>
<script type="text/javascript">

</script>
<%@ include file="footer.jsp" %>
