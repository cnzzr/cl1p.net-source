/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: DBConnectionMgr.java,v 1.1.1.1 2003/10/13 19:19:31 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.dbmapper;

import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.*;

import com.diodesoftware.scb.ClipConfig;
import com.diodesoftware.scb.agents.SystemStatus;
import com.mchange.v2.c3p0.ComboPooledDataSource;


public class DBConnectionMgr
{
    private static Logger log = Logger.getLogger(DBConnectionMgr.class);
    private static ComboPooledDataSource cpds = null; 
	

    public DBConnectionMgr()
    {
    	try{
    		if(cpds != null)
    			return;
    		cpds = new ComboPooledDataSource();
    		ClipConfig.getInstance();
    	cpds.setDriverClass( "com.mysql.jdbc.Driver" ); //loads the jdbc driver
    	String url = ClipConfig.DB_URL + ClipConfig.DB_NAME+"?useEncoding=true&amp;characterEncoding=UTF-8";
    	cpds.setJdbcUrl(url); 
    	cpds.setUser(ClipConfig.DB_USERNAME); 
    	cpds.setPassword(ClipConfig.DB_PASSWORD);
    	cpds.setMinPoolSize(5);
    	cpds.setAcquireIncrement(5);
    	cpds.setMaxPoolSize(200); 
    	cpds.setCheckoutTimeout(6000);
    	cpds.setMaxConnectionAge(10);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }

    public Connection getConnection()
    {
	return getSimpleConnection();
	
    }

    public void returnConnection(Connection con)
    {
	if(con!=null)
	    try{con.close();}catch(Exception e){throw new RuntimeException(e);}
	
    }

    

    private Connection getSimpleConnection()
    {

    	try
    	{
    		return cpds.getConnection();
    	}catch(SQLException e)
    	{
    		int code = e.getErrorCode();
    		System.err.println("**************************Error Code " + code + " message " + e.getMessage());
    		throw new RuntimeException(e);
    	}
  
//        String url = null;
//        Connection result = null;
//        try
//        {
//            Class.forName("com.mysql.jdbc.Driver");
//            url = "jdbc:mysql://localhost/" + ClipConfig.DB_NAME;
//            url = ClipConfig.getInstance().DB_URL +  ClipConfig.getInstance().DB_NAME + "?useUnicode=yes&characterEncoding=UTF-8";
//            result = DriverManager.getConnection(url, ClipConfig.getInstance().DB_USERNAME, ClipConfig.getInstance().DB_PASSWORD);
//        }
//        catch (Exception e)
//        {
//            log.error("Can't get db connection url[" + url + "]",e);
//            SystemStatus.getInstance().setERROR(true);
//            throw new RuntimeException(e);
//        }
//        return result;
    }
    
    public Connection testConnection(String url, String dbName, String username, String password)
    	throws SQLException, Exception 	{
        String conUrl = null;
        Connection result = null;
        Class.forName("com.mysql.jdbc.Driver");        
        url = url +  dbName;
        result = DriverManager.getConnection(url, username, password);
        return result;
    	    	
    }

   

}



