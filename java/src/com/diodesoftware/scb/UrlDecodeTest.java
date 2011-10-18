package com.diodesoftware.scb;

import junit.framework.TestCase;

import java.net.URLDecoder;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Nov 25, 2007
 * Time: 10:12:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class UrlDecodeTest extends TestCase {

    public void testDecode() throws Exception{
        String in = "http://coldtone:8080/btn?nl=yes&A=%u0108";
        String out = URLDecoder.decode(in,"UTF-8");
        System.err.println("IN [" + in + "] OUT [" + out + "]");
        assertTrue(true);
    }
}
