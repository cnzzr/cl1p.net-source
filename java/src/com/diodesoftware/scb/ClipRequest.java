package com.diodesoftware.scb;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.sql.Connection;

import com.diodesoftware.scb.clipboard.ClipType;
import com.diodesoftware.scb.sysop.SysopSession;
import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.scb.tables.Owner;
import com.diodesoftware.scb.tables.User;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Apr 30, 2006
 * Time: 3:17:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClipRequest {
    private HttpServletRequest request;
    private String uri;
    private String url;
    private String extention;
    private String referer;
    private Map parameters;
    private static Logger log = Logger.getLogger(ClipRequest.class);
    private Connection con;
    private String baseName;
    private boolean SSL = false;
    private Clip clip;
    private Owner owner;
    private User user;
    private boolean wap;
    private int viewMode;
    private ClipType clipType;
    private boolean stumbler = false;

    public ClipRequest(HttpServletRequest request, Connection con){
        this.request =  request;
        uri = request.getRequestURI();
        url = request.getRequestURL().toString();
        extention = parseExtention(request);
        wap = (extention.equals(".xml"));
        parameters = buildMap(request);
        referer = request.getHeader("referer");
        if(referer!= null && referer.indexOf("stumbleupon.com") != -1){
        	stumbler = true;
        }
        this.con = con;
        if(request.getParameter(HtmlParam.JUMP) != null){
            uri = "/" + request.getParameter(HtmlParam.JUMP);
        }

    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setAttribute(String key, Object value){
        this.request.setAttribute(key, value);
    }

    public Object getAttribute(String key){
        return this.request.getAttribute(key);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpSession getSession(){
        return this.request.getSession();
    }

    public String getExtention() {
        return extention;
    }

    public void setExtention(String extention) {
        this.extention = extention;
    }

    public String getParameter(String key){
        return (String)parameters.get(key);
    }

    public void setParameter(String key, String value){
        parameters.put(key, value);
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    private static String parseExtention(HttpServletRequest request) {
        String extention = ".html";
        Boolean mobile = (Boolean)request.getAttribute(HtmlParam.MOBILE_ATTR);
        if(mobile!=null&&mobile.booleanValue()){
             if(log.isDebugEnabled())
                log.debug("Extention is xml");
            extention = ".xml";
        }else{
            if(log.isDebugEnabled()){
                log.debug("Extention is html");
            }
        }

        return extention;
    }

    public void reInitParamererMap(){
        parameters = buildMap(request);
    }

     private Map buildMap(HttpServletRequest request) {
        Map map = new HashMap();
         if(log.isDebugEnabled())
            log.debug("Building Parameter map");
         
         Map parmMap = new HashMap();
         try{
         parmMap = request.getParameterMap();
         }catch(Throwable e){
             log.error("Error getting parameters from request:" + request.getRequestURL(),e);
         }
        
         if(log.isDebugEnabled()){
             log.debug("Param map size [" + parmMap.size() + "]");
         }
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = request.getParameter(name);
            if(log.isDebugEnabled()){
                log.debug("Adding Name [" + name + "] Value [" + value + "] To Parameter map");
            }
            map.put(name, value);
        }
        return map;
    }

    public Connection getCon() {
        return con;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public static void setClipRequest(HttpServletRequest request, ClipRequest clipRequest){
        request.setAttribute("clipRequest", clipRequest);
    }

    public static ClipRequest getClipRequest(HttpServletRequest request){
        return (ClipRequest)request.getAttribute("clipRequest");        
    }

    public boolean isSSL() {
        return SSL;
    }

    public void setSSL(boolean SSL) {
        this.SSL = SSL;
    }


    public Clip getClip() {
        return clip;
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isOwned(){
        return  this.owner != null;
    }

    public boolean isOwner(){
        return (isOwned() &&
                this.user != null &&
                this.user.getNumber() == this.owner.getUserId());
    }

    public boolean isSellable(){
        return (!isOwned());
    }

    public boolean showAds(){
    	if(!ClipConfig.CL1P_SITE)return false;
        return (!isOwned() && (user == null || !user.isPro() ));
    }

    public boolean showNonLoggedInAds(){
        if(!ClipConfig.CL1P_SITE)return false;
        if(isOwned())return false;
        if(isLoggedIn())return false;
        return true;
    }
    
    public boolean isLoggedIn(){
    	return user != null;
    }


    public boolean isWap() {
        return wap;
    }

    public void setWap(boolean wap) {
        this.wap = wap;
    }

	public int getViewMode() {
		return viewMode;
	}

	public void setViewMode(int viewMode) {
		this.viewMode = viewMode;
	}

	public ClipType getCl1pType() {
		return clipType;
	}

	public void setClipType(ClipType clipType) {
		this.clipType = clipType;
	}
    
	
	public boolean isSslAllowed(){
		return (isOwned() && owner.isSslAccess());
	}

	public boolean isStumbler() {
		return stumbler;
	}

	public void setStumbler(boolean stumbler) {
		this.stumbler = stumbler;
	}
	
	public Iterator getParameterNames(){
		return parameters.keySet().iterator();
	}

	public boolean isRoot()
	{
		SysopSession sysopSession = SysopSession.getInstance(request);      
		return sysopSession.isLoggedIn();
	}

    public void setErrorMessage(String s)
    {
        request.setAttribute("errorMessage",s);
    }
	
	
    
}
