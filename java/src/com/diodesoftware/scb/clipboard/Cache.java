/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Sep 4, 2003
 * Time: 10:30:15 PM
 * To change this template use Options | File Templates.
 */
package com.diodesoftware.scb.clipboard;

import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.scb.agents.ClipAgent;
import com.diodesoftware.scb.ClipRequest;
import com.diodesoftware.scb.ClipSession;

import java.sql.Connection;

public class Cache
{
    private static Cache instance = null;
    private ClipAgent clipAgent = null;

    private Cache()
    {
        clipAgent = ClipAgent.getInstance();
    }

    public static synchronized void initialize()
    {
        instance = new Cache();
    }

    public static Cache getInstance()
    {
        return instance;
    }

    public Clip get(ClipSession session, ClipRequest request){
        String path = session.getLastUri();
        if(path != null){
            if(!path.endsWith("/"))
            {
                path = path + "/";
            }
        }
        Clip clip = clipAgent.loadClip(path, request.getCon());
        if(clip == null)
        {
            clip = new Clip();
            clip.setUri(path);
        }
        return clip;
    }

    public Clip get(ClipRequest request)
    {
        String path = getKey(request);        
        return get(path, request.getCon());
    }

    public Clip get(String path, Connection con){
        Clip clip = clipAgent.loadClip(path, con);
        if(clip == null)
        {
            clip = new Clip();
            if(!path.endsWith("/"))
            {
                path = path + "/";
            }
            clip.setUri(path);
        }
        return clip;
    }

    public static String getKey(ClipRequest request){
        String path = request.getUri();
        if(!path.endsWith("/"))
        {
            path = path + "/";
        }
        return path;
    }



//    public void set(String path, String value)
//    {
//
//        if(!path.endsWith("/"))
//        {
//            path = path + "/";
//        }
//        Clip clip = clipAgent.loadClip(path);
//        if(clip == null)
//        {
//            clip = new Clip();
//            clip.setUri(path);
//        }
//        clip.setValue(value);
//        clip.setLastEdit(Calendar.getInstance());
//        clipAgent.saveClip(clip);
//
//    }
}
