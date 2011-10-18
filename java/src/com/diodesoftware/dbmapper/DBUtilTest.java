/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: DBUtilTest.java,v 1.1.1.1 2003/10/13 19:19:31 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.dbmapper;


import junit.framework.*;

import java.util.Calendar;



public class DBUtilTest
    extends TestCase
{

    public DBUtilTest(String name)
    {
         super(name);
    }

    public void setUp()
    {


    }



    public void assertEquals(String message, DummyDatabaseEntry result, DummyDatabaseEntry expected)
    {
        assertEquals(message + "-Number", result.getNumber(), expected.getNumber());
        assertEquals(message + "-Message", result.getMessage(), expected.getMessage());
        assertEquals(message + "-Date", result.getDate(), expected.getDate());
    }

    public void assertEquals(String message, Calendar result, Calendar expected )
    {
        assertEquals(message + "-year", result.get(Calendar.YEAR), expected.get(Calendar.YEAR));
        assertEquals(message + "-month", result.get(Calendar.MONTH), expected.get(Calendar.MONTH));
        assertEquals(message + "-day", result.get(Calendar.DAY_OF_MONTH), expected.get(Calendar.DAY_OF_MONTH));
        assertEquals(message + "-hour", result.get(Calendar.HOUR), expected.get(Calendar.HOUR));
        assertEquals(message + "-minute", result.get(Calendar.MINUTE), expected.get(Calendar.MINUTE));
    }

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite()
    {
        return new TestSuite(DBUtilTest.class);
    }

}
