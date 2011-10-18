package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;
import com.diodesoftware.dbmapper.DatabaseEntry;

public class SystemSetting implements DatabaseEntry {

    private int number;
    private String settingKey;
    private String settingValue;
    private String description;
    private int settingType;

    private DatabaseColumn[] columns = new DatabaseColumn[]{
            new DatabaseColumn("SettingKey", DatabaseColumnType.CHAR_200),
            new DatabaseColumn("SettingValue", DatabaseColumnType.TEXT),
            new DatabaseColumn("Description", DatabaseColumnType.TEXT),
            new DatabaseColumn("SettingType", DatabaseColumnType.DECIMAL)
    };

    public static final int TYPE_BOOLEAN = 1;
    public static final int TYPE_STRING = 2;
    public static final int TYPE_TEXTAREA = 3;

    public SystemSetting() {
    }

    public SystemSetting(String key, String defaultValue, String description, int type) {
        this.settingKey = key;
        this.settingValue = defaultValue;
        this.description = description;
        this.settingType = type;
    }

    public DatabaseColumn[] columns() {
        return columns;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;

    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSettingType() {
        return settingType;
    }

    public void setSettingType(int settingType) {
        this.settingType = settingType;
    }


    public static final String LOGIN_REQUIRED = "login.required";
    public static final String INDEX_DISPLAY_LAST = "index.display.last";
    public static final String ROOT_URL = "root.url";

    public static final String ICON_LARGE = "icon.large";
    public static final String ICON_SMALL = "icon.small";
    public static final String TITLE = "title";

    public static SystemSetting[] DEFAULTS = new SystemSetting[]{
            new SystemSetting("login.required", "false",
                    "In order to use the clipboard, is a login required",
                    TYPE_BOOLEAN),
            new SystemSetting("index.display.last", "true",
                    "Display the latest cl1ps on the main page.",
                    TYPE_BOOLEAN),
            new SystemSetting(ROOT_URL, "http://localhost:8080",
                    "The root URL of the application. (http://cl1p is default)",
                    TYPE_STRING),

            new SystemSetting(ICON_LARGE, "http://localhost:8080/cl1p-inc-rgdm/images/cl1p-network.jpg",
                    "Large Icon - displaed on the index page.", TYPE_STRING),
            new SystemSetting(ICON_SMALL, "http://localhost:8080/cl1p-inc-rgdm/images/cl1p-mini.jpg",
                    "Small Icon - displayed at the top of most pages", TYPE_STRING),
            new SystemSetting(TITLE, "cl1p - The Network Clipboard", "Title text", TYPE_STRING)
    };

}
