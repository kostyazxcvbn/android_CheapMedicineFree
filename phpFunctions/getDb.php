<?php


include("config.php");

function checkVersion($currentVersion, $currentTableName){
	
	$sql = "SELECT `$currentTableName` FROM `app1_db_ver`";
	$result = mysql_query($sql);
	$buff=null;
	$lastVersion = mysql_result($result, $buff);
	
	if($lastVersion > $currentVersion){

		return $lastVersion;
	}
	else{
		return false;
	}
}

function getUpdates($db) {
	
	$currentVersion=preg_replace('/[^0-9]/','',$_GET["v"]);
	$tableNumber=preg_replace('/[^0-9]/','',$_GET["t"]);

	$sql = "SELECT `".$column3_tableName."` FROM `".$table3_appTables."` WHERE `".$column3_tableId."` like '".$tableNumber."'";
	$result = mysql_query($sql);
	$buff=null;
	$currentTableName = mysql_result($result, $buff);
	$resultVersion=checkVersion($currentVersion, $currentTableName);

	if($resultVersion==true){

			$text = "";
			$text .= "\nINSERT INTO `".$currentTableName."`(";
			
			$sql = "SHOW COLUMNS FROM `".$appPrefix.$currentTableName."`";
			$result = mysql_query($sql);
	    	for($i = 0; $i < mysql_num_rows($result); $i++) {
				$row = mysql_fetch_row($result);
				$text.= "`".$row[0]."`,";
				}
			$text = rtrim($text,",");
			$text .= ") "; 	
			echo $text;
					
			$sql2 = "SELECT * FROM `".$appPrefix.$currentTableName."`";
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
		
		
		$sql = "SELECT `".$currentTableName."` FROM `".$table2_databaseVersion."`";
		$result = mysql_query($sql);
	    $newDatabaseVersion = mysql_result($result, $buff);
		
		$text="";
		$text.= "UPDATE `".$appTable_databaseVersion."` SET `".$currentTableName."`=\"".$newDatabaseVersion."\";";
		echo $text;

	}
	else{
		
	}
	
}
getUpdates($dbh);

?>
