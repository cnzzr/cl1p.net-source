package com.diodesoftware.scb.upload;

import org.apache.log4j.Logger;

import java.util.*;




public class UploadStatusMgr {

    private static UploadStatusMgr instance = null;
    private static Logger log = Logger.getLogger(UploadStatusMgr.class);
     private static Logger statusLog = Logger.getLogger("custom.status");
    private Map map = new HashMap();

    private UploadStatusMgr(){}

    public static void initialize(){
        log.debug("Upload Status Mgr Init");
        instance = new UploadStatusMgr();
    }

    public static UploadStatusMgr getInstance(){
        return instance;
    }

    public synchronized  UploadStatus getUploadStatus(String key){
        if(log.isDebugEnabled())log.debug("Getting Status for [" + key + "]");
        if(statusLog.isDebugEnabled())statusLog.debug("StatusMgr Getting Status for [" + key + "]");
        UploadStatus status = (UploadStatus)map.get(key);
        if(status == null){
            if(log.isDebugEnabled())
                log.debug("building new Status for [" + key + "]");
            if(statusLog.isDebugEnabled())statusLog.debug("StatusMgr Building new Status for [" + key + "]");
            status = new UploadStatus();
            map.put(key, status);
        }
        return status;
    }

    public void cleanStatus(){
        long now = System.currentTimeMillis();
        List toRemove = new ArrayList();
        Iterator iter = map.keySet().iterator();
        while(iter.hasNext()){
            String key = (String)iter.next();
            UploadStatus status = (UploadStatus)map.get(key);
            if(status.getCleanAt() != 0 && status.getCleanAt() < now){
                toRemove.add(key);
            }
        }
        
        iter = toRemove.iterator();
        while(iter.hasNext()){
            String key = (String)iter.next();
            if(log.isDebugEnabled()){
               log.debug("Cleaning [" + key + "]");
            }

            map.remove(key);
        }
    }
}
