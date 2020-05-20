<?php
class gitee {
    function __destruct() {
        echo '你用上了Git，可是，代价是什么呢（悲）';
    }
    function __construct($f) {
        $this->file = $f;
    }
}
class github {
    public $cmd = '';
    function __destruct() {
        if (preg_match("/[A-Za-oq-z0-9$]+/", $this->cmd))
            die("cerror");
        $blacklist = "~!@#%^&*()（）-_{}[]'\":,";
        foreach(str_split($blacklist) as $char) {
            echo $char;
            if(strchr($this->cmd, $char) !== false) 
                die('serror');
        }
        eval($this->cmd);
    }
    public function __wakeup() {
        if ($_SERVER["HTTP_X_REAL_IP"] !== '127.0.0.1') {
            // proxy_set_header X-Real-IP $remote_addr;
            die('across the great ... nope');
        }
    }
}
