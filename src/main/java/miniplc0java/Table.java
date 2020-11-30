package miniplc0java;

import miniplc0java.analyser.NameType;
import miniplc0java.analyser.SymbolEntry;
import miniplc0java.error.AnalyzeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.util.Pos;

import java.util.ArrayList;
import java.util.List;

public class Table {
    List<FunctionTable> functionTables;
    List<Token> global;
    List<Instruction> instructions;
    /** 符号表 */
    List<SymbolEntry> symbolEntries;

    int outFuncNum;
    String[] outFunc={"getint","getchar","putint","putchar","putstr","putln",};

    public Table() throws AnalyzeError {
        this.functionTables = new ArrayList<>();
        this.global = new ArrayList<>();
        this.symbolEntries=new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.outFuncNum=0;
        initGlobal();
        init();

    }

    public List<Token> getGlobal() {
        return global;
    }

    private void initGlobal() {
        for (String s:outFunc){
            global.add(new Token(TokenType.IDENT,s,new Pos(-1,-1),new Pos(-1,-1)));
        }
    }
    private void init() throws AnalyzeError {
        this.symbolEntries.add(new SymbolEntry("getint",NameType.Proc,TokenType.IDENT,1,true,true,0));
        FunctionTable getint=new FunctionTable("getint",getGlobalId(new Token(TokenType.IDENT,"getint",new Pos(-1,-1),new Pos(-1,-1))),TokenType.UINT_LITERAL);
        this.functionTables.add(getint);

      /*  this.symbolEntries.add(new SymbolEntry("getdouble",NameType.Proc,TokenType.IDENT,1,true,true,0));
        FunctionTable getdouble=new FunctionTable("getdouble",-1);
        this.functionTables.add(getdouble);*/

        this.symbolEntries.add(new SymbolEntry("getchar",NameType.Proc,TokenType.IDENT,1,true,true,0));
        FunctionTable getchar=new FunctionTable("getchar",getGlobalId(new Token(TokenType.IDENT,"getchar",new Pos(-1,-1),new Pos(-1,-1))),TokenType.UINT_LITERAL);
        this.functionTables.add(getchar);

        this.symbolEntries.add(new SymbolEntry("putint",NameType.Proc,TokenType.IDENT,1,true,true,0));
        FunctionTable putint=new FunctionTable("putint",getGlobalId(new Token(TokenType.IDENT,"putint",new Pos(-1,-1),new Pos(-1,-1))),TokenType.VOID_KW);
        putint.getSymbolEntries().add(new SymbolEntry("",NameType.Params,TokenType.UINT_LITERAL,2,false,true,0));
        this.functionTables.add(putint);
    /*    this.symbolEntries.add(new SymbolEntry("putdouble",NameType.Proc,TokenType.IDENT,1,true,true,0));
        FunctionTable putdouble=new FunctionTable("putdouble",-1);
        this.functionTables.add(putdouble);*/

        this.symbolEntries.add(new SymbolEntry("putchar",NameType.Proc,TokenType.IDENT,1,true,true,0));
        FunctionTable putchar=new FunctionTable("putchar",getGlobalId(new Token(TokenType.IDENT,"putchar",new Pos(-1,-1),new Pos(-1,-1))),TokenType.VOID_KW);
        putchar.getSymbolEntries().add(new SymbolEntry("",NameType.Params,TokenType.UINT_LITERAL,2,false,true,0));
        this.functionTables.add(putchar);

        this.symbolEntries.add(new SymbolEntry("putstr",NameType.Proc,TokenType.VOID_KW,1,true,true,0));
        FunctionTable putstr=new FunctionTable("putstr",getGlobalId(new Token(TokenType.IDENT,"putstr",new Pos(-1,-1),new Pos(-1,-1))),TokenType.VOID_KW);
        putstr.getSymbolEntries().add(new SymbolEntry("",NameType.Params,TokenType.UINT_LITERAL,2,false,true,0));
        this.functionTables.add(putstr);

        this.symbolEntries.add(new SymbolEntry("putln",NameType.Proc,TokenType.VOID_KW,1,true,true,0));
        FunctionTable putln=new FunctionTable("putln",getGlobalId(new Token(TokenType.IDENT,"putln",new Pos(-1,-1),new Pos(-1,-1))),TokenType.VOID_KW);
        this.functionTables.add(putln);

        this.outFuncNum=6;
    }


    public List<FunctionTable> getFunctionTables() {
        return functionTables;
    }

    public SymbolEntry get(String name,int deep,Pos curPos) {
        int off=-1;

        if(deep>1){
            List<SymbolEntry> symbolEntries=this.functionTables.get(this.functionTables.size()-1).getSymbolEntries();
            String funcName=this.functionTables.get(this.functionTables.size()-1).getName();
            if(getSymbolEntry(funcName).getTokenType()!=TokenType.VOID_KW)
                off++;
            for(SymbolEntry symbolEntry:symbolEntries){
                off++;
                if(symbolEntry.getNameType()==NameType.Var)
                    off=0;
                if(symbolEntry.getName().equals(name)){
                    symbolEntry.setOff(off);
                    return symbolEntry;
                }
            }
        }
        off=0;
        for (SymbolEntry symbolEntry : symbolEntries) {
            off++;
            if (symbolEntry.getName().equals(name)) {
                symbolEntry.setOff(off);
                return symbolEntry;
            }
        }
        return null;
    }

    private SymbolEntry getSymbolEntry(String name) {
        for(SymbolEntry symbolEntry:symbolEntries){
            if(symbolEntry.getName().equals(name))
                return symbolEntry;
        }
        return null;
    }

    public int getOutFuncNum() {
        return outFuncNum;
    }

    public void put(SymbolEntry symbolEntry, int deep, Token token) {
        if(deep==1){
            this.addGlobal(token);
            this.symbolEntries.add(symbolEntry);
            if(symbolEntry.getNameType()== NameType.Proc){
                this.functionTables.add(new FunctionTable(symbolEntry.getName(),this.global.size()-1,symbolEntry.getTokenType()));
            }
        }
        else{
            this.functionTables.get(this.functionTables.size()-1).addSymbolEntry(symbolEntry);
        }
    }

    public void addInstruction(Instruction instruction,int deep) {
        if(deep==1)
            this.instructions.add(instruction);
        else
            this.functionTables.get(this.functionTables.size()-1).getInstructions().add(instruction);
    }


    public void addAllInstructions(List<Instruction> instructions,int deep) {
        if(deep==1)
            this.instructions.addAll(instructions);
        else
            this.functionTables.get(this.functionTables.size()-1).getInstructions().addAll(instructions);
    }

    public List<TokenType> getFunctionParamsType(Token nameToken) throws AnalyzeError {
        boolean flag=false;
        List<TokenType> tokenTypes=new ArrayList<>();
        for (FunctionTable func:functionTables) {
            if(func.getName().equals(nameToken.getValueString())){
                flag=true;
                for (SymbolEntry symbolEntry:func.getSymbolEntries()){
                    if(symbolEntry.getNameType()==NameType.Params){
                        tokenTypes.add(symbolEntry.getTokenType());
                    }
                    else break;
                }
                if(flag)
                    break;
            }
        }
        if(flag) return tokenTypes;
        throw new AnalyzeError(ErrorCode.ExpectFuncToken, nameToken.getStartPos());
    }

    public long getGlobalId(Token token) throws AnalyzeError {
        long id=0;
        for(Token s:global){
            if(s.getValueString().equals(token.getValueString())&&s.getTokenType()==token.getTokenType())
                return id;
            id++;
        }
        throw new AnalyzeError(ErrorCode.NotGlobal, token.getStartPos());
    }

/*    public TokenType getVarOrParamTokenType(Token token,int deep) throws AnalyzeError {
        SymbolEntry symbolEntry=get(token.getValueString(),deep,token.getStartPos());
        if(symbolEntry.getNametype()==NameType.Proc){
            throw new AnalyzeError(ErrorCode.AssignedToFunction, token.getStartPos());
        }
        else{
            return symbolEntry.getTokenType();
        }
    }*/

    public void addGlobal(Token token) {
        this.global.add(token);
    }

    public void generate() throws AnalyzeError {
        SymbolEntry symbolEntry=getSymbolEntry("main");
        if(symbolEntry==null||symbolEntry.getNameType()!=NameType.Proc){
            throw new AnalyzeError(ErrorCode.NoMainFunc,new Pos(0,0));
        }
        long id=getGlobalId(new Token(TokenType.IDENT,"main",new Pos(-1,-1),new Pos(-1,-1)));
        addGlobal(new Token(TokenType.IDENT,"_start",new Pos(-1,-1),new Pos(-1,-1)));
        FunctionTable functionTable=new FunctionTable("_start",this.global.size()-1,TokenType.VOID_KW);

        List<Instruction> instructions=functionTable.getInstructions();

        instructions.addAll(this.instructions);

        instructions.addAll(addstackllocInstruction("main"));

        instructions.add(new Instruction(Operation.call,getFunclId(new Token(TokenType.IDENT,"main",new Pos(-1,-1),new Pos(-1,-1)))));

        this.functionTables.add(0,functionTable);
    }

    public List<Instruction> addstackllocInstruction(String name) throws AnalyzeError {
        FunctionTable func=null;
        for (FunctionTable functionTable:functionTables){
            if(functionTable.getName().equals(name)){
                func=functionTable;
                break;
            }
        }
        List<Instruction> instructions=new ArrayList<>();
        instructions.add(new Instruction(Operation.stackalloc,(long)func.getReturnSoltNum()));

        return instructions;
    }

    public void outDeep(int deep) {
        this.functionTables.get(functionTables.size()-1).outDeep(deep);
    }

    public FunctionTable getNowFuncTable() {
        return this.functionTables.get(functionTables.size()-1);
    }

    public void setFuncReturn(String valueString, int deep, Pos startPos, TokenType tokenType) {
        get(valueString,deep,startPos).setTokenType(tokenType);
        functionTables.get(functionTables.size()-1).setTokenType(tokenType);
    }

    public boolean checkOutFunc(String valueString) {
        for (String s:outFunc){
            if(s.equals(valueString))
                return true;
        }
        return false;
    }

    public Long getFunclId(Token token) throws AnalyzeError {
        String valueString=token.getValueString();
        long ind=1;
        for (FunctionTable functionTable:functionTables){
            if(functionTable.getName().equals(valueString))
                return ind;
            ind++;
        }

        throw new AnalyzeError(ErrorCode.ExpectFuncToken, token.getStartPos());
    }

}
