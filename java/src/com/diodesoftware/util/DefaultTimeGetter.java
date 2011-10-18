package com.diodesoftware.util;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jul 7, 2007
 * Time: 12:00:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultTimeGetter implements TimeGetter{

    public long currentTime() {
        return System.currentTimeMillis();
    }
}
