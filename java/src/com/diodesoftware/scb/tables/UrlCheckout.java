package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;

import java.util.Calendar;

public class UrlCheckout implements DatabaseEntry {

    private int number;
    private int userId;
    private Calendar created;
    private String url;
    private boolean complete;
    private Calendar completeDate;

    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("UserId",DatabaseColumnType.DECIMAL),
            new DatabaseColumn("Created", DatabaseColumnType.DATE12),
            new DatabaseColumn("Url", DatabaseColumnType.BLOB),
            new DatabaseColumn("Complete", DatabaseColumnType.BOOLEAN),
            new DatabaseColumn("CompleteDate", DatabaseColumnType.DATE12)           
    };


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public boolean getComplete(){
        return isComplete();
    }
    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Calendar getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Calendar completeDate) {
        this.completeDate = completeDate;
    }

    public DatabaseColumn[] columns(){
        return columns;
    }
}
