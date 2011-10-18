package com.diodesoftware.scb;

import java.sql.*;

import com.diodesoftware.dbmapper.*;
import org.apache.log4j.Logger;

public class ClipCounter implements Runnable {
    private Logger log = Logger.getLogger(ClipCounter.class);
    private int count = 0;
    private DBConnectionMgr dbMgr;
    private boolean stopRequested;
    private Object waiter = new Object();

    public ClipCounter(DBConnectionMgr dbMgr) {
       /* this.dbMgr = dbMgr;
        Thread t = new Thread(this);
        t.setName("Cl1p Counter");
        t.start();  */
    }

    public int getCount() {
        return count;
    }

    public void stop() {
        stopRequested = true;
        synchronized (waiter) {
            waiter.notifyAll();
        }
    }

    public void run() {
        try {
            while (!stopRequested) {
                updateCount();
                synchronized (waiter) {
                    waiter.wait(60 * 60 * 1000);
                }
            }
        } catch (InterruptedException e) {
            // Thread interupted. shutdown
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ClipCounter Thread Failure", e);
        }
    }

    private void updateCount()
            throws SQLException {
        Connection con = dbMgr.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("Select max(Number) from Clip");
        if (rs.next()) {
            count = rs.getInt(1);
        }
        rs.close();
        stmt.close();
        con.close();
    }

}
