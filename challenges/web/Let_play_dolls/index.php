<?php
error_reporting(0);
if(isset($_GET['a'])){
    unserialize($_GET['a']);
}
else{
    highlight_file(__FILE__);
}

class foo1{
    public $var='';
    function __construct(){
        $this->var='phpinfo();';
    }
    function execute(){
        if(';' === preg_replace('/[^\W]+\((?R)?\)/', '', $this->var)) { 
            if(!preg_match('/header|bin|hex|oct|dec|na|eval|exec|system|pass/i',$this->var)){
                eval($this->var);
            }  
            else{
                die("hacked!");
            }  
        }

    }
    function __wakeup(){
        $this->var="phpinfo();";
    }
    function __desctuct(){
        echo '<br>desctuct foo1</br>';
    }
}
class foo2{
    public $var;
    public $obj;
    function __construct(){
        $this->var='hi';
        $this->obj=null;
    }
    function __toString(){
        $this->obj->execute();
        return $this->var;
    }
    function __desctuct(){
        echo '<br>desctuct foo2</br>';
    }
}
class foo3{
    public $var;
    function __construct(){
        $this->var="index.php";
    }
    function __destruct(){
        if(file_exists($this->var)){
            echo "<br>".$this->var."exist</br>";
        }
        echo "<br>desctuct foo3</br>";
    }
    function execute(){
        print("hi");
    }
}
