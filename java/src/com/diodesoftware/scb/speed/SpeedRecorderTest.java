package com.diodesoftware.scb.speed;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jan 14, 2008
 * Time: 8:32:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpeedRecorderTest extends TestCase {

    private static Logger log = Logger.getLogger(SpeedRecorderTest.class);

    public void testRecorder(){
        final Random rnd = new Random(System.currentTimeMillis());
        org.apache.log4j.BasicConfigurator.configure();
        Runnable r = new Runnable(){
            public void run() {
                SpeedRecorder.start();
                for(int i = 0; i < 10; i++){
                    try{
                        Thread.sleep(rnd.nextInt(10)*100);
                    }catch(Exception e){}
                    SpeedRecorder.checkpoint("CHECK " + i);
                }
                SpeedRecorder.end();
                log.error("Thread Ended");
            }
        };
        Thread[] t = new Thread[10];
        for(int i = 0; i < t.length; i++){
            t[i] = new Thread(r);
        }
        for(int i = 0; i < t.length; i++){
            t[i].start();
        }
        try{
            Thread.sleep(10*1000);
            System.err.println(SpeedRecorder.printSummry());
        }catch(Exception e){}
    }
}
