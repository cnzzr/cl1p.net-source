package com.diodesoftware.scb.clipboard;

import com.diodesoftware.scb.ClipRequest;

public class PlainTextType extends ClipType {
	
	private String data = null;
	
	public PlainTextType(ClipRequest request){
		data = request.getClip().getValue();		
	}

	@Override
	public String getHeaderHtml(int mode) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getJsp(int mode) {
		if(mode == ViewMode.EDIT)
			return "plainText_edit.jsp";
		else if(mode == ViewMode.READ_ONLY)
			return "plainText_readOnly.jsp";
		// TODO Auto-generated method stub
		return "viewRestricted.jsp";
	}

	@Override
	public String getOnLoadFunction(int mode) {
		// TODO Auto-generated method stub
		return "";
	}
	
	public void save(ClipRequest clipRequest){
		// Nothing to do here. Handled by other code for now
	}

	public Object getData(int viewMode){
		return data;
	}
}
