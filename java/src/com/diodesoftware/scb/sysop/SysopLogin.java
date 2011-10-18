package com.diodesoftware.scb.sysop;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;

public class SysopLogin implements DatabaseEntry {

    private int number;
    private String password;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    private DatabaseColumn[] columns = new DatabaseColumn[]{
        new DatabaseColumn("Password", DatabaseColumnType.PASSWORD)
    };

    public DatabaseColumn[] columns(){
        return columns;
    }


}
