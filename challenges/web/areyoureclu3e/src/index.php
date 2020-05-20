<?php
    include "flag.php";//$flag="minilctf{****}";
    session_start();
    if (empty($_SESSION['uid'])) {
        include "loginForm.html";
    }
    else{
        echo '<h1>Hello, reclu3e!</h1>';
        $p=unserialize(isset($_GET["p"])?$_GET["p"]:"");
    }
?>
<?php
class person{
    public $name='';
    public $age=0;
    public $weight=0;
    public $height=0;
    private $serialize='';
    public function __wakeup(){
        if(is_numeric($this->serialize)){
            $this->serialize++;
        }
    }
    public function __destruct(){
        @eval('$s="'.$this->serialize.'";');
    }
}
