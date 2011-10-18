/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: RenderUtil.java,v 1.1.1.1 2003/10/13 19:19:32 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.util;

import java.util.Calendar;

public class RenderUtil
{
    public static void renderDate(Calendar calendar, boolean withTime, StringBuffer sb)
    {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        sb.append(month).append("/").append(day).append("/");
        sb.append(year);
        if (withTime)
        {
            sb.append(" ").append(zeroFill(hour, 2)).append(":");
            sb.append(zeroFill(minute, 2));
        }
    }

    public static void renderTime(Calendar calendar, StringBuffer sb)
    {
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);


        sb.append(zeroFill(hour, 2)).append(":");
        sb.append(zeroFill(minute, 2));
        if(calendar.get(Calendar.AM_PM) == Calendar.AM)
        {
            sb.append("a");
        }
        else
        {
            sb.append("p");
        }

    }

    public static String renderTime(Calendar calendar)
    {
        StringBuffer sb = new StringBuffer();
        renderTime(calendar, sb);
        return sb.toString();
    }


    public static String zeroFill(int number, int digits)
    {
        String s = "" + number;
        int size = s.length();
        size = digits - size;
        StringBuffer sb = new StringBuffer();
        if (size > 0)
        {
            for (int i = 0; i < size; i++)
            {
                sb.append("0");
            }
        }
        sb.append(s);
        return sb.toString();
    }
}


