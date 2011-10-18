<%@ page import="com.diodesoftware.scb.ClipConfig" %>
<%@ page import="com.diodesoftware.scb.agents.SystemStatus" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.diodesoftware.scb.tables.Clip" %>
<%@ page import="com.diodesoftware.dbmapper.DBMapper" %>
<%@ page import="java.util.*" %>
<html>
<body>
<%
    String run = request.getParameter("run");
    if ("YES".equals(run)) {
        Connection oldCon = getClusterfuckConnection();
        DBConnectionMgr dbMgr = new DBConnectionMgr();
        Connection con = dbMgr.getConnection();
        String sql = "";
        try{
            HashMap oldValues = new HashMap();
            HashMap currentValues = new HashMap();

            sql = "Select * from Clip where Lookup is null";
            PreparedStatement oldprepStmt = oldCon.prepareStatement(sql);
            ResultSet oldRs = oldprepStmt.executeQuery();
            while(oldRs.next()){
                Clip oldClip = (Clip)DBMapper.loadSingle(Clip.class, oldRs);
  				
            }
            oldRs.close();
            oldprepStmt.close();
            PreparedStatement prepStmt = con.prepareStatement(sql);
            ResultSet rs = prepStmt.executeQuery();
            while(rs.next()){
               Clip currentClip = (Clip)DBMapper.loadSingle(Clip.class, rs);
               currentValues.put(new Integer(currentClip.getNumber()), currentClip.getUri());
            }
            rs.close();
            prepStmt.close();
            Collection newUrls = currentValues.values();
            Iterator  iter = oldValues.keySet().iterator();
            int count = 0;
            while(iter.hasNext()){
                Integer key = (Integer)iter.next();
                if(!currentValues.containsKey(key)){
                    if(!currentValues.containsValue(oldValues.get(key))){
                        Clip clip = (Clip)DBMapper.load(Clip.class, key.intValue(), oldCon);
                        clip.setNumber(-1);
                        DBMapper.save(clip, con);
                        count++;
                        log.error("Inserted " + count + " ID " + clip.getNumber());
                    }
                }
            }%>
            <%= count %> Moved<br/><%



        }catch(SQLException e)
        {
            log.error("Error running SQL [" + sql + "]", e);
        }finally{
            dbMgr.returnConnection(con);
            oldCon.close();
        }

    }
%>
<form method="post">
    <input type="hidden" name="run" value="YES"/>
    <input type="submit" value="RUN ONCE"/>
</form>
</body>
</html>
<%!

    private static Logger log = Logger.getLogger("jsp.repair");


    private Connection getClusterfuckConnection() {
        String url = null;
        Connection result = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //url = "jdbc:mysql://localhost/" + ClipConfig.DB_NAME;
            url = "jdbc:mysql://localhost/cl1pcf?useUnicode=true&characterEncoding=UTF-8";
            result = DriverManager.getConnection(url, "root", null);
        }
        catch (Exception e) {
            log.error("Can't get db connection url[" + url + "]", e);
            SystemStatus.getInstance().setERROR(true);
            throw new RuntimeException(e);
        }
        return result;
    }



%>