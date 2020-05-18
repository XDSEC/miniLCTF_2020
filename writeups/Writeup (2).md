### 0x31 Testonly?
一道简单的安卓逆向题。把apk文件拖进jadx，定位到MainActivity处。
```java
package com.happy.testonly;

import a.b.a.m;
import android.app.Activity;
import android.os.Bundle;
import b.a.a.b;
import b.a.a.d;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MainActivity extends m {
    public MainActivity() {
        super();
    }

    public final void I() {
        ((Activity)this).getWindow().setFlags(8192, 8192);
        new Thread(new b(this)).start();
    }

    public final String J() {
        String v1;
        String v0 = "B08020D0FACFDAF81DB46890E4040EDBB8613DA5ABF038F8B86BD44525D2E27B26E22ACD06388112D8467FD688C79CC7EA83F27440577350E8168C2560368616";
        try {
            v1 = MainActivity.b(v0);//十六进制的SHA加密后的字节字符串，33d40461bb5dc676ac72cfdd51f68bc5f88668c7
        }
        catch(Exception v2) {
            v2.printStackTrace();
        }

        char[] v2_1 = new char[]{'U', '_', '\u0005', 'S', 'K', '`', '^', '\u0000', '\u0011', '=', 'f', 'W', 'P', '{', '\u0004', 'i', 'U', 'S', 'e', 'm', '7', 'U', '\u0017', '0', 'j', '\u0001', '(', '\u0007', 'a', '\u001F'};
        int v3;
        for(v3 = 0; v3 < v2_1.length; ++v3) {
            v2_1[v3] = ((char)(v2_1[v3] ^ MainActivity.a(v1.charAt(v3))));
        }

        return String.copyValueOf(v2_1).replace("flag", "minil");
    }

    public static int a(char arg1) {
        int v0 = arg1 < 128 ? arg1 : MainActivity.a(Character.toString(arg1));
        return v0;
    }

    public static int a(String arg2) {
        int v1 = 0;
        if(arg2.length() > 0) {
            v1 = arg2.getBytes(StandardCharsets.UTF_8)[0] & 255;
        }

        return v1;
    }

    public static String a(MainActivity arg1) {
        return arg1.J();
    }

    public static String b(String arg7) {
        MessageDigest v1_1;
        try {
            v1_1 = MessageDigest.getInstance("SHA");
        }
        catch(Exception v1) {
            System.out.println(v1.toString());
            v1.printStackTrace();
            return "";
        }

        byte[] v2 = v1_1.digest(arg7.getBytes("UTF-8")); //SHA加密
        StringBuffer v3 = new StringBuffer();
        int v4;
        for(v4 = 0; v4 < v2.length; ++v4) {
            int v5 = v2[v4] & 255;
            if(v5 < 16) {
                v3.append("0");
            }

            v3.append(Integer.toHexString(v5));
        }

        return v3.toString(); //返回值是sha加密的字节字符串
    }

    public void onCreate(Bundle arg2) {
        super.onCreate(arg2);
        ((m)this).setContentView(2131361820);
        this.I();
        this.run();
    }

    public final void run() {
        new Thread(new d(this)).start();
    }
}
```
看到public final string J()这个方法，先对一串字符调用b()做了一顿操作，然后与一个列表的值进行相应的异或，最后得到的是形如flag{}的字符串。再来看一下b()具体做了哪些操作，就是对目标字符串进行了一个SHA的hash,再将得到的字节流转化为十六进制的字符串。比如得到的字节流是b'\xa1\a2\a3\08'就会被转化成'a1a2a308'。然后每个字符与列表中的字符进行异或就能的到flag。
现在需要确定用的是SHA几。由于我们可以确定结果字符串是含有'flag'的。将它们分别于table的前四个字符异或，我们可以得到'33d4'。用这个开头，就可以确定使用的SHA算法了。在一个在线加密的网站进行尝试，确定了hash后转化为十六进制字符串的结果为'33d40461bb5dc676ac72cfdd51f68bc5f88668c7'
最后，使用以下脚本就能得到flag{},再将flag替换成minil就可以了。
```python
sha_str = "33d40461bb5dc676ac72cfdd51f68bc5f88668c7"
list1 =['U', '_', '\u0005', 'S', 'K', '`', '^', '\u0000', '\u0011', '=', 'f', 'W', 'P', '{', '\u0004', 'i', 'U', 'S', 'e', 'm', '7', 'U', '\u0017', '0', 'j', '\u0001', '(', '\u0007', 'a', '\u001F']
res = ''

for i in range(30):   	
	int1 = ord(sha_str[i])
	int2 = ord(list1[i])
	res +=chr(int1^int2)

print(res)
```

### 0x32 Khronos
依照惯例，拖进jadx看看。在mainActivity里发现了一个可疑的方法
```java
public native int check(String str);
```
查看用例，找到了下面一段代码：
```java
    public void onClick(View view) {
        Toast makeText;
        MainActivity mainActivity;
        String str;
        String trim = this.f666a.getText().toString().trim();
        int check = this.f667b.check(trim);
        if (check == 0) {
            mainActivity = this.f667b;
            str = "Wrong flag.";
        } else if (check == 1) {
            mainActivity = this.f667b;
            str = "Khronos is transcendental, your flag is not correct.";
        } else if (check == 2) {
            mainActivity = this.f667b;
            str = "Khronos is mysterious, wrong flag but is almost correct.";
        } else if (check == 3) {
            MainActivity mainActivity2 = this.f667b;
            makeText = Toast.makeText(mainActivity2, "Good job. The flag is " + trim, 1);
            makeText.show();
        } else {
            return;
        }
        makeText = Toast.makeText(mainActivity, str, 0);
        makeText.show();
    }
}
```
大概就是外部的这个C函数用来判断flag是否输入正确。
两个动态库文件，出于对x86汇编相对熟悉一些，就先分析了x86的.so，不知道另一个是不是差不多的思路。

![32.PNG](https://i.loli.net/2020/05/18/yNS7FTAVs5hB2ZX.png)

将libnative-lib.so拖入IDA，定位到check函数。分析流程大概就是v3指向的地址处保存了用户输入的flag。然后有长度和对于开头的判断，初步确定flag形式为minil{xxxxxxxxxxxxxxxxxxxxxxxxx}
之后的khronos函数，一共调用了（15-3+1）=13次，将'{'后的输入两两一组作为输入，得到的输出是一个uint_8被保存在uint_32的低位，形如0x000000ff。一共13个这样的uint_32依次保存在3个\__int_128(即v11~v13),还有一个保存在v14的低32位。

![33.PNG](https://i.loli.net/2020/05/18/x8CKMRGrdqmOwVX.png)

接下来看结果的比对，首先第一层就对于khronos函数返回值的判断，依次需要等于0xF1,0XB7,0X1A，0x52等等依次类推

![34.PNG](https://i.loli.net/2020/05/18/XuCLTYztBogkWN1.png)

用如下脚本复现khronos函数，先筛选出可能的两两一组的字符集
```python
def kronos(int_16):
    v1 = 0
    v2 = int_16
    v3 = 0
    while(v1 != 32):
        v41 = v3
        v5 = v2 & 0x88880C92;
        v6 = 0
        if(v5 != 0):
            
            while(v5 != 0):
                v6 ^= v5 & 1                           #v5二进制中1的个数，偶数的话v6为0，否则v6为1
                v5 >>= 1
    
        v7 = v6 ^ 2 * v2                           # v7=v6^(2*a2) 等价于a2左移一位，末尾补上v6
        v10 = v7 & 0x88880C92;
        v11 = 2 * v7;
        v12 = 0;
        if (v10 != 0):
            while(v10 !=0):
                v12 ^= v10 & 1
                v10 >>= 1
        v13 = v12 ^ v11
        v14 = v12 ^ 2 * v6
        v15 = v13 & 0x88880C92
        v8 = 0
        if ( v15 != 0):
            while(v15 !=0):
                v8 ^= v15 & 1
                v15 >>= 1
        v16 = v8 ^ 2 * v13
        v17 = v8 ^ 2 * v14
        v19 = v16 & 0x88880C92
        v20 = 2 * v16
        v21 = 0
        if(v19 !=0):
            while(v19 !=0):
                v21 ^= v19 & 1
                v19 >>= 1
        v22 = v21 ^ v20
        v23 = v21 ^ 2 * v17
        v24 = v22 & 0x88880C92
        v25 = 2 * v22
        v18 = 0
        if(v24 != 0):
            while(v24 != 0):
                v18 ^= v24 & 1
                v24 >>= 1
        v26 = v18 ^ v25
        v27 = v18 ^ 2 * v23
        v29 = v26 & 0x88880C92
        v30 = 2 * v26
        v31 = 0
        if(v29 != 0):
            while(v29 != 0):
                v31 ^= v29 & 1
                v29 >>= 1
        v32 = v31 ^ v30
        v33 = v31 ^ 2 * v27
        v34 = v32 & 0x88880C92
        v35 = 2 * v32
        v28 = 0
        if (v34 != 0):
            while(v34 !=0):
                v28 ^= v34 & 1
                v34 >>= 1
        v36 = v28 ^ v35
        v37 = v28 ^ 2 * v33
        v38 = v36 & 0x88880C92
        v39 = 2 * v36
        v4 = 0
        if(v38 != 0):
            while(v38 != 0):
                v4 ^= v38 & 1
                v38 >>= 1
        v2 = v4 ^ v39
        v3 = (v4 ^ 2 * v37) + 2 * v41
        v1 +=1
    
    res = (v4 ^ 2 * v37 + 2 * v41) & 0xff
    return res
_F1 = []
_B7 = []
_1A = []
_52 = []
_6B = []
_49 = []
_76 = []
_02 = []
_C1 = []
_D6 = []
_4E = []
_B6 = []
_E0 = []


for i in range(0x20,0x7F):
    for j in range(0X20,0X7F):
        res = kronos(i*256+j)
        if(res == 0xf1):
            _F1.append(chr(i)+chr(j))
            continue
        if(res == 0xB7):
            _B7.append(chr(i)+chr(j))
            continue
        if(res == 0x1A):
            _1A.append(chr(i)+chr(j))
            continue
        if(res == 0x52):
            _52.append(chr(i)+chr(j))
            continue
        if(res == 0x6B):
            _6B.append(chr(i)+chr(j))
            continue
        if(res == 0x49):
            _49.append(chr(i)+chr(j))
            continue
        if(res == 0x76):
            _76.append(chr(i)+chr(j))
            continue
        if(res == 0x02):
            _02.append(chr(i)+chr(j))
            continue
        if(res == 0xC1):
            _C1.append(chr(i)+chr(j))
            continue
        if(res == 0xD6):
            _D6.append(chr(i)+chr(j))
            continue
        if(res == 0x4E):
            _4E.append(chr(i)+chr(j))
            continue
        if(res == 0xB6):
            _B6.append(chr(i)+chr(j))
            continue
        if(res == 0xE0):
            _E0.append(chr(i)+chr(j))
        
for s in _F1:
    print(s,end=',')
print()
for s in _B7:
    print(s,end=',')
print()
for s in _1A:
    print(s,end=',')
print()
for s in _52:
    print(s,end=',')
print()
for s in _6B:
    print(s,end=',')
print()
for s in _49:
    print(s,end=',')
print()
for s in _76:
    print(s,end=',')
print()
for s in _02:
    print(s,end=',')
print()
for s in _C1:
    print(s,end=',')
print()
for s in _D6:
    print(s,end=',')
print()
for s in _4E:
    print(s,end=',')
print()
for s in _B6:
    print(s,end=',')
print()
for s in _E0:
    print(s,end=',')
print()
        
```
进一步做人工的筛查排除掉一部分不太可能出现在flag中的字符，得到如下的字符集（结尾肯定是 }）
第1行表示可能的第1和第2个字符的组合,第2行表示第3和第4个字符的组合，以此类推
```
FJ,Kh,L5,TU,Wu,Z6,aK,eS,pp
R0,S+,S-,T.,\6,aA,gp,id,pA,,wk,xY,xp,yF,yT,ym
C5,D0,J&,P7,Q.,R3,,fx,h|,iu,kX,mB,mk,nO,oT,pP,wG,yA,zN
A3,K9,dE,jn,lb,s_,sr,tg,wW
0L,1S,2Z,3C,4V,6F,6V,7d,EO,JT,La,MC,OS,Oz,Sq,Tr,ZM,a4,p2
4c,94,IS,Jc,Qk,Zn,_m,mf,t0,z6
0O,1R,4S,6G,JU,Lf,S_,Sv,Tc,Ux,Ze,a5,a7,p3,w4
cV,ew,fC,lv,rm,sp,te,uA,uG,vg,wU,wh,zw
69,6u,7S,9U,BB,BF,DX,RO,R_,Rb,UJ,da,en,fJ,gU,iC,qk,sD,tx,vC,vG,vW,vz,xA,xU
0M,0f,0v,1y,3o,?k,CR,HC,Kc,MT,Rm,XL,ZL,Zc
8E,Dj,Fh,Gq,Gu,Hn,Po,Q_,Ri,Sr,VJ,_t,c3,d2,m5,y4
0d,1D,1k,1m,3F,6n,7X,Co,Is,KZ,Ka,Lt,MB,MR,Nf,TJ,UE,YU,e9,f6
E}
```
然后偶然发现前三行能组成khrono，然后两边往中间填补，大概确定了
KhR0nOs_1S_mxxxxR_of_t1mE}
接下来的四个字符就通过第二个条件进行确定（前面的IDA代码中的第二个do while循环）
写出脚本
```python
def hash(str):
    res = 0
    for ch in str:
        res = (1331*res+ord(ch))&0xffffffff
    return res



a =['0O','1R','4S','6G','JU','Lf','S_','Sv','Tc','Ux','Ze','a5','a7','p3']
b = ['cV','ew','fC','lv','rm','sp','te','uA','uG','vg','wU','wh','zw']
for ch1 in a:
    for ch2 in b:
        str ="m"
        str+=ch1;
        str+=ch2;
        str+="R"
        res=hash("minil{KhR0nOs_1S_"+str+"_0f_t1mE}") #minil{KhR0nOs_1S_m4SteR_0f_t1mE}
        if(res &0x7fffffff == 1929691002):
            print(str)

```
## 0x4 Reverse
### Easyre
看hint,提醒我们注意反调试和crc校验。在IDA反汇编的代码中看到两处于Debuger有关的调用。一是IsDebuggerPresent，二是CheckRemoteDebuggerPresent。
拖到x64dbg里进行动态调试，在两处都下断点，发现第一处的断点不会执行到，而第二处的逻辑如下：

若CheckRemoteDebuggerPresent结果不为0，则不执行跳转，那么就会调用下面的exit导致程序退出。更改je为jne，也就是将0x74改为0x75

![41.PNG](https://i.loli.net/2020/05/18/r2PmsSVzB9pIbuN.png)

同样的，在下面有一个校验，如果之前的je->jne我们是直接在debuger里改的话，这里的校验不会对我们产生影响，我们还是能正常的执行。来到7FF60F141200。这里要求我们输入一个flag，看汇编还是不太清楚，就回到IDA看c代码。就是依次比较输入的字符和目标结果是否一致，如果不一样就提前退出。

![42.PNG](https://i.loli.net/2020/05/18/JM4Bu8UHQYEpAzD.png)

那么我们可以输入一个'a'（肯定是错的），然后把判断条件改成一致就退出，不一致就继续。循环20次，就可以得到flag啦。

![43.PNG](https://i.loli.net/2020/05/18/SquZ61ehBatrm9V.png)

## End
