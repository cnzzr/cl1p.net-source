/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Sep 8, 2003
 * Time: 7:47:23 PM
 * To change this template use Options | File Templates.
 */
package com.diodesoftware.scb.agents;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.tables.UserVisit;
import com.diodesoftware.scb.tables.UserVisitHit;

import java.sql.PreparedStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Calendar;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class StatsAgent
{
    private static StatsAgent instance = null;
    private DBMapper dbMapper = null;

    private Logger log = Logger.getLogger(StatsAgent.class);

    public static synchronized void initialize(DBMapper mapper)
    {
        instance = new StatsAgent(mapper);
    }

    public static StatsAgent getInstance()
    {
        return instance;
    }

    private StatsAgent(DBMapper dbMappper)
    {
        this.dbMapper = dbMappper;
    }

    public int getPageHits(Connection con)
    {
        String sql = "SELECT MAX(Number) from Visitor";

        int hitCount = 0;
        try
        {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next())
            {
                hitCount = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        }catch(SQLException e)
        {
            log.error("Error running SQL [" + sql + "]", e);
        }

        return hitCount;
    }

  /*  public void savePageHit(ClipRequest request, Connection con)
    {
        HttpServletRequest httpRequest = request.getRequest();
        Visitor visitor = new Visitor();
        visitor.setAgentString(httpRequest.getHeader("User-Agent"));
        visitor.setIp(httpRequest.getRemoteAddr());
        visitor.setUrl(httpRequest.getRequestURL().toString());
        visitor.setVisitDate(Calendar.getInstance());

            dbMapper.save(visitor, con);
       
    }*/
    
    public void track(HttpServletRequest request, Connection con){
    	
    	UserVisit userVisit = loadUserVisit(request, con);
    	userVisit.addHit();
    	Calendar now = Calendar.getInstance();
    	userVisit.setVisitEnd(now);
    	UserVisitHit userVisitHit = new UserVisitHit(request);
    	dbMapper.save(userVisit,con);
    	userVisitHit.setUserVisitId(userVisit.getNumber());
    	dbMapper.save(userVisitHit,con);
    	
    	
    }
    
    private UserVisit loadUserVisit(HttpServletRequest request, Connection con){
    	UserVisit result = new UserVisit(request);
    	String sql = "Select * from UserVisit where SessionId = ?";
    	try{
    		PreparedStatement prepStmt = con.prepareStatement(sql);
    		prepStmt.setString(1, result.getSessionId());
    		ResultSet rs = prepStmt.executeQuery();
    		if(rs.next()){
    			result = (UserVisit)dbMapper.loadSingle(UserVisit.class, rs);
    		}
    		rs.close();
    		prepStmt.close();
    	}catch(SQLException e){
    		log.error("Error running SQL[" + sql + "]", e);
    	}    	
    	return result;
    }
}
