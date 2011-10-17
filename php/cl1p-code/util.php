<?php

function cl1p_db_to_date($s){
  $year = (int)substr($s,0,4);
  $month = (int)substr($s,4,2); // Add one month is base zero
  $month++;
  $day = (int)substr($s,6,2);
  $hour = (int)substr($s,8,2);
  $minute = (int)substr($s,10,2);

  $result = strtotime("$year-$month-$day $hour:$minute");
#  print strftime('%c',$result);
  return getdate($result);
}

function cl1p_date_to_db($date){
  $year = $date['year'];
  $month = $date['mon'];
  $day = $date['mday'];
  $hour = $date['hours'];
  $minute = $date['minutes'];
  // Month is base Zero. Original app was in Java.
  $month--;
 
  $month = cl1p_pad($month);
  $day = cl1p_pad($day);
  $hour = cl1p_pad($hour);
  $minute = cl1p_pad($minute);
  $result = $year . $month . $day . $hour . $minute;
  return $result;
}

function cl1p_pad($d){
  if($d > 9) return $d;
  return '0' . $d;
}
?>