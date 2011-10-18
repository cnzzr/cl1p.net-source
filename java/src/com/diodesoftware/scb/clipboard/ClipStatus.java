package com.diodesoftware.scb.clipboard;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jun 27, 2007
 * Time: 9:41:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClipStatus {

    private Map<String, Map<String, CurrentStatus>> uris = new HashMap<String, Map<String, CurrentStatus>>();
    private Map<String, Long> lastEditMap = new HashMap<String, Long>();
    private static ClipStatus instance = null;
    private Map<String, Map<String, Boolean>> accessMap = new HashMap<String, Map<String, Boolean>>();
    private static Logger log = Logger.getLogger(ClipStatus.class);
    private ClipStatus(){

    }

    public static ClipStatus getInstance(){
        if(instance == null)
            instance = new ClipStatus();
        return instance;
    }


    public void grantAccess(String sessionKey, String uri){
        synchronized(accessMap){
            Map<String, Boolean> map = accessMap.get(sessionKey);
            if(map == null){
                map = new HashMap<String, Boolean>();
                accessMap.put(sessionKey, map);
            }
            map.put(uri, true);
            log.debug("Access Granted to [" + sessionKey + "] URI [" + uri + "]");
        }
    }

    public boolean hasAccess(String sessionKey, String uri){
         synchronized(accessMap){

            Map map = accessMap.get(sessionKey);
            if(map == null){
                log.debug("Access denied to [" + uri + "] session [" + sessionKey + "]");
                return false;
            }
            log.debug("Access  to [" + sessionKey + "] URI [" + uri + "] Granted [" + (Boolean)map.get(uri) + "]");
            if(map.containsKey(uri))
                return (Boolean)map.get(uri);
             return false;
        }
    }


    public void setStatus(String sessionKey, String uri, int status, long lastEdit) {
        synchronized (uris) {
            Map<String, CurrentStatus> map = uris.get(uri);
            if (map == null) {
                map = new HashMap<String, CurrentStatus>();
                uris.put(uri, map);
            }
            CurrentStatus cStatus = map.get(sessionKey);
            if (cStatus == null) {
                cStatus = new CurrentStatus();
                map.put(sessionKey, cStatus);
            }
            cStatus.lastUpdate = System.currentTimeMillis();
            cStatus.status = status;
        }

        synchronized(lastEditMap){
            lastEditMap.put(uri, lastEdit);
        }
    }

    public int[] getStatus(String uri) {
        int viewers = 0;
        int editors = 0;
        synchronized (uris) {
            Map<String, CurrentStatus> map = uris.get(uri);
            if (map == null) {
                return new int[]{0, 0};
            }

            Iterator<CurrentStatus> iter = map.values().iterator();
            while (iter.hasNext()) {
                if (iter.next().status == 1)
                    editors++;
                else
                    viewers++;
            }
        }
        return new int[]{viewers, editors};
    }

    public long getLastEdit(String uri){
        long result = 0;
        synchronized(lastEditMap){
            result = lastEditMap.get(uri);
        }
        return result;
    }



    public void cleanOld(){
        long now = System.currentTimeMillis();
        long max = 60*1000;
        synchronized(uris){
            List<String> sessionsToClean = new ArrayList<String>();
            Iterator<Map<String, CurrentStatus>> alpha = uris.values().iterator();
            while(alpha.hasNext()){
                Map<String, CurrentStatus> m = alpha.next();
                Iterator<String> iter = m.keySet().iterator();
                while(iter.hasNext()){
                    String sessionKey = iter.next();
                    CurrentStatus cs = m.get(sessionKey);
                    if(now - cs.lastUpdate > max){
                        sessionsToClean.add(sessionKey);
                    }
                }
                iter = sessionsToClean.iterator();
                while(iter.hasNext()){
                    m.remove(iter.next());
                }
            }
        }
    }


    class CurrentStatus {
        int status;
        long lastUpdate;
    }
}
