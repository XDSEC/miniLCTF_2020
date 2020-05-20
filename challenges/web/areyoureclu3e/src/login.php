<?php
    include "connection.php";
    mysqli_query($conn, "SET CHARACTER SET 'gbk'");

    $username=addslashes($_POST['username']);
    $password=addslashes($_POST['password']);
    $msg='';
    if(empty($username)){
        $msg='please post your username';
    }
    else{
        $sql="select * from users where username='$username'";
        $result=mysqli_query($conn,$sql);
        if($result){
            $row=mysqli_fetch_array($result,MYSQLI_ASSOC);
        }
        if(empty($row)){
            $msg='you are not reclu3e';
        }
        else{
            if($row['password']!==$password){
                $msg='I know you are reclu3e but you need post the right password';
            }
            else{
                session_start();
                $_SESSION['uid'] = $username;
                echo '<script>alert("Yes! you are reclu3e")</script>';
            }
        }
    }
    if(!empty($msg)){
        echo "<script>alert('$msg')</script>";
    }
    $conn->close();
    echo "<script type='text/javascript'>";  
    echo "window.location.href='index.php'";  
    echo "</script>";  