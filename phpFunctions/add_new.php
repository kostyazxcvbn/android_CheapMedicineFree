<?php

include("../config.php");

$new_analog=$_GET["a"];
$new_drug=$_GET["d"];
$new_drug=preg_replace('/[^a-zA-Zа-яА-ЯёЁ0-9 ]/u','',$new_drug);
$new_analog=preg_replace('/[^a-zA-Zа-яА-ЯёЁу0-9 ]/u','',$new_analog);

$sql = "insert into `app1_new_pairs` (`drug_name`,`analog_name`) values('$new_drug','$new_analog')";
mysql_query($sql);

echo "app_ok";
?>