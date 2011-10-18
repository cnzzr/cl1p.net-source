package com.diodesoftware.scb.util;

import junit.framework.TestCase;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Apr 3, 2008
 * Time: 6:47:57 PM
 */
public class TrackerTest extends TestCase {


    public void testTracker() throws Exception
    {
        long time = 1000;
        Tracker t = new Tracker(time);
        assertEquals(1, t.count());
        for(int i = 0; i < 10 ;i ++){
            t.count();
        }
        assertEquals(12, t.count());
        Thread.sleep(time+1);
        assertEquals(1,t.count());
    }
}
