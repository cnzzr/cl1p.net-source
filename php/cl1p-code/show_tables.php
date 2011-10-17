<?php
include("db.php");
// Database db.php
$sql = "show tables";
$result = mysql_query($sql, $dbh);
while($row = mysql_fetch_row($result))
{
print("$row[0]<br>");
} 
?>