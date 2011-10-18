package com.diodesoftware.scb;

import java.util.Calendar;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Feb 18, 2008
 * Time: 1:47:13 PM
 */
public class CreateDate {

    public static void main(String[] args){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);
        System.err.println(cal.getTimeInMillis());

    }
}
