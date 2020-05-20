<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<body bgcolor="#000000">
<center>
    <img src='http://www.f1ag.com/wp-content/uploads/2020/04/unnamed.jpg'><br>
</center>
</body>
<?php
error_reporting(0);
$file=fopen("ip.txt","a");
$ip=$_SERVER["REMOTE_ADDR"];
echo "<center><font color=\"#FF0000\">你的ip是：$ip<br>已发送给管理员</font><br><h1><b><font color=\"#FF0000\">XDSEC成员-1</font></b></h1></center>";
fwrite($file, $ip."\n");
echo "<center><h1><font color=\"#FF0000\">当你看到它的时候就应该知道与本题flag无关了，所以不要继续尝试这个界面了</font></h1></center>";
?>

