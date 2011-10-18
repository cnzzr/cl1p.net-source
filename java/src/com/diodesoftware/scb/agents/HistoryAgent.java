package com.diodesoftware.scb.agents;

import com.diodesoftware.scb.tables.ClipHistory;
import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.scb.tables.Owner;
import com.diodesoftware.scb.tables.User;
import com.diodesoftware.scb.ClipSession;
import com.diodesoftware.dbmapper.DBMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class HistoryAgent {

    private static Logger log  = Logger.getLogger(HistoryAgent.class);

    public ClipHistory[] loadHistory(String uri, ClipSession clipSession, Connection con)
            throws FormException {
        DBMapper mapper = DBMapper.getInstance();


        if (!clipSession.isLoggedIn()) {
            throw new FormException("Not logged in");
        }


        ClipAgent clipAgent = ClipAgent.getInstance();
        Clip clip = clipAgent.loadClip(uri, con);
        if (clip == null) {
            throw new FormException("Clip is null [" + uri + "]");

        }
        int ownerId = clip.getOwnerId();
        Owner owner = (Owner) mapper.load(Owner.class, ownerId, con);
        if (owner == null) {

            throw new FormException("Clip is null");
        }
        User user = clipSession.getUser();
        if (owner.getUserId() != user.getNumber()) {

            throw new FormException("Not clip owner");
        }
        // OK to show history
        ClipHistory[] result = null;
        String sql = "Select * from ClipHistory where ClipNumber = ? order by LastEdit DESC";
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setInt(1, clip.getNumber());
            ArrayList list = new ArrayList();
            ResultSet rs = prepStmt.executeQuery();
            while(rs.next()){
                list.add(mapper.loadSingle(ClipHistory.class, rs));
            }
            rs.close();
            prepStmt.close();
            result = new ClipHistory[list.size()];
            list.toArray(result);
        }catch(SQLException e){
            log.error("Error running SQL [" + sql + "]", e);
        }
        return result;
    }
}
