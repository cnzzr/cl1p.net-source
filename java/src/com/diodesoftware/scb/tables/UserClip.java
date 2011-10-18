
package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;



public class UserClip
    implements DatabaseEntry
{
    private int number;
    private int clipId;
    private int userId;
    private int clipHistoryId;

    private DatabaseColumn[] columns = new DatabaseColumn[]{
        new DatabaseColumn("ClipId", DatabaseColumnType.DECIMAL),
        new DatabaseColumn("UserId", DatabaseColumnType.DECIMAL),
	new DatabaseColumn("ClipHistoryId", DatabaseColumnType.DECIMAL)   
    };

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }


    public DatabaseColumn[] columns()
    {
        return columns;
    }

    public void setUserId(int i){
	userId = i;
    }

    public int getUserId(){
	return userId;
    }

    public void setClipId(int i){
	clipId = i;
    }

    public int getClipId(){
	return clipId;
    }

    public void setClipHistoryId(int i){
	clipHistoryId = i;
    }

    public int getClipHistoryId(){
	return clipHistoryId;
    }
}


