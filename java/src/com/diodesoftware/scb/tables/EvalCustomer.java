package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;

import java.util.Calendar;

public class EvalCustomer implements DatabaseEntry {

    private int number;
    private String name;
    private String companyName;
    private String email;
    private Calendar evalDate;
    private Calendar expiryDate;
    private String product;


    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("Name", DatabaseColumnType.TEXT),
            new DatabaseColumn("CompanyName", DatabaseColumnType.TEXT),
            new DatabaseColumn("Email", DatabaseColumnType.TEXT),
            new DatabaseColumn("EvalDate", DatabaseColumnType.DATEEPOCH),
            new DatabaseColumn("ExpiryDate", DatabaseColumnType.DATEEPOCH),
            new DatabaseColumn("Product", DatabaseColumnType.CHAR_200)
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Calendar getEvalDate() {
        return evalDate;
    }

    public void setEvalDate(Calendar evalDate) {
        this.evalDate = evalDate;
    }

    public Calendar getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Calendar expiryDate) {
        this.expiryDate = expiryDate;
    }


    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}
