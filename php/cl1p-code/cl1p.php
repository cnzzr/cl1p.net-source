<?php
include("db.php");
//print "DUDE";
class Cl1p{
   var $number;
   var $uri;
   var $value;
   var $title;
   var $keepFor;
   var $viewPassword;
   var $cleanDate;
   var $lastEdit;   
}

function cl1p_build_from_request(){
  $result = new Cl1p();
  cl1p_populate_from_request($result):
  return $result; 
}

function cl1p_populate_from_request($cl1p){
  $clip->value = $_REQUEST["cl1p_value"];
  $cl1p->title = $_REQUEST["cl1p_title"];
}

function cl1p_load_clip($dbh, $uri){

   // print $uri;
    $sql = sprintf("Select Number, Uri, Value, LastEdit, Password, ViewPassword, CleanDate, KeepFor, Title from Clip where Uri = '%s'", mysql_escape_string($uri));
//print $sql;
    $rs = mysql_query($sql,$dbh);
#       print "<table border='1'><tr><Td>Number</td><td>Uri</td><td>Value</td><td>LastEdit</td><td>Password</td><td>ViewPassword</td><td>CleanDate</td><td>KeepFor</td><td>Title</td></tr>";
       while($row = mysql_fetch_row($rs))
       {
	 $result = new Cl1p();
         $result->number = $row[0]; 
         $result->uri = $row[1];
         $result->value = $row[2];
         $result->title = $row[3];
         $result->keepFor = $row[4];
         $result->viewPassword = $row[5];
         $result->cleanDate = $row[6];
         $result->lastEdit = $row[7];
       }
       return $result;
#       print "</table>";
    
}
//$KEY = "/review/";
//cl1p_load_clip($dbh,$KEY);
?>