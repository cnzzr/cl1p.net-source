/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: DummyDatabaseEntry.java,v 1.1.1.1 2003/10/13 19:19:31 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.dbmapper;

import java.util.Calendar;

public class DummyDatabaseEntry
    implements DatabaseEntry
{
    private int number = 0;
    private Calendar date = null;
    private String message = null;
    private DatabaseColumn[] columns;
    public DummyDatabaseEntry()
    {
        columns = new DatabaseColumn[]{
            new DatabaseColumn("Date", DatabaseColumnType.DATE12),
            new DatabaseColumn("Message", DatabaseColumnType.BLOB)
        };
    }

    public DatabaseColumn[] columns()
    {
        return columns;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public Calendar getDate()
    {
        return date;
    }

    public void setDate(Calendar date)
    {
        this.date = date;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }


}
