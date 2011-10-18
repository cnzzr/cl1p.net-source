/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: SQLUtilTest.java,v 1.1.1.1 2003/10/13 19:19:31 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.dbmapper;

import junit.framework.*;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class SQLUtilTest
    extends TestCase
{
    public SQLUtilTest(String name)
    {
        super(name);
    }

    
    public void testCalendarToLong()
    {
        Calendar expected = new GregorianCalendar();
        long l = SQLUtil.calendarToLong(expected);
        Calendar result = SQLUtil.longToCalendar(l);
        assertCalendarEqual("Dates not equal", expected, result);
    }

    public void assertCalendarEqual(String message, Calendar expected, Calendar result )
    {
        assertEquals(message, expected.get(Calendar.YEAR), result.get(Calendar.YEAR) );
        assertEquals(message, expected.get(Calendar.MONTH), result.get(Calendar.MONTH) );
        assertEquals(message, expected.get(Calendar.DAY_OF_MONTH), result.get(Calendar.DAY_OF_MONTH) );
        assertEquals(message, expected.get(Calendar.HOUR_OF_DAY), result.get(Calendar.HOUR_OF_DAY) );
        assertEquals(message, expected.get(Calendar.MINUTE), result.get(Calendar.MINUTE) );
    }


}
