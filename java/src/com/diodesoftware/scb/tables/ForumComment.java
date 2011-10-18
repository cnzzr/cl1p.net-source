package com.diodesoftware.scb.tables;

import java.util.Calendar;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;


public class ForumComment implements DatabaseEntry {
	
	private int number;
	private int forumId;
	private String comment;
	private Calendar commentDate = Calendar.getInstance();
	private String handle = "";


    private DatabaseColumn[] columns = new DatabaseColumn[]{
			new DatabaseColumn("ForumId", DatabaseColumnType.DECIMAL),
			new DatabaseColumn("Comment", DatabaseColumnType.TEXT),
			new DatabaseColumn("CommentDate", DatabaseColumnType.DATE12),
            new DatabaseColumn("Handle", DatabaseColumnType.CHAR_100)
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Calendar getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(Calendar commentDate) {
		this.commentDate = commentDate;
	}

	public int getForumId() {
		return forumId;
	}

	public void setForumId(int forumId) {
		this.forumId = forumId;
	}

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }
}
