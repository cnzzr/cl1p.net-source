/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jul 14, 2007
 * Time: 11:07:13 PM
 * To change this template use File | Settings | File Templates.
 */
package com.diodesoftware.scb.agents;

import java.util.Date;

public class SystemStatus {
    private static SystemStatus ourInstance = new SystemStatus();

    public static SystemStatus getInstance() {
        return ourInstance;
    }

    private SystemStatus() {
    }

    private boolean ERROR = false;
    private int errorCount;
    private Date lastError;


    public boolean isERROR() {
        return ERROR;
    }

    public void setERROR(boolean ERROR) {
        this.ERROR = ERROR;
        if(ERROR){
            errorCount++;
            lastError = new Date();
        }
    }


    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public Date getLastError() {
        return lastError;
    }

    public void setLastError(Date lastError) {
        this.lastError = lastError;
    }
}
