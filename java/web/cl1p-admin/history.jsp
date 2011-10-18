<%@ page import="com.diodesoftware.scb.ClipSession" %>
<%@ page import="com.diodesoftware.scb.ClipRequest" %>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.diodesoftware.scb.agents.ClipAgent" %>
<%@ page import="com.diodesoftware.scb.tables.Clip" %>
<%@ page import="com.diodesoftware.scb.tables.Owner" %>
<%@ page import="com.diodesoftware.dbmapper.DBMapper" %>
<%@ page import="com.diodesoftware.scb.tables.User" %>
<%@ page import="com.diodesoftware.scb.agents.FormException" %>
<%@ page import="com.diodesoftware.scb.tables.ClipHistory" %>
<%@ page import="com.diodesoftware.scb.agents.HistoryAgent" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="com.diodesoftware.scb.UrlWriter" %>
<%@ page import="com.diodesoftware.R" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>History</title>
    <link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= R.T()%>">
    <style type="text/css">
        .evenRow{
            background-color:#BBBBBB;
            cursor:pointer;
        }
        .oddRow{
            background-color:#DDDDDD;
            cursor:pointer;
        }
        .overRow{
            background-color: midnightblue;
            color: white;
        }
        .selectedRow{
            background-color:#0000AA;
            color:white;
        }

    </style>
</head>
<script type="text/javascript" src="prototype.js"></script> 
<body>
<%
    ClipSession clipSession = ClipSession.getSession(request);
    DBConnectionMgr dbMgr = new DBConnectionMgr();
    DBMapper mapper = DBMapper.getInstance();
    Connection con = dbMgr.getConnection();
    String hisotryUri = clipSession.getLastUri();
    if(request.getParameter("uri") != null)
        hisotryUri = request.getParameter("uri");
    try {
        ClipHistory[] history = null;
        try {
            HistoryAgent historyAgent = new HistoryAgent();
            history = historyAgent.loadHistory(hisotryUri,clipSession, con);
        } catch (FormException e) {
%>
<%= e.getMsg() %><br>
<%
        return;
    }
    DateFormat df = DateFormat.getDateTimeInstance();
%>
<img src="/cl1p-inc-rgdm/images/cl1p_logo_small.jpg" alt="cl1p the internet clipboard"> <br>
<center>
<span><b>History for </b> <a href="<%= UrlWriter.write(hisotryUri,null,request)%>">http://cl1p.net<%= hisotryUri %></a></span><br>
</center>
<table width="100%">
    <tr>
        <td><b>Date&nbsp;of&nbsp;change</b></td>
        <td width="100%"><b>Cl1p Value</b></td>
    </tr>
    <tr>
        <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0"><tr><td class=""></td></tr>
                <tr><td class="" align="center">
            <table cellspacing="0" cellpadding="0" style="border:1px solid black;">

                <%
                    String[] styleClasss = {"evenRow","oddRow"};
                    boolean even = true;
                    for(int i = 0; i < history.length;i++ ){
                        %>
                <tr id="row<%= i%>" onmouseover="rowMouseOver(this)"
                    onmouseout="rowMouseOut(this)" class="<%= even?styleClasss[0]:styleClasss[1] %>"
                    onclick="selectRow('<%= i %>')"><td><%= df.format(history[i].getLastEdit().getTime()) %></td></tr>
                <%
                        even = !even;
                    }
                %>


                </table>
                    </td></tr>
                <tr><td class=""></td></tr>
                </table>
        </td>
        <td width="100%">
            <table width="100%" border="0" cellpadding="0" cellspacing="0"><tr><td width="100%">
            <textarea rows="20" cols="80" id="display_version" style="width:100%;"></textarea>
            </td></tr></table>
        <a href="<%= UrlWriter.write(hisotryUri, null, request)%>"><img src="/cl1p-inc-rgdm/images/back.gif" border="0" alt="Back"/></a><br>    
        </td>
    </tr>
</table>

<div style="display:none;">
<%
    for(int i = 0; i < history.length;i++ ){
        %>
<textarea rows="10" cols="10" id="history<%= i%>"><%= history[i].getValue() %></textarea>
    <%
        }
    %>
</div>
<script language="javascript">
    var lastRow = -1;

            function rowMouseOver(row){
                Element.addClassName(row, 'overRow');
            }

            function rowMouseOut(row){
                 Element.removeClassName(row, 'overRow'); 
            }

    function selectRow(i){
        show(i);
        if(lastRow != -1)
            deselectRow(lastRow);
        var row = $('row' + i);
        Element.addClassName(row, 'selectedRow');
        lastRow = i;
    }
    function deselectRow(i){
         var row = $('row' + i);    
         Element.removeClassName(row, 'selectedRow');
    }

    function show(i){

        var history =  $('history' + i);
        var target = $('display_version');
        target.value = history.value;
        return false;
    }
</script>
</body>
</html>
<%        
    } finally {
        dbMgr.returnConnection(con);
    }
%>
