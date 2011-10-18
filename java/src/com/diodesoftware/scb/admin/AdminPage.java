package com.diodesoftware.scb.admin;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Jul 3, 2006
 * Time: 5:57:31 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AdminPage {

    protected String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    abstract public String doPage(HttpServletRequest request,
                                HttpServletResponse response,
                                ServletContext context,
                                Connection con);


}
