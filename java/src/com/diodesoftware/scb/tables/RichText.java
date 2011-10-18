package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;

public class RichText implements DatabaseEntry {

	private int number;
	private String value;
	
	private DatabaseColumn[] columns = new DatabaseColumn[]{
		new DatabaseColumn("Value", DatabaseColumnType.TEXT)	
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
