package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Oct 9, 2006
 * Time: 3:11:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlainTextTab implements DatabaseEntry {
    private int number;

    private String content;


    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("Content", DatabaseColumnType.BLOB)

    };

    public PlainTextTab() {

    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public DatabaseColumn[] columns() {
        return columns;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}