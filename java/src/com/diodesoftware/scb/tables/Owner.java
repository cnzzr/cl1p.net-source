package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;

import java.util.Calendar;


public class Owner implements DatabaseEntry {
    private int number;
    private int userId;
    private Calendar start;
    private Calendar end;
    private boolean sslAccess;


    private DatabaseColumn[] columns = new DatabaseColumn[]{
        new DatabaseColumn("UserId", DatabaseColumnType.DECIMAL),
        new DatabaseColumn("Start", DatabaseColumnType.DATE12),
        new DatabaseColumn("End", DatabaseColumnType.DATE12),
        new DatabaseColumn("SslAccess", DatabaseColumnType.BOOLEAN)
    };


    public Owner() {
        start = Calendar.getInstance();
        end = Calendar.getInstance();
        end.add(Calendar.YEAR, 5);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public DatabaseColumn[] columns(){
        return columns;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }
    
    public void setSslAccess(boolean s){
    	sslAccess = s;
    }
    
    public boolean getSslAccess(){
    	return sslAccess;
    }
    
    public boolean isSslAccess(){
    	return sslAccess;
    }
}