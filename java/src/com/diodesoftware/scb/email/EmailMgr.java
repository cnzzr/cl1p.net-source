/*===========================================================================
 *            Copyright ( c ) Robert Mayhew 2002
 *                       All Rights Protected
 *  This software is protected by international copyright law.  No part of
 *  this software may be reproduced, duplicated, published, distributed,
 *  rented out, transmitted, or communicated to the public by
 *  telecommunication, in any form or by any means except as expressly
 *  permitted, in writing, by Robert Mayhew.
 *===========================================================================
 * $Id: EmailMgr.java,v 1.1.1.1 2003/06/26 00:52:41 Administrator Exp $
 *===========================================================================
 */
package com.diodesoftware.scb.email;

import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.servlet.ServletContext;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URLEncoder;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.dbmapper.SQLUtil;
import com.diodesoftware.dbmapper.DBConnectionMgr;
import com.diodesoftware.scb.agents.UserAgent;
import com.diodesoftware.scb.tables.*;

public class EmailMgr {

    private Session session = null;
    private static EmailMgr instnace = null;
    private DBMapper mapper = null;
    private Logger log = Logger.getLogger(EmailMgr.class);
    private Logger emailLog = Logger.getLogger("email");
    private UserAgent userAgent;

    private EmailMgr(String host, DBMapper mapper) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        session = Session.getDefaultInstance(props, null);
        this.mapper = mapper;
        userAgent = UserAgent.getInstance();
    }

    public static void initialize(String host, DBMapper mapper) {
        instnace = new EmailMgr(host, mapper);
    }

    public static EmailMgr getInstance() {
        if (instnace == null) {
            throw new IllegalStateException("EmailMgr not initalized");
        }
        return instnace;
    }

    public void sendShare(String from, String to, String subject, String message, Connection con)
            throws Exception
    {
        if(!Ignoremail.isIgnored(to, con)){
            String url = "http://cl1p.net/cl1p-inc-rgdm/shareIgnore.jsp?e=" + URLEncoder.encode(to, "UTF-8");

            String inError = "If this message was sent in error please go to " + url + "  to prevent cl1p from spaming you.";
            message = message + "\n\n" + inError;
            send(from,to,subject,message,con);
        }
    }


    public void send(String from, String to, String subject, String message, Connection con)
            throws AddressException, MessagingException, SpamException {
       spamCheck(to, con);
        EmailMessage em = new EmailMessage();
        em.setFrom(from);
        em.setTo(to);
        em.setSubject(subject);
        em.setMessage(message);
        send(em);
    }

    public void send(EmailMessage message)
            throws AddressException, MessagingException {
        if (message.getTo().length == 1) {
            if(message.getFilename() == null)
                send(message, message.getTo()[0]);
            else
                sendAttachment(message, message.getTo()[0]);
        } else {
            Thread t = new Thread(new MultiSender(message));
            t.setName("cl1p-email Multi-Sender");
            t.start();
        }
    }

    public void sendToAll(String from, boolean test, String subject, String message) {
        User[] targetUsers = new User[0];
        DBConnectionMgr dbCon = new DBConnectionMgr();
        Connection con = dbCon.getConnection();
        MassEmailSender mes = null;
        userAgent = UserAgent.getInstance();
        String lm = "Sending mass email from [" + from + "] Test email [" + test + "] Subject [" + subject + "]";
        if (log.isDebugEnabled())
            log.debug(lm);
        emailLog.info(lm);
        try {
            if (test) {
                emailLog.info("Test Email");
                User maldrax = userAgent.load("maldrax", con);
                targetUsers = new User[]{maldrax};
            } else {
                emailLog.info("Send to ALL USERS");
                String sql = "Select * from User where NoEmail = 'N' or NoEmail is null";
                try {
                    PreparedStatement prepStmt = con.prepareStatement(sql);
                    ResultSet rs = prepStmt.executeQuery();
                    ArrayList list = new ArrayList();
                    while (rs.next()) {
                        list.add(mapper.loadSingle(User.class, rs));
                    }
                    rs.close();
                    prepStmt.close();
                    targetUsers = new User[list.size()];
                    list.toArray(targetUsers);
                } catch (SQLException e) {
                    log.error("Error running SQL [" + sql + "]");
                }
            }
            String logMsg = "Sending Mass E-mail Subject [" + subject + "] to [" + targetUsers.length + "] users";
            if (log.isDebugEnabled())
                log.debug(logMsg);
            emailLog.info(logMsg);
            mes = new MassEmailSender(targetUsers, from, subject, message);
        } finally {
            dbCon.returnConnection(con);
        }
        Thread t = new Thread(mes);
        t.setName("Cl1p Mass E_mail Thread");
        t.start();

    }

    public void sendPurchaseThankyou(UrlCheckout urlCheckout, User user, ServletContext context) {
        String from = "cl1p@cl1p.net";
        String to = user.getEmail();
        String url = urlCheckout.getUrl();
        String fileName = context.getRealPath("/WEB-INF/purchaseThankyou.html");
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while (in.ready()) {
                sb.append(in.readLine());
            }
            in.close();
        } catch (Exception e) {
            String lm = "Error sending thankyou email";
            emailLog.error(lm, e);
        }
        EmailMessage msg = new EmailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("You now own cl1p.net" + url);
        String messageBody = sb.toString();
        messageBody = messageBody.replaceAll("RGDMUSERNAME", user.getUsername());
        messageBody = messageBody.replaceAll("RGDMURL", "http://cl1p.net" + url);

        msg.setMessage(messageBody);
        try {
            send(msg);
        } catch (Exception e) {
            emailLog.error("Error sending thanyout email (send)", e);
        }
    }

    public void sendCreditThankyou(UrlCredit credit, ServletContext context) {
        String from = "cl1p@cl1p.net";
        String to = credit.getEmail();
        String url = credit.getUri();
        String fileName = context.getRealPath("/WEB-INF/creditThankyou.html");
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while (in.ready()) {
                sb.append(in.readLine());
            }
            in.close();
        } catch (Exception e) {
            String lm = "Error sending thankyou email";
            emailLog.error(lm, e);
        }
        EmailMessage msg = new EmailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("You now own cl1p.net" + url);
        String messageBody = sb.toString();
        String tokenLink = "http://cl1p.net/cl1p-admin/complete-purchase.jsp?t=" + credit.getToken();
        messageBody = messageBody.replaceAll("RGDMTOKENLINK", tokenLink);
        messageBody = messageBody.replaceAll("RGDMURL", "http://cl1p.net" + url);

        msg.setMessage(messageBody);
        try {
            send(msg);
        } catch (Exception e) {
            emailLog.error("Error sending thanyout email (send)", e);
        }
    }

    private void send(EmailMessage emailMessage, String to)
            throws AddressException, MessagingException {
        synchronized (session) {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(emailMessage.getFrom()));
            try{
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(emailMessage.getSubject());
            msg.setDataHandler(new DataHandler(
                    new ByteArrayDataSource(emailMessage.getMessage(),
                            "text/html")));
            msg.setSentDate(new Date());

            Transport.send(msg);
            }catch(AddressException e){
                log.debug("Address exception", e);   
            }
        }
    }

    private void sendAttachment(EmailMessage emailMessage, String to)
    throws AddressException, MessagingException{
        // Define message

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailMessage.getFrom()));
        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(to));
        message.setSubject(emailMessage.getSubject());

// Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

// Fill the message
        messageBodyPart.setText(emailMessage.getMessage());

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

// Part two is attachment
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(emailMessage.getFilename());
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(emailMessage.getFilename());
        multipart.addBodyPart(messageBodyPart);

// Put parts in message
        message.setContent(multipart);

// Send the message
        Transport.send(message);

    }

    public void spamCheck(String to, Connection con)
            throws SpamException {
        int count = 0;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        long l = SQLUtil.calendarToLong(cal);
        String sql = "Select count(*) from EMail where EmailTo = ? and SentDate > ?";
        try {
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1, to);
            prepStmt.setLong(2, l);
            ResultSet rs = prepStmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            prepStmt.close();
        } catch (SQLException e) {
            log.error("Error running SQL [" + sql + "]", e);
        }
        if (count > 50) {
            throw new SpamException();
        }
    }

    class MassEmailSender
            implements Runnable {
        User[] users;
        String from;
        String subject;
        String message;


        MassEmailSender(User[] users, String from, String subject, String message) {
            this.users = users;
            this.subject = subject;
            this.message = message;
            this.from = from;

        }

        public void run() {
            try {
                DBConnectionMgr dbCon = new DBConnectionMgr();
                Connection con = dbCon.getConnection();
                String lm = "Starting email Mass Email thread";
                if (log.isDebugEnabled())
                    log.debug(lm);
                emailLog.info(lm);
                try {
                    Random rnd = new Random(System.currentTimeMillis());
                    for (int i = 0; i < users.length; i++) {
                        User u = users[i];
                        String cancelValue = CancelEmail.genValue(con);
                        CancelEmail cancelEmail = new CancelEmail();
                        cancelEmail.setUserId(u.getNumber());
                        cancelEmail.setValue(cancelValue);
                        mapper.save(cancelEmail, con);
                        EmailMessage emailMsg = new EmailMessage();
                        emailMsg.setTo(u.getEmail());
                        emailMsg.setFrom(from);
                        emailMsg.setSubject(subject);

                        String canecelMessage = "\nIf you don't want to hear from me again then go to the address here:\n";
                        canecelMessage += "http://cl1p.net/cl1p-admin/cancelEmail.jsp?v=" + cancelValue;
                        String msg = message + canecelMessage;
                        emailMsg.setMessage(msg);
                        try {
                            send(emailMsg);
                            lm = "Message sent to [" + u.getEmail() + "]";
                            if (log.isDebugEnabled())
                                log.debug(lm);
                            emailLog.info(lm);
                        } catch (Exception e) {
                            emailLog.debug("Error sending email to [" + u.getEmail() + "]");
                        }
                        int time = rnd.nextInt(10);
                        time++;
                        emailLog.info("Waiting Start " + time);
                        Object o = new Object();
                        //to avoid godaddy spam filters
                        try {
                            Thread.sleep(1000 * time);
                        } catch (InterruptedException e) {
                            emailLog.error("Error waiting to send email", e);
                        }
                        emailLog.info("Waiting End ");
                    }
                } finally {
                    dbCon.returnConnection(con);
                }
                lm = "Finishing send email thread";
                if (log.isDebugEnabled())
                    log.debug(lm);
                emailLog.info(lm);
            }
            catch (Throwable t) {
                emailLog.error("Error in send thread", t);
                log.error("Error in send thread", t);
            }
        }
    }

    class MultiSender
            implements Runnable {
        private EmailMessage message = null;
        private Logger log = Logger.getLogger(EmailMgr.class);

        MultiSender(EmailMessage message) {
            this.message = message;
        }

        public void run() {
            String[] to = message.getTo();
            for (int i = 0; i < to.length; i++) {
                try {
                    if(message.getFilename() == null)
                        send(message, to[i]);
                    else
                        sendAttachment(message, to[i]);
                } catch (Throwable t) {
                    log.error("Error sending email to [" + to[i] + "]", t);
                }
            }
        }
    }
}
