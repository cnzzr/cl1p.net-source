package com.diodesoftware.scb.agents;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.ClipRequest;
import com.diodesoftware.scb.tables.SystemSetting;
import java.sql.ResultSet;
import java.util.*;

public class SystemSettingAgent {

	private static Logger log = Logger.getLogger(SystemSettingAgent.class);
    private static Map<String, String> settings = new HashMap<String, String>();

    public static List<SystemSetting> loadAll(Connection con){
		List<SystemSetting> list = new ArrayList<SystemSetting>();
		String sql = "Select * from SystemSetting";
		try{
			PreparedStatement prepStmt = con.prepareStatement(sql);

			ResultSet rs = prepStmt.executeQuery();
			while(rs.next()){
				list.add((SystemSetting)DBMapper.loadSingle(SystemSetting.class, rs));
			}
			rs.close();
			prepStmt.close();
		}catch (SQLException e) {
			log.error("Error running SQL [" + sql + "]",e);
		}
		
		return list;
	}

    public static void loadMap(Connection con){
        List<SystemSetting> list = loadAll(con);
        synchronized(settings){

            Iterator<SystemSetting> iter = list.iterator();
            while(iter.hasNext()){
                 SystemSetting setting = iter.next();
                settings.put(setting.getSettingKey(), setting.getSettingValue());
            }
        }
    }

    public static SystemSetting load(String key, Connection con){
		SystemSetting result = null;
		String sql = "Select * from SystemSetting where SettingKey = ?";
		try{
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, key);
			ResultSet rs = prepStmt.executeQuery();
			while(rs.next()){
				result = (SystemSetting)DBMapper.loadSingle(SystemSetting.class, rs);
			}
			rs.close();
			prepStmt.close();
		}catch (SQLException e) {
			log.error("Error running SQL [" + sql + "]",e);
		}
		return result;
	}
	
	public static void checkDefaults(Connection con){
		for(SystemSetting setting : SystemSetting.DEFAULTS){
			SystemSetting current = load(setting.getSettingKey(),con);
			if(current == null){
				DBMapper.save(setting,con);
			}
		}
	}

    public static String getSetting(String key){
        String result = null;
        synchronized(settings){
            result = settings.get(key);
        }
        return result;
    }

    public static String getSetting(String key, ClipRequest request){
		String result = null;
		SystemSetting setting = load(key, request.getCon());
		if(setting != null)
			result = setting.getSettingValue();			
		return result;
	}
}
