# base22
baseTable = 'd59nD71EcAt38aT24eCN06'
baseArray = {}
for i in range(22):
    baseArray[baseTable[i]] = i
# xor  = 50790   = (0000 0000 1100 0110 0110 0110)
# 22^5 = 5153632 = (0100 1110 1010 0011 0110 0000)
xor = 50790
# 野兽先辈的增益buff！
inc = 114514
# AV number range from 1000000 to 4000000
startNum = 1000000
endNum = 4000000
# DV number static string
dvStaticStr = 'DV t ACD Ne  '
# DV number dynamic index
dynIndex = [12, 11, 8, 4, 2]


def encrypt(x):
    x = (x ^ xor) + inc
    dvNum = list(dvStaticStr)
    for i in range(5):
        dvNum[dynIndex[i]] = baseTable[x // 22 ** i % 22]
    return ''.join(dvNum)


def decrypt(x):
    r = 0
    for i in range(5):
        r += baseArray[x[dynIndex[i]]] * 22 ** i
    return (r - inc) ^ xor

# 出题av号
testAv = 4123456
print(encrypt(testAv))
print(decrypt(encrypt(testAv)))
# av4123456 - DVetNACDANe80
# dv base64 RFZldE5BQ0RBTmU4MA==
# flag should be miniLCTF{RFZldE5BQ0RBTmU4MA==}

# file = open('av1000000To3999999.txt', 'w')
# for num in range(startNum, endNum):
#     file.write('av'+str(num)+' - '+encrypt(num)+'\n')


# print(encrypt(2598876))
# print(decrypt("DV3t3ACDnNe5C"))
# print(encrypt(3454756))
# print(decrypt("DV2tnACD3Ne8c"))
# print(encrypt(4000000))
# print(decrypt("DVeteACDDNedd"))
# print(encrypt(1000000))
# print(decrypt("DVDtCACD7Ne88"))
