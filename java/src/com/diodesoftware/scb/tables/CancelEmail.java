package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.scb.ClipUtil;

import java.util.Calendar;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

public class CancelEmail implements DatabaseEntry {
    private static Logger log = Logger.getLogger(CancelEmail.class);

    private int number;
    private int userId;
    private String value;
    private Calendar created = Calendar.getInstance();

    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("UserId", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("Value", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("Created", DatabaseColumnType.DATE12)
    };

    public DatabaseColumn[] columns() {
        return columns;
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }



   public static String genValue(Connection con) {
        String s = ClipUtil.genString(150);
        boolean found = false;
        String sql = "Select * from CancelEmail where Value = ?";
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1,s);
            ResultSet rs = prepStmt.executeQuery();
            if(rs.next()){
                found = true;
            }
            rs.close();
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error running SQL [" + sql + "]", e);
        }
        if(found)return genValue(con);
        return s;
    }


}
