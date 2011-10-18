/**
 * $Date: 2003/10/13 22:11:23 $
 * $Author: Administrator $
 * $Id: Clip.java,v 1.2 2003/10/13 22:11:23 Administrator Exp $
 * $Revision: 1.2 $
 * $Source: /mnt/flashdrive/cvshome/cl1p/web-inf/src/com/diodesoftware/scb/tables/Clip.java,v $
 */
package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

public class Clip
    implements DatabaseEntry
{
    private int number;
    private String uri;
    private String value= "";
    private Calendar lastEdit = Calendar.getInstance();
    private String password = "";
    private boolean html;
    private boolean viewPassword;
    private Calendar cleanDate;
    private int keepFor = 10080; // One Week by default
    private String title = "cl1p.net";
    private int ownerId;
    private int clipType;
    private int clipTypeId;
    private int rows = 40;
    private String emailPassword = "";
    private String email = "";
    private Calendar created = Calendar.getInstance();


    


    public static final int[] ROWS = new int[]{
    	5,10,15,20,25,30,35,40,50,60
    };
    
    public static final int[] KEEP_TIMES = {
        60,120,480,1440,2880,10080,20160,525600
    };
    
    public static final String[] CLIP_TYPE_NAMES = new String[]{
    	"Plain Text",
    	"Rich Text",
    	"Message Board",
    	"Todo List"
    };
    
    public static final int CLIP_TYPE_PLAIN_TEXT = 0;
    public static final int CLIP_TYPE_RICH_TEXT = 1;
    public static final int CLIP_TYPE_FORUM = 2;
    public static final int CLIP_TYPE_TODO_LIST = 3;
    
    public static final int[] CLIP_TYPES = new int[]{
    	CLIP_TYPE_PLAIN_TEXT,
    	CLIP_TYPE_RICH_TEXT,
    	CLIP_TYPE_FORUM,
    	CLIP_TYPE_TODO_LIST
    };
    
    private boolean secure;

    private DatabaseColumn[] columns = new DatabaseColumn[]{
        new DatabaseColumn("Uri", DatabaseColumnType.BLOB),
        new DatabaseColumn("Value", DatabaseColumnType.BLOB),
        new DatabaseColumn("LastEdit", DatabaseColumnType.DATE12),
        new DatabaseColumn("Password", DatabaseColumnType.PASSWORD),
        new DatabaseColumn("Html", DatabaseColumnType.BOOLEAN),
        new DatabaseColumn("ViewPassword", DatabaseColumnType.BOOLEAN),
        new DatabaseColumn("CleanDate", DatabaseColumnType.DATE12),
        new DatabaseColumn("KeepFor", DatabaseColumnType.DECIMAL),
	    new DatabaseColumn("Title", DatabaseColumnType.CHAR_100),
        new DatabaseColumn("Secure", DatabaseColumnType.BOOLEAN),
        new DatabaseColumn("OwnerId", DatabaseColumnType.DECIMAL),
        new DatabaseColumn("ClipType", DatabaseColumnType.DECIMAL),
        new DatabaseColumn("ClipTypeId", DatabaseColumnType.DECIMAL),
        new DatabaseColumn("Rows", DatabaseColumnType.DECIMAL),
        new DatabaseColumn("FineLastEdit", DatabaseColumnType.DATEEPOCH),
        new DatabaseColumn("EmailPassword", DatabaseColumnType.CHAR_20),
        new DatabaseColumn("Email", DatabaseColumnType.TEXT),
        new DatabaseColumn("Created", DatabaseColumnType.DATEEPOCH)
    };

    public Clip(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        cleanDate = cal;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public Calendar getLastEdit()
    {
        return lastEdit;
    }

    public void setLastEdit(Calendar lastEdit)
    {
        this.lastEdit = lastEdit;

    }

    public String getPassword()
    {
        return password;
    }

    public boolean isHtml()
    {
        return html;
    }

    public boolean getHtml()
    {
        return html;
    }

    public void setHtml(boolean html)
    {
        this.html = html;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public boolean isViewPassword() {
        return viewPassword;
    }

    public boolean getViewPassword(){
        return viewPassword;
    }

    public void setViewPassword(boolean viewPassword) {
        this.viewPassword = viewPassword;
    }

    public Calendar getCleanDate() {
        return cleanDate;
    }

    public void setCleanDate(Calendar cleanDate) {
        this.cleanDate = cleanDate;
    }

    public int getKeepFor() {
        return keepFor;
    }

    public void setKeepFor(int keepFor) {
        this.keepFor = keepFor;
    }

    public void setTitle(String s){
	    title = s;
    }

    public String getTitle(){
	    return title;
    }

    public boolean getSecure(){
        return isSecure();
    }


    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }


    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public DatabaseColumn[] columns()
    {
        return columns;
    }

    public static void setClip(HttpServletRequest request, Clip clip){
        request.setAttribute("cl1p", clip);
    }

    public static Clip getClip(HttpServletRequest request){
        return (Clip)request.getAttribute("cl1p");

    }

	public int getClipType() {
		return clipType;
	}

	public void setClipType(int clipType) {
		this.clipType = clipType;
	}

	public int getClipTypeId() {
		return clipTypeId;
	}

	public void setClipTypeId(int clipTypeId) {
		this.clipTypeId = clipTypeId;
	}
	
	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getIncludeJSP(){
		switch(clipType){
			case CLIP_TYPE_PLAIN_TEXT:
				return "plainText";
			case CLIP_TYPE_RICH_TEXT:
				return "richText";
		}
		return "plainText";		
	}
    

    public long getSortCleanDate(){
        return cleanDate.getTime().getTime();
    }

    public long getSortLastEdit(){
        return lastEdit.getTime().getTime();
    }


    public Calendar getFineLastEdit() {
        return lastEdit;
    }

    public void setFineLastEdit(Calendar fineLastEdit) {
        if(fineLastEdit != null)
            this.lastEdit = fineLastEdit;
    }


    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public int daysLeft(){
        return daysLeft(Calendar.getInstance());
    }

    public int daysLeft(Calendar cal){
        if(created == null)return 7*30; //TODO make sure this can't happen
        Calendar expire = Calendar.getInstance();
        expire.setTimeInMillis(created.getTimeInMillis());
        expire.add(Calendar.MINUTE, keepFor);
        if(cal.after(expire))return 0;
        long l =  expire.getTimeInMillis() - cal.getTimeInMillis();
        int days = (int)(l / (long)(24*60*60*1000));
        days++;
        return days;

    }
}



