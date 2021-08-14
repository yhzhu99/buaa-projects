#include<stdio.h>
#include<string.h>
FILE *in;
char c,st[1005],s[1005],l,r,x,y,z;
int i,j,top=0,pos=0,stat=1;
int p[150][150];
char find_last_sym(char s[]){
    int i,len=strlen(s);
    for(i=len-1;i>=0;i--){
        c=s[i];
        if(c=='+'||c=='*'||c=='('||c==')'||c=='i'||c=='#')return c;
    }
    return 0;
}
int main(int argc, char* argv[]){
    in=fopen(argv[1],"r");
    //in=fopen("test.in","r");
    //0: error, 1: larger, 2: equal, -1: smaller
    p['+']['+']=1,p['+']['*']=-1,p['+']['i']=-1,p['+']['(']=-1,p['+'][')']=1,p['+']['#']=1;
    p['*']['+']=1,p['*']['*']=1,p['*']['i']=-1,p['*']['(']=-1,p['*'][')']=1,p['*']['#']=1;
    p['i']['+']=1,p['i']['*']=1,p['i']['i']=0,p['i']['(']=0,p['i'][')']=1,p['i']['#']=1;
    p['(']['+']=-1,p['(']['*']=-1,p['(']['i']=-1,p['(']['(']=-1,p['('][')']=2,p['(']['#']=0;
    p[')']['+']=1,p[')']['*']=1,p[')']['i']=0,p[')']['(']=0,p[')'][')']=1,p[')']['#']=1;
    p['#']['+']=-1,p['#']['*']=-1,p['#']['i']=-1,p['#']['(']=-1,p['#'][')']=0,p['#']['#']=0;
    fscanf(in,"%s",s);
    int len=strlen(s);
    s[len++]='#';
    st[top++]='#';
    while(stat){
        //printf("         %d, %s\n",top,st);
        l=find_last_sym(st),r=s[pos];
        if(top==2&&st[1]=='E'&&r=='#'){
            stat=0;
        }
        else if(p[l][r]==0){
            printf("E\n");
            stat=0;
        }
        else if(p[l][r]==-1){
            printf("I%c\n",r);
            st[top++]=r;
            pos++;
        }
        else if(p[l][r]==1){
            if(st[top-1]=='i'){
                printf("R\n");
                st[top-1]='E';
            }
            else{
                if(top<=2){
                    printf("RE\n");
                    stat=0;
                }
                else{
                    x=st[top-3],y=st[top-2],z=st[top-1];
                    if(x=='E'&&y=='+'&&z=='E'){
                        printf("R\n");
                        st[--top]='\0',st[--top]='\0',st[--top]='\0';
                        st[top++]='E';
                    }
                    else if(x=='E'&&y=='*'&&z=='E'){
                        printf("R\n");
                        st[--top]='\0',st[--top]='\0',st[--top]='\0';
                        st[top++]='E';
                    }
                    else if(x=='('&&y=='E'&&z==')'){
                        printf("R\n");
                        st[--top]='\0',st[--top]='\0',st[--top]='\0';
                        st[top++]='E';
                    }
                    else{
                        printf("RE\n");
                        stat=0;
                    }
                }
            }
        }
        else if(p[l][r]==2){
            printf("I%c\n",r);
            st[top++]=r;
            pos++;
        }
    }
}