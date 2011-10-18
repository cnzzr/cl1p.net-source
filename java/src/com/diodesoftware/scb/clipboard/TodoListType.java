package com.diodesoftware.scb.clipboard;


import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.ClipRequest;
import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.scb.tables.TodoListItem;

import java.sql.*;

public class TodoListType extends ClipType {
	private Logger log = Logger.getLogger(TodoListType.class);
	private List items = new ArrayList();
	public static final String DONE_PREFIX = "todo_D_";
	public static final String INDEX_PREFIX = "todo_I_";
	public static final String DESCRIPTION_PREFIX = "todo_D_";

	public TodoListType(ClipRequest request){
		Clip clip = request.getClip();
		int number = clip.getClipTypeId();
		String sql = "Select * from TodoListItem where TodoListId = ?";
		DBMapper mapper = DBMapper.getInstance();
		Connection con = request.getCon();
		try{
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, number);
			ResultSet rs = prepStmt.executeQuery();
			while(rs.next()){
				items.add(mapper.loadSingle(TodoListItem.class, rs));
			}
			rs.close();
			prepStmt.close();
		}catch(SQLException e){
			log.error("Error running SQL [" + sql + "]",e);
		}
	}
	
	@Override
	public String getHeaderHtml(int mode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJsp(int mode) {
		if(mode == ViewMode.READ_ONLY){
			return "todo_readOnly.jsp";
		}
		if(mode == ViewMode.PASSWORD_REQUIRED){
			return "viewRestricted.jsp";
		}
		return "todo_edit.jsp";
	}

	@Override
	public String getOnLoadFunction(int mode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getData(int mode) {
		return items;
	}

	@Override
	public void save(ClipRequest clipRequest) {
		int i = 0;
		String description = clipRequest.getParameter(DESCRIPTION_PREFIX + i);
		while(description != null){
			description = clipRequest.getParameter(DESCRIPTION_PREFIX + i);
			String done = clipRequest.getParameter(DONE_PREFIX + i);
			String index = clipRequest.getParameter(INDEX_PREFIX + i);
		}
		
		
		
	}
	
	

}
