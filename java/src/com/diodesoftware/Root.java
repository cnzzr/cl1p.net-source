package com.diodesoftware;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public class Root {

    private static ThreadLocal<Root> _threadLocal = new ThreadLocal<Root>();

    private Root(){}
    private String rootUrl = null;

    private String getRootUrl(){
        return rootUrl;
    }

    public void setRootUrl(String s){
        rootUrl = s;
    }

    public static void initialize(HttpServletRequest request){
        String root = request.getContextPath();
        Root r = _threadLocal.get();
        if(r == null)r =new Root();
        r.setRootUrl(root);
        _threadLocal.set(r);
    }

    public static String r(){
        Root r = _threadLocal.get();
        return r.getRootUrl();
    }
    
    public static String R(HttpServletRequest request){
    	try{
    		URL url = new URL(request.getRequestURL().toString());
    		String root = request.getContextPath();
    		String port = "";
    		if(url.getPort() != 80 && url.getPort() != -1){
    			port = ":" + url.getPort();
    		}
    		String result = "http://" + url.getHost() + port + root;
    		return result;
    	}catch (Exception e) {
    		throw new RuntimeException(e);
		}
    }    
}
