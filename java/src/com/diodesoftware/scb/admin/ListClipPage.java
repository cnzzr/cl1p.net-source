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
import org.apache.ecs.html.A;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Jul 14, 2006
 * Time: 8:38:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListClipPage extends AdminPage{

    protected String fileName;

    public String doPage(HttpServletRequest request,
                         HttpServletResponse response,
                         ServletContext context,
                         Connection con) {
        String errorMsg = null;

        UserAgent userAgent = UserAgent.getInstance();
        ClipSession session = ClipSession.getSession(request);

        Page page = new Page(context.getRealPath("list.html"));
        Table table = new Table();
        DateFormat df = DateFormat.getDateTimeInstance();
        table.addElement(new TR().addElement(new TD("cl1p")).addElement(new TD("Last Change")).addElement(new TD("Clean")));
        Clip[] clips = userAgent.getUserClips(session.getUser(), con);
        for(int i = 0; i < clips.length; i++){
            Clip clip = clips[i];
            TR tr = new TR();
            String url = "http://cl1p.net" + clip.getUri();
            tr.addElement(new TD().addElement(new A(url).setHref(url)));
            tr.addElement(new TD(df.format(clip.getLastEdit().getTime())));
            tr.addElement(new TD(df.format(clip.getCleanDate().getTime())));
            table.addElement(tr);
        }
        page.replace("RGDM-USERNAME-RGDM", session.getUser().getUsername());
        page.replace("RGDM-CLIPS-RGDM",table.toString());
        return page.toString();
    }
}
