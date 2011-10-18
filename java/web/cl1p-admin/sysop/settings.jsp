<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%!

    private static Logger log = Logger.getLogger("jsp.admin.cl1p-list");
%>
<%
    String msg = "";
    SysopSession sysopSession = SysopSession.getInstance(request);
    if (!sysopSession.isLoggedIn()) {
%>
<jsp:forward page="index.jsp"/>
<%
        return;
    }
    ClipRequest clipRequest = ClipRequest.getClipRequest(request);
    String save = request.getParameter("save");
    if ("yes".equals(save)) {
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            if (!"save".equals(name) && !"button".equals(name)) {
                String value = request.getParameter(name);
                SystemSetting systemSetting = SystemSettingAgent.load(name, clipRequest.getCon());
                systemSetting.setSettingValue(value);
                DBMapper.save(systemSetting, clipRequest.getCon());
            }
        }
        SystemSettingAgent.loadMap(clipRequest.getCon());
        msg = W.w("settings.saved");
    }
%>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.diodesoftware.W" %>
<%@ page import="com.diodesoftware.scb.sysop.SysopSession" %>
<%@ page import="com.diodesoftware.scb.ClipRequest" %>
<%@ page import="java.util.List" %>
<%@ page import="com.diodesoftware.scb.tables.SystemSetting" %>
<%@ page import="com.diodesoftware.scb.agents.SystemSettingAgent" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.diodesoftware.dbmapper.DBMapper" %>
<html>
<head>
    <title><%= W.w("clip.settings") %>
    </title>
    <link rel="stylesheet" type="text/css" media="screen, projection" href="/cl1p-inc-rgdm/style.css<%= com.diodesoftware.R.T()%>">
</head>
<body>
<center>
<table height="100%" cellspacing="0" cellpadding="0" width="875">
<tr>
<td valign="top">
<h2><%= W.w("clip.settings") %>
</h2>

<form method="post">
    <%
        try {

            List<SystemSetting> list = SystemSettingAgent.loadAll(clipRequest.getCon());
            request.setAttribute("list", list);
    %>
    <display:table name="list" id="setting" class="clipTable">
        <display:setProperty name="paging.banner.placement" value="bottom"/>
        <display:column property="settingKey" title="Setting"/>
        <display:column title="Value">
            <%
                SystemSetting s = (SystemSetting) pageContext.getAttribute("setting");
                String value = s.getSettingValue();
                if (s.getSettingType() == SystemSetting.TYPE_BOOLEAN) {
            %>
            True<input type="radio" value="true" <%= ("true".equals(value) ? "checked" : "") %>
            name="<%= s.getSettingKey() %>">
            False<input type="radio" value="false" <%= ((!"true".equals(value)) ? "checked" : "") %>
            name="<%= s.getSettingKey() %>">
            <%
            } else if (s.getSettingType() == SystemSetting.TYPE_TEXTAREA) {
            %>
            <textarea name="<%= s.getSettingKey() %>" rows="4" cols="60">
                <%= s.getSettingValue() %>
            </textarea>
            <%
            } else {
            %>
            <input type="text" name="<%= s.getSettingKey() %>" value="<%= s.getSettingValue()%>">
            <%
                }
            %>

        </display:column>
        <display:column property="description" title="Description"/>


    </display:table>
    <%
        } catch (Exception e) {
            e.printStackTrace();
        }
    %>
    <input type="hidden" name="save" value="yes">
    <input type="submit" value="Save" name="button">
</form>
<br/>
<b><%= msg%></b><br/>
<a href="./index.jsp"><%= W.w("menu") %>
</a>
</td></tr></table>
</center>
</body>
</html>