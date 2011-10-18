package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;

public class TodoListItem implements DatabaseEntry{
	
	private int number;
	private int todoListId;
	private String description;
	private boolean done;
	private int itemIndex;
	
	private DatabaseColumn[] columns = new DatabaseColumn[]{
		new DatabaseColumn("TodoListId", DatabaseColumnType.DECIMAL),
		new DatabaseColumn("Description", DatabaseColumnType.TEXT),
		new DatabaseColumn("Done", DatabaseColumnType.BOOLEAN),
		new DatabaseColumn("ItemIndex", DatabaseColumnType.DECIMAL)
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

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDone(){		
		return getDone();
	}
	
	public boolean getDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public int getItemIndex() {
		return itemIndex;
	}

	public void setItemIndex(int priority) {
		this.itemIndex = priority;
	}

	public int getTodoListId() {
		return todoListId;
	}

	public void setTodoListId(int todoListId) {
		this.todoListId = todoListId;
	}
	
	

}

