<html>
<body>
<center>
    <h2>为了让学弟学妹们更好的认识各位师傅</h2><br>
    <h2>F1ag决定让学弟学妹们一睹师傅们的芳容</h2> <br>
    <h2>在下面输入id(均小写)即刻看到师傅们的 私 房 照</h2>
    <form action="" method="post">
        id:<input type="text" name="id"></br>
        <input type="submit" value="想好看谁了🐎">
    </form>
</center>
</body>
</html>

<?php
/**
 * Created by PhpStorm.
 * User: F1ag
 * Date: 2020/4/11
 * Time: 9:40
 */
error_reporting(0);
header("Content-type:text/html;charset=utf-8");
$conn = new mysqli('localhost','web','web','miniL');

if($conn->connect_error){
    die("连接失败,请联系出题人：".$conn->connect_error);
}

$id=$_POST['id'];

if(!isset($id)){
    echo "<center>e.g.:当你输入w1nd，将显示<br><img src='http://www.f1ag.com/wp-content/uploads/2020/04/F@G2S0VOQ1@9K37CK7EC.jpg' height=\"300\" width=\"300\"></center>";
    return;
}
if(preg_match("/f1ag|flag|f1@g|F1@g|F1@G|f1@G|fl@g|Fl@g|FL@G|fL@g|fl@G|Fl@G|fL@G|\./i",$id)){
	echo "<script>location.href='./flag.php'</script>";
	exit;
}

if(preg_match("/select|update|delete|drop|insert|where|alter|change|rename|\./i",$id)){
    die("hack");
}

if(strstr($id, "execute") || strstr($id, "prepare") || strstr($id, "deallocate")){
    die('almost there!'."<br>".'hint:strstr');
}


$sql="select * from user where id=('$id')";
 $query = $conn->multi_query($sql);
    if (!$query){
        echo "error ".$conn->errno." : ".$conn->error;
    } else {
        do{
            if ($result = $conn->store_result()){
                if(!$ans = $result->fetch_row()){
		    echo "<script>location.href='./xdsec.php'</script>";
		    exit;
                }
                //$ans = $result->fetch_row();
                do{
                    if (preg_match("/(http):\/\/([\w.]+\/?)\S*/", $ans[1]))echo "<center><img src='$ans[1]'></center>";
                    var_dump($ans);
                    echo "<br>";
                }while($ans = $result->fetch_row());
                $result->Close();
                if ($conn->more_results()){
                    echo "<hr>";
                }
            }
        }while($conn->next_result());
    }
    $conn->close();

?>
