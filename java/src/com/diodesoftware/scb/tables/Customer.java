package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Oct 14, 2007
 * Time: 11:13:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class Customer implements DatabaseEntry {


    private int number;
    private String name;
    private String companyName;
    private String email;
    private Calendar purchaseDate;
    private String product;
    private String transaction;
    private String supportKey;



    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("Name", DatabaseColumnType.TEXT),
            new DatabaseColumn("CompanyName", DatabaseColumnType.TEXT),
            new DatabaseColumn("Email", DatabaseColumnType.TEXT),
            new DatabaseColumn("PurchaseDate", DatabaseColumnType.DATEEPOCH),
            new DatabaseColumn("Product", DatabaseColumnType.TEXT),
            new DatabaseColumn("Transaction", DatabaseColumnType.TEXT),
            new DatabaseColumn("SupportKey", DatabaseColumnType.CHAR_100)
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

    public Calendar getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Calendar purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }


    public String getSupportKey() {
        return supportKey;
    }

    public void setSupportKey(String supportKey) {
        this.supportKey = supportKey;
    }
}
