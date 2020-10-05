#include<stdio.h>
#include<string.h>
char c='\0';
char token[105];
int num;
int symbol;
int flag=1;
FILE *in;
void clearToken(){
    memset(token,0,sizeof(token));
}
int isSpace(char c){
    if(c==' ')return 1;
    return 0;
}
int isNewline(char c){
    if(c=='\n')return 1;
    return 0;
}
int isTab(char c){
    if(c=='\t')return 1;
    return 0;
}
int isLetter(char c){
    if((c>='a'&&c<='z')||(c>='A'&&c<='Z'))return 1;
    return 0;
}
int isDigit(char c){
    if(c>='0'&&c<='9')return 1;
    return 0;
}
int isColon(char c){
    if(c==':')return 1;
    return 0;
}//10
int isPlus(char c){
    if(c=='+')return 1;
    return 0;
}//11
int isStar(char c){
    if(c=='*')return 1;
    return 0;
}//12
int isComma(char c){
    if(c==',')return 1;
    return 0;
}//13
int isLpar(char c){
    if(c=='(')return 1;
    return 0;
}//14
int isRpar(char c){
    if(c==')')return 1;
    return 0;
}//15
int isEqu(char c){
    if(c=='=')return 1;
    return 0;
}
void catToken(char c){
    int len=strlen(token);
    token[len]=c;
}
void retract(){}
int reserver(char s[]){
    if(strcmp(s,"BEGIN")==0)return 1;
    if(strcmp(s,"END")==0)return 2;
    if(strcmp(s,"FOR")==0)return 3;
    //if(strcmp(s,"DO")==0)return 4;
    if(strcmp(s,"IF")==0)return 5;
    if(strcmp(s,"THEN")==0)return 6;
    if(strcmp(s,"ELSE")==0)return 7;
    return 0;
}
int transNum(char s[]){
    int num=0;
    int i=0;
    while(s[i]){
        num=num*10+s[i]-'0';
        i++;
    }
    return num;
}
void error(){
    if(c!=EOF){
        strcpy(token,"ERROR");
        symbol=-1;
        printf("Unknown\n");
        flag=0;
    }
    else{
        symbol=-1;
        flag=0;
    }
}
void getsym(){
    clearToken();
    if(c=='\0'){
        c=fgetc(in);
        if(c==EOF){flag=0;}
    }//c=getchar();
    while(isSpace(c)||isNewline(c)||isTab(c)){
        c=fgetc(in);
        if(c==EOF)flag=0;
        //c=getchar();
    }
    if(isLetter(c)){
        while(isLetter(c)||isDigit(c)){
            catToken(c);
            c=fgetc(in);
            if(c==EOF)flag=0;
            //c=getchar();
        }
        retract();
        int resultValue=reserver(token);
        if(resultValue==0)symbol=8;
        else symbol=resultValue;
    }
    else if(isDigit(c)){
        while(isDigit(c)){
            catToken(c);
            c=fgetc(in);
            if(c==EOF)flag=0;
            //c=getchar();
        }
        retract();
        num=transNum(token);
        symbol=9;
    }
    else if(isColon(c)){
        c=fgetc(in);
        if(c==EOF)flag=0;
        //c=getchar();
        if(isEqu(c)){strcpy(token,":=");symbol=16;c='\0';}
        else{retract();strcpy(token,":");symbol=10;}
    }
    else if(isPlus(c)) {strcpy(token,"+");symbol=11;c='\0';}
    else if(isStar(c)) {strcpy(token,"*");symbol=12;c='\0';}
    else if(isComma(c)) {strcpy(token,",");symbol=13;c='\0';}
    else if(isLpar(c)) {strcpy(token,"(");symbol=14;c='\0';}
    else if(isRpar(c)) {strcpy(token,")");symbol=15;c='\0';}
    else {error();c='\0';}
    //printf("%s : %d\n",token,symbol);
    if(symbol==1){printf("Begin\n");}
    else if(symbol==2){printf("End\n");}
    else if(symbol==3){printf("\n");}
    //else if(symbol==4){printf("Do\n");}
    else if(symbol==5){printf("If\n");}
    else if(symbol==6){printf("Then\n");}
    else if(symbol==7){printf("Else\n");}
    else if(symbol==8){printf("Ident(%s)\n",token);}
    else if(symbol==9){printf("Int(%d)\n",num);}
    else if(symbol==10){printf("Colon\n");}
    else if(symbol==11){printf("Plus\n");}
    else if(symbol==12){printf("Star\n");}
    else if(symbol==13){printf("Comma\n");}
    else if(symbol==14){printf("LParenthesis\n");}
    else if(symbol==15){printf("RParenthesis\n");}
    else if(symbol==16){printf("Assign\n");}
}
int main(int argc, char* argv[]){
    in=fopen(argv[1],"r");
    while(flag){getsym();}
}