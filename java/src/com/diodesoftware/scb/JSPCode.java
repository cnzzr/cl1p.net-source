package com.diodesoftware.scb;

import com.diodesoftware.scb.tables.Clip;

/**
 * Copyright 2008 Sensemaker Software Inc.
 * User: rob
 * Date: Feb 9, 2008
 * Time: 12:31:15 PM
 */
public class JSPCode {

    public static String renderKeepFor(ClipRequest request, ClipSession clipSession) {
        StringBuilder sb = new StringBuilder();
        Clip clip = request.getClip();

        sb.append("<select name=\"keepfor\"  onchange=\"$('aform').submit()\">");


        if (clipSession.isPro()) {

        }
        int[] keepForValues = {
                60,
                120,
                480,
                1440,
                1440 * 2,
                1440 * 7,
                1440 * 14,
                1440 * 31,
                1440 * 31 * 2,
                1440 * 31 * 3,
                1440 * 31 * 6,
                1440 * 31 * 9};
        String[] lables = {
                "1 Hour",
                "2 Hours",
                "8 Hours",
                "1 Day",
                "2 Days",
                "1 Week",
                "2 Weeks",
                "1 Month",
                "2 Months",
                "3 Months",
                "6 Months",
                "9 Months"
        };

        if (clipSession.isPro()) {
            keepForValues = new int[]{
                    60,
                    120,
                    480,
                    1440,
                    1440 * 2,
                    1440 * 7,
                    1440 * 14,
                    1440 * 31 * 1,
                    1440 * 31 * 2,
                    1440 * 31 * 3,
                    1440 * 31 * 6,
                    525600,
                    -1
            };
            lables = new String[]{
                    "1 Hour",
                    "2 Hours",
                    "8 Hours",
                    "1 Day",
                    "2 Days",
                    "1 Week",
                    "2 Weeks",
                    "1 Month",
                    "2 Months",
                    "3 Months",
                    "6 Months",
                    "1 Year",
                    "Until I delete it"
            };
        }

        boolean foundOption = false;
        for (int i = 0; i < keepForValues.length; i++) {
            int val = keepForValues[i];
            String selected = "";

            if (val == clip.getKeepFor()) {
                selected = " Selected ";
                foundOption = true;
            }
            int n = i + i;
            if(n >= keepForValues.length && !foundOption){
                selected = " Selected "; //Select the last one if not found
            }

            sb.append("<option value=\"").append(val).append("\" ").append(selected).append(">");
            sb.append(lables[i]).append("</option>");


        }
      
        sb.append("</select>");
        return sb.toString();
    }
}
