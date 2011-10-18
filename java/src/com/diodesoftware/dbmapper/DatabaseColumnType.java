/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: DatabaseColumnType.java,v 1.2 2003/10/13 19:34:39 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.dbmapper;

import java.util.TimeZone;



import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.TimeZone;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseColumnType
{
    private static final String VARCHAR_TYPE = "VARCHAR";
    private static final String DECIMAL_TYPE = "DECIMAL";
    private static final String BLOB_TYPE = "BLOB";
    private static final String TEXT_TYPE = "TEXT";
    private static final String TINY_TEXT_TYPE = "TINYTEXT";
    private static final String LONG_TEXT_TYPE ="LONGTEXT";
    private static final String DATE12_TYPE = "DATE12";
    private static final String DATEEPOCH_TYPE = "DATEEPOCH";
    private static final String PASSWORD_TYPE = "PASSWORD";
    private static final String JAVA_SQL_INTEGER = "INTEGER";
    private static final String JAVA_SQL_LONG = "LONG";
    private static final String JAVA_SQL_STRING = "STRING";
    private static final String TIMEZONE_TYPE = "TIMEZONE";

    private static final String BOOLEAN_TYPE = "BOOLEAN";

    private String type = null;
    private boolean quotesRequired = false;
    private String sqlType = null;
    private int size = -1;
    private String javaSqlType = null;
    private Class resultClass = null;

    private DatabaseColumnType(String type,
                               boolean quotesRequired,
                               String sqlType,
                               String javaSqlType,
                               Class resultClass)
    {
        this(type,
            quotesRequired,
            sqlType,
            javaSqlType,
            resultClass,
            -1);
    }

    private DatabaseColumnType(String type,
                               boolean quotesRequired,
                               String sqlType,
                               String javaSqlType,
                               Class resultClass,
                               int size)
    {
        this.type = type;
        this.quotesRequired = quotesRequired;
        this.sqlType = sqlType;
        this.javaSqlType = javaSqlType;
        this.resultClass = resultClass;
        this.size = size;
    }

    public String getType()
    {
        return type;
    }

    public String getSqlType()
    {
        return sqlType;
    }

    public boolean isQuotesRequired()
    {
        return false;
    }

    public int getSize()
    {
        return size;
    }

    public boolean hasSetSize()
    {
        return size != -1;
    }

    public Object getSqlValue(Object o)
    {

        Object result = null;
        if (type == DATE12_TYPE)
        {
            result = SQLUtil.calendarToLong((Calendar) o);
        }else if(type == DATEEPOCH_TYPE){
        	if(o==null){
        		result = "0";
        	}else{
        		result = new Long(((Calendar)o).getTimeInMillis());
        	}
        }else if(type == TIMEZONE_TYPE){
        	TimeZone tz = (TimeZone)o;
        	result = tz.getID();
        }
        else if(type == BOOLEAN_TYPE)
        {
            result = SQLUtil.booleanToString((Boolean)o);
        }
        else if(type == PASSWORD_TYPE)
        {
            if(o != null)
                result = PasswordEncrypter.encrypt((String)o);
            else
                result = "";
        }
        else
        {
            if(o != null)
            {
                result = o.toString();
            }
            else
            {
                result = "null";
            }
        }
       
        return result;
    }

    public Object getResultSetValue(ResultSet rs, String name)
        throws SQLException
    {

        Object result = null;
        if (javaSqlType == JAVA_SQL_STRING)
        {
            result = rs.getString(name);
             if(type == BOOLEAN_TYPE)
            {
                result = new Boolean(SQLUtil.getBooleanValue((String)result));
            }
             if(type == TIMEZONE_TYPE){
            	 TimeZone tz = TimeZone.getTimeZone(result.toString());
            	 result = tz;
             }
        }
        else if (javaSqlType == JAVA_SQL_INTEGER)
        {
            result = new Integer(rs.getInt(name));
        }else if(javaSqlType == JAVA_SQL_LONG)
        {
            if (type == DATE12_TYPE)
            {
                result = SQLUtil.longToCalendar(rs.getLong(name));
            }
            if(type == DATEEPOCH_TYPE){
            	Calendar cal = Calendar.getInstance();
            	cal.setTimeInMillis(rs.getLong(name));
            	result = cal;
            }
        }
        return result;
    }

    public Class getResultClass()
    {
        return resultClass;
    }

    public static final DatabaseColumnType DECIMAL =
        new DatabaseColumnType(DECIMAL_TYPE,
            false,
            "DECIMAL",
            JAVA_SQL_INTEGER,
            Integer.TYPE,
            10);
    public static final DatabaseColumnType VARCHAR =
        new DatabaseColumnType(VARCHAR_TYPE,
            true,
            "VARCHAR",
            JAVA_SQL_STRING,
            String.class);
    public static final DatabaseColumnType BLOB =
        new DatabaseColumnType(BLOB_TYPE,
            true,
            "TEXT",
            JAVA_SQL_STRING,
            String.class);
    public static final DatabaseColumnType LONG_TEXT =
        new DatabaseColumnType(LONG_TEXT_TYPE,
            true,
            "LONGTEXT",
            JAVA_SQL_STRING,
            String.class);

    public static final DatabaseColumnType TEXT =
            new DatabaseColumnType(TEXT_TYPE,
                true,
                "TEXT",
                JAVA_SQL_STRING,
                String.class);

    public static final DatabaseColumnType TINY_TEXT =
            new DatabaseColumnType(TINY_TEXT_TYPE,
                true,
                "TINYTEXT",
                JAVA_SQL_STRING,
                String.class);


    public static final DatabaseColumnType DATE12 =
        new DatabaseColumnType(DATE12_TYPE,
            false,
            "DECIMAL",
            JAVA_SQL_LONG,
            Calendar.class,
            12);

    public static final DatabaseColumnType DATEEPOCH =
    	new DatabaseColumnType(DATEEPOCH_TYPE,
    			false,
    			"DECIMAL",
    			JAVA_SQL_LONG,
    			Calendar.class,
    			14);

    public static final DatabaseColumnType BOOLEAN =
        new DatabaseColumnType(BOOLEAN_TYPE,
            true,
            "VARCHAR",
            JAVA_SQL_STRING,
            Boolean.TYPE,
            1);
    public static final DatabaseColumnType CHAR_20 =
        new DatabaseColumnType(VARCHAR_TYPE,
            true,
            "VARCHAR",
            JAVA_SQL_STRING,
            String.class,
            20);

    public static final DatabaseColumnType CHAR_100 =
        new DatabaseColumnType(VARCHAR_TYPE,
            true,
            "VARCHAR",
            JAVA_SQL_STRING,
            String.class,
            100);

    public static final DatabaseColumnType CHAR_200 =
        new DatabaseColumnType(VARCHAR_TYPE,
            true,
            "VARCHAR",
            JAVA_SQL_STRING,
            String.class,
            200);

    public static final DatabaseColumnType CHAR_40 =
        new DatabaseColumnType(VARCHAR_TYPE,
            true,
            "VARCHAR",
            JAVA_SQL_STRING,
            String.class,
            40);
     public static final DatabaseColumnType CHAR_3 =
        new DatabaseColumnType(VARCHAR_TYPE,
            true,
            "VARCHAR",
            JAVA_SQL_STRING,
            String.class,
            3);

     public static final DatabaseColumnType PASSWORD =
        new DatabaseColumnType(PASSWORD_TYPE,
            true,
            "VARCHAR",
            JAVA_SQL_STRING,
            String.class,
            200);
     public static final DatabaseColumnType TIMEZONE =
    	 new DatabaseColumnType(TIMEZONE_TYPE,
    			 true,
    			 "VARCHAR",
    			 JAVA_SQL_STRING,
    			 TimeZone.class,
    			 200);


}
