<?php
// 创建连接
$conn = new mysqli('localhost', 'ctf', 'ctf', 'users');

// 检测连接
if ($conn->connect_error) {
    die("数据库连接失败，请联系管理员。");
}
