<?php

function clean_temp(){
		$file=$_GET["f"];
		$dir = "temp";
		$flag_find=1;
		
		if ($handle = opendir($dir)) {
   			 while ($flag_find && (false != ($file_del = readdir($handle)))) { 

       		 	if ($file_del == $file) { 
            		unlink($dir."/".$file_del);
					$flag_find=0;
        		} 
    		}
			closedir($handle); 
		} 	
}

clean_temp();

?>
