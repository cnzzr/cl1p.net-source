<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>RGDM-TITLE-RGDM</title>
<meta http-equiv="content-type"
      content="text/html; charset=ISO-8859-1">
<style>
    BODY {
        color: #000000;
        margin-top: 2px;
        margin-left: 2px;
        margin-right: 2px;
    }

    A, BODY, INPUT, OPTION, SELECT, TABLE, TD, TR, P, LI {
        font-family: arial;
        font-size: 12px;
    }

    A {
        font-family: arial;
        font-size: 12px;
        color: #000000;
        font-weight: bold;
        TEXT-DECORATION: underline;
    }

    .clipHead {
        font-family: arial;
        font-size: 32px;
    }

    .clipHeadSmall {
        font-family: arial;
        font-size: 24px;
    }

    .ohw {
        width: 100%;
    }

</style>
<script type="text/javascript" language="javascript">


var debug = false;
function GetXmlHttp() {
    var xmlhttp = false;
    if (window.XMLHttpRequest)
    {
        xmlhttp = new XMLHttpRequest();
    }
    else if (window.ActiveXObject)// code for IE
    {
        try
        {
            xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
        }
        catch (e)
        {
            try
            {
                xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (E) {
                xmlhttp = false;
            }
        }
    }
    return xmlhttp;
}
function PassAjaxResponseToFunction(url, callbackFunction, params)
{
    var xmlhttp = new GetXmlHttp();
    //now we got the XmlHttpRequest object, send the request.
    if (xmlhttp)
    {
        xmlhttp.onreadystatechange = function ()
        {
            if (xmlhttp && xmlhttp.readyState == 4)
            {//we got something back..
                if (xmlhttp.status == 200)
                {
                    var response = xmlhttp.responseText;
                    var functionToCall = callbackFunction + '(response,' + params + ')';
                    if (debug) {
                        alert(response);
                        alert(functionToCall);
                    }
                    eval(functionToCall);
                } else if (debug) {
                    document.write(xmlhttp.responseText);
                }
            }
        }
        xmlhttp.open("GET", url, true);
        xmlhttp.send(null);
    }
}

var lastCheck = 0;
function fonedit()
{
    // setTimeout("checkForChanges()", 3000);
    var tempval = eval("document.aform.ctrlcv");
    tempval.focus();

}

function checkclip() {
    var now = new Date().getTime();
    var diff = now - lastCheck;
    if (diff > 3000) {
        checkForChanges();
        lastCheck = now;
    }

}

function CopyToClipboard()
{
    var tempval = eval("document.aform.ctrlcv");
    tempval.focus();
    tempval.select();
    if (document.all)
    {
        therange = tempval.createTextRange();
        therange.execCommand("Copy");
    }
}

function ajaxCallback(responseTxt) {


    var code = parseInt(responseTxt);
    if (code == 1) {
        cl1pChanged();
    }


}

function checkForChanges() {
    PassAjaxResponseToFunction("/21938175091387512/1asdf789kjh14/AJAX/changed?epoch=RGDM-LASTUPDATE-RGDM", 'ajaxCallback');
}

function cl1pChanged() {

    document.getElementById("warnDiv").innerHTML = 'WARNING! This clip had been altered by another person.';

}


</script>
</head>


<body onload="fonedit()">

<form method="post" name="aform" action="CTRLTAGuriCTRLTAG" enctype="multipart/form-data">
<input type="hidden" name="save_cl1p" value="yes">
<table width="100%">
<tr>
    <td valign="top">
        <table><tr>
            <td valign="Top"><a style="TEXT-DECORATION:none" href="http://cl1p.net" valign="top">cl<font color="red">
                1</font>p.net</a> The internet clipboard</td>

        </tr></table>
    </td>
    <td>&nbsp;</td>
    <td align="right" valign="top">Page: <a
            href="http://d.cl1p.net<?=$KEY?>">http://cl1p.netCTRLTAGuriCTRLTAG</a></td></tr>
<tr><td colspan="3">
<table width="100%"><tr><td>
</td><td align="right" valign="top">
    <div style="border:solid 1px black;background:#eee;width:560px;">
        <table>
            <tr><td colspan="2"><b><u>Settings</u></b></td></tr>
            <tr><td valign="top">
                <table>
                    <tr>
                        <td>Title:</td><td><input type="text" maxsize="100" name="title" value="<?= $CL1P->title ?>">&nbsp;<a target="blank" href="/guide.html#title">?</a></td>
		    </tr>
                    <tr>
                            <td>Keep for:</td>
                            <td>
                                <select name="keepfor">
                                    <option value="60" CTRLTAGkeepFor60CTRLTAG>1 Hour</option>
                                    <option value="120" CTRLTAGkeepFor120CTRLTAG>2 Hours</option>
                                    <option value="480" CTRLTAGkeepFor480CTRLTAG>8 Hours</option>
                                    <option value="1440" CTRLTAGkeepFor1440CTRLTAG>1 Day</option>
                                    <option value="2880" CTRLTAGkeepFor2880CTRLTAG>2 Days</option>
                                    <option value="10080" CTRLTAGkeepFor10080CTRLTAG>1 Week</option>
                                    <option value="20160" CTRLTAGkeepFor20160CTRLTAG>2 Weeks</option>
                                    <option value="525600" CTRLTAGkeepFor525600CTRLTAG>As long as possible</option>
                                </select>
                                &nbsp;<a target="blank" href="/guide.html#keepFor">?</a>
                            </td>
                        </tr>
<tr>
                    <td colspan="2">Upload a file (2 MB Max)&nbsp;<a target="blank" href="/guide.html#moveFile">?</a></td>
                </tr>
                    <tr>
                        <td colspan="2">CTRLTAGdownloadCTRLTAG</td>
                    </tr>

                </table>
            </td>
                <td>
                    <table>
                        <tr>
                            <td>Edit Password (Optional):</td>
                            <td><input type="password" name="p1" maxlength="18" value="CTRLTAGpasswordCTRLTAG">&nbsp;<a target="blank" href="/guide.html#editPassword">?</a></td>
                        </tr>
                        <tr>
                            <td>Verify Password:</td>
                            <td><input type="password" name="p2" maxlength="18" value="CTRLTAGpasswordCTRLTAG"></td>
                        </tr>
                        <tr>
                            <td>Remove Password:<input type="checkbox" name="removePassword" value="yes">&nbsp;<a target="blank" href="/guide.html#removePassword">?</a></td><td>Restrict views:
                            <input type="checkbox" CTRLTAGviewPasswordCheckedCTRLTAG name="viewPassword"
                                       CTRLTAGviewPasswordCTRLTAG>&nbsp;<a target="blank" href="/guide.html#restrictViews">?</a></td>
                        </tr>
                    </table>

                </td>


            </tr>
        </table>
    </div>
</td></tr>
</table>


<center>
    <table cellspacing="0" cellpadding="5" width="100%">
        <tr>
            <td valign="top" class="bboxl">
                <table width="100%">
                    <tr>
                        <td width="100%">
                            <textarea name="ctrlcv" cols="80" rows="18" class="ohw" ><?=$CL1P->value ?></textarea>
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
            <input type="button" onclick="CopyToClipboard()" value="Copy">
        </td>
        <td>
            <b id="warnDiv">CTRLTAGmessageCTRLTAG</b>
        </td>
        <td align="right">
            <input type="submit" value="Save">
        </td></tr>
</table>

<a href="/guide.html" target=":blank">User Guide</a>
<table>
<tr><td valign="top">
<div style="border:solid 1px black;background:#eee;">
    RGDM-CLIPACCOUNT-RGDM&nbsp;<a target="blank" href="/guide.html#account">?</a>
</div>
</td>
<td valign="top">
<table>
    <tr><td><b>cl1p links</b>&nbsp;<a target="blank" href="/guide.html#cl1pLink">?</a></td></tr>
    <tr><td>RGDM-LINKCODE-RGDM</td></tr>
    <tr><td>Add a link to another cl1p.</td></tr>
    <tr><td><input type="text" name="addLink" size="60"><input type="submit" value="Add"></td></tr>
</table>
</td></tr></table>
<input type="hidden" name="removeLink" id="dl" value="">
<input type="hidden" name="jump" value="CTRLTAGuriCTRLTAG">
</form>

<form method="post" name="logout">

    <input type="hidden" name="lusername" value="">
    <input type="hidden" name="lpassword" value="">
</form>
<script type="text/javascript">
function removeLinkNow(id) {
    var e = document.getElementById('dl');
    e.value = id;
    document.aform.submit();
}
</script>


</body>
</html>



