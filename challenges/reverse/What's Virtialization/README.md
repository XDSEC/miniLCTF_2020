## What's Virtialization
# 非原始版本
比赛的时候被选手发现有非预期解，因此将所有非预期通过简易移位hash做了检测。其余逻辑主体没有改动。仅仅只加了一个验证，不影响正常的思路和原始预期解。

另：
1、选手在写WP的时候说不懂本题的虚拟化是什么意思，详见ollvm的相关虚拟化手段, [https://github.com/obfuscator-llvm/obfuscator/wiki/Instructions-Substitution] 本题仅运用了VMP万用门作为虚拟化的引入。
2、流平整代码已作为注释在源码中给出，可编译后dbg感受一下流平整的难度（
