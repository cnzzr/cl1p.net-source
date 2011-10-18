package com.diodesoftware.scb.tables;

import java.net.URL;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.scb.ClipRequest;
import com.diodesoftware.scb.ClipSession;

public class UserVisit implements DatabaseEntry {
	
	private Calendar visitStart = Calendar.getInstance();
	private int userId;
	private int clipId;
	private String userAgent;
	private String sessionId;
	private String ip;
	private String ref;
	private String enterUri;
	private String host;
	private int number;
	private int hitCount;
	private Calendar visitEnd = Calendar.getInstance();
	
	private static Logger log = Logger.getLogger(UserVisit.class);
	
	private DatabaseColumn[] columns = new DatabaseColumn[]{
			new DatabaseColumn("VisitStart", DatabaseColumnType.DATE12),
			new DatabaseColumn("UserId", DatabaseColumnType.DECIMAL),
			new DatabaseColumn("UserAgent", DatabaseColumnType.TINY_TEXT),
			new DatabaseColumn("SessionId", DatabaseColumnType.TINY_TEXT),
			new DatabaseColumn("Ip", DatabaseColumnType.CHAR_100),
			new DatabaseColumn("Ref", DatabaseColumnType.TEXT),
			new DatabaseColumn("EnterUri", DatabaseColumnType.TEXT),
			new DatabaseColumn("Host", DatabaseColumnType.CHAR_20),
			new DatabaseColumn("VisitEnd", DatabaseColumnType.DATE12),
			new DatabaseColumn("HitCount", DatabaseColumnType.DECIMAL),
			new DatabaseColumn("ClipId", DatabaseColumnType.DECIMAL)
	};
	
	
	public UserVisit(){}
	
	public UserVisit(HttpServletRequest request){
		HttpSession httpSession = request.getSession();
    	sessionId = httpSession.getId();
    	ip = request.getRemoteAddr();
    	userAgent = request.getHeader("User-Agent");
    	ClipRequest clipRequest = ClipRequest.getClipRequest(request);
    	if(clipRequest != null){
    		Clip clip = clipRequest.getClip();
    		if(clip != null){
    			clipId = clip.getNumber();
    		}
    	}
    	ref = request.getHeader("referer");
    	if(ref == null){
    		ref = request.getHeader("referrer");
    	}
    	try{
    		URL url = new URL(request.getRequestURL().toString());
    		host = url.getHost();
    	}catch(Exception e){
    		log.error("Error creating URL",e);
    	}
    	enterUri = request.getRequestURI();
    	ClipSession clipSession = ClipSession.getSession(request);
    	if(clipSession != null){
    		if(clipSession.isLoggedIn()){
    			userId = clipSession.getUser().getNumber();
    		}
    	}
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

	public String getEnterUri() {
		return enterUri;
	}

	public void setEnterUri(String enterUri) {
		this.enterUri = enterUri;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}	

	public int getHitCount() {
		return hitCount;
	}

	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	public Calendar getVisitEnd() {
		return visitEnd;
	}

	public void setVisitEnd(Calendar visitEnd) {
		this.visitEnd = visitEnd;
	}

	public Calendar getVisitStart() {
		return visitStart;
	}

	public void setVisitStart(Calendar visitStart) {
		this.visitStart = visitStart;
	}

	public int getClipId() {
		return clipId;
	}

	public void setClipId(int cl1pId) {
		this.clipId = cl1pId;
	}
	
	public void addHit(){
		hitCount++;
	}
	
	

}
