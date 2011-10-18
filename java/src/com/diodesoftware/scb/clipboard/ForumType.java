package com.diodesoftware.scb.clipboard;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.AvoidRepost;
import com.diodesoftware.scb.ClipRequest;
import com.diodesoftware.scb.tables.Forum;
import com.diodesoftware.scb.tables.ForumComment;

public class ForumType extends ClipType {
	
	private ForumComment[] comments;
	private Forum forum;
	private Logger log = Logger.getLogger(ForumType.class);
	
	public static String DELETE_COMMENT = "del_comment_";
	public static String ADD_COMMENT = "add_comment_";
	public static String LAST_POST_FIRST = "lastPostFirst";
	
	

	public ForumType(ClipRequest request){
		DBMapper mapper = DBMapper.getInstance();
		int typeId = request.getClip().getClipTypeId();
		Connection con = request.getCon();
		forum = (Forum)mapper.load(Forum.class, typeId , con);
		String sql = "Select * from ForumComment where ForumId = ?";
		if(forum.isLastCommentFirst())
			sql += " order by Number desc";
		ArrayList list = new ArrayList();
		try{
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1,typeId);
			ResultSet rs = prepStmt.executeQuery();
			while(rs.next()){
				list.add(mapper.loadSingle(ForumComment.class, rs));
			}
			rs.close();
			prepStmt.close();
		}catch (SQLException e) {
			log.error("Error running SQL [" + sql + "]", e);
		}
		comments = new ForumComment[list.size()];
		list.toArray(comments);		
	}

	public Object getData(int mode) {
		return this;
	}

	
	public String getHeaderHtml(int mode) {	
		return null;
	}

	public String getJsp(int mode) {
		if(mode == ViewMode.READ_ONLY){
			return "forum_edit.jsp";
		}
		if(mode == ViewMode.PASSWORD_REQUIRED){
			return "viewRestricted.jsp";
		}
		return "forum_edit.jsp";
	}

	public String getOnLoadFunction(int mode) {
		return null;
	}

	
	public void save(ClipRequest request) {
		String okToProcessParam = request.getParameter(AvoidRepost.getFieldName("forum"));
		if(!AvoidRepost.okToProcess(okToProcessParam, request.getRequest(), "forum"))
			return;
		DBMapper mapper = DBMapper.getInstance();
		int typeId = request.getClip().getClipTypeId();
		
		
		boolean editMode = ViewMode.EDIT == request.getViewMode();
		Connection con = request.getCon();
		forum = (Forum)mapper.load(Forum.class, typeId , con);
		// Save Settings
		if(editMode){
			forum.setLastCommentFirst(request.getParameter(LAST_POST_FIRST)!=null);
			mapper.save(forum, con);
		}
		// Add Comment
		String comment = request.getParameter(ADD_COMMENT);
		if(comment != null && comment.trim().length() > 0){
			ForumComment fc = new ForumComment();
			fc.setForumId(typeId);
			comment = comment.replaceAll("<", "&lt;");
			comment = comment.replaceAll("\n", "<br/>");
			String handle = request.getParameter("handle");
            if(handle != null){
                handle = handle.replaceAll("<", "&lt;");
                handle = handle.replaceAll("\n", "<br/>");
			    fc.setHandle(handle);
            }
            fc.setComment(comment);
			mapper.save(fc,con);			
		}
		// Delete comments
		if(editMode){
			Iterator paramNames = request.getParameterNames();
			while(paramNames.hasNext()){
				String parm = (String)paramNames.next();
				if(parm.startsWith(DELETE_COMMENT)){
					int id = Integer.parseInt(parm.substring(DELETE_COMMENT.length()));
					deleteComment(id, typeId, con);
				}
			}
		}
		
	}
	
	private void deleteComment(int commentId, int typeId, Connection con){
		log.debug("Deleting comment [" +commentId + "] forom type [" + typeId + "]");
		String sql  = "Delete from ForumComment where ForumId = ? and Number = ?";
		try{
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, typeId);
			prepStmt.setInt(2, commentId);
			prepStmt.executeUpdate();
			prepStmt.close();
		}catch(SQLException e){
			log.error("Error running sql [" + sql + "]", e);
		}
	}

	public ForumComment[] getComments() {
		return comments;
	}

	public void setComments(ForumComment[] comments) {
		this.comments = comments;
	}

	public Forum getForum() {
		return forum;
	}

	public void setForum(Forum forum) {
		this.forum = forum;
	}
	
	

}
