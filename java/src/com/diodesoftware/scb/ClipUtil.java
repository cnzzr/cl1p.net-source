package com.diodesoftware.scb;

import org.apache.log4j.Logger;

import java.util.Random;
import java.net.URL;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Jul 3, 2006
 * Time: 6:05:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClipUtil {

    private static Random rnd = new Random(System.currentTimeMillis());
    private static Logger log = Logger.getLogger(ClipUtil.class);

    public static boolean isBlank(String s){
        if(s==null)return true;
        if(s.trim().length()==0)return true;
        return false;
    }

    public static String blankNull(String s){
        if(isBlank(s))return "";
        return s;
    }

    public static char genChar(){
        int i = rnd.nextInt(26);
        i += 65;
        return (char)i;
    }

    public static String genString(int length){
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < length;  i++){
            sb.append(genChar());
        }
        return sb.toString();
    }

    public static void close(Statement stmt){
        if(stmt!=null)try{stmt.close();}catch(SQLException e){log.error("error closing statement", e);}
    }

    public static void close(ResultSet rs){
        if(rs!=null)try{rs.close();}catch(SQLException e){log.error("error closing statement", e);}
    }
    
    public static String baseUrl(HttpServletRequest request){
    	StringBuffer base = new StringBuffer();;
    	StringBuffer sb =request.getRequestURL();
    	try{
    		URL url = new URL(sb.toString());
    		base.append(url.getProtocol());
    		base.append("://");
    		base.append(url.getHost());
    		int port = url.getPort();
    		if(port != 80 && port != -1){
    			base.append(":").append(port);
    		}
    		    		    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}    	
    	return base.toString();
    }
}
