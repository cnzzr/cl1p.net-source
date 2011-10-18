package com.diodesoftware.scb.tables;

import com.diodesoftware.dbmapper.DatabaseEntry;
import com.diodesoftware.dbmapper.DatabaseColumn;
import com.diodesoftware.dbmapper.DatabaseColumnType;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Jul 17, 2006
 * Time: 9:48:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class EMail implements DatabaseEntry
{
    private int number;
    private String emailTo;
    private String emailFrom;
    private String subject;
    private String message;
    private Calendar sentDate;

    private DatabaseColumn[] columns = new DatabaseColumn[]{
        new DatabaseColumn("EmailTo", DatabaseColumnType.CHAR_20),
        new DatabaseColumn("EmailFrom", DatabaseColumnType.CHAR_200),
        new DatabaseColumn("Subject", DatabaseColumnType.CHAR_200),
        new DatabaseColumn("Message", DatabaseColumnType.BLOB),
        new DatabaseColumn("SentDate", DatabaseColumnType.DATE12)
    };

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Calendar getSentDate() {
        return sentDate;
    }

    public void setSentDate(Calendar sentDate) {
        this.sentDate = sentDate;
    }

    public DatabaseColumn[] columns() {
        return columns;
    }


}
