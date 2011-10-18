package com.diodesoftware;

import com.diodesoftware.scb.agents.SystemSettingAgent;
import com.diodesoftware.scb.tables.SystemSetting;
import com.diodesoftware.scb.filter.ClipFilter;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jul 15, 2007
 * Time: 2:05:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class R {

    public static String R = null;
    public static String TITLE = null;
    public static String ICON_LARGE = null;
    public static String ICON_SMALL = null;
    public static boolean LOGIN_REQUIRED = false;



    public static String build(String uri){
        if(R == null){
            R =  SystemSettingAgent.getSetting(SystemSetting.ROOT_URL);
        }
        if(!uri.startsWith("/")){
            uri = "/" + uri;
        }
        return  R + uri;
    }

    public static void refresh(){
        R =  SystemSettingAgent.getSetting(SystemSetting.ROOT_URL);
        TITLE = SystemSettingAgent.getSetting(SystemSetting.TITLE);
        ICON_LARGE = SystemSettingAgent.getSetting(SystemSetting.ICON_LARGE);
        ICON_SMALL = SystemSettingAgent.getSetting(SystemSetting.ICON_SMALL);
        LOGIN_REQUIRED = Boolean.parseBoolean(SystemSettingAgent.getSetting(SystemSetting.LOGIN_REQUIRED));
    }

    public static String T(){
        return "?T=" + ClipFilter.start;
    }
}
