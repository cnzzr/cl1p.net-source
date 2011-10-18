package com.diodesoftware.scb.agents;

import com.diodesoftware.scb.tables.*;
import com.diodesoftware.scb.email.EmailMgr;
import com.diodesoftware.dbmapper.DBMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import org.apache.log4j.Logger;

import javax.servlet.ServletContext;

public class UrlPurchaseAgent {

    private static Logger log = Logger.getLogger(UrlPurchaseAgent.class);

    public UrlPurchaseAgent() {
    }

    public UrlCheckout queueUrlToBuy(String url, int userId, Connection con) {
        if (log.isDebugEnabled())
            log.debug("Queing URL to purchase URL [" + url + "] User ID [" + userId + "]");
        if (url != null && !url.endsWith("/")) {
            url += "/";
        }
        // Check to see if already purchased
        String sql = "Select * from UrlCheckout where Url = ? and Complete = 'Y'";
        boolean alreadyPurchased = false;
        try {
            PreparedStatement prepStmt = con.prepareStatement(sql);

            prepStmt.setString(1, url);
            ResultSet rs = prepStmt.executeQuery();
            alreadyPurchased = rs.next();
            rs.close();
            prepStmt.close();
        } catch (SQLException e) {
            log.error("Error running SQL [" + sql + "]", e);
        }
        if (alreadyPurchased) {
            if (log.isDebugEnabled())
                log.debug("URL [" + url + "] User ID [" + userId + "] has been purchased already.");
            // Renew
            ClipAgent clipAgent = ClipAgent.getInstance();
            Clip clip = clipAgent.loadClip(url, con);
            boolean renewing = false;
            if(clip.getOwnerId() > 0){
                Owner owner = (Owner)DBMapper.load(Owner.class, clip.getOwnerId(), con);
                if(owner != null && owner.getUserId() == userId){
                    renewing = true;
                }
            }
            if(!renewing)return null;// Only the purchaing user can renew
        }
        UrlCheckout urlCheckout = new UrlCheckout();
        urlCheckout.setCreated(Calendar.getInstance());
        urlCheckout.setCompleteDate(Calendar.getInstance());
        urlCheckout.setComplete(false);
        urlCheckout.setUrl(url);
        urlCheckout.setUserId(userId);
        DBMapper.getInstance().save(urlCheckout, con);
        if (log.isDebugEnabled())
            log.debug("URL [" + url + "] User ID [" + userId + "] checkout queued.");
        return urlCheckout;
    }

    public synchronized void buyUrl(Payment payment, Connection con, ServletContext context) {
        // This is comming in from pay pal Figure out the URL based on the USER ID.
        // This is set to be one at time.
        // TODO: When on multiple servers we will need to find a new way of doing this
        if (log.isDebugEnabled())
            log.debug("Completeing URL purchase");
        DBMapper mapper = DBMapper.getInstance();
        int urlcheckOutID = Integer.parseInt(payment.getCustom());
        UrlCheckout urlCheckout = (UrlCheckout) mapper.load(UrlCheckout.class, urlcheckOutID, con);
        int years = 0;
        String itemNumber = payment.getItemNumber();
        boolean ssl = false;
        if ("URL-1YR".equals(itemNumber) || "URL-1YR-SSL".equals(itemNumber)) {
            years = 1;
        } else if ("URL-5YR".equals(itemNumber) || "URL-5YR-SSL".equals(itemNumber)) {
            years = 5;
        } else if ("URL-10YR".equals(itemNumber) || "URL-10YR-SSL".equals(itemNumber)) {
            years = 10;
        }
        if(itemNumber != null && itemNumber.indexOf("SSL") != -1){
        	ssl = true;
        }
        if (years == 0) {
            log.error("Unknown item number [" + itemNumber + "] aborting purchase");
            throw new RuntimeException("Unknow item number [" + itemNumber + "] aborting purchase");
        }
        if (log.isDebugEnabled())
            log.debug("item Number [" + itemNumber + "] Years [" + years + "]");
        int userId = urlCheckout.getUserId();
        if (userId != -1) {
            // If we have a user ID then make owner

            Owner owner = new Owner();
            ClipAgent clipAgent = ClipAgent.getInstance();
            Clip clip = clipAgent.loadClip(urlCheckout.getUrl(), con);
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, years);
            owner.setUserId(urlCheckout.getUserId());
            owner.setStart(start);
            owner.setEnd(end);
            owner.setSslAccess(ssl);
            mapper.save(owner, con);
            if (clip == null) {
                clip = new Clip();
                clip.setUri(urlCheckout.getUrl());

            }
            clip.setOwnerId(owner.getNumber());
            clip.setKeepFor(-1);   // Never Delete
            Calendar cleanDate = Calendar.getInstance();
            cleanDate.set(Calendar.YEAR, 2525);
            clip.setCleanDate(cleanDate);
            mapper.save(clip, con);

            urlCheckout.setComplete(true);
            urlCheckout.setCompleteDate(Calendar.getInstance());
            mapper.save(urlCheckout, con);

            User user = (User) mapper.load(User.class, urlCheckout.getUserId(), con);
            EmailMgr.getInstance().sendPurchaseThankyou(urlCheckout, user, context);
            if (log.isDebugEnabled())
                log.debug("Url purchase complete");
        } else {
            //  No URL, make a credit
            if (log.isDebugEnabled())
                log.debug("User ID not set, creating a token");
            String token = UrlCredit.generateToken(con);
            UrlCredit credit = new UrlCredit();
            credit.setTransactionId(payment.getTxnId());
            credit.setEmail(payment.getPayerEmail());
            credit.setUri(urlCheckout.getUrl());
            credit.setToken(token);
            credit.setYears(years);
            credit.setSslAccess(ssl);
            mapper.save(credit, con);
            EmailMgr.getInstance().sendCreditThankyou(credit, context);
            urlCheckout.setComplete(true);
            urlCheckout.setCompleteDate(Calendar.getInstance());
            mapper.save(urlCheckout, con);
            // Never delete purchased cl1ps. Never
            ClipAgent clipAgent = ClipAgent.getInstance();            
            Clip clip = clipAgent.loadClip(urlCheckout.getUrl(), con);            
            clip.setKeepFor(-1);   // Never Delete
            Calendar cleanDate = Calendar.getInstance();
            cleanDate.set(Calendar.YEAR, 2525);
            clip.setCleanDate(cleanDate);
            mapper.save(clip, con);
            if (log.isDebugEnabled())
                log.debug("Token created, e-mail sent");
        }
    }

    public UrlCredit loadCredit(String token, Connection con) {
        UrlCredit result = null;

        String sql = "Select * from UrlCredit where Token = ? and Used = 'N'";
        try {
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1, token);
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                result = (UrlCredit) DBMapper.getInstance().loadSingle(UrlCredit.class, rs);
            }
            rs.close();
            prepStmt.close();
        } catch (SQLException e) {
            log.error("Error running sql [" + e + "]");
        }
        return result;
    }

    public void completeCredit(UrlCredit urlCredit, User user, Connection con) {
        DBMapper mapper = DBMapper.getInstance();
        Owner owner = new Owner();
        ClipAgent clipAgent = ClipAgent.getInstance();
        Clip clip = clipAgent.loadClip(urlCredit.getUri(), con);
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, urlCredit.getYears());
        owner.setUserId(user.getNumber());
        owner.setStart(start);
        owner.setEnd(end);
        owner.setSslAccess(urlCredit.isSslAccess());
        mapper.save(owner, con);
        if (clip == null) {
            clip = new Clip();
            clip.setUri(urlCredit.getUri());

        }
        clip.setOwnerId(owner.getNumber());
        clip.setKeepFor(-1);   // Never Delete
        Calendar cleanDate = Calendar.getInstance();
        cleanDate.set(Calendar.YEAR, 2525);
        clip.setCleanDate(cleanDate);
        urlCredit.setUsed(true);
        urlCredit.setOwnerId(owner.getNumber());
        mapper.save(clip, con);
        mapper.save(urlCredit,con);
    }
}
