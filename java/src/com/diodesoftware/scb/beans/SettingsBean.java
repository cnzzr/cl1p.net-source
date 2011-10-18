package com.diodesoftware.scb.beans;

import com.diodesoftware.scb.tables.SystemSetting;
import com.diodesoftware.scb.agents.SystemSettingAgent;
import com.diodesoftware.dbmapper.DBConnectionMgr;

import java.util.List;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jun 25, 2007
 * Time: 8:27:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsBean {

    private List<SystemSetting> settings;

    private long lastAccessed;

    private void loadSettings(){
        DBConnectionMgr dbCon = new DBConnectionMgr();
        Connection con = dbCon.getConnection();
        try{
            settings = SystemSettingAgent.loadAll(con);
            lastAccessed = System.currentTimeMillis();
        }finally{
            dbCon.returnConnection(con);
        }
    }

    public List<SystemSetting> getSettings() {
        if(System.currentTimeMillis() - lastAccessed > 10000)
            loadSettings();

        return settings;
    }

    public void setSettings(List<SystemSetting> settings) {
        this.settings = settings;
    }
}
