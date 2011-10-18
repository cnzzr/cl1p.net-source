package com.diodesoftware.scb.diff;

import java.util.StringTokenizer;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jul 6, 2007
 * Time: 7:55:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestClipDiff {

    public static void main(String[] args) {
        String existingValue = "Hey there, hows it going!\n" +
                "Cool I like cl1p\n" +
                "Its the best\n" +
                "I love it";
        String newValue = "Hey there, hows it going!\n" +
                "NAKED I like cl1p\n" +
                "Its the best\n" +
                "I love it\n" +
                "Oh yeah";
        String[] aLines = breakUpLines(existingValue);
        String[] bLines = breakUpLines(newValue);
        List<String> result = new ArrayList<String>();
        List diffs = (new Diff(aLines, bLines)).diff();
        for(String s : bLines){
            result.add(s);
        }
        Iterator it = diffs.iterator();
        while (it.hasNext()) {
            Difference diff = (Difference) it.next();
            int delStart = diff.getDeletedStart();
            int delEnd = diff.getDeletedEnd();
            int addStart = diff.getAddedStart();
            int addEnd = diff.getAddedEnd();
            String from = toString(delStart, delEnd);
            String to = toString(addStart, addEnd);
            String type = delEnd != Difference.NONE && addEnd != Difference.NONE ? "c" : (delEnd == Difference.NONE ? "a" : "d");
            // Remove changed lines, then add new lines starting


            if (delEnd != Difference.NONE) {
                printLines(delStart, delEnd, "<", aLines);
                if (addEnd != Difference.NONE) {
                    System.out.println("---");
                }
            }
            if (addEnd != Difference.NONE) {
                printLines(addStart, addEnd, ">", bLines);
            }
        }
    }

    protected static void printLines(int start, int end, String ind, String[] lines) {
        for (int lnum = start; lnum <= end; ++lnum) {
            System.out.println(ind + " " + lines[lnum]);
        }
    }

    protected static String toString(int start, int end) {
        // adjusted, because file lines are one-indexed, not zero.

        StringBuffer buf = new StringBuffer();

        // match the line numbering from diff(1):
        buf.append(end == Difference.NONE ? start : (1 + start));

        if (end != Difference.NONE && start != end) {
            buf.append(",").append(1 + end);
        }
        return buf.toString();
    }


    private static String[] breakUpLines(String s) {
        StringTokenizer st = new StringTokenizer(s, "\n");
        String[] result = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            result[i] = st.nextToken();
            i++;
        }
        return result;
    }
}
