package com.diodesoftware.util;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jul 7, 2007
 * Time: 12:01:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class MockTimeGetter implements TimeGetter{

    private long time = 0;

    public void setTime(long l){
        time = l;
    }


    public long currentTime() {
        return time;
    }
}
