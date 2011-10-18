/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: DatabaseColumn.java,v 1.1.1.1 2003/10/13 19:19:31 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.dbmapper;

public class DatabaseColumn
{
    private String name;
    private DatabaseColumnType type;
    private int size;


    public DatabaseColumn(String name, DatabaseColumnType type)
    {
        this(name, type, -1);
    }

    public DatabaseColumn(String name, DatabaseColumnType type, int size)
    {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public String getName()
    {
        return name;
    }

    public DatabaseColumnType getType()
    {
        return type;
    }

    public int getSize()
    {
        return size;
    }

}
