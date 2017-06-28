<?php
$host="localhost"; 
$database="database"; 
$user="user"; 
$pswd="password"; 

$appPrefix = "app_";

$table1_pairsDrugAnalog = $appPrefix."new_pairs";
$table2_databaseVersion = $appPrefix."db_ver";
$table3_appTables = $appPrefix."tables";

$column1_drugName = "drug_name";
$column1_analogName = "analog_name";
$column1_checkedByManager = "checked";

$column3_tableId = "id";
$column3_tableName = "table_name";

$appTable_databaseVersion = "db_ver";

$message_ok ="app_ok";
$message_error ="app_err";

$dbh = mysql_connect($host, $user, $pswd) or die($message_error);
mysql_select_db($database) or die($message_error);
mysql_query("set names 'utf8'");
?>