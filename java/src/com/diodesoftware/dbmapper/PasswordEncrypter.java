/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: PasswordEncrypter.java,v 1.1.1.1 2003/10/13 19:19:31 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.dbmapper;

import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncrypter
{
    private static Logger log = Logger.getLogger(PasswordEncrypter.class);

    public static String encrypt(String in)
    {
        if(in == null)
            return null;
        if(in.trim().length() == 0)
            return in;
        // Don't enrypt enrcypted passwords.
        if(in.length() > 20)
        {
            return in;
        }
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        byte[] buf = in.getBytes();
        md.update(buf);
        buf = md.digest();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < buf.length; i++)
        {
            sb.append(getHex(buf[i]));
        }
        String out = sb.toString();
        if(log.isDebugEnabled())
        {
            log.debug("Password [" + in + "] encrypted to [" + out + "]");
        }
        return out;
    }

    private static String getHex(byte b)
    {
        int i = (int)b;
        return Integer.toString(i, 16);
    }

    public static void main(String[] args){
        //System.err.println("Password [" +"" + "] = [" + encrypt("")+"]");
    }
}
