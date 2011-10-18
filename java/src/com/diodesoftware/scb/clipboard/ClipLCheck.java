package com.diodesoftware.scb.clipboard;

import java.util.Properties;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jul 7, 2007
 * Time: 6:06:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClipLCheck {


    public void check(String license){
        String decrypted = decrypt(license);
        Properties properties = new Properties();
        StringReader reader = new StringReader(decrypted);
        try{
            properties.load(new ByteArrayInputStream(decrypted.getBytes()));
        }catch(IOException e){
            
        }
    }

    private String decrypt(String license){
        return license;
    }
}
