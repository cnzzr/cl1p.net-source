<?php 
$loadcontent = "index.php"; 
$load_file = $_REQUEST['filename'];
if($load_file){
  $loadcontent = $load_file;
}
$create_file = $_REQUEST['create_file'];
if($create_file){
  $ourFileHandle = fopen($load_file, 'w') or die("can't open file");
  fclose($ourFileHandle);
}
$save_file = $_REQUEST['save_file'];
$savecontent = $_REQUEST['savecontent'];
if($save_file) { 
  $savecontent = stripslashes($savecontent); 
  $fp = @fopen($loadcontent, "w"); 
  if ($fp) { 
    fwrite($fp, $savecontent); 
    fclose($fp);
   
    print "<html></head><body>"; 
 
  } 
} 
$fp = @fopen($loadcontent, "r"); 
$loadcontent = fread($fp, filesize($loadcontent)); 
$lines = explode("n", $loadcontent);
$count = count($lines);
$loadcontent = htmlspecialchars($loadcontent); 
fclose($fp); 
for ($a = 1; $a < $count+1; $a++) {
  $line .= "$an";
}
?> 
<form method=post action="<?=$_SERVER[PHP_SELF]?>"> 
<input type="text" name="filename" value="">    
<input type="submit" name="load_file" value="Load">

</form>
<form method=post action="<?=$_SERVER[PHP_SELF]?>"> 
<input type="text" name="filename" value="">    
<input type="submit" name="create_file" value="Create">

</form><?=$load_file ?>
<form method=post action="<?=$_SERVER[PHP_SELF]?>"> 
<input type="hidden" name="filename" value="<?=$load_file ?>">    
<input type="submit" name="save_file" value="Save">    
<table width="100%" valign="top" border="0" cellspacing="1" cellpadding="1">
  <tr>
    <td width="97%" align="left" valign="top"><textarea style="text-align: left; padding: 0px; overflow: auto; border: 3px groove; font-size: 12px" name="savecontent" cols="150" rows="35" wrap="OFF"><?=$loadcontent?></textarea></td>
  </tr>
</table>
 
<br> 
<input type="submit" name="save_file" value="Save">    
</form>