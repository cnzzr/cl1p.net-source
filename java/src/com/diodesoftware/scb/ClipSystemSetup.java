package com.diodesoftware.scb;

import java.io.File;
import java.sql.Connection;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.agents.SystemSettingAgent;
import com.diodesoftware.scb.sysop.SysopLogin;
import com.diodesoftware.scb.tables.*;

public class ClipSystemSetup {
	public static final String UPLOAD_TEMP = "/WEB-INF/tempupload";
	private static Logger log = Logger.getLogger(ClipSystemSetup.class);
	
public static void setupTables(Connection con, StringBuffer messages){
	log.info("Startup SCB");
	
    DBMapper mapper = new DBMapper();
                                                                  
    
    
    
    try {
        mapper.createTable(new Clip(), con,messages);
        mapper.createTable(new ClipHistory(), con,messages);           
        mapper.createTable(new User(), con,messages);
        mapper.createTable(new ClipCookie(), con,messages);
        mapper.createTable(new UserClip(), con,messages);
        mapper.createTable(new EMail(), con,messages);
        mapper.createTable(new ClipLink(), con,messages);
        mapper.createTable(new ClipTab(), con,messages);
        mapper.createTable(new PlainTextTab(), con,messages);
        mapper.createTable(new Payment(), con,messages);
        mapper.createTable(new DownloadCount(), con,messages);
        mapper.createTable(new Owner(), con,messages);
        mapper.createTable(new UrlCheckout(), con,messages);
        mapper.createTable(new SysopLogin(), con,messages);
        mapper.createTable(new CancelEmail(), con,messages);
        mapper.createTable(new UrlCredit(), con,messages);
        mapper.createTable(new RichText(), con,messages);
        mapper.createTable(new UserVisit(), con,messages);
        mapper.createTable(new UserVisitHit(), con,messages);
        mapper.createTable(new TodoList(), con,messages);
        mapper.createTable(new TodoListItem(), con,messages);
        mapper.createTable(new Forum(), con,messages);
        mapper.createTable(new ForumComment(),con,messages);
        mapper.createTable(new SystemSetting(), con,messages);
        mapper.createTable(new EvalCustomer(), con, messages);
        mapper.createTable(new Customer(), con, messages);
        mapper.createTable(new ClipS3Object(), con, messages);
        mapper.createTable(new Ignoremail(), con, messages);
        mapper.createTable(new ReportedClip(),con,messages);
        mapper.createTable(new ClipS3Useage(),con,messages);
        mapper.createTable(new Bob(),con,messages);
    } finally {
    
    }
    SystemSettingAgent.checkDefaults(con);
    
}
public static void createUploadDir(String dir, StringBuffer messages, ServletContext context){
    try{
    File f = new File(dir);
    f.mkdirs();
    f = new File(context.getRealPath(UPLOAD_TEMP));
    f.mkdirs();
    }catch(Exception e){
        log.error("Error creating Upload Dir [" + dir + "] and temp UPLOAD DIR [" + UPLOAD_TEMP + "]",e);
    }
}
}
