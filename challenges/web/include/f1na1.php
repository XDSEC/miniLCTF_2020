<?php
	error_reporting(0);
	if(!$_GET[file]){echo '<a href="./f1na1.php?file=hint.php">See hint.</a>';}
	$file=$_GET['file'];
	if(strstr($file,"../")||stristr($file, "tp")||stristr($file,"input")||stristr($file,"data")){
		echo "Hacker!";
		exit();
	}
	include($file); 
?>