/**
 * $Date: 2003/10/13 22:11:23 $
 * $Author: Administrator $
 * $Id: ClipAgent.java,v 1.2 2003/10/13 22:11:23 Administrator Exp $
 * $Revision: 1.2 $
 * $Source: /mnt/flashdrive/cvshome/cl1p/web-inf/src/com/diodesoftware/scb/agents/ClipAgent.java,v $
 */
package com.diodesoftware.scb.agents;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.clipboard.ClipSaver;
import com.diodesoftware.scb.clipboard.ClipSqlException;
import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.scb.tables.ClipHistory;
import com.diodesoftware.scb.tables.ClipLink;
import com.diodesoftware.scb.tables.Forum;
import com.diodesoftware.scb.tables.RichText;
import com.diodesoftware.scb.tables.TodoList;

public class ClipAgent
{
    private static ClipAgent instance = null;
    public DBMapper dbMapper;
    private Logger log = Logger.getLogger(ClipAgent.class);

    private ClipAgent(DBMapper mapper)
    {
        this.dbMapper = mapper;
        
    }

    public static synchronized void initalize(DBMapper dbMapper)
    {
        instance = new ClipAgent(dbMapper);
    }

    public static ClipAgent getInstance()
    {
        return instance;
    }

    public Clip loadClip(String uri, Connection con)
    {
        Clip result = null;
        String sql = "Select * from Clip where Uri = ?";

        try
        {
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1, uri);
            ResultSet rs = prepStmt.executeQuery();
            if(rs.next())
                result = (Clip)dbMapper.loadSingle(Clip.class, rs);
            rs.close();
            prepStmt.close();
        }catch(SQLException e)
        {
            log.error("Error running SQL [" + sql + "]", e);
        }
        return result;
    }

    public void saveClip(Clip clip, Connection con)
    {
    	String content = clip.getValue();
    	if(content == null)
    		content = "";
    	if(content.getBytes().length > ClipSaver.MAX_VALUE_SIZE)
		{
			byte[] trim = new byte[ClipSaver.MAX_VALUE_SIZE];
			System.arraycopy(content.getBytes(), 0, trim, 0, ClipSaver.MAX_VALUE_SIZE);
			content = new String(trim);
			log.error("Content Bytes:" + content.getBytes().length  + " > " + ClipSaver.MAX_VALUE_SIZE + ". Triming");
			clip.setValue(content);
		}
    	try
    	{
            dbMapper.save(clip,con);
    	}catch(ClipSqlException e)
    	{
    		log.error("Error Saving clip.\n" +
    				"Title Size:" + clip.getTitle().length() + " \n" +
    						"Value Size:" + clip.getValue().length(),e);
    	}
    	
//	    ClipHistory history = new ClipHistory();
//	    history.setClipNumber(clip.getNumber());
//	    history.setLastEdit(clip.getLastEdit());
//	    history.setValue(clip.getValue());
//	    dbMapper.save(history,con);
    }

    public void removePassword(Clip clip, Connection con){
        String sql = "update Clip set Password = null where Uri = ?";
        log.debug("Setting password  null for [" +clip.getUri() + "] [" + sql + "]");
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1, clip.getUri());
            prepStmt.executeUpdate();
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error running sql [" + sql + "]", e);
        }
    }

    public void saveClipLink(ClipLink cl, Connection con){
            dbMapper.save(
                    cl, con
            );
    }

    public ClipLink[] getLinksTo(Clip clip, Connection con){
        List list = new ArrayList();
          String sql = "Select * from ClipLink where ClipId = ? and Blocked = 'N'";
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setInt(1, clip.getNumber());
            ResultSet rs = prepStmt.executeQuery();
            while(rs.next()){
                list.add(dbMapper.loadSingle(ClipLink.class, rs));
            }
            rs.close();
            prepStmt.close();

        }catch(SQLException e){
            log.error("SQL Exception loading link code [" + sql + "]",e);
        }
        //log.error("getLinks got [" + list.size() + "] from [" + sql + "] Id[" + clip.getNumber() + "]");
        ClipLink[] result = new ClipLink[list.size()];
        list.toArray(result);
        return result;
    }

    public ClipLink[] getLinksFrom(Clip clip, Connection con){
        List list = new ArrayList();
        String sql = "Select cl.*, c.Uri from ClipLink cl inner join Clip c on c.Number = cl.ClipId where cl.ToClipId = ? and cl.Blocked = 'N'";
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setInt(1, clip.getNumber());
            ResultSet rs = prepStmt.executeQuery();
            while(rs.next()){
                ClipLink cl = new ClipLink();
                cl.setNumber(rs.getInt("cl.Number"));
                cl.setClipId(rs.getInt("cl.ClipId"));
                cl.setToClipId(rs.getInt("cl.toClipId"));
                cl.setUri(rs.getString("c.Uri"));
                list.add(cl);
            }
            rs.close();
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error running SQL [" + sql + "]", e);
        }
        ClipLink[] result = new ClipLink[list.size()];
        list.toArray(result);
        return result;
    }

    public void changeClipType(Clip clip, Connection con, int clipType){
    	clip.setClipType(clipType);
    	if(clipType == Clip.CLIP_TYPE_RICH_TEXT){
    		RichText richText = new RichText();
    		dbMapper.save(richText, con);
    		clip.setClipTypeId(richText.getNumber());    		
    	}else if(clipType == Clip.CLIP_TYPE_TODO_LIST){
    		TodoList todoList = new TodoList();
    		dbMapper.save(todoList, con);
    		clip.setClipTypeId(todoList.getNumber());    		
    	}else if(clipType == Clip.CLIP_TYPE_FORUM){
    		Forum forum = new Forum();
    		dbMapper.save(forum, con);
            clip.setRows(5);
            clip.setClipTypeId(forum.getNumber());
    	}
    	dbMapper.save(clip, con);
    	
    }



}

/**
 * $Log: ClipAgent.java,v $
 * Revision 1.2  2003/10/13 22:11:23  Administrator
 * *** empty log message ***
 *
 * Revision 1.1.1.1  2003/10/13 19:19:31  Administrator
 * no message
 *
 * Revision 1.1.1.1  2003/10/13 00:21:18  root
 * no message
 *
 * Revision 1.1  2003/09/06 23:48:28  Administrator
 * *** empty log message ***
 *
 */

