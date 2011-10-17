<?php

class Cl1pSession{
  var $userId;
  var $currentKey;
}

function cl1p_get_session(){
  $result = $_SESSION['cl1p_session'];
  if(!isset($result)){
    $result = new Cl1pSession();
    $_SESSION['cl1p_session'] = $result;
  }
  return $result;
}
?>