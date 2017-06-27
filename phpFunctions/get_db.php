<?php


include("../config.php");

function check_ver($current_ver, $tables){
	
	$sql = "SELECT `$tables` FROM `app1_db_ver`";
	$result = mysql_query($sql);
	$buff=null;
	$last_ver = mysql_result($result, $buff);
	
	if($last_ver > $current_ver){

		return $last_ver;
	}
	else{
		return false;
	}
}

function get_update($db) {
	
	$current_ver=preg_replace('/[^0-9]/','',$_GET["v"]);
	$table_num=preg_replace('/[^0-9]/','',$_GET["t"]);

	$sql = "SELECT `table_name` FROM `app1_tables` WHERE `_id` like $table_num";
	$result = mysql_query($sql);
	$buff=null;
	$tables = mysql_result($result, $buff);
	$result_ver=check_ver($current_ver, $tables);

	if($result_ver){

			$text = "";
			$text .= "\nINSERT INTO `".$tables."`(";
			
			$sql = "SHOW COLUMNS FROM `app1_".$tables."`";
			$result = mysql_query($sql);
	    	for($i = 0; $i < mysql_num_rows($result); $i++) {
				$row = mysql_fetch_row($result);
				$text.= "`".$row[0]."`,";
				}
			$text = rtrim($text,",");
			$text .= ") "; 	
			echo $text;
					
			$sql2 = "SELECT * FROM `app1_$tables`";
			$result2 = mysql_query($sql2);
			
			if(!$result2) {
				exit(mysql_error($db));
			}

			$text = "";
			for($i = 0; $i < mysql_num_rows($result2); $i++) {
				$row = mysql_fetch_row($result2);
			
				if($i == 0) $text .= "SELECT ";
				else  $text .= " UNION SELECT ";
				
				foreach($row as $v) {
					$text .= "'".$v."',";
				}
				$text = rtrim($text,","); 
				
				if($i > 10) {
					echo $text;
					$text = "";
				}
			}
		$text .= ";\n";
		echo $text;
		
		
		$sql = "SELECT `$tables` FROM `app1_db_ver`";
		$result = mysql_query($sql);
	    $db_new_ver = mysql_result($result, $buff);
		
		$text="";
		$text.= "UPDATE `db_ver` SET `".$tables."`=\"".$db_new_ver."\";";
		echo $text;

	}
	else{
		
	}
	
}
get_update($dbh);

?>
