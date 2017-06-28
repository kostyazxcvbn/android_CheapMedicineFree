<?php


include("config.php");

function getNewDrugsList() {
	
	$sql = "SELECT `".$column1_drugName."` FROM `".$table1_pairsDrugAnalog."` where `".$column1_checkedByManager."` like '1'";
	$result = mysql_query($sql);
	while ($row = mysql_fetch_array($result, MYSQL_BOTH)) {
    	echo $row[0]."\n";
	}
}

getNewDrugsList();

?>
