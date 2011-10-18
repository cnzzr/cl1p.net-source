package com.diodesoftware.scb.clipboard;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.ClipRequest;
import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.scb.tables.RichText;

public class RichTextType extends ClipType {

	private RichText data = null;
	
	public RichTextType(ClipRequest request){
		Clip clip = request.getClip();
		DBMapper dbMapper = DBMapper.getInstance(); 
		RichText rt = (RichText)dbMapper.load(RichText.class, 
				clip.getClipTypeId(), request.getCon());
		data = rt;
	}
	
	@Override
	public String getHeaderHtml(int mode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJsp(int mode) {
		if(mode == ViewMode.READ_ONLY){
			return "richText_readOnly.jsp";
		}
		if(mode == ViewMode.PASSWORD_REQUIRED){
			return "viewRestricted.jsp";
		}
		return "richText_edit.jsp";
	}

	@Override
	public String getOnLoadFunction(int mode) {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(ClipRequest request){
		Clip clip = request.getClip();
		DBMapper dbMapper = DBMapper.getInstance(); 
		RichText rt = (RichText)dbMapper.load(RichText.class, 
				clip.getClipTypeId(), request.getCon());
		String newValue = request.getParameter("ctrlcv");
		rt.setValue(newValue);
		dbMapper.save(rt, request.getCon());
	}
	
	public Object getData(int viewMode){
		return data;
	}
}
