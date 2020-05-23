#include <jni.h>
#include <string>
#include<android/log.h>

unsigned khronos(unsigned R);

unsigned secure(const char *);

//#define TAG "Khronos_HAPPY"
//#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_happy_khronos_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string welcome = "See Khronos";
    return env->NewStringUTF(welcome.c_str());
}

#pragma clang diagnostic push
#pragma ide diagnostic ignored "readability-magic-numbers"
extern "C" JNIEXPORT jint JNICALL
Java_com_happy_khronos_MainActivity_check(
        JNIEnv *env,
        jobject /* this */,
        jstring flag_j) {
    const char *flag = nullptr;

    // minil{KhR0nOs_1S_m4SteR_0f_t1mE}

    if (flag_j) {
        flag = env->GetStringUTFChars(flag_j, nullptr);
    }
    size_t len = strlen(flag);
    if (len != 32)return 0;
    if (strncmp(flag, "minil{", 6) != 0)return 0;
    if (flag[len - 1] != '}')return 0;

    int table[0x10] = {0};
    for (int i = 6; i < 32; i += 2) {
        table[(i / 2) - 3] = khronos((flag[i] << 8) + flag[i + 1]);
    }
    int god[] = {241, 183, 26, 82, 107, 73, 118, 2, 193, 214, 78, 182, 224,};
    for (int i = 0; i < 13; ++i) {
        if (god[i] != table[i])return 1;
    }
    unsigned answer = secure(flag);
    if (1929691002 != answer)return 2;
    return 3;
}
#pragma clang diagnostic pop

unsigned secure(const char *input) {
    unsigned int seed = 1331;
    unsigned int hash = 0;
    while (*input)hash = hash * seed + (*input++);
    return (hash & 0x7FFFFFFF);
}

struct Data {
    unsigned output;
    unsigned lastbit;
};

struct Data magicData;

void run(unsigned R, unsigned mask) {
    unsigned temp = (R << 1) & 0xffffffff;
    unsigned i = (R & mask) & 0xffffffff;
    unsigned lastbit = 0;
    while (i) {
        lastbit ^= (i & 1);
        i = i >> 1;
    }
    temp ^= lastbit;
    magicData.output = temp;
    magicData.lastbit = lastbit;
}

unsigned khronos(unsigned R) {
    unsigned mask = 0b10001000100010000000110010010010;
    unsigned magic = 0;
    for (unsigned i = 0; i < 32; ++i) {
        int tmp = 0;
        for (int j = 0; j < 8; ++j) {
            run(R, mask);
            R = magicData.output;
            tmp = (tmp << 1) ^ magicData.lastbit;
        }
        magic = (magic << 1) + tmp & 0xFF;
    }
    return magic;
}
