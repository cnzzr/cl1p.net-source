package com.diodesoftware.scb.licensegen;

import com.diodesoftware.scb.tables.EvalCustomer;
import com.diodesoftware.scb.tables.Customer;
import com.diodesoftware.scb.license.License;
import com.diodesoftware.scb.email.EmailMessage;
import com.diodesoftware.scb.email.EmailMgr;
import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.dbmapper.DBConnectionMgr;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Mac;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.util.Properties;
import java.util.Calendar;
import java.util.Date;
import java.io.*;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.security.SecureRandom;

import org.jasypt.util.text.BasicTextEncryptor;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: Jul 7, 2007
 * Time: 6:14:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenLicense {

    private static final String KEY = "Please don't steal this software. I run a one person company and need every sale.";
    private static final String LICENSE_DIR = "/home/rob/cl1p-lic/";
    private static SecureRandom secRnd = new SecureRandom();

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
    private static Logger log = Logger.getLogger(GenLicense.class);

    public static void main(String[] args) {
        //String licDir = LICENSE_DIR + dateFormat.format(new Date()) + "R" + secRnd.nextInt(2000000);
        File f = new File(".");
        f.mkdirs();
        genEvalLicense(args[0], args[1], args[2], f, false);
    }

    private static void genEvalLicense(String email, String name,
                                       String companyName, File dir, boolean sendEmail) {
        EvalCustomer customer = new EvalCustomer();
        customer.setEmail(email);
        customer.setName(name);
        customer.setCompanyName(companyName);
        customer.setEvalDate(Calendar.getInstance());
        DBConnectionMgr dbMgr = new DBConnectionMgr();
        Connection con = dbMgr.getConnection();
        try{
            DBMapper.save(customer, con);
        }finally{
            dbMgr.returnConnection(con);
        }
        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.DAY_OF_MONTH, 30);
        customer.setExpiryDate(expiry);
        License lic = new License("cl1p-standard", "1.2", companyName, expiry);
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(KEY);
        String licKey = textEncryptor.encrypt(lic.toString());
        String licFileName = licenseFileName();// Don't send the path down!

        try {
            PrintWriter pw = new PrintWriter(new FileWriter(licFileName));
            pw.println(licKey);
            pw.println(lic.toString());
            pw.flush();
            pw.close();
            if (sendEmail) {
                EmailMessage message = new EmailMessage();
                message.setFilename(licFileName);
                message.setFrom("support@sensemaker.net");
                message.setTo(email);
                message.setSubject("Clipboard Core Eval license");
                message.setMessage("Thank you for evaluating Clipboard Core.\n\r" +
                        "Attached is the license file to run Clipboard Core for 30 days.\n\r" +
                        "\n\r" +
                        "");
                EmailMgr.getInstance().send(message);
            }
        } catch (IOException e) {
            log.error("Error creating license file " + lic.toString(), e);
        } catch (MessagingException e) {
            log.error("Error sending license email", e);
        }

    }

    public static void genLicense(String email, String name, String companyName, String paypalId, Connection con){
        //String licDir = LICENSE_DIR + dateFormat.format(new Date()) + "R" + secRnd.nextInt(2000000);
        //File f = new File(licDir);
        //f.mkdirs();
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setName(name);
        customer.setCompanyName(companyName);
        customer.setPurchaseDate(Calendar.getInstance());
        customer.setTransaction(paypalId);
        DBMapper.save(customer, con);//Get the ID
        String supportKey = "CL1P-" + customer.getNumber() + "81";
        customer.setSupportKey(supportKey);
        DBMapper.save(customer, con);
        License lic = new License("cl1p-standard", "1.2", companyName,null);
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(KEY);
        String licKey = textEncryptor.encrypt(lic.toString());
        String licFileName = licenseFileName();// Don't send the path down!

        try {
            PrintWriter pw = new PrintWriter(new FileWriter(licFileName));
            pw.println(licKey);
            pw.println(lic.toString());
            pw.flush();
            pw.close();

                EmailMessage message = new EmailMessage();
                message.setFilename(licFileName);
                message.setFrom("support@sensemaker.net");
                message.setTo(email);
                message.setSubject("Clipboard Core License");
                message.setMessage("Thank you for purchasing Clipboard Core.\n\r" +
                        "Attached is the license file to run Clipboard Core." +
                        "Included in your purchase is 1 Year of support and 1 Year of free upgrades and bug fixes." +
                        "For support with Clipboard Core please send an e-mail to support@sensemaker.net and include the work cl1p support in the subject.\n\r" +
                        "Your support key is: " + supportKey + "\n\r" +
                        "\n\r" +
                        "");
                EmailMgr.getInstance().send(message);

        } catch (IOException e) {
            log.error("Error creating license file " + lic.toString(), e);
        } catch (MessagingException e) {
            log.error("Error sending license email", e);
        }

    }


     public static void genBasicLicense(String email, String name, String companyName, String paypalId, Connection con){
        //String licDir = LICENSE_DIR + dateFormat.format(new Date()) + "R" + secRnd.nextInt(2000000);
        //File f = new File(licDir);
        //f.mkdirs();
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setName(name);
        customer.setCompanyName(companyName);
        customer.setPurchaseDate(Calendar.getInstance());
        customer.setTransaction(paypalId);
        DBMapper.save(customer, con);//Get the ID
        String supportKey = "CL1P-" + customer.getNumber() + "81";
        customer.setSupportKey(supportKey);
        DBMapper.save(customer, con);
        License lic = new License("cl1p-basic-500", "1.2", companyName,null);
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(KEY);
        String licKey = textEncryptor.encrypt(lic.toString());
        String licFileName = licenseFileName();// Don't send the path down!

        try {
            PrintWriter pw = new PrintWriter(new FileWriter(licFileName));
            pw.println(licKey);
            pw.println(lic.toString());
            pw.flush();
            pw.close();

                EmailMessage message = new EmailMessage();
                message.setFilename(licFileName);
                message.setFrom("support@sensemaker.net");
                message.setTo(email);
                message.setSubject("Clipboard Core License");
                message.setMessage("Thank you for purchasing Clipboard Core.\n\r" +
                        "Attached is the license file to run Clipboard Core." +
                        "Included in your purchase is 1 Year of support and 1 Year of free upgrades and bug fixes." +
                        "For support with Clipboard Core please send an e-mail to support@sensemaker.net and include the work cl1p support in the subject.\n\r" +
                        "Your support key is: " + supportKey + "\n\r" +
                        "\n\r" +
                        "");
                EmailMgr.getInstance().send(message);

        } catch (IOException e) {
            log.error("Error creating license file " + lic.toString(), e);
        } catch (MessagingException e) {
            log.error("Error sending license email", e);
        }

    }
    public static synchronized void genEvalLicense(String email, String name, String companyName, Connection con) {
        String licDir = LICENSE_DIR + dateFormat.format(new Date()) + "R" + secRnd.nextInt(2000000);
        File f = new File(licDir);
        f.mkdirs();
        genEvalLicense(email, name, companyName, f, true);
    }

    private static DateFormat dateFormatLic = new SimpleDateFormat("yyyyMMddkkmmss");

    private static String licenseFileName(){
        String s = "cc-license" + dateFormatLic.format(new Date());
        return s;
    }
}
