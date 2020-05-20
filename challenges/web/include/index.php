<?php
error_reporting(0);
$id= $_COOKIE["ID"];
show_source(__FILE__);
if(unserialize($id) === "$admin")
{   
    include("next.php");
	$key = @$_REQUEST['key'];
	if(preg_match('/p@d/is',$key)){
		show_source("next.php");
	}
}
?>

<?php
//hint
$admin = 'm0ectf';
?>