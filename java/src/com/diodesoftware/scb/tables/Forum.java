package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;

public class Forum implements DatabaseEntry {

	private int number;
	private boolean lastCommentFirst = true;

	private DatabaseColumn[] columns = new DatabaseColumn[]{
			new DatabaseColumn("LastCommentFirst", DatabaseColumnType.BOOLEAN)
	};
	
	public DatabaseColumn[] columns() {
		return columns;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isLastCommentFirst() {
		return lastCommentFirst;
	}

	public void setLastCommentFirst(boolean lastCommentFirst) {
		this.lastCommentFirst = lastCommentFirst;
	}
	
	public boolean getLastCommentFirst(){
		return isLastCommentFirst();
	}

}
