package com.diodesoftware.scb.license;

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class License {

    private String product;
    private String version;

    private String company;
    private Calendar expiry;


    public License(String product, String version, String company, Calendar expiry) {
        this.product = product;
        this.version = version;


        this.company = company;
        this.expiry = expiry;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Calendar getExpiry() {
        return expiry;
    }

    public void setExpiry(Calendar expiry) {
        this.expiry = expiry;
    }

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public String toString() {
        String s = "Product:" + product + "\n" +
                "Version:" + version + "\n" +
                "Company:" + company + "\n";
        String exp = null;
        if (expiry == null) {
            exp = "Never";
        } else {
            exp = dateFormat.format(expiry.getTime());
        }
        s += "Expiry:" + exp + "\n";
        return s;
    }
}
