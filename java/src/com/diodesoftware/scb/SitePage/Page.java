/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Sep 4, 2003
 * Time: 10:23:35 PM
 * To change this template use Options | File Templates.
 */
package com.diodesoftware.scb.SitePage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Page
{

    private StringBuffer sb = new StringBuffer();
    private String jsp;
    private boolean editable = false;
    private boolean viewRestricted;

    public Page(String fileName)
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while(in.ready())
            {
                String s = in.readLine();
                sb.append(s).append("\n");
            }
            in.close();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public Page()
    {

    }

    public void a(String s)
    {
        sb.append(s).append("\n");
    }

    public String toString()
    {
        return sb.toString();
    }

    public void replace(String tag, String replacement)
    {
        String s = sb.toString();
        int start = s.indexOf(tag);
        while (start != -1)
        {
            int end = start + tag.length();
            String before = s.substring(0, start);
            String after = s.substring(end);
            sb = new StringBuffer(before);
            sb.append(replacement);
            sb.append(after);
            s = sb.toString();
            start = s.indexOf(tag);
        }

    }

    public String getJsp() {
        return jsp;
    }

    public void setJsp(String jsp) {
        this.jsp = jsp;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

	public boolean isViewRestricted() {
		return viewRestricted;
	}

	public void setViewRestricted(boolean viewRestricted) {
		this.viewRestricted = viewRestricted;
	}
    
    
}
