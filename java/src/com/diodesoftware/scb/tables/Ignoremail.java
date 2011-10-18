package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DBMapper;

import java.util.Calendar;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Apr 3, 2008
 * Time: 7:03:04 PM
 */
public class Ignoremail implements DatabaseEntry {

    private int number;
    private String email;
    private Calendar created;
    private static Logger log = Logger.getLogger(Ignoremail.class);


    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("Email", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("Created", DatabaseColumnType.DATEEPOCH)
           };


    public DatabaseColumn[] columns(){
        return columns;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public static boolean isIgnored(String email, Connection con){
        boolean result = false;
        email = email.toLowerCase();
        String sql = "select * from Ignoreemail where Email = ?";
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1, email);
            ResultSet rs = prepStmt.executeQuery();
            result = rs.next();
            rs.close();
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error running SQL [" + sql + "]",e);
        }
        return result;
    }

    public static void add(String email, Connection con){
        if(!isIgnored(email,con)){
            email = email.toLowerCase();
            Ignoremail im = new Ignoremail();
            im.setEmail(email);
            im.setCreated(Calendar.getInstance());
            DBMapper.save(im, con);
        }
    }
}
