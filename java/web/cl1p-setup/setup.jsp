<%@page import="com.diodesoftware.scb.ClipContextListener"%>
<html>
<head>
   <link rel="stylesheet" type="text/css" media="screen, projection" href="style.css">
</head>
<body>
<%
    if (!ClipConfig.configFileNotFound) {
%>
Already setup, you need to remove the file <%= ClipContextListener.configFile %> and restart the application.<%@page import="java.sql.Connection"%>
<%@page import="com.diodesoftware.dbmapper.DBConnectionMgr"%>
<%@page import="com.diodesoftware.scb.ClipSystemSetup"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.ResultSet"%>
</br>
	<%
}
%>
Welcome to cl1p setup
<%
int foundIn = ClipContextListener.FOUND_IN;


if(foundIn == 0){
%>
<%@page import="com.diodesoftware.scb.ClipUtil"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.IOException"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="com.diodesoftware.scb.ClipConfig"%>
<h2>Unable to find the configuration file.</h2>
You need to specify a location, but setting one of the following properties.

<ol>
	<li>Servlet Init Parameter <b><%=  ClipContextListener.PROPERTY_NAME %></b></li>
	<li>Java Property <b><%=  ClipContextListener.PROPERTY_NAME %></b></li>
    <li>Environment Variable <b><%=  ClipContextListener.ENVIRONMENT_VARIABLE %></b></li>
</ol>
<%
} else {
    //TODO: Have 2 downloads for MYSQL versions

    String errMsg = "";
    String exception = "";
    String msg = "";
    boolean error = false;
    String dbhost = request.getParameter("dbhost");
    String dbname = request.getParameter("dbname");
    String dbuser = request.getParameter("dbuser");
    String dbpassword = request.getParameter("dbpassword");
    String uploadDir = request.getParameter("upload.dir");
    String log4j = request.getParameter("log4j");
    String dbport = request.getParameter("dbport");
    if (dbhost != null) {
        if (ClipUtil.isBlank(dbhost)) {
            errMsg += " Database host Requried.";
            error = true;
        }
        if (ClipUtil.isBlank(dbname)) {
            errMsg += " Database name Requried.";
            error = true;
        }
        if (ClipUtil.isBlank(dbuser)) {
            errMsg += " Database user Requried.";
            error = true;
        }
        if (ClipUtil.isBlank(uploadDir)) {
            errMsg += " Upload Storage Dir Requried.";
            error = true;
        }
        String dbUrl = "jdbc:mysql://" + dbhost + "/";
        if (!error) {
            // Test Database Config
            msg += "Testing Database connection<br/>";
            try {
                Connection con = new DBConnectionMgr().testConnection(dbUrl, dbname, dbuser, dbpassword);
                msg += "Connection created.<br/>";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("show tables");
                rs.close();
                stmt.close();
                StringBuffer sb = new StringBuffer();
                ClipSystemSetup.setupTables(con, sb);
                msg += sb.toString();
                con.close();
                msg += "Connection closed.<br/>";
            } catch (Exception e) {
                msg += "Error connecting to database. Make sure database is running and Database name is created. Check server log for full exception<br/>";
                msg += "Error Msg:" + e.getMessage() + "<br/>";
                e.printStackTrace();
                error = true;
            }
            try {
                StringBuffer sb = new StringBuffer();
                ClipSystemSetup.createUploadDir(uploadDir, sb, session.getServletContext());
                msg += "Upload dir created";
            } catch (Exception e) {
                error = true;
                msg += "Error creating upload dir [" + uploadDir + "] Message" + e.getMessage();
            }
        }
        if (!error) {

            String fileName = ClipContextListener.configFile;
            int i = fileName.lastIndexOf(File.separator);
            if (i != -1 && i < (fileName.length() - 1)) {
                String dirs = fileName.substring(0, i);
                File dir = new File(dirs);
                msg += "Checking dir [" + dirs + "] For File [" + fileName + "].<br/>";
                if (dir.exists()) {
                    msg += "[" + dir.getAbsolutePath() + "] Exists.<br/>";
                    if (dir.isDirectory()) {
                        msg += "Dir [" + dir.getAbsolutePath() + "] is a directory.<br/>";
                    } else {
                        msg += "Dir [" + dir.getAbsolutePath() + "] is not a directory.<br/>";
                    }
                } else {
                    msg += "[" + dir.getAbsolutePath() + "] Dosen't Exist. Attempting to create.<br/>";
                    boolean worked = dir.mkdirs();
                    if (worked) {
                        msg += "Dir [" + dir.getAbsolutePath() + "] Created.<br/>";
                    } else {
                        msg += "Dir [" + dir.getAbsolutePath() + "] Failed to create.<br/>";
                    }
                }
            }
            Properties props = new Properties();
            props.setProperty("database.url", dbUrl);
            props.setProperty("database.name", dbname);
            props.setProperty("database.username", dbuser);
            props.setProperty("database.password", dbpassword);
            props.setProperty("database.port", dbport);
            props.setProperty("upload.dir", uploadDir);
            props.setProperty("log4j", log4j);
            props.setProperty("max.type", "3");
            //TODO: Make upgradeable
            props.setProperty("version", ClipConfig.VERSION);
            try {
                File f = new File(fileName);
                if (f.exists()) {
                    msg += "File [" + f.getAbsolutePath() + "] Exists and will be replaced.<br/>";
                } else {
                    msg += "File [" + f.getAbsolutePath() + "] Does not exist. Attempting to create.<br/>";
                    boolean worked = f.createNewFile();
                    if (worked) {
                        msg += "File [" + f.getAbsolutePath() + "] created.<br/>";
                    } else {
                        msg += "File [" + f.getAbsolutePath() + "] failed to create.<br/>";
                    }
                }
                FileOutputStream outStream = new FileOutputStream(f);
                props.store(outStream, "");
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                exception = e.getMessage();
                errMsg += " Error creating file ";
                error = true;
            }
            if (!error) {
                msg += "Cl1p has been setup.</br><a href='setup-2.jsp'>Next</a>";
            }


        }
    }
    dbhost = ClipUtil.blankNull(dbhost);
    dbname = ClipUtil.blankNull(dbname);
    dbuser = ClipUtil.blankNull(dbuser);
    dbpassword = ClipUtil.blankNull(dbpassword);
    uploadDir = ClipUtil.blankNull(uploadDir);
    log4j = ClipUtil.blankNull(log4j);
    dbport = ClipUtil.blankNull(dbport);

%>
<h2>System Settings</h2>
<p>These setting define where cl1p stores data.</p>
<h3>Database Setup</h3>
<form method="post">
<table>
<tr><td>Database Host</td><td><input type="text" name="dbhost" value="<%= dbhost %>"></td>
<td>Example: localhost</td>
</tr>
<tr><td>Database Name</td><td><input type="text" name="dbname" value="<%= dbname %>"></td>
<td>Example: cl1p</td>
</tr>
<tr><td>Database Port</td><td><input type="text" name="dbport" value="<%= dbport %>"></td>
<td>Leave blank for default</td>
</tr>
<tr><td>Database Username</td><td><input type="text" name="dbuser"  value="<%= dbuser %>"></td>
<td>Username to access the database</td>
</tr>
<tr><td>Database Password</td><td><input type="text" name="dbpassword"  value="<%= dbpassword %>"></td>
<td>Password to access the database</td>
</tr>
<tr><td>Upload Storage Dir</td><td><input type="text" name="upload.dir"  value="<%= uploadDir %>"></td>
<td>Root folder that will contain all uploaded files</td></tr>
<tr><td>Log4j File</td><td><input type="text" name="log4j"  value="<%= log4j %>"></td>
<td>Location of log4j file to use. (Optional)</td></tr>
<tr><td colspan="3"><input type="submit" value="Submit"></td></tr>

<tr><td colspan="3"><span style="color:red;"><%= errMsg %></span><br><%= exception %><br/><%= msg %></td></tr>

</table>

</form>
<%
}
%>
</body>
</html>
