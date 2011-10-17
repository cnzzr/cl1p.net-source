<?php
include("cl1p.php");
include("session.php");
include("util.php");
session_start();

$KEY = $_SERVER["REQUEST_URI"];
$CL1P = cl1p_load_clip($dbh,$KEY);
$save_cl1p = $_REQUEST["save_clip"];
$CL1P_SESSION = cl1p_get_session();

// If nothing is set and nothing is being changed 
// for that KEY then all clear just go to the cl1p
if(!isset($CL1P) && !isset($save_cl1p){
  $CL1P = new  Cl1p();  
  include("../text.php");
  return;
}

   if(isset($save_cl1p)){
     
   }







?>