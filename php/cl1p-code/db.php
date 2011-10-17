<?php
include("config.php");
// Database db.php

if(!defined("CL1P_DB_CONNECT")){
   if(!$dbh = mysql_connect(CL1P_DB_HOST, CL1P_DB_USER, CL1P_DB_PASSWORD)){
	die("Can't connect to Database.");
   }
   if(!mysql_select_db(CL1P_DB_DATABASE)) die ("Can't select db");
   define("CL1P_DB_CONNECT");
}

function cl1p_close_db(){
   mysql_close($dbh); 
}

?>