package com.diodesoftware.scb.clipboard;

import com.diodesoftware.scb.ClipRequest;
import com.diodesoftware.scb.tables.Clip;

public abstract class ClipType {
	
	public abstract String getJsp(int mode);
	public abstract String getHeaderHtml(int mode);
	public abstract String getOnLoadFunction(int mode);
	public abstract Object getData(int mode);
	public abstract void save(ClipRequest clipRequest);
	
	public static void load(ClipRequest request){
		if(request.getCl1pType() != null) return;
		Clip clip = request.getClip();		
		ClipType clipType = null;
		switch(clip.getClipType()){
			case Clip.CLIP_TYPE_PLAIN_TEXT:
				clipType = new PlainTextType(request);
				break;
			case Clip.CLIP_TYPE_RICH_TEXT:
				clipType = new RichTextType(request);
				break;
			case Clip.CLIP_TYPE_FORUM:
				clipType = new ForumType(request);
				break;
			case Clip.CLIP_TYPE_TODO_LIST:				
				clipType = new TodoListType(request);
				break;
		}
		request.setClipType(clipType);
	}				
}
