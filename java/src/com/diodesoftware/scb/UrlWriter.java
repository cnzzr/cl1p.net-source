package com.diodesoftware.scb;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;

public class UrlWriter {
    private static Logger log = Logger.getLogger(UrlWriter.class);

    public static String write(String uri, String prependHost, HttpServletRequest request){
        try{
            URL requestURL = new URL(request.getRequestURL().toString());
            String host = requestURL.getHost();
            int port = requestURL.getPort();
            StringBuffer result = new StringBuffer();
            result.append("http://");
            if(prependHost != null){
                result.append(prependHost).append(".");
            }
            result.append(host);
            if(port != 80 && port != -1){ // The -1 shows up in production
                result.append(":").append(port);
            }
          
            result.append(uri);
            return result.toString();
        }catch(Exception e){
            log.error("Error wrting URL [" + uri + "]", e);
        }
        return null;
    }
}
