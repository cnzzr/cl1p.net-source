package com.diodesoftware.scb;
import javax.servlet.http.HttpServletRequest;



public class AvoidRepost {
	
	public static String getHiddenField(HttpServletRequest request, String formName){
		String okValue = System.currentTimeMillis() + "";
		request.getSession().setAttribute(AvoidRepost.class.getName()+ formName, okValue);
		return "<input type='hidden' name='" + AvoidRepost.class.getName()+ formName + "' value='" + okValue + "'>";
	}
	
	public static String getFieldName(String forumName){
		return AvoidRepost.class.getName()+ forumName;
	}
	
	public static boolean okToProcess(String value , HttpServletRequest request, String formName){
				
		if(value == null)return false;
		String expected = (String)request.getSession().getAttribute(AvoidRepost.class.getName()+ formName);
		return (value.equals(expected));
	}
}
