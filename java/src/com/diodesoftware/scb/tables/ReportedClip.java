package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DBMapper;
import java.sql.ResultSet;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Apr 5, 2008
 * Time: 1:42:02 PM
 */
public class ReportedClip implements DatabaseEntry {

    private int number;
    private String uri;
    private String reason;
    private Calendar created = Calendar.getInstance();
    private boolean handled;
    private static Logger log = Logger.getLogger(ReportedClip.class);


    private DatabaseColumn[] columns = new DatabaseColumn[]{
        new DatabaseColumn("Uri", DatabaseColumnType.TEXT),
            new DatabaseColumn("Reason", DatabaseColumnType.TEXT),
            new DatabaseColumn("Created", DatabaseColumnType.DATEEPOCH),
            new DatabaseColumn("Handled", DatabaseColumnType.BOOLEAN)
    };


    public DatabaseColumn[] columns(){return columns;}

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public boolean getHandled(){
        return isHandled();
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }


    public static void report(String uri, String reason, Connection con){
        ReportedClip rc = new ReportedClip();
        rc.setUri(uri);
        rc.setReason(reason);
        DBMapper.save(rc, con);
    }

    public static List<ReportedClip> listUnhandled(Connection con){
        List<ReportedClip> result = new ArrayList<ReportedClip>();
        String sql = "Select * from ReportedClip where Handled = 'N'";
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            ResultSet rs = prepStmt.executeQuery();
            while(rs.next()){
                result.add((ReportedClip)DBMapper.loadSingle(ReportedClip.class, rs));
            }
            rs.close();
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error running SQL [" + sql + "]",e);
        }
        return result;
    }

    public static void handle(int number, Connection con){
        ReportedClip rc = (ReportedClip)DBMapper.load(ReportedClip.class, number, con);
        rc.setHandled(true);
        DBMapper.save(rc,con);
    }
}
