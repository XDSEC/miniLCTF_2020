#include <stdio.h>
#include <stdlib.h>
char *s[2] = {"do you want the flag?\n", "really?\n"};
char expression[50];
int main() {
    srand(fgetc(fopen("/dev/urandom", "r")));
    for (int i = 0; i < 2; i++) {
        printf("%s", s[i]);
        if (scanf("%50s", expression) <= 0 || expression[0] != 'y')
            return -1;
    }
    int a = rand() % 100, b = rand() % 100;
    printf("then calculate %d+%d=", a, b);
    fflush(stdout);
    a += b;
    if (scanf("%d", &b) <= 0 || a != b) {
        printf("?\n");
        return -1;
    }
    fscanf(fopen("/flag", "r"), "%s", s);
    printf("okay, here you are\n%s", s);
}