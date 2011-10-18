package com.diodesoftware.scb;

import junit.framework.TestCase;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Mar 16, 2008
 * Time: 11:22:08 AM
 */
public class PictureInfoTest extends TestCase {


    public void testResze() throws Exception
    {
        int width = 2000;
        int height = 2000;
        PictureInfo pi = new PictureInfo(0,null, width, height, null);
        assertEquals(200, pi.getSmallWidth());
        assertEquals(200, pi.getSmallHeight());
    }
}
