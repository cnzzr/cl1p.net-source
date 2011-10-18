/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Sep 4, 2003
 * Time: 10:34:32 PM
 * To change this template use Options | File Templates.
 */
package com.diodesoftware.scb.clipboard;

import com.diodesoftware.scb.SitePage.Page;

public class ClipboardPage
    extends Page
{
    public ClipboardPage(String value)
    {
        a("<html><head><title>cl1p.com</title>");
        a("<style<!--");
        a(".ohw{width:100%;}");
        a("-->");
        a("</style>");
        a("</head><body>");
        a("<form method=\"post\">");
        a("<textarea name=\"scb\" cols=\"80\" rows=\"20\" class=\"ohw\">");
        a(value);
        a("</textarea><br><input type=\"submit\" value=\"Save\"></form></body></html>");
    }
}
