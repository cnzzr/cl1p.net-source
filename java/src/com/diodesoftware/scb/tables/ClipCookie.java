package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;


/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Jul 4, 2006
 * Time: 9:11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClipCookie implements DatabaseEntry
{
    private int number;
    private String value;
    private int userId;

    private DatabaseColumn[] columns = new DatabaseColumn[]{
        new DatabaseColumn("Value", DatabaseColumnType.CHAR_200),
        new DatabaseColumn("UserId", DatabaseColumnType.DECIMAL)        
    };

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public DatabaseColumn[] columns()
    {
        return columns;
    }
}