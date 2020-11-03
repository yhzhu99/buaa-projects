#include<stdio.h>
#include<string.h>
FILE *in;
char c;
int main(int argc, char* argv[]){
    //in=fopen(argv[1],"r");
    in=fopen("test.in","r");
    c=fgetc(in);
    while(c!=EOF){
        printf("%c",c);
        c=fgetc(in);
    }
}