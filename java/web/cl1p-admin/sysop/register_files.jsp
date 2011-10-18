<%@ page import="com.diodesoftware.dbmapper.DBConnectionMgr" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.diodesoftware.dbmapper.DBMapper" %>
<%@ page import="com.diodesoftware.scb.filter.ClipFilter" %>
<%@ page import="com.diodesoftware.scb.tables.ClipS3Object" %>
<%@ page import="com.diodesoftware.scb.S3FileHandler" %>
<%@ page import="java.util.Iterator" %>
<%!
    private static Logger log = Logger.getLogger("jso.register_files");
%>
<html>
<head></head>
<body>
<%


    String run = request.getParameter("run");
    if("YES".equals(run)){
        DBConnectionMgr dbMgr = new DBConnectionMgr();
        Connection con = dbMgr.getConnection();
        String sql = "Select Number, Uri from Clip";
        try{

            PreparedStatement prepStmt = con.prepareStatement(sql);
            ResultSet rs = prepStmt.executeQuery();
            List list = new ArrayList();
            while(rs.next()){
                String uri =   rs.getString(2);
                String[] files = ClipFilter.getFileName(uri);
                if(files != null && files.length > 0)
                    list.add(new ClipStub(rs.getInt(1),uri, files));
            }
            rs.close();
            prepStmt.close();
            Iterator iter = list.iterator();
        %>
      <table>
          <tr><th>Clip Id</th><th>Uri</th></tr><%
            while(iter.hasNext()){

                ClipStub s = (ClipStub)iter.next();
                             %>
            <tr><td><%= s.number %></td><td> <%= s.uri %></td></tr>
<%
                registerToS3(s.number, s.uri, s.files, con);
            }
            %>
        </table>
             <%
        }catch(Exception e){
            log.error("Error running sql [" + sql + "]", e);
            %>
Error
    <%= e.getMessage() %>
<%
        }finally{
            dbMgr.returnConnection(con);
        }


    }
%>
<form mehtod="post">
    <input type="hidden" value="YES" name="run"/>
    <input type="submit" value="Run Once!"/>
</form>

</body>
</html>
<%!
    private S3FileHandler s3FileHandler = new S3FileHandler();

    private void registerToS3(int clip, String uri, String[] files, Connection con){
        for(int i=0;i<files.length;i++){
            s3FileHandler.registerFile(clip,uri, files[i], con);
        }
    }

    private class ClipStub{

        int number;
        String uri;
        String[] files;
        private ClipStub(int number, String uri, String[] files) {
            this.number = number;
            this.uri = uri;
            this.files = files;
        }
    }
%>