package com.diodesoftware.scb.agents;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.S3FileHandler;
import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.scb.tables.ClipS3Object;
import org.apache.log4j.Logger;
import org.jets3t.service.S3ServiceException;

import java.sql.Connection;
import java.util.*;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Mar 16, 2008
 * Time: 3:04:25 PM
 */
public class S3AccessAgent implements Runnable{

    private static Logger log = Logger.getLogger(S3AccessAgent.class);

    private S3FileHandler s3FileHandler = new S3FileHandler();
    private Map<Integer, Loc> open = new HashMap<Integer, Loc>();
    private static S3AccessAgent instance = null;

    public static S3AccessAgent getInstance(){
        if(instance == null)instance = new S3AccessAgent();
        return instance;
    }

    private S3AccessAgent(){
        Thread t = new Thread(this);
        t.start();
    }

    // Record access to an object
    public String open(Clip clip, ClipS3Object cs3o, Connection con) {
        String key = null;
        int downloadCount = cs3o.getDownloadCount();
        downloadCount++;
        cs3o.setLastDownloaded(Calendar.getInstance());
        cs3o.setDownloadCount(downloadCount);
        DBMapper.save(cs3o, con);
        try {
            key = s3FileHandler.openKey(clip, cs3o.getName());
            Loc loc = null;
            synchronized (open) {
                loc = open.get(cs3o.getNumber());
            }
            if (loc == null) {
                loc = new Loc(key, cs3o.getName(), System.currentTimeMillis());
                synchronized (open) {
                    open.put(cs3o.getNumber(), loc);
                }
            } else {
                loc.lastAccess = System.currentTimeMillis();
            }
        } catch (S3ServiceException e) {
            log.error("Error Opening access to " + clip.getNumber(), e);
        }
        return key;
    }

    // Keep open for a while
    private static final int OPEN_FOR = 20 * 60 * 1000;
    private static final Object mutext = new Object();
    private boolean stopRequested;

    public void run() {
        try {
            while (!stopRequested) {
                synchronized (mutext) {
                    mutext.wait(60 * 1000);

                }
                long now = System.currentTimeMillis();
                long before = now - OPEN_FOR;
                synchronized (open) {
                    List<Integer> toRemove = new ArrayList<Integer>();
                    Iterator<Integer> iter = open.keySet().iterator();
                    while(iter.hasNext()){
                        int i = iter.next();
                        Loc loc = open.get(i);
                        if(loc.lastAccess > before){
                            toRemove.add(i);
                        }
                    }
                    iter = toRemove.iterator();
                    while(iter.hasNext()){
                        Loc loc = open.remove(iter.next());
                        // Close the door
                        try{
                            s3FileHandler.close(loc.key, loc.name);
                        }catch(S3ServiceException e){
                            log.warn("Error closing S3 Access", e);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {

        }

    }

    public void kill() {
        stopRequested = true;
        synchronized (mutext) {
            mutext.notifyAll();
        }
    }





    class Loc {
        String key;
        String name;
        long lastAccess;

        Loc(String key, String name, long lastAccess) {
            this.key = key;
            this.name = name;
            this.lastAccess = lastAccess;
        }
    }
}
