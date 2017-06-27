<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<style type="text/css">
	div{
		color: #222222;
	}
	.drug_name{
		font-size: 14px;
		font-weight: bold;
		text-align: center;
	}
	.drug_h1{
		font-size: 12px;
		font-weight: bold;
		padding-top: 6px; 
	}
	
	.drug_notice{
		font-size: 12px;
		text-align: justify;
	}
	select
	{
		width: 500;
		height: 200;
	}
</style>
</head>
<body bgcolor="#fafafa">
<div class="drug_notice"><center>
ИНСТРУКЦИЯ
<br/>
по применению лекарственного препарата</center>
</div>
<form action="addNew_analog.php" method="post">
<div class="drug_name">
	<input name="drug_name" type="text">
</div>

<center>
<table>
	<tr valign="top">
		<td>
			<div class="drug_notice">Аналог для: </div>
		</td>
		<td>
			<select multiple="true" name="selected_drug">
<?php
	include("../config.php");
	$sql = "select `_id`,`drug_name` from `app1_drugs` order by `drug_name`";
	$result=mysql_query($sql);
	while($row = mysql_fetch_array($result, MYSQL_ASSOC)){
		echo '<option value="'.$row["_id"].'">'.$row["drug_name"].'</option>';
	}
?>		
			</select>
			
		</td>
	</tr>
</table>


<center>


<hr/>
<table  width=100%">
	<tr>
		<td valign="top" width=50%">
<div class="drug_h1">Лекарственная форма и состав:</div>
<div class="drug_notice">
	<textarea name="lek_form" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Действующее вещество:</div>
<div class="drug_notice">
	<input name="deistv_veshestvo" type="text">
</div>
<div class="drug_h1">Фармакотерапевтическая группа:</div>
<div class="drug_notice">
	<textarea name="group" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Фармакологические свойства:</div>
<div class="drug_notice">
	<textarea name="svoistva" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Фармакокинетика:</div>
<div class="drug_notice">
	<textarea name="kinetika" cols="300" rows="4" style="width: 400"></textarea>
</div>	
<div class="drug_h1">Показания к применению:</div>
<div class="drug_notice">
	<textarea name="pokaz" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Противопоказания:</div>
<div class="drug_notice">
	<textarea name="protivopokaz" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Применение при беременности и в период лактации:</div>
<div class="drug_notice">
	<textarea name="berem" cols="300" rows="4" style="width: 400"></textarea>
</div>
		</td>
		<td valign="top" width=50%">

<div class="drug_h1">Способ применения и дозы:</div>
<div class="drug_notice">
	<textarea name="sposob" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Побочное действие:</div>
<div class="drug_notice">
	<textarea name="pobochn" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Передозировка:</div>
<div class="drug_notice">
	<textarea name="peredoz" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Взаимодействие с другими лекарственными ссредствами:</div>
<div class="drug_notice">
	<textarea name="drugie_lek" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Особые указания:</div>
<div class="drug_notice">
	<textarea name="ukazaniya" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Форма выпуска:</div>
<div class="drug_notice">
	<textarea name="forma" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Условия хранения:</div>
<div class="drug_notice">
	<textarea name="hranenie" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Срок годности:</div>
<div class="drug_notice">
	<textarea name="godnost" cols="300" rows="4" style="width: 400"></textarea>
</div>
<div class="drug_h1">Условия отпуска из аптек:</div>
<div class="drug_notice">
	<textarea name="otpusk" cols="300" rows="4" style="width: 400"></textarea>
</div>			
		</td>
	</tr>
</table>


<center><label>
<input name="add" type="submit" id="submit" value="Добавить">
</label></center>
</form>
</body>
</html>

<?php

if($_POST['add']){
	
function checkString($s){
	if($s=="" || $s==null){
		$s="Нет информации";
	};
	return $s;
}

$drug_name = htmlspecialchars ($_POST['drug_name']); 
$lek_form = htmlspecialchars ($_POST['lek_form']); 
$sostav = htmlspecialchars ($_POST['sostav']); 
$deistv_veshestvo = htmlspecialchars ($_POST['deistv_veshestvo']);
$notice = htmlspecialchars ($_POST['notice']); 
$group = htmlspecialchars ($_POST['group']); 
$svoistva = htmlspecialchars ($_POST['svoistva']); 
$kinetika = htmlspecialchars ($_POST['kinetika']);
$pokaz = htmlspecialchars ($_POST['pokaz']); 
$protivopokaz = htmlspecialchars ($_POST['protivopokaz']); 
$berem = htmlspecialchars ($_POST['berem']); 
$sposob = htmlspecialchars ($_POST['sposob']);
$pobochn = htmlspecialchars ($_POST['pobochn']); 
$peredoz = htmlspecialchars ($_POST['peredoz']);
$drugie_lek = htmlspecialchars ($_POST['drugie_lek']); 
$ukazaniya = htmlspecialchars ($_POST['ukazaniya']); 
$forma = htmlspecialchars ($_POST['forma']); 
$hranenie = htmlspecialchars ($_POST['hranenie']);
$godnost = htmlspecialchars ($_POST['godnost']); 
$otpusk = htmlspecialchars ($_POST['otpusk']); 

$drug_id=htmlspecialchars ($_POST['selected_drug']); 

setlocale(LC_ALL,'ru_RU.utf8');

$drug_name = mb_strtoupper($drug_name, "UTF-8"); 
$lek_form = nl2br(checkString($lek_form));
$sostav = nl2br(checkString($sostav));
$deistv_veshestvo = nl2br(checkString($deistv_veshestvo));
$notice = nl2br(checkString($notice)); 
$group = nl2br(checkString($group));
$svoistva = nl2br(checkString($svoistva));
$kinetika = nl2br(checkString($kinetika));
$pokaz = nl2br(checkString($pokaz));
$protivopokaz = nl2br(checkString($protivopokaz));
$berem = nl2br(checkString($berem));
$sposob = nl2br(checkString($sposob));
$pobochn = nl2br(checkString($pobochn));
$peredoz = nl2br(checkString($peredoz));
$drugie_lek = nl2br(checkString($drugie_lek));
$ukazaniya = nl2br(checkString($ukazaniya));
$forma = nl2br(checkString($forma));
$hranenie = nl2br(checkString($hranenie));
$godnost = nl2br(checkString($godnost));
$otpusk = nl2br(checkString($otpusk));

$drug_notice=file_get_contents("./drugInfo_template.html");

if(strpos($drug_name, ",")!=false){
	$drug_name_substr=explode(",", $drug_name);
	$drug_name_part1=$drug_name_substr[0];
	$drug_name_part2=mb_substr($drug_name_substr[1],0,4, "utf-8");
}
else{
	$drug_name_part1=$drug_name;
	$drug_name_part2="";
}

$drug_search=$drug_name_part1.$drug_name_part2;
$drug_notice=preg_replace("/v_drug_name/u", $drug_name_part1, $drug_notice);
$drug_notice=preg_replace("/v_lek_form/u", $lek_form, $drug_notice);
$drug_notice=preg_replace("/v_sostav/u", $sostav, $drug_notice);
$drug_notice=preg_replace("/v_deistv_veshestvo/u", $deistv_veshestvo, $drug_notice);
$drug_notice=preg_replace("/v_notice/u", $notice, $drug_notice);
$drug_notice=preg_replace("/v_group/u", $group, $drug_notice);
$drug_notice=preg_replace("/v_svoistva/u", $svoistva, $drug_notice);
$drug_notice=preg_replace("/v_kinetika/u", $kinetika, $drug_notice);
$drug_notice=preg_replace("/v_pokaz/u", $pokaz, $drug_notice);
$drug_notice=preg_replace("/v_protivopokaz/u", $protivopokaz, $drug_notice);
$drug_notice=preg_replace("/v_berem/u", $berem, $drug_notice);
$drug_notice=preg_replace("/v_sposob/u", $sposob, $drug_notice);
$drug_notice=preg_replace("/v_pobochn/u", $pobochn, $drug_notice);
$drug_notice=preg_replace("/v_peredoz/u", $peredoz, $drug_notice);
$drug_notice=preg_replace("/v_drugie_lek/u", $drugie_lek, $drug_notice);
$drug_notice=preg_replace("/v_ukazaniya/u", $ukazaniya, $drug_notice);
$drug_notice=preg_replace("/v_forma/u", $forma, $drug_notice);
$drug_notice=preg_replace("/v_hranenie/u", $hranenie, $drug_notice);
$drug_notice=preg_replace("/v_godnost/u", $godnost, $drug_notice);
$drug_notice=preg_replace("/v_otpusk/u", $otpusk, $drug_notice);



if($drug_notice!=false){

	$sql = "insert into `app1_analogs` (`analog_name`,`notice`, `find_name`, `drugs_id`) values('$drug_name','$drug_notice', '$drug_search', '$drug_id');";
	mysql_query($sql);
}
else{
	echo "Ошибка загрузки шаблона!"; 
}	
	
}

?>
