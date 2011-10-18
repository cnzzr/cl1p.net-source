package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;



/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Oct 9, 2006
 * Time: 3:07:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClipTab implements DatabaseEntry {
    private int number;

    private int clipId;
    private int tabType;
    private int typeId;
    private int tabNumber;
    private String tabName;


    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("ClipId", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("TabType", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("TypeId", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("TabNumber", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("TabName", DatabaseColumnType.CHAR_40)
    };

    public ClipTab() {

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

    public int getClipId() {
        return clipId;
    }

    public void setClipId(int clipId) {
        this.clipId = clipId;
    }

    public int getTabType() {
        return tabType;
    }

    public void setTabType(int tabType) {
        this.tabType = tabType;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getTabNumber() {
        return tabNumber;
    }

    public void setTabNumber(int tabNumber) {
        this.tabNumber = tabNumber;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }
}
