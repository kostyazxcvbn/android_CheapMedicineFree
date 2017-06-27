<?php


include("../config.php");

function get_new_list() {
	
	$sql = "SELECT `drug_name` FROM `app1_new_pairs` where `checked` like '1'";
	$result = mysql_query($sql);
	while ($row = mysql_fetch_array($result, MYSQL_BOTH)) {
    	echo $row[0]."\n";
	}
}

get_new_list();

?>
