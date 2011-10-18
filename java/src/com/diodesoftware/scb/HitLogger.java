package com.diodesoftware.scb;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class HitLogger {

    private static Logger log = Logger.getLogger("hits");

    public static void logHit(HttpServletRequest request) {
        String referer = request.getHeader("referer");
        String url = request.getRequestURL().toString();
        String host = null;
        String uri = request.getRequestURI();
        if(uri.startsWith("/cl1p-inc-rgdm/")){
        	return;
        }
        String[] endings = {".js",".gif",".jpg",".css",".ico"};
        for(String s : endings){
        	if(uri.endsWith(s))return;
        }
        
        int hostStart = url.indexOf("cl1p.net");
        if (hostStart != -1) {
            host = url.substring(7, hostStart);
        }
        
        String id = request.getSession().getId();
        if(id.length() > 4){
        	id = id.substring(id.length()-5);
        }
        log.debug("H[" + host + "]S[" + id + "]U[" + uri + "]R[" + referer + "]I[" +
                request.getRemoteHost() + "]");
    }
}
