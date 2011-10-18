/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Sep 27, 2003
 * Time: 9:05:53 AM
 * To change this template use Options | File Templates.
 */
package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;

import java.util.Calendar;

public class ClipHistory
    implements DatabaseEntry
{
    private int number;
    private int clipNumber;
    private String uri;
    private String value;
    private Calendar lastEdit;
    private String password;
    private boolean html;

    private DatabaseColumn[] columns = new DatabaseColumn[]{
        new DatabaseColumn("ClipNumber", DatabaseColumnType.DECIMAL),
        new DatabaseColumn("Uri", DatabaseColumnType.BLOB),
        new DatabaseColumn("Value", DatabaseColumnType.BLOB),
        new DatabaseColumn("LastEdit", DatabaseColumnType.DATE12),
        new DatabaseColumn("Password", DatabaseColumnType.PASSWORD),
        new DatabaseColumn("Html", DatabaseColumnType.BOOLEAN)
    };

    public ClipHistory()
    {

    }

    public ClipHistory(Clip clip)
    {
        clipNumber = clip.getNumber();
        uri = clip.getUri();
        value = clip.getValue();
        lastEdit = clip.getLastEdit();
        password = clip.getPassword();
        html = clip.isHtml();
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public Calendar getLastEdit()
    {
        return lastEdit;
    }

    public void setLastEdit(Calendar lastEdit)
    {
        this.lastEdit = lastEdit;
    }

    public String getPassword()
    {
        return password;
    }

    public boolean isHtml()
    {
        return html;
    }

    public boolean getHtml()
    {
        return html;
    }

    public void setHtml(boolean html)
    {
        this.html = html;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public int getClipNumber()
    {
        return clipNumber;
    }

    public void setClipNumber(int clipNumber)
    {
        this.clipNumber = clipNumber;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    public DatabaseColumn[] columns()
    {
        return columns;
    }
}
