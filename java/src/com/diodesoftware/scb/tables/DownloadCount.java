/**
 * $Date: 2003/10/13 22:11:23 $
 * $Author: Administrator $
 * $Id: Clip.java,v 1.2 2003/10/13 22:11:23 Administrator Exp $
 * $Revision: 1.2 $
 * $Source: /mnt/flashdrive/cvshome/cl1p/web-inf/src/com/diodesoftware/scb/tables/Clip.java,v $
 */
package com.diodesoftware.scb.tables;

import com.diodesoftware.scb.GLOBAL;
import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.*;
import java.sql.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import org.apache.log4j.Logger;


public class DownloadCount
    implements DatabaseEntry
{
    private static Logger log = Logger.getLogger(DownloadCount.class);
    private int number;
    private int clipId;
    private int count;
    private Calendar created = Calendar.getInstance();


    private DatabaseColumn[] columns = new DatabaseColumn[]{
        new DatabaseColumn("ClipId", DatabaseColumnType.DECIMAL),
	new DatabaseColumn("Count", DatabaseColumnType.DECIMAL),
        new DatabaseColumn("Created", DatabaseColumnType.DATE12)
    
    };
    
    public int getNumber(){
	return number;
    }

    public void setNumber(int i){
	number = i;
    }

    public DatabaseColumn[] columns(){
	return columns;
    }

    public int getClipId(){
	return clipId;
    }

    public void setClipId(int i){
	clipId = i;
    }

    public int getCount(){
	return count;
    }

    public void setCount(int i){
	count = i;
    }

    public Calendar getCreated(){
	return created;
    }

    public void setCreated(Calendar c){
	created = c;
    }

    public static int getDownloadsLeft(Clip clip, Connection con){
	int max = GLOBAL.MAX_DOWNLOADS;
	int used = loadCount(clip, con);
	if( used == -1)return 0;
	return max - used;
    }

    public static int loadCount(Clip clip, Connection con){
	if(clip.getNumber() < 2){
	    if(log.isDebugEnabled())
		log.debug("Can't load clip count. Number is [" + clip.getNumber() + "] < 2");
	    return -1;
	}
	int result = -1;
	DownloadCount dc = loadCount(clip.getNumber(), con);

	int count = 0;
	if(dc != null){
	    count = dc.getCount();
	}else{
	    if(log.isDebugEnabled()){
		log.debug("Download Cound not found for clip [" + clip.getNumber() + "]");
	    }
	}
	return count;
    }

    private static DownloadCount loadCount(int clipId, Connection con){
	DownloadCount dc = null;
	String sql = "Select * from DownloadCount where ClipId = ?";
	try{
	    PreparedStatement prepStmt = con.prepareStatement(sql);
	    prepStmt.setInt(1, clipId);
	    ResultSet rs = prepStmt.executeQuery();
	    
	    if(rs.next()){
		dc = (DownloadCount)DBMapper.getInstance().loadSingle(DownloadCount.class, rs);
	    }
	    rs.close();
	    prepStmt.close();
	}catch(SQLException e){
	    log.error("Error running SQL [" + sql + "]",e);
	}
	return dc;
    }

    public static void resetCount(Clip clip, Connection con){
if(clip.getNumber() < 2){
	    	    if(log.isDebugEnabled())
		log.debug("Can't add clip count. Number is [" + clip.getNumber() + "] < 2");

	    return;
	}
	int result = -1;
	DownloadCount dc = loadCount(clip.getNumber(), con);

	int count = 0;
	if(dc == null){
	    dc = new DownloadCount();
	    dc.setClipId(clip.getNumber());
	    count = 0;
	}
	else{
	    count = dc.getCount();
	}

	dc.setCount(0);
	DBMapper.getInstance().save(dc, con);
	if(log.isDebugEnabled())
	    log.debug("Reset clip count for clip [" + clip.getNumber() + "]"); 
    
    }

    public static void addCount(Clip clip, Connection con){
	if(clip.getNumber() < 2){
	    	    if(log.isDebugEnabled())
		log.debug("Can't add clip count. Number is [" + clip.getNumber() + "] < 2");

	    return;
	}
	int result = -1;
	DownloadCount dc = loadCount(clip.getNumber(), con);

	int count = 0;
	if(dc == null){
	    dc = new DownloadCount();
	    dc.setClipId(clip.getNumber());
	    count = 0;
	}
	else{
	    count = dc.getCount();
	}
	int next = count + 1;
	dc.setCount(next);
	DBMapper.getInstance().save(dc, con);
	if(log.isDebugEnabled())
	    log.debug("Saving clip count for clip [" + clip.getNumber() + "] count [" + next + "]"); 
    }
}
