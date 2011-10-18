package com.diodesoftware.scb.util;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Apr 3, 2008
 * Time: 6:46:55 PM
 */
public class Tracker {
    private List<Long> event = new ArrayList<Long>();
    private long time;
    public Tracker(long time) {
        this.time = time;
    }

    public int count() {
        List<Long> n = new ArrayList<Long>();
        long now = System.currentTimeMillis();
        n.add(now + time);
        for (long l : event) {
            if (l > now) {
                n.add(l);
            }
        }
        event = n;
        return event.size();
    }
}
