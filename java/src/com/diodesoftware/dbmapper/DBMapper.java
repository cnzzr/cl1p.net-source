/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: DBMapper.java,v 1.1.1.1 2003/10/13 19:19:31 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.dbmapper;


import org.apache.ecs.xhtml.col;
import org.apache.log4j.Logger;

import javax.servlet.jsp.PageContext;
import javax.servlet.ServletContext;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.diodesoftware.scb.ClipUtil;
import com.diodesoftware.scb.clipboard.ClipSqlException;

public class DBMapper {
    private static Logger log = Logger.getLogger(DBMapper.class);
    
    private static final int CREATE_TABLE = 1;
    private static final int INSERT_ENTRY = 2;
    private static final int UPDATE_ENTRY = 3;
    public static final String KEY = "DBMapper";
    private static DBMapper instance = null;


    public DBMapper(DBConnectionMgr db) {
        
        instance = this;
    }
    
    public DBMapper(){}

    public static DBMapper getInstance() {
        return instance;
    }

    public static void save(DatabaseEntry entry, Connection con) throws ClipSqlException{

        switch (checkAction(entry, con)) {
            case CREATE_TABLE:
                if (log.isDebugEnabled()) {
                    log.debug("Creating table and inserting for [" + getTableName(entry.getClass()) + "]");
                }
                createTable(entry, con);
                insertEntry(entry, con);
                break;
            case INSERT_ENTRY:
                if (log.isDebugEnabled()) {
                    log.debug("Inserting for [" + getTableName(entry.getClass()) + "]");
                }
                insertEntry(entry, con);
                break;
            case UPDATE_ENTRY:
                if (log.isDebugEnabled()) {
                    log.debug("Updating for [" + getTableName(entry.getClass()) + "]");
                }
                updateEntry(entry, con);
                break;
        }
    }

    public static DatabaseEntry load(Class entryClass,
                              int number, Connection con) {
        String tableName = getTableName(entryClass);
        StringBuffer sb = new StringBuffer("SELECT * FROM ");
        sb.append(tableName).append(" WHERE Number = ").append(number);

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sb.toString());
            if (rs.next()) {
                DatabaseEntry entry = loadSingle(entryClass, rs);
                rs.close();
                stmt.close();
                return entry;
            }
        } catch (SQLException e) {
            log.error("Error loading", e);
        } finally {
            ClipUtil.close(rs);
            ClipUtil.close(stmt);
        }


        return null;
    }

    public static DatabaseEntry[] load(Class entryClass,
                                ResultSet rs)
            throws SQLException {
        ArrayList list = new ArrayList();
        while (rs.next()) {
            list.add(loadSingle(entryClass, rs));
        }
        DatabaseEntry[] result = new DatabaseEntry[list.size()];
        list.toArray(result);
        return result;
    }

    public static DatabaseEntry loadSingle(Class entryClass,
                                    ResultSet rs)
            throws SQLException {

        DatabaseEntry result = null;
        try {
            int number = rs.getInt("Number");
            result = (DatabaseEntry) entryClass.newInstance();
            result.setNumber(number);
            DatabaseColumn[] columns = result.columns();
            for (int i = 0; i < columns.length; i++) {
                populateEntry(result, columns[i], rs);
            }
        }
        catch (IllegalAccessException e) {
            log.error("Don't have access to create class [" + entryClass.getName() + "]", e);
        }
        catch (InstantiationException e) {
            log.error("Error creating instance of class [" + entryClass.getName() + "]", e);
        }
        catch (SQLException e) {
            if (e.getErrorCode() != 0) {
                throw e;
            }
        }

        return result;
    }

    private static void populateEntry(DatabaseEntry entry,
                               DatabaseColumn column,
                               ResultSet rs)
            throws SQLException {
        DatabaseColumnType type = column.getType();
        String name = forceFirstCharToUpper(column.getName());
        Object value = type.getResultSetValue(rs, name);

        String methodName = "set" + name;
        try {
            Class[] parms = new Class[]{type.getResultClass()};
            Method method = entry.getClass().getMethod(methodName, parms);
            Object[] args = new Object[]{value};
            method.invoke(entry, args);
        }
        catch (NoSuchMethodException e) {
            String errMsg = "Can't find the method [" + methodName
                    + "] with parm [" + type.getResultClass().getName()
                    + "] in class [" + entry.getClass().getName() + "]";
            log.error(errMsg);
            Method[] methods = entry.getClass().getMethods();
            log.error("Dumping class [" + entry.getClass().getName() + "] methods.");
            for (int i = 0; i < methods.length; i++) {
                Class[] parms = methods[i].getParameterTypes();
                for (int j = 0; j < parms.length; j++) {
                    log.error("Method [" + methods[i].getName() + "] parm[" + parms[j].getName() + "]");
                }
            }
            throw new RuntimeException(errMsg, e);
        }
        catch (IllegalAccessException e) {
            String errMsg = "Can't invoke method [" + methodName + "] in class [" + entry.getClass().getName() + "]";
            log.error(errMsg);
            throw new RuntimeException(errMsg, e);
        }
        catch (InvocationTargetException e) {
            String errMsg = "Error invoking method [" + methodName + "] in class [" + entry.getClass().getName() + "]";
            log.error(errMsg);
            throw new RuntimeException(errMsg, e);
        }
    }


    private static int checkAction(DatabaseEntry entry, Connection con) {
        int result = -1;


        DatabaseEntry existingRecord = load(entry.getClass(), entry.getNumber(), con);
        if (existingRecord == null) {
            result = INSERT_ENTRY;
            if (log.isDebugEnabled()) {
                log.debug("check: Insert");
            }
        } else {
            result = UPDATE_ENTRY;
            if (log.isDebugEnabled()) {
                log.debug("check: Update");
            }
        }


        return result;
    }


    private static void insertEntry(DatabaseEntry entry, Connection con) throws ClipSqlException{
        if (log.isDebugEnabled()) {
            log.debug("Inserting row in [" + entry.getClass().getName() + "]");
        }
        StringBuffer sb = new StringBuffer("INSERT INTO ");
        String tableName = getTableName(entry);
        sb.append(tableName);
        DatabaseColumn[] columns = entry.columns();
        List values = new ArrayList();
        sb.append(" (");
        for (int i = 0; i < columns.length; i++) {
            sb.append(columns[i].getName());
            int next = i + 1;
            if (next < columns.length) {
                sb.append(", ");
            }
        }
        sb.append(") VALUES ( ");
        int i = 0;
        while (i < columns.length) {
        	sb.append("?");
            addColumnInsertValue(entry, columns[i], values);
            i++;
            if (i < columns.length)
                sb.append(", ");
        }
        sb.append(")");

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(sb.toString());
            i = 1;
            for(Object o : values)
            {
            	stmt.setObject(i, o);
            	i++;
            }
            stmt.executeUpdate();
            rs = stmt.executeQuery("select LAST_INSERT_ID()");
            rs.next();
            entry.setNumber(rs.getInt(1));
        } catch (SQLException e) {
            log.error("Error running INSERT SQL [" + sb + "]", e);
            throw new ClipSqlException(e, sb.toString());
        } finally {
            ClipUtil.close(rs);
            ClipUtil.close(stmt);
        }


        if (log.isDebugEnabled()) {
            log.debug("Row inserted in [" + entry.getClass().getName() + "]");
        }
    }

    private static void addColumnInsertValue(DatabaseEntry entry,
                                      DatabaseColumn column,
                                      List values) {
    	Object o =getColumnStringValue(entry, column);
        values.add(column.getType().getSqlValue(o));
    }


    private static Object getColumnStringValue(DatabaseEntry entry,
                                        DatabaseColumn column) {

        String name = column.getName();
        String methodName = "get" + forceFirstCharToUpper(name);
        Class entryClass = entry.getClass();
        Method method = null;
        Object value = null;
        try {
            method = entryClass.getMethod(methodName, (Class[])null);
            value = method.invoke(entry, (Object[])null);
            return value;
        }
        catch (NoSuchMethodException e) {
            String errMsg = "Can't find the method [" +
                    methodName + "] in class [" +
                    entryClass.getName() + "]";
            log.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
        catch (IllegalAccessException e) {
            String errMsg = "Can't access method [" +
                    methodName + "] in class [" +
                    entryClass.getName() + "]";
            log.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
        catch (InvocationTargetException e) {
            String errMsg = "Error executing method [" +
                    methodName + "] in class [" +
                    entryClass.getName() + "]";
            log.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
    }

    private static String forceFirstCharToUpper(String s) {
        String result = null;
        result = Character.toUpperCase(s.charAt(0)) + s.substring(1);
        return result;
    }

    public static void createTable(DatabaseEntry entry, Connection con){
    	createTable(entry, con, new StringBuffer());
    }
    
    public static void createTable(DatabaseEntry entry, Connection con, StringBuffer user) {
        
        	String msg = "Creating table for [" + entry.getClass().getName() + "]"; 
            log.debug(msg);
            user.append(msg).append("<br/>");
        

        String tableName = getTableName(entry);
        DatabaseColumn[] columns = entry.columns();
        StringBuffer sb = new StringBuffer("Create Table ");
        sb.append(tableName).append(" ( Number int(9) NOT NULL auto_increment PRIMARY KEY, ");
        int i = 0;
        while (i < columns.length) {
            sb.append(columns[i].getName()).append(" ");
            DatabaseColumnType type = columns[i].getType();
            sb.append(type.getSqlType());
            int size = columns[i].getSize();
            if (type.hasSetSize()) {
                size = type.getSize();
            }

            if (size != -1) {
                sb.append("( ").append(size).append(" )");
            }
            i++;
            if (i < columns.length) {
                sb.append(", ");
            }
        }
        sb.append(" );");

        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sb.toString());
            stmt.close();
            String info = "Table created for [" + entry.getClass().getName() + "]"; 
            log.debug(info);
            user.append(info).append("<br/>");
                
        } catch (SQLException e) {
            if (e.getErrorCode() == 1050) {
            	String info = "Table [" + tableName + "] already exists. No need to create."; 
                log.info(info);
                user.append(info).append("<br/>");
            } else {
            	String error = "Error creating Table [" + tableName + "] SQL [" + sb.toString() + "] ERROR_CODE [" + e.getErrorCode() + "]";
            	user.append(error).append("Message [" + e.getMessage() + "]<br/>");
                log.error(error, e);                
            }
        }
        
    }

    private static String getTableName(DatabaseEntry entry) {
        return getTableName(entry.getClass());
    }

    private static String getTableName(Class entryClass) {
        String result = null;
        StringTokenizer st = new StringTokenizer(entryClass.getName(), ".");
        while (st.hasMoreTokens()) {
            result = st.nextToken();
        }
        return result;
    }


    private static void updateEntry(DatabaseEntry entry, Connection con) {
        if (log.isDebugEnabled()) {
            log.debug("Updating TableRowSelection in [" + entry.getClass().getName() + "]");
        }
        String tableName = getTableName(entry);
        StringBuffer sb = new StringBuffer("UPDATE ");
        sb.append(tableName).append(" SET ");
        DatabaseColumn[] columns = entry.columns();
        List values = new ArrayList();
        int i = 0;
        while (i < columns.length) {
            addColumnUpdateValue(entry, columns[i], sb, values);
            i++;
            if (i < columns.length) {
                sb.append(", ");
            }
        }
        sb.append(" WHERE number = ").append(entry.getNumber());


        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(sb.toString());
            i = 1;
            for(Object o : values)
            {
            	stmt.setObject(i, o);
            	i++;
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating SQL [" + sb.toString() + "]", e);
            throw new ClipSqlException(e, sb.toString());        
            } finally {
            ClipUtil.close(stmt);
        }


        if (log.isDebugEnabled()) {
            log.debug("TableRowSelection Updated in [" + entry.getClass().getName() + "]");
        }
    }

    private static void addColumnUpdateValue(DatabaseEntry entry,
                                      DatabaseColumn column,
                                      StringBuffer sb,
                                      List values) {
        Object o = getColumnStringValue(entry, column);
        Object value = column.getType().getSqlValue(o);
        String columnName = column.getName();
        columnName = forceFirstCharToUpper(columnName);
        sb.append(columnName).append(" = ");
        sb.append("?");
        values.add(value);

    }

    


    public static DBMapper getInstance(PageContext pageContext) {
        return getInstance(pageContext.getServletContext());
    }

    public static DBMapper getInstance(ServletContext servletContext) {
        DBMapper mapper =
                (DBMapper) servletContext.getAttribute(KEY);
        return mapper;
    }
}