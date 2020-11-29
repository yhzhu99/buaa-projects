package miniplc0java;

import miniplc0java.analyser.NameType;
import miniplc0java.analyser.SymbolEntry;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class FunctionTable {
    List<Instruction> instructions;
    List<Token> params;
    String name;
    TokenType tokenType;
    long pos;
    int paramSoltNum;
    int varSoltNmum;
    int deep;
    /** 符号表 */
    List<SymbolEntry> symbolEntries;
    public FunctionTable(String name,long pos,TokenType tokenType) {
        this.name=name;//main 这种
        this.pos=pos;
        this.instructions = new ArrayList<>();
        this.params=new ArrayList<>();
        this.symbolEntries=new ArrayList<>();
        this.paramSoltNum=0;
        this.varSoltNmum=0;
        this.tokenType=tokenType;
    }

    public List<Instruction>  getInstructions(){
        return this.instructions;
    }

    public List<SymbolEntry> getSymbolEntries() {
        return symbolEntries;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getName() {
        return name;
    }

    public long getPos() {
        return pos;
    }

    public int getParamSoltNum() {
        return paramSoltNum;
    }

    public int getVarSoltNmum() {
        return varSoltNmum;
    }

    public int getReturnSoltNmum() {
        if (tokenType!=TokenType.VOID_KW)
            return 1;
        else return 0;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public void addSymbolEntry(SymbolEntry symbolEntry) {
        this.symbolEntries.add(symbolEntry);
        if(symbolEntry.getNametype()== NameType.Params)
            this.paramSoltNum++;
        else{
            int currentvarSoltNUm=this.symbolEntries.size()-this.paramSoltNum;
            if(currentvarSoltNUm>this.varSoltNmum)
                this.varSoltNmum=currentvarSoltNUm;
        }
    }

    public void outDeep(int deep) {
        int i=symbolEntries.size()-1;
        for(;i>=0;i--){
            if (symbolEntries.get(i).getNametype()==NameType.Params)
                return;
            if(symbolEntries.get(i).getDeep()==deep)
                symbolEntries.remove(i);
            else break;
        }
    }
}
