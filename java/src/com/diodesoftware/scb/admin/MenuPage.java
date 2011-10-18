package com.diodesoftware.scb.admin;

import com.diodesoftware.scb.agents.UserAgent;
import com.diodesoftware.scb.ClipSession;
import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.scb.SitePage.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import java.sql.Connection;
import java.text.DateFormat;

import org.apache.ecs.html.Table;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.TD;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Jul 8, 2006
 * Time: 10:24:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class MenuPage extends AdminPage{

    protected String fileName;

    public String doPage(HttpServletRequest request,
                         HttpServletResponse response,
                         ServletContext context,
                         Connection con) {
        String errorMsg = null;

        UserAgent userAgent = UserAgent.getInstance();
        ClipSession session = ClipSession.getSession(request);

        Page page = new Page(context.getRealPath("menu.html"));
         Table table = new Table();
        table.setBorder(1);
        DateFormat df = DateFormat.getDateTimeInstance();
        TR tr = new TR();
        tr.setBgColor("#BBBBBB");
        tr.addElement(new TD("cl1p")).addElement(new TD("Last Change")).addElement(new TD("Clean"));
        table.addElement(tr);
        Clip[] clips = userAgent.getUserClips(session.getUser(), con);
        if (clips.length > 0) {
            for (Clip clip : clips) {
                tr = new TR();
                String url = "http://cl1p.net" + clip.getUri();
                String a = "<a href='" + url + "'>" + url + "</a>";
                tr.addElement(new TD(a));
                tr.addElement(new TD(df.format(clip.getLastEdit().getTime())));
                tr.addElement(new TD(df.format(clip.getCleanDate().getTime())));
                table.addElement(tr);
            }
        } else {
            String s = "No Cl1ps are currently being tracked. To add to this list create or edit a cl1p while logged in";
            tr = new TR();
            TD td = new TD(s).setColSpan(3);
            tr.addElement(td);
            table.addElement(tr);
        }
        

        page.replace("RGDM-CLIPS-RGDM",table.toString());
        page.replace("RGDM-USERNAME-RGDM", session.getUser().getUsername());
        return page.toString();
    }
}