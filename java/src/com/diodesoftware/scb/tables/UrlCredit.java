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

import javax.servlet.http.HttpServletRequest;

public class UrlCredit implements DatabaseEntry {

    private static Logger log = Logger.getLogger(UrlCredit.class);

    private int number;
    private int userId;
    private Calendar created = Calendar.getInstance();
    private String transactionId;
    private int ownerId;
    private boolean used;
    private String token;
    private String uri;
    private String email;
    private int years;
    private boolean sslAccess;
    

    public DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("UserId", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("Created", DatabaseColumnType.DATE12),
            new DatabaseColumn("TransactionId", DatabaseColumnType.TINY_TEXT),
            new DatabaseColumn("OwnerId", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("Used", DatabaseColumnType.BOOLEAN),
            new DatabaseColumn("Token", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("Uri", DatabaseColumnType.TEXT),
            new DatabaseColumn("Email", DatabaseColumnType.TEXT),
            new DatabaseColumn("Years", DatabaseColumnType.DECIMAL),
            new DatabaseColumn("SslAccess", DatabaseColumnType.BOOLEAN)
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


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public boolean getUsed(){
        return isUsed();
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }
    
    public boolean getSslAccess(){
    	return isSslAccess();
    }
    
    public boolean isSslAccess(){
    	return sslAccess;
    }
    
    public void setSslAccess(boolean s){
    	sslAccess = s;
    }

    public static String generateToken(Connection con){
        String sql = "Select * from UrlCredit where Token = ?";
        String token = null;
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            boolean unique = false;
            while(!unique){
                token = ClipUtil.genString(150);
                prepStmt.setString(1, sql);
                ResultSet rs = prepStmt.executeQuery();
                if(!rs.next()){
                    unique = true;
                }
                rs.close();
            }
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error running SQL [" + sql + "]");
        }
        return token;
    }

    private static final String CREDIT_KEY = "cl1p-url-credit";
    public static void setCredit(HttpServletRequest request, UrlCredit credit){
        request.getSession().setAttribute(CREDIT_KEY, credit);
        

    }

    public static UrlCredit getCredit(HttpServletRequest request){
        UrlCredit credit = (UrlCredit)request.getSession().getAttribute(CREDIT_KEY);
        return credit;
    }
}
