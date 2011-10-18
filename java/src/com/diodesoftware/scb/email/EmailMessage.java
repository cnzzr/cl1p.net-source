package com.diodesoftware.scb.email;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;

public class EmailMessage
    implements Serializable
{
    private String[] to = null;
    private String from = null;
    private String subject = null;
    private String message = null;
    private String filename;
    private static Pattern vaildEmail = Pattern.compile(
        "\\b                                            \n" +
        "#Capture the address to $1...                  \n" +
        "(                                              \n" +
        "  \\w[-.\\w]*                    #username     \n" +
        "  @                                            \n" +
        "  [-\\w]+(\\.[-\\w]+)*\\..?      #hostname     \n" +
        ")                                              \n" +
        "\\b                                            \n",
        Pattern.CASE_INSENSITIVE|Pattern.COMMENTS);

    public EmailMessage()
    {
    }

    public void setTo(String to)
    {
        this.to = new String[]{to};
    }

    public void setTo(String[] to)
    {
        this.to = to;
    }

    public String[] getTo()
    {
        return to;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getFrom()
    {
        return from;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public static boolean isEmailVaild(String email)
    {
        Matcher mather = vaildEmail.matcher(email);
        return mather.find();
    }
}

