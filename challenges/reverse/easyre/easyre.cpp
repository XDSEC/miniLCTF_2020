#include<Windows.h>
#include<iostream>

int um()
{
    UINT64 dwBaseImage = (UINT64)GetModuleHandle(NULL);
	DWORD checksum = 0;
	for (int i = 0; i < 0x1853; i++)
	{
		checksum += *(PBYTE)(dwBaseImage + i);
	}
	return checksum;
}
void Test()
{
	int flagNum[24] = { 0x9,0x5,0xa,0x5,0x8,0x17,0x1,-3,0xf,0x15,0xe,0x1,-61,0xf,-5,0x1,-3,0xf,0x15,0x19};
	char hint[64] = { 0x50,0x6c,0x65,0x61,0x73,0x65,0x20,0x69,0x6e,0x70,0x75,0x74,0x20,0x79,0x6f,0x75,0x72,0x20,0x66,0x6c,0x61,0x67,0x3a,0x0,0x77,0x72,0x06f,0x6e,0x67,0x00,0x52,0x69,0x67,0x68,0x74 };
	char flag[256];
	int flagLen = 0;
	puts((char*)hint);
	std::cin >> flag;
	flagLen = 20;
	if (flagLen != 20)
	{
		puts((char*)hint + 24);
		exit(1);
	}
	for (int i = 0; i < flagLen; i++)
	{
		if (flag[i] != flagNum[i]+0x64)
		{
			puts((char*)hint + 24);
			exit(1);
		}
	}
	puts((char*)hint + 30);
}
int am()
{
	BOOL ret;
	CheckRemoteDebuggerPresent(GetCurrentProcess(), &ret);
	if (ret)
	{
		exit(1);
	}
	return 0;
}
int sum = um();
int main(void)
{
	int flag = am();
	if (sum == um())
	{
		Test();
		system("pause");
	}
	return 0;
}