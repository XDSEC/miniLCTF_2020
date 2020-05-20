<?php
include 'classes.php';
if (!isset($_COOKIE['git'])) {
    ob_start();
    setcookie('git', base64_encode(serialize(new gitee('index.php'))));
    echo '<script>location.reload()</script>';
    ob_end_flush();
    die();
}
$comp = unserialize(base64_decode($_COOKIE['git']));
highlight_file($comp->file);
echo '<br>';
