package com.diodesoftware.scb.clipboard;

import com.diodesoftware.util.TimeGetter;
import com.diodesoftware.util.DefaultTimeGetter;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


public class LockMaster {

    public static final int UNLOCKED = 0;
    public static final int LOCKED = 1;
    public static final int HAS_KEY = 2;
    public static final long EXPIRY_TIME = 2*60*1000;
    private TimeGetter timeGetter;
    private static LockMaster instance = null;

    private Map<String, ClipLock> locks = new HashMap<String, ClipLock>();

    LockMaster(){
        setTimeGetter(new DefaultTimeGetter());
    }

    public static LockMaster getInstnace(){
        if(instance == null)instance = new LockMaster();
        return instance;
    }


    public void setTimeGetter(TimeGetter timeGetter) {
        this.timeGetter = timeGetter;
    }

    public int getLock(String sessionId, String uri){
        int result;
        ClipLock lock = locks.get(uri);
        if(lock == null){
            lock = new ClipLock(sessionId, timeGetter.currentTime());
            result = HAS_KEY;
            locks.put(uri, lock);
        }else{
            if(lock.sessionId == null|| lock.sessionId.equals(sessionId)){
                lock.sessionId = sessionId;
                lock.lastUpdate = timeGetter.currentTime();
                result = HAS_KEY;
            }else{
                result = LOCKED;
            }
        }
        return result;
    }

    public void returnLock(String sessionId, String uri){
        ClipLock lock = locks.get(uri);
        if(lock != null && lock.sessionId != null){
            if(lock.sessionId.equals(sessionId)){
                lock.sessionId = null;
            }
        }
    }

    public int getStatus(String sessionID, String uri){
        int result = UNLOCKED;
        ClipLock lock = locks.get(uri);
        if(lock != null){
            if(sessionID.equals(lock.sessionId)){
                result = HAS_KEY;
                lock.lastUpdate = timeGetter.currentTime();
            }else{
                if(lock.sessionId == null){
                    result = UNLOCKED;
                }else{
                    result = LOCKED;
                }
            }
        }
        return result;
    }



    public void cleanOldLocks(){
        long now = timeGetter.currentTime();
        Iterator<ClipLock> iter =  locks.values().iterator();
        while(iter.hasNext()){
            ClipLock lock = iter.next();
            if(now - lock.lastUpdate > EXPIRY_TIME){
                lock.sessionId = null;//Unlock
            }
        }
    }

    private class ClipLock{
        String sessionId;
        long lastUpdate;

        public ClipLock(String sessionId, long lastUpdate) {
            this.sessionId = sessionId;
            this.lastUpdate = lastUpdate;
        }
    }


}
