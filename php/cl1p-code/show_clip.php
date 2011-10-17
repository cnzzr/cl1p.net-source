<?php
include("db.php");
// Database db.php
$sql = "desc Clip";
$result = mysql_query($sql, $dbh);
while($row = mysql_fetch_row($result))
{
print("$row[0] $row[1]<br>");
} 
?>