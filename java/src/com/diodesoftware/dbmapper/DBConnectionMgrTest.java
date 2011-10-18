/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: DBConnectionMgrTest.java,v 1.1.1.1 2003/10/13 19:19:31 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.dbmapper;

import junit.framework.TestCase;

import java.sql.Connection;

public class DBConnectionMgrTest
    extends TestCase
{
    public DBConnectionMgrTest(String s)
    {
        super(s);
    }

    public void testTestPool()
        throws Exception
    {
        int conCount = 10;
        DBConnectionMgr db = new DBConnectionMgr();
        Connection[] con = new Connection[conCount];
        for(int i =0; i < conCount; i++)
        {
            con[i] = db.getConnection();
        }
        for(int i =0; i < conCount; i++)
        {
            db.returnConnection(con[i]);
        }
        for(int i =0; i < conCount; i++)
        {
            con[i] = db.getConnection();
        }
        for(int i =0; i < conCount; i++)
        {
            db.returnConnection(con[i]);
        }
    }
}
