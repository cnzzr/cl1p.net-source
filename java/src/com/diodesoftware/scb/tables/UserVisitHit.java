package com.diodesoftware.scb.tables;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.scb.ClipRequest;

public class UserVisitHit implements DatabaseEntry {

	private int number;
	private Calendar hitDate = Calendar.getInstance();
	private int userVisitId;
	private String hitUri;
	private int clipId;
	private DatabaseColumn[] columns = new DatabaseColumn[]{
		new DatabaseColumn("HitDate", DatabaseColumnType.DATE12),
		new DatabaseColumn("HitUri", DatabaseColumnType.TEXT),
		new DatabaseColumn("ClipId", DatabaseColumnType.DECIMAL),
		new DatabaseColumn("UserVisitId", DatabaseColumnType.DECIMAL)
	};
	
	public UserVisitHit(){
		
	}
	
	public UserVisitHit(HttpServletRequest request){
		ClipRequest clipRequest = ClipRequest.getClipRequest(request);
    	if(clipRequest != null){
    		Clip clip = clipRequest.getClip();
    		if(clip != null){
    			clipId = clip.getNumber();
    		}
    	}
    	hitUri = request.getRequestURI();
	}
	
	
	public DatabaseColumn[] columns() {
		return columns;
	}

	public int getNumber() {

		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getClipId() {
		return clipId;
	}

	public void setClipId(int clipId) {
		this.clipId = clipId;
	}

	public Calendar getHitDate() {
		return hitDate;
	}

	public void setHitDate(Calendar hitDate) {
		this.hitDate = hitDate;
	}

	public String getHitUri() {
		return hitUri;
	}

	public void setHitUri(String hitUri) {
		this.hitUri = hitUri;
	}

	public int getUserVisitId() {
		return userVisitId;
	}

	public void setUserVisitId(int userVisitId) {
		this.userVisitId = userVisitId;
	}
	

	
}
