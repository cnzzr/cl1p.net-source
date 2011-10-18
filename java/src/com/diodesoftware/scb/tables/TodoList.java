package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;

public class TodoList implements DatabaseEntry{
	
	private int number;
	private String name;
	
	private DatabaseColumn[] columns = new DatabaseColumn[]{
		new DatabaseColumn("Name", DatabaseColumnType.TEXT)	
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

	public String getName() {
		return name;
	}

	public void setName(String value) {
		this.name = value;
	}

}
