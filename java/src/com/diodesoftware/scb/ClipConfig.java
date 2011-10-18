package com.diodesoftware.scb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;


public class ClipConfig {

//    private static final String LINUX_FILE_NAME = "/home/rob/cl1p.properties";
//    private static final String WINDOWS_FILE_NAME = "C:\\cl1p.properties";

    private static Logger log = Logger.getLogger(ClipConfig.class);
    public Properties resources;
    public static String VERSION = "1.0";
    public static String BUILD = "/*SVN_REPLACE_BUILD*/";
    public static String DB_NAME;
    public static String DB_USERNAME;
    public static String DB_PASSWORD;
    public static String DB_URL;
    public static String DB_PORT;
    public static String UPLOADED_FILE_DIR;
    public static String AWS_ACCESS_KEY_ID;
    public static String AWS_SECRET_ACCESS_KEY;
    
    public static int MAX_TYPE = 1;
    public static String MESSAGE = "";
    public static boolean configFileNotFound = false;
    public static boolean CL1P_SITE = true;
    public static IOException configFileNotFoundException;

    public static String LOG4J;
    private final String BUNDEL_NAME = "cl1p.properties";
    private final String P_DB_NAME = "database.name";
    private final String P_DB_USERNAME = "database.username";
    private final String P_DB_PASSWORD = "database.password";
    private final String P_DB_URL = "database.url";
    private final String P_DB_PORT = "database.port";
    private final String P_UPLOADED_FILE_DIR = "upload.dir";
    private final String P_MAX_TYPE = "max.type";

    private final String P_LOG4J = "log4j";
    private final String P_MESSAGE = "message";
    private static String P_AWS_ACCESS_KEY_ID = "awsAccessKeyId";
    private static String P_AWS_SECRET_ACCESS_KEY = "awsSecretAccessKey";

    private static ClipConfig instance = null;

    public static ClipConfig getInstance() {
        if (instance == null)
            instance = new ClipConfig();
        return instance;
    }

    private ClipConfig() {
        log.debug("Loading configuration properties");
        loadConfigFile();
        if (configFileNotFound) return;
        loadProperties();
    }

    public void loadProperties() {
        try {
            InputStream stream = this.getClass().getResourceAsStream("/sitesettings.properties");
            Properties prps = new Properties();

            if (stream != null) {
                prps.load(stream);
                String s = (String) prps.get("live");
                if (s != null) {
                    if (s.equals("true")) {
                        CL1P_SITE = true;
                        log.info("Site is live cl1p.net");
                    } else {
                        log.debug("Setting not live [" + s + "]");
                    }
                } else {
                    log.debug("live setting not found");
                }
                stream.close();
            } else {
                log.debug("Stream not found");
            }

        } catch (Exception e) {
            log.error("Error loading live settings", e);

        }
        loadConfigFile();
        try {
            DB_NAME = resources.getProperty(P_DB_NAME);
        } catch (Throwable t) {
            String msg = "Error loading property " + P_DB_NAME + " from bundle " + BUNDEL_NAME;
            log.error(msg);

        }
        try {
            DB_USERNAME = resources.getProperty(P_DB_USERNAME);
        } catch (Throwable t) {
            String msg = "Error loading property " + P_DB_USERNAME + " from bundle " + BUNDEL_NAME;
            log.error(msg);

        }
        try {
            DB_PASSWORD = resources.getProperty(P_DB_PASSWORD);
        } catch (Throwable t) {
            String msg = "Error loading property " + P_DB_PASSWORD + " from bundle " + BUNDEL_NAME;
            log.error(msg);

        }
        try {
            DB_URL = resources.getProperty(P_DB_URL);
        } catch (Throwable t) {
            String msg = "Error loading property " + P_DB_URL + " from bundle " + BUNDEL_NAME;
            log.error(msg);

        }
        try {
            DB_PORT = resources.getProperty(P_DB_PORT);
        } catch (Throwable t) {
            String msg = "Error loading property " + P_DB_PORT + " from bundle " + BUNDEL_NAME;
            log.error(msg);

        }
        try {
            UPLOADED_FILE_DIR = resources.getProperty(P_UPLOADED_FILE_DIR);
        } catch (Throwable t) {
            String msg = "Error loading property " + P_UPLOADED_FILE_DIR + " from bundle " + BUNDEL_NAME;
            log.error(msg);

        }
        try {
            LOG4J = resources.getProperty(P_LOG4J);
        } catch (Throwable t) {


        }


        try {
            AWS_ACCESS_KEY_ID = resources.getProperty(P_AWS_ACCESS_KEY_ID );
        } catch (Throwable t) {


        }
        try {
           AWS_SECRET_ACCESS_KEY = resources.getProperty(P_AWS_SECRET_ACCESS_KEY);
        } catch (Throwable t) {


        }
        try {
            MAX_TYPE = Integer.parseInt(resources.getProperty(P_MAX_TYPE));
        } catch (Throwable t) {
            String msg = "Error loading property " + P_MAX_TYPE + " from bundle " + BUNDEL_NAME;
            log.error(msg);
            //throw new RuntimeException(msg, t);
        }
        try {
            MESSAGE = resources.getProperty(P_MESSAGE);
        } catch (Throwable t) {
            String msg = "Error loading property " + P_MESSAGE + " from bundle " + BUNDEL_NAME;
            log.error(msg, t);

        }
        log.info("configuration properties loaded");
    }

    private void loadConfigFile() {
        configFileNotFound = false;
        String fileName = "/home/rob/cl1p.properties";

        File test = new File(fileName);
        if (!test.exists()) fileName = "C:\\src\\cl1p.properties";
        String override = System.getProperty("cl1p.test.override");
        if(override != null)
            fileName = override;
        try {
            resources = new Properties();
            if (linux())
                resources.load(new FileInputStream(fileName));
            else
                resources.load(new FileInputStream(fileName));
        } catch (IOException e) {
            log.error("Could not find property file [" + fileName + "]");
            configFileNotFound = true;
            configFileNotFoundException = e;
        }
    }

    public static boolean linux() {
        char c = File.separatorChar;
        return c == '/';
    }
}
