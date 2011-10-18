/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: SQLUtil.java,v 1.1.1.1 2003/10/13 19:19:31 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.dbmapper;



import com.diodesoftware.util.RenderUtil;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class SQLUtil
{
    private static final long YEAR_M =  100000000L;
    private static final long MONTH_M =   1000000L;
    private static final long DAY_M =       10000L;
    private static final long HOUR_M =        100L;
    private static final long MINUTE_M =        1L;
    public static String prepForQuery(String s, int max) throws SQLException
    {
        if(s.length() >= max)
            throw new SQLException("String is longer then maximum");

        return s;
    }

 
    public static String calendarToString(Calendar calendar)
    {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        StringBuffer sb = new StringBuffer();
        sb.append(year);
        sb.append(RenderUtil.zeroFill(month, 2));
        sb.append(RenderUtil.zeroFill(day, 2));
        sb.append(RenderUtil.zeroFill(hour, 2));
        sb.append(RenderUtil.zeroFill(minute, 2));
        return sb.toString();
    }

    public static Calendar stringToCalendar(String s)
    {
        int year = Integer.parseInt(s.substring(0, 4));
        int month = Integer.parseInt(s.substring(4,6));
        int day = Integer.parseInt(s.substring(6,8));
        int hour = Integer.parseInt(s.substring(8,10));
        int minute = Integer.parseInt(s.substring(10,12));
        Calendar result = new GregorianCalendar(year,
            month,
            day,
            hour,
            minute);
        return result;
    }

    public static long calendarToLong(Calendar c)
    {
        long result = c.get(Calendar.YEAR) * YEAR_M;
        result = result + (c.get(Calendar.MONTH) * MONTH_M);
        result = result + (c.get(Calendar.DAY_OF_MONTH) * DAY_M);
        result = result + (c.get(Calendar.HOUR_OF_DAY) * HOUR_M);
        result = result + (c.get(Calendar.MINUTE) * MINUTE_M);
        return result;
    }

    public static Calendar longToCalendar(long l)
    {
        int year = (int)(l / YEAR_M);
        l = l - (year * YEAR_M);
        int month = (int)(l / MONTH_M);
        l = l - (month * MONTH_M);
        int day = (int)(l / DAY_M);
        l = l - (day * DAY_M);
        int hour = (int)(l / HOUR_M);
        l = l - (hour * HOUR_M);
        int minute = (int)(l / MINUTE_M);
        Calendar calendar = new GregorianCalendar(year, month, day, hour, minute);
        return calendar;
    }

    public static String getCharValue(boolean b)
    {
        String result ="N";
        if(b)
        {
            result = "Y";
        }
        return result;
    }

    public static boolean getBooleanValue(String s)
    {
        boolean result = false;
        if("Y".equals(s))
        {
            result = true;
        }
        return result;
    }

    public static String booleanToString(Boolean b)
    {
        String result = "N";
        if(b.booleanValue())
        {
            result = "Y";
        }
        return result;
    }

    private static String escapeBackSlashes(String s, int offset)
    {
        int i = s.indexOf("\\", offset);
        if(i == -1)
            return s;
        String start = s.substring(0, i);
        String end = s.substring(i);
        StringBuffer sb = new StringBuffer(start);
        sb.append("\\");
        sb.append(end);
        i++;
        i++;
        return escapeBackSlashes(sb.toString(), i);
    }

    private static String makeQuoteSafe(String s, int offset)
    {
        int i = s.indexOf("'", offset);
        if(i == -1)
            return s;
        String start = s.substring(0, i);
        String end = s.substring(i);
        StringBuffer sb = new StringBuffer(start);
        sb.append("'");
        sb.append(end);
        i++;
        i++;
        return makeQuoteSafe(sb.toString(), i);
    }


}
