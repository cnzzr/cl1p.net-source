package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;

import java.util.Calendar;


public class User implements DatabaseEntry
{
    private int number;
    private String username;
    private String password;
    private String email;
    private String defaultPassword;
    private int defaultKeepFor;
    private boolean pro;
    private boolean noEmail;
    private boolean disabled;
    private Calendar created = Calendar.getInstance();
    private boolean selected; // Display only

    private DatabaseColumn[] columns = new DatabaseColumn[]{
        new DatabaseColumn("Username", DatabaseColumnType.CHAR_20),
        new DatabaseColumn("Password", DatabaseColumnType.PASSWORD),
        new DatabaseColumn("Email", DatabaseColumnType.CHAR_200),
        new DatabaseColumn("DefaultPassword", DatabaseColumnType.CHAR_100),
        new DatabaseColumn("DefaultKeepFor", DatabaseColumnType.DECIMAL),
        new DatabaseColumn("Pro", DatabaseColumnType.BOOLEAN),
        new DatabaseColumn("NoEmail", DatabaseColumnType.BOOLEAN),
        new DatabaseColumn("Disabled", DatabaseColumnType.BOOLEAN),
        new DatabaseColumn("Created", DatabaseColumnType.DATEEPOCH)
    };

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public DatabaseColumn[] columns() {
        return columns;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public int getDefaultKeepFor() {
        return defaultKeepFor;
    }

    public void setDefaultKeepFor(int defaultKeepFor) {
        this.defaultKeepFor = defaultKeepFor;
    }

    public DatabaseColumn[] getColumns() {
        return columns;
    }

    public void setColumns(DatabaseColumn[] columns) {
        this.columns = columns;
    }

    public boolean getPro(){
        return isPro();
    }

    public boolean isPro() {
        return pro;
    }

    public void setPro(boolean pro) {
        this.pro = pro;
    }



    public boolean isNoEmail(){
        return getNoEmail();
    }
    public boolean getNoEmail() {
        return noEmail;
    }

    public void setNoEmail(boolean noEmail) {
        this.noEmail = noEmail;
    }
    
    public boolean isDisabled(){
    	return disabled;    	
    }
    
    public boolean getDisabled(){
    	return disabled;
    }
    
    public void setDisabled(boolean b){
    	disabled = b;
    }

    public String toString(){
	return "Number [" + number + "] Username [" + username + "] email [" + email + "] Pro [" + pro + "] NoEmail [" + noEmail + "]";
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }
}
