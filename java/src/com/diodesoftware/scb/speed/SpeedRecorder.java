package com.diodesoftware.scb.speed;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jan 14, 2008
 * Time: 7:55:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpeedRecorder {


    private static ThreadLocal tc = new ThreadLocal();

    private static ArrayList totals = new ArrayList();
    private static long lastCleaned = 0;

    public static void start() {
        Timer t = new Timer();
        t.start = System.currentTimeMillis();
        t.last = t.start;
        tc.set(t);

    }

    public static void checkpoint(String name) {
        Timer t = (Timer) tc.get();
        long now = System.currentTimeMillis();
        long since = now - t.last;
        long time = now - t.start;
        Checkpoint c = new Checkpoint();
        c.name = name;
        c.time = time;
        c.sinceLast = since;
        t.events.put(name, c);
    }

    public static void end() {
        Timer t = (Timer) tc.get();
        t.end = System.currentTimeMillis();
        synchronized (totals) {
            long passed = t.end - lastCleaned;
            if(passed > (24*60*60*1000))
                totals = new ArrayList();
            lastCleaned = t.end;
            totals.add(t);
        }
    }

    static class Timer {
        long start;
        long last;
        long end;
        HashMap events = new HashMap();
    }

    static class Checkpoint {
        String name;
        long time;
        long sinceLast;
    }

    public static String printSummry() {
        DateFormat df = DateFormat.getDateTimeInstance();
        StringBuffer sb = new StringBuffer();
        synchronized (totals) {
            HashMap eventTotals = new HashMap();
            ArrayList totalTime = new ArrayList();
            Iterator timers = totals.iterator();

            while (timers.hasNext()) {

                Timer t = (Timer) timers.next();
                totalTime.add(t);
                Iterator keys = t.events.keySet().iterator();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    ArrayList checkpoints = (ArrayList) eventTotals.get(key);
                    if (checkpoints == null) {
                        checkpoints = new ArrayList();
                        eventTotals.put(key, checkpoints);
                    }
                    checkpoints.add(t.events.get(key));
                }
            }
            Iterator keys = eventTotals.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                long shortest = -1;
                long longest = -1;
                long avg = 0;
                int count;
                ArrayList checkpoints = (ArrayList) eventTotals.get(key);
                count = checkpoints.size();
                Iterator cIter = checkpoints.iterator();
                while (cIter.hasNext()) {
                    Checkpoint c = (Checkpoint) cIter.next();
                    if (c.sinceLast < shortest || shortest == -1)
                        shortest = c.sinceLast;
                    if (c.time > longest || longest == -1)
                        longest = c.sinceLast;
                    avg += c.sinceLast;
                }
                avg = avg / (long) count;
                sb.append("Checkpoint [" + key + "] Avg[" + avg + "] Short[" + shortest + "] Long[" + longest + "]\n");
            }
            Iterator timerIterator = totalTime.iterator();
            long shortest = -1;
            long longest = -1;
            long avg = 0;
            int count = totalTime.size();
            while (timerIterator.hasNext()) {
                Timer c = (Timer) timerIterator.next();
                long l = c.end - c.start;
                if (l < shortest || shortest == -1)
                    shortest = l;
                if (l > longest || longest == -1)
                    longest = l;
                avg += l;
            }
            avg = avg / (long) count;
            sb.append("Total Times Avg[" + avg + "] Short[" + shortest +"] Long[" + longest + "]\n");
        }
        return sb.toString();
    }

}
