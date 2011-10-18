var temp = window.onload;
var changeHandlers = new Array();
if (temp)changeHandlers.push(temp);
window.onload = function(event) {
    for (var i = 0; i < changeHandlers.length; i++) {
        changeHandlers[i](event);
    }
}
window.onLoad = function(f) {
    changeHandlers.push(f);
}

var LAST_EDIT = new Date().getTime();


var rgdm = {
    autoSaving:false,
    byId:function(/* element ID */id) {
        return document.getElementById(id);
    }
};


rgdm.stateMonitor = {
    STATE_EDITING:1,
    STATE_VIEWING:2,
    state:2,
    saved:function() {
        this.changeState(this.STATE_VIEWING);
    },
    editing:function() {
        this.changeState(this.STATE_EDITING);
    },
    changeState:function(newState) {
        console.log("State changed to " + newState)
        rgdm.stateMonitor.state = newState;
    },
    changed:function() {
        var btn = $('theSaveButton');
        btn.value = "Save";
        //rgdm.stateMonitor.state = this.STATE_VIEWING;
        rgdm.activityMonitor.userAction();
    }
}

var LOCK_STATUS_UNLOCKED = 0;
var LOCK_STATUS_LOCKED = 1;
var LOCK_STATUS_HAS_KEY = 2;
var HAS_LOCK = false;

rgdm.activityMonitor = {
    firstTime:true,
    lastAction:0,
    userActive:false,
    lastUpdate:0,
    minDelay:(5 * 1000), // Don't update more then once every 5 seconds
    updateInterval:(2 * 60 * 1000),// 2 Minutes
    userActivityTimeout:2000,// Period without user actions
    timeout:(10 * 60 * 1000),
    pasue:false,// Functions like uploading need to not have the connection taken
    loop:function() {
        var THIS = rgdm.activityMonitor;
        var now = new Date().getTime();
        if (THIS.firstTime) {
            THIS.firstTime = false;
        }
        if (!THIS.pasue) {
            // Processing loop
            var clear = THIS.clearToUpdate();
            var time = THIS.timeToUpdate();
            if (clear && time) {
                console.log('Saving changes');
                THIS.saveChanges();
                THIS.lastUpdate = now;
            }
        } else {
           // console.log("ActivityMonitor: Paused Not Running")
        }
        setTimeout(rgdm.activityMonitor.loop, 500);
    },
    userAction:function() {
        var THIS = rgdm.activityMonitor;
        console.log("ActivityMonitor: User Action");
        THIS.lastAction = new Date().getTime();

        if (!HAS_LOCK && !THIS.requestingLock) {
            THIS.requestingLock = true;
            THIS.checkStatus(true);
        }

    },
    clearToUpdate:function() {
        var THIS = rgdm.activityMonitor;
        var diff = new Date().getTime() - THIS.lastAction;
        if (diff > THIS.userActivityTimeout)
            return true;
        return false;
    },
    timeToUpdate:function() {
        var THIS = rgdm.activityMonitor;
        var now = new Date().getTime();
        var sinceLastUpdate = now - THIS.lastUpdate;
        var sinceLastAction = now - THIS.lastAction;
        if (sinceLastAction < THIS.updateInterval
                && sinceLastUpdate > THIS.minDelay) {
            return true;
        }
        if (sinceLastUpdate > THIS.updateInterval) {
            return true;
        }
        return false;
    },
    saveChanges:function() {
        if (rgdm.stateMonitor.state == rgdm.stateMonitor.STATE_EDITING) {
            console.log(new Date().toTimeString() + "ActivityMonitor:saveChanges");
            // If Changed then submit
            var url = "/cl1p-inc-rgdm/autosave";
            if (cl1p_beforeSave) {
                cl1p_beforeSave();
            }
            var f = $('aform');
            console.log('Saving [' + f.ctrlcv.value + ']');

            http("POST", url, rgdm.activityMonitor.saveComplete, {ctrlcv: f.ctrlcv.value,uri:$('currentURI').value});
        } else {
            console.log("Can't save. Reason: State " + rgdm.stateMonitor.state + " != " + rgdm.stateMonitor.STATE_EDITING);
            rgdm.activityMonitor.checkStatus(rgdm.stateMonitor.state == rgdm.stateMonitor.STATE_EDITING);
        }
    },
    saveComplete:function(transport) {
        var text = transport.responseText;
        var myObject = null;
        try {
            myObject = eval('(' + text + ')');
        } catch(ee) {
            console.log(ee);

        }
        if (myObject == null) {
            rgdm.activityMonitor.checkStatus();
            return;
        } else {
            if (myObject.abortLoop)return;
        }
        LAST_EDIT = myObject.lastEdit;
        $('theSaveButton').value = 'Auto Saved';
        rgdm.stateMonitor.saved();
        rgdm.autoSaving = false;
        rgdm.activityMonitor.checkStatus();
    },
    checkStatus:function(requestLock) {
        console.log("ActivityMonitor:checkStatus");

        // Check to see the current status for a cl1p
        if ($('currentURI') == null)return;
        var uriValue = $('currentURI').value;
        var url = "/cl1p-inc-rgdm/status?uri=" + escape(uriValue) +
                  "&clipStatus=" + rgdm.stateMonitor.state +
                  "&onetime=" + false +
                  "&requestLock=" + requestLock +
                  "&lastEdit=" + LAST_EDIT;
        http("GET", url, rgdm.activityMonitor.checkStatusComplete);
    },
    checkStatusComplete:function(transport) {
        var text = transport.responseText;
        var THIS = rgdm.activityMonitor;
        console.log("cl1p_updateStatus Text [" + text + "]");
        var myObject = null;
        try {
            myObject = eval('(' + text + ')');
        } catch(ee) {
            console.log(ee);
        }
        if (myObject == null) {
            return;
        }
        var responseType = myObject.responseType;
        var msg = "";
        switch (myObject.lockStatus) {
            case LOCK_STATUS_UNLOCKED: msg = "Status: Editable";break
            case LOCK_STATUS_LOCKED:msg = "Status: Read Only";break;
            case LOCK_STATUS_HAS_KEY:msg = "Status: Editing"; break;

        }
        HAS_LOCK = (myObject.lockStatus == LOCK_STATUS_HAS_KEY);
        if (myObject.lockRequested) {
            console.log("Lock Requested");
            THIS.requestingLock = false;
            var lockStatus = myObject.lockStatus;
            if (!HAS_LOCK) {
                console.log("Failed to get lock");
                show_lock_request_failed_msg();
            } else {
                rgdm.stateMonitor.editing();
                console.log("Got Lock")
                LAST_EDIT = myObject.lastEdit;
            }
        } else {
            LAST_EDIT = myObject.lastEdit;
        }
        if ($('currentStatus')) {
            $('currentStatus').innerHTML = msg;
            if (myObject.updateClip) {
                var uriValue = $('currentURI').value;
                var url = "/cl1p-inc-rgdm/updateFilter?uri=" + escape(uriValue) + "&lastEdit=" + LAST_EDIT;
                http("GET", url, rgdm.activityMonitor.updateContent);
            }
        }
    },
    updateContent:function(transport) {
        var text = transport.responseText;
        console.log("ActivityMonitor:updateContent");
        var ta = $('ctrlcv');
        if (ta != null && text != "") {
            ta.value = text;
            console.log("Updating content [" + text + "]");
        }
        if (cl1p_setValue && text != "") {
            cl1p_setValue(text);
            console.log("Updating content [" + text + "]");
        }
    }
};

function cl1p_setValue() {
}//To be overrieden
function cl1p_beforeSave() {
}//To be overrieden

function clearIfEmpty(field) {
    if (field.value == cl1pConfig.defaultValue) {
        field.value = "";
    }
}

function upload_file_start() {
    console.log('Starting upload');
    rgdm.activityMonitor.pasue = true;
    $('uploadBrowse').style.display = 'none';
    $('uploadMsg').innerHTML = '<p>Uploading</p>';
    var f = document.aform;
    f.target = "UploadTarget";
    setTimeout('upload_update()', 1000);
    f.submit();
}

function upload_update() {
     console.log('Getting update');
    var url = cl1pConfig.uri + '?ajaxUpload=yes&key=' + cl1pConfig.encodedUri;
    http("GET", url, ajax_upload_update);
}

function appendDownloaded() {
    var count = cl1pConfig.fileCount;
    count++;
    if (count > 10)return;
    cl1pConfig.fileCount = count;
    var div = document.createElement("div");
    var fileUpload = document.createElement("input");
    fileUpload.type = "file";
    fileUpload.name = "uploadFile" + count;
    var button = document.createElement("input");
    button.type = "button";
    button.value = "Upload";
    button.onclick = upload_file_start;
    div.appendChild(fileUpload);
    var target = document.getElementById("uploadBrowse");
    target.appendChild(div);
}

function ajax_upload_update(transport) {
    var text = transport.responseText;
     console.log('Got update ' + text);
    var myObject = null;
    try {
        myObject = eval('(' + text + ')');
    } catch(ee) {
        console.log(ee);
        return;
    }
    if (myObject == null)return;
    if (myObject.msg && myObject.error) {
        var picSpan= $('picUploadMsg');
        if(picScan)picScan.innerHTML = '<p>' + myObject.msg + '</p>';
        $('uploadMsg').innerHTML = '<p>' + myObject.msg + '</p>';

        $('uploadBrowse').style.display = 'block';
    } else {
        if(myObject.msg){
            $('uploadMsg').innerHTML = '<p>' + myObject.msg + '</p>';
            $('picUploadMsg').innerHTML=  '<p>' + myObject.msg + '</p>';
        }else{
            $('uploadMsg').innerHTML = '<p>' + myObject.uploaded + 'MB of ' +
                                   myObject.total + 'MB ' + myObject.percent + '%</p>';
            $('picUploadMsg').innerHTML = '<p>' + myObject.uploaded + 'MB of ' +
                                   myObject.total + 'MB ' + myObject.percent + '%</p>';
        }
    }

    if (myObject.askAgain) {
        setTimeout('upload_update()', 1000);
    } else if (!myObject.error) {
        rgdm.activityMonitor.pasue = false;
        var form = document.aform;
        //form.target = null;
        var msg = "Done. Please wait.";
        $('uploadMsg').innerHTML = '<p>' + msg + '</p>';
        window.location = cl1pConfig.uri;
    } else {
        rgdm.activityMonitor.pasue = false;
    }
}

function show_lock_request_failed_msg() {
    alert('Failed to get lock on the cl1p.');
}


function printCl1p() {
    var s = document.aform.ctrlcv.value;
    var regExp = /\n/gi;
    s = s.replace(regExp, '<br>');
    pWin = window.open('', 'pWin', 'location=yes, menubar=yes, toolbar=yes');
    pWin.document.open();
    pWin.document.write('<html><head></head><body>');
    pWin.document.write(s);
    pWin.document.write('</body></html>');
    pWin.print();
    pWin.document.close();
    pWin.close();
}

function http(method, url, cb, q) {
    var options = {
        method:method,
        onSuccess:cb
    };
    if (q) {
       
        options.parameters = q;
    }
    new Ajax.Request(url, options);
}

function setDebug(){

    $('DEBUGELE').value="TRUE";
}
function debug(){
    var s = $('DEBUGELE').value;
    return s == 'TRUE';
}

function deleteFileNow(fileNumber) {

    if (debug() || confirm("Delete file. Are you sure?") ) {
        var e = document.getElementById('deleteFile');
        e.value = 'yes';
        var e2 = document.getElementById('deleteFileNumber');
        e2.value = fileNumber;
        document.aform.submit();
    }
}

function removeLinkNow(id) {
    var e = document.getElementById('dl');
    e.value = id;
    document.aform.submit();
}

function show(id) {
    $(id).style.display = 'block';
}
function hide(id) {
    $(id).style.display = 'none';
}

function passwordsMatch() {
    var msg = "";
    var span = $('passwordErr');
    var p1 = $('password1').value;
    var p2 = $('password2').value;
    if (p1 != p2) {
        msg = "Passwords don't match";
    }
    span.innerHTML = msg;
}

window.onLoad(function() {
    rgdm.activityMonitor.loop();
});


function $dt(millis) {
    var curDate = new Date(millis);
    document.write(curDate.toLocaleString());
}
var linksActive  =false;
function toggleMode(){
    if(linksActive){
      deactivateLinks();
    }else{
      activateLinks();
    }
}



function deactivateLinks(){
   if(!linksActive)return;
    linksActive=false;
    $('modeImg').src = '/cl1p-inc-rgdm/images/link_broken_48.gif';
    var textArea = $('ctrlcv');
    var rteEle = $('rte');
    textArea.style.display = "block";
    rteEle.style.display = "none";
}

function activateLinks() {
    if(linksActive)return;
    console.log("Activating links");
    linksActive=true;
    $('modeImg').src = '/cl1p-inc-rgdm/images/link_48.gif';
    console.log('clicked');
    var textArea = $('ctrlcv');

    var rteEle = $('rte');
    if(rteEle == null){
        var rteElt = document.createElement('iframe');
        //rteElt.onload = rteOnLoad;
        rteElt.id = 'rte';

        var t = $('ctrlcv-container');
        console.log("Activating Appending");
        Element.clonePosition(rteElt, textArea);
        t.appendChild(rteElt);

    }else{
        rteEle.style.display = "block";

    }

    textArea.style.display = "none";
    // Thanks IE7 for disbling onload for iframes!
    setTimeout(rteOnLoad, 200);

}

function rteOnLoad() {
    console.log('loaded');
    var textArea = $('ctrlcv');
    rteElt = $('rte');
    rteElt.id = 'rte';

    var text = textArea.value;
    var html = buildRteValue(text);
    rteElt.contentWindow.document.body.innerHTML = html;
    rteElt.contentWindow.document.body.style.fontFamily = 'monospace';
}

function buildRteValue(text) {
    text = replaceLinks(text, 0);
    var i = 0;
    do{
        text = text.replace('\n', '<br/>');
        i = text.indexOf('\n');
    } while (i != -1);
    return text;
}

function replaceLinks(currentValue) {
    var lines = currentValue.split('\n');
    var result = "";
    for (var j = 0; j < lines.length; j++) {
        var line = lines[j];
        var sa = line.split(" ");
        for (var i = 0; i < sa.length; i++) {
            var s = sa[i];
            var t = s.indexOf('http://');
            console.log('Link ? ' + t + ' ' + s + ' indexOf ' + line.indexOf(' '));
            if (t == 0) {
                var href = "<a target='_blank' href='" + s + "'>" + s + "</a>";
                result = result + href;
            } else {
                result = result + s;
            }
            var nextI = i + 1;
            if (nextI < sa.length)result = result + "  ";
        }
        var nextJ = j + 1;
        if (nextJ < lines.length) result = result + "<br/>";
    }
    return result;
}


function popup(id){



/*    var popupframeclickblocker = document.getElementById('popup-click-blocker');

    popupframeclickblocker.border = '0';
    popupframeclickblocker.style.position = 'absolute';
    popupframeclickblocker.style.display = 'block';
    popupframeclickblocker.style.zIndex = '1000';
    popupframeclickblocker.style.top = '0';
    popupframeclickblocker.style.left = '0';
    popupframeclickblocker.style.width = document.documentElement.scrollWidth-10 + 'px';
    popupframeclickblocker.style.height = document.documentElement.clientHeight + 'px';
    popupframeclickblocker.style.visibility = 'visible';
    popupframeclickblocker.style.opacity = 0.5;
    popupframeclickblocker.style.filter = 'alpha(opacity=0.50)';
    popupframeclickblocker.style.backgroundColor = 'black';
    popupframeclickblocker.style.display = 'block';
  */
    // width and height need to be set inline in order to be read
      var popup = document.getElementById(id);
      var ch = document.documentElement.scrollHeight;
      var cw = document.documentElement.scrollWidth;
      var dh = popup.style.height;
      var dw = popup.style.width;
      dh = dh.substring(0,dh.length - 2);
      dw = dw.substring(0,dw.length - 2);
      var top = ch / 2 - dh / 2;
      var left = cw / 2 - dw / 2;
      popup.style.top =  '200' + 'px';
      popup.style.left = left + 'px';
      popup.style.display = 'block';
      popup.style.zIndex = '10010';
    
}



function closePopup(id){
    var popup = document.getElementById(id);
    popup.style.display = 'none';
    var iframe = document.getElementById('popup-click-blocker');
    iframe.style.display = 'none';

}


function showPic(url, width, height, alt)
{
    var PIC_DIV_ID =  'pictureDiv';
    var PIC_IMG_ID =  'pictureImg';
    var PIC_CLOSE_BTN = 'pictureCloseBtn';
    var div = $(PIC_DIV_ID);
    var img = null;
    if(!div){
        div = document.createElement("div");
        div.style.zIndex = 20001;                         
        div.style.position = "absolute";
        div.style.left = "100";
        div.style.top = "100";
        div.id = PIC_DIV_ID;
        document.body.appendChild(div);
        img = document.createElement('img');
        img.id = PIC_IMG_ID;
        div.appendChild(img);
        var clsBtn = document.createElement('input');
        clsBtn.type = 'button';
        clsBtn.value = 'Close';
        clsBtn.onclick = function(){
            div.style.display='none';
        }
        div.appendChild(clsBtn);
    }else{
        img = $(PIC_IMG_ID);
    }
    img.src = url;
    img.style.Width = width + "px";
    img.style.height = height + "px";
    img.alt = 'alt';
    div.style.display='block';

}

function sendShare(){
    var url = "/cl1p-inc-rgdm/share.jsp";



            http("POST", url, sendShareComplete, {
                "shareEmail":$('shareEmail').value,
                "shareSubject":$('shareSubject').value,
                "shareMsg":$('shareMsg').value
            });
}

function sendShareComplete(transport){

        var text = transport.responseText;
        var myObject = null;
        try {
            myObject = eval('(' + text + ')');
        } catch(ee) {
            console.log(ee);
        }
        if(!myObject.ok){
            $('shareResponse').innerHTML = myObject.error;
        }else{
            $('shareResponse').innerHTML = "Message sent!";
            $('shareEmail').value = '';
            closePopup('sharePopup');

        }
}
