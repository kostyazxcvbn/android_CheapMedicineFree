<?php

include("config.php");

$newAnalog=$_GET["a"];
$newDrug=$_GET["d"];
$newDrug=preg_replace('/[^a-zA-Zа-яА-ЯёЁ0-9 ]/u','',$newDrug);
$newAnalog=preg_replace('/[^a-zA-Zа-яА-ЯёЁу0-9 ]/u','',$newAnalog);

$sql = "insert into `".$table1_pairsDrugAnalog."` (`".$column1_drugName."`,`".$column1_analogName."`) values('".$newDrug."','".$newAnalog."')";
mysql_query($sql);

echo $message_ok;
?>