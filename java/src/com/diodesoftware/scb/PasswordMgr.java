package com.diodesoftware.scb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Apr 13, 2006
 * Time: 5:55:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class PasswordMgr {
    private static final String KEY = "PasswordMgrRGDM";
    private Map keys = new HashMap();

    private PasswordMgr(){

    }

    public static PasswordMgr getInstance(ClipRequest request){
        HttpSession session = request.getSession();
        PasswordMgr result = (PasswordMgr)session.getAttribute(KEY);
        if(result == null){
            result = new PasswordMgr();
            session.setAttribute(KEY, result);
        }
        return result;
    }

    public void addKey(String uri){
        synchronized(keys){keys.put(uri, new Boolean(true));}
    }

    public void removeKey(String uri){
        synchronized(keys){keys.remove(uri);}        
    }

    public boolean hasKey(String uri){
        boolean result = false;
        synchronized(keys){
            Boolean b = (Boolean)keys.get(uri);
            if(b != null)
                result = b.booleanValue();
        }
        return result;
    }


}
