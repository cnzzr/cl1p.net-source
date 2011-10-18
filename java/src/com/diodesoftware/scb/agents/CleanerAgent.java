/**
 * $Date: 2003/12/01 13:46:35 $
 * $Author: Rob $
 * $Id: CleanerAgent.java,v 1.3 2003/12/01 13:46:35 Rob Exp $
 * $Revision: 1.3 $
 * $Source: /mnt/flashdrive/cvshome/cl1p/web-inf/src/com/diodesoftware/scb/agents/CleanerAgent.java,v $
 */
package com.diodesoftware.scb.agents;

import com.diodesoftware.R;
import com.diodesoftware.dbmapper.DBConnectionMgr;
import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.ClipMsg;
import com.diodesoftware.scb.DownloadTokenMgr;
import com.diodesoftware.scb.S3FileHandler;
import com.diodesoftware.scb.clipboard.LockMaster;
import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.scb.tables.ClipS3Object;
import com.diodesoftware.scb.upload.UploadStatusMgr;



import org.apache.log4j.Logger;
import org.jets3t.service.S3ServiceException;

import javax.servlet.ServletContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.text.*;
public class CleanerAgent {
    private static CleanerAgent instance;
    private static Logger log = Logger.getLogger(CleanerAgent.class);

    private DBConnectionMgr db = null;
    private boolean running = true;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static DateFormat dateFormat = DateFormat.getDateTimeInstance();
    private CleanerAgent(DBMapper mapper) {
        
        this.db = new DBConnectionMgr();
    }

    public static synchronized void initialize(DBMapper mapper) {
        instance = new CleanerAgent(mapper);
    }

    public static CleanerAgent getInstance() {
        return instance;
    }

    public void startCleanerThread(ServletContext context) {

    	
        Runnable runner = new Runnable() {
            public void run() {
                        ClipMsg.loadMsgFile();
                 
                        if (running) {
                            try {
                                clean();
                            } catch (Throwable t) {
                                log.error("Error cleaning Cl1p!", t);
                            }
                        }
                 
            }
        };
        executor.scheduleAtFixedRate(runner, 1l, 5l, TimeUnit.MINUTES);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void kill() {
        this.running = false;
        executor.shutdownNow();
    }

    public void clean() {
        LockMaster.getInstnace().cleanOldLocks();
        R.refresh();
        String sql3 = "Select Number, Created, KeepFor from Clip where OwnerId = 0";
        Connection con = db.getConnection();
        cleanFiles(con);
        long now = System.currentTimeMillis();
        String sql = null;
        try {
            Statement stmt = con.createStatement();

            sql = sql3;
            ResultSet rs = stmt.executeQuery(sql);
            List list = new ArrayList();
            while (rs.next()) {
                int clipId = rs.getInt(1);
                long created = rs.getLong(2);
                long keepFor = (long)rs.getInt(3);
                long cleanTime = created + ( (keepFor * 60l * 1000l));
                if (now > cleanTime) {
		      Date ct = new Date(cleanTime);
		      Date nt = new Date(now);
	              Date cr = new Date(created);
                      log.debug("KeepFor[" + keepFor + "] cleantime[" + cleanTime + "] now [" +now + "] ct[" + dateFormat.format(ct) + "] nt[" + dateFormat.format(nt) + "]");
	              log.debug("Created[" + dateFormat.format(cr) + "]");   
                   list.add(clipId);
                }
            }
            rs.close();
            stmt.close();
            Iterator iter = list.iterator();
            while (iter.hasNext()) {
                int i = ((Integer) iter.next()).intValue();
                clean(i, con);
            }
            if (log.isDebugEnabled())
                log.debug("Cleaned [" + list.size() + "] Cl1ps");

        } catch (SQLException e) {
            log.error("Error running SQL [" + sql + "]", e);
        } finally {
            db.returnConnection(con);
        }
    }

    public static void clean(int clipId, Connection con) {
        Clip clip = (Clip) DBMapper.getInstance().load(Clip.class, clipId, con);
        String sql = "Delete from Clip where Number = ?";
        try {
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setInt(1, clipId);
            prepStmt.executeUpdate();
            prepStmt.close();

            
        } catch (SQLException e) {
            log.error("Error cleaning clip [" + clipId + "]", e);
        }
    }

    public static void deleteFile(Clip clip) {
       /* String dirName = ClipFilter.uploadDirName(clip.getUri());
        File dir = new File(dirName);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (!file.getName().equals("..") && !file.getName().equals(".")) {
                    if (!file.delete()) {
                        log.error("Could not delete file [" + file.getAbsolutePath() + "]");
                    }
                }
            }
        }*/
    }

    public static void deleteFile(Clip clip, int fileNumber, Connection con) {
       String sql = "Select * from ClipS3Object where Number = ? and ClipId = ?";
        try{
            S3FileHandler s3file = new S3FileHandler();
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setInt(1, fileNumber);
            prepStmt.setInt(2, clip.getNumber());
            ResultSet rs = prepStmt.executeQuery();
            while(rs.next()){
                ClipS3Object s3o = (ClipS3Object)DBMapper.loadSingle(ClipS3Object.class, rs);
                try{
                s3file.deleteFile(clip, s3o.getName());
                }catch(S3ServiceException seE)
                {
                	log.error("Error deleteing file",seE);
                }
            }
            rs.close();
            prepStmt.close();
            sql = "Delete from ClipS3Object where Number = ? and ClipId = ?";
            prepStmt = con.prepareStatement(sql);
            prepStmt.setInt(1, fileNumber);
            prepStmt.setInt(2, clip.getNumber());
            prepStmt.executeUpdate();
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error running sql [" + sql + "]",e);
        }
    }

    private void cleanFiles(Connection con) {
      /*  Calendar now = Calendar.getInstance();
        long l = SQLUtil.calendarToLong(now);
        String sql = "Select Uri from Clip where CleanDate < " + l;

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String uri = rs.getString(1);
                String dirName = ClipFilter.uploadDirName(uri);
                File dir = new File(dirName);
                if (dir.exists()) {
                    dir.delete();
                }
            }
            stmt.close();
        } catch (SQLException e) {
            log.error("Error running SQL [" + sql + "]", e);
        }*/
    }


}

/**
 * $Log: CleanerAgent.java,v $
 * Revision 1.3  2003/12/01 13:46:35  Rob
 * Cleanup Thread now runs
 *
 * Revision 1.2  2003/10/13 22:11:23  Administrator
 * *** empty log message ***
 *
 * Revision 1.1.1.1  2003/10/13 19:19:31  Administrator
 * no message
 *
 * Revision 1.1.1.1  2003/10/13 00:21:18  root
 * no message
 *
 */

