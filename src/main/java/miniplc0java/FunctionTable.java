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
    int varSoltNum;
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
        this.varSoltNum=0;
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

    public int getVarSoltNum() {
        return varSoltNum;
    }

    public int getReturnSoltNum() {
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
        if(symbolEntry.getNameType()== NameType.Params)
            this.paramSoltNum++;
        else{
            int currentVarSoltNum=this.symbolEntries.size()-this.paramSoltNum;
            if(currentVarSoltNum>this.varSoltNum)
                this.varSoltNum=currentVarSoltNum;
        }
    }

    public void outDeep(int deep) {
        int i=symbolEntries.size()-1;
        for(;i>=0;i--){
            if (symbolEntries.get(i).getNameType()==NameType.Params)
                return;
            if(symbolEntries.get(i).getDeep()==deep)
                symbolEntries.remove(i);
            else break;
        }
    }
}
