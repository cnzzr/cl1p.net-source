package com.diodesoftware.scb.agents;

import com.diodesoftware.dbmapper.DBMapper;


/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Apr 11, 2006
 * Time: 7:38:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityAgent {

    private static SecurityAgent instance = null;


    private SecurityAgent(DBMapper mapper)
    {
    }

    public static synchronized void initalize(DBMapper dbMapper)
    {
        instance = new SecurityAgent(dbMapper);
    }

    public static SecurityAgent getInstance()
    {
        return instance;
    }

    public void checkForAbuse(){
        
    }
}
