package com.diodesoftware.scb.clipboard;

import com.diodesoftware.util.MockTimeGetter;
import com.diodesoftware.scb.ClipUtil;

import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;

public class LockMasterTest extends TestCase {

    private LockMaster lockMaster;
    private MockTimeGetter timeGetter;
    private List<String> sessions = new ArrayList<String>();
    private static final String URI_A = "/uri/a/";
    private static final String URI_B = "/uri/b/";

    public LockMasterTest(String s){
        super(s);
    }

    public void setUp() throws Exception{
        lockMaster = new LockMaster();
        timeGetter = new MockTimeGetter();
        timeGetter.setTime(System.currentTimeMillis());
        lockMaster.setTimeGetter(timeGetter);
    }

    private String newSession(){
        String s = ClipUtil.genString(10);
        while(sessions.contains(s)){
            s = ClipUtil.genString(10);
        }
        return s;
    }


    public void testFirstViewer() throws Exception{
        String sessionA = newSession();
        int currentStatus = lockMaster.getStatus(sessionA, URI_A);
        assertEquals("Expected UNLOCKED",  LockMaster.UNLOCKED, currentStatus);
        currentStatus = lockMaster.getLock(sessionA, URI_A);
        assertEquals("Expected HAS_KEY", LockMaster.HAS_KEY, currentStatus);
        currentStatus = lockMaster.getStatus(sessionA, URI_A);
        assertEquals("Does not have KEY", LockMaster.HAS_KEY, currentStatus);
        lockMaster.returnLock(sessionA, URI_A);
        currentStatus = lockMaster.getStatus(sessionA, URI_A);
        assertEquals("Should not have key", LockMaster.UNLOCKED, currentStatus);
    }

    public void testTimeout() throws Exception{
        String sessionA = newSession();
        lockMaster.getLock(sessionA, URI_A);
        lockMaster.cleanOldLocks();
        int status = lockMaster.getStatus(sessionA, URI_A);
        assertEquals("Should still have lock", LockMaster.HAS_KEY, status);
        //Move time ahead;
        long newTime = System.currentTimeMillis() + LockMaster.EXPIRY_TIME;
        timeGetter.setTime(newTime);
        lockMaster.cleanOldLocks();
        status = lockMaster.getStatus(sessionA, URI_A);
        assertEquals("Should have lost lock", LockMaster.UNLOCKED, status);
    }

    public void testSecondViewer() throws Exception{
        String sessionA = newSession();
        String sessionB = newSession();
        lockMaster.getLock(sessionA, URI_A);
        int bStatus = lockMaster.getStatus(sessionB, URI_A);
        assertEquals("Should be locked", LockMaster.LOCKED, bStatus);
        bStatus = lockMaster.getLock(sessionB, URI_A);
        assertEquals("Should not get locked on locked uri", LockMaster.LOCKED, bStatus);
        lockMaster.returnLock(sessionA, URI_A);
        bStatus = lockMaster.getStatus(sessionB, URI_A);
        assertEquals("Should be unlocked", LockMaster.UNLOCKED, bStatus);
        bStatus = lockMaster.getLock(sessionB, URI_A);
        assertEquals("Should have lock", LockMaster.HAS_KEY, bStatus);
        int aStatus = lockMaster.getLock(sessionA, URI_A);
        assertEquals("Should be locked out", LockMaster.LOCKED, aStatus);
        aStatus = lockMaster.getLock(sessionA, URI_B);
        assertEquals("Should have lock on different URI", LockMaster.HAS_KEY, aStatus);
    }

}
