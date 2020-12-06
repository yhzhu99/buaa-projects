import java.util.ArrayList;
import java.util.List;

public class Table {
    List<FunctionTable> functionTables;
    List<FunctionTable> outFunctionTables=new ArrayList<>();
    List<Token> global;
    List<Instruction> instructions;
    /** 符号表 */
    List<SymbolEntry> symbolEntries;

    String[] outFunc={"getint","getdouble","getchar","putint","putdouble","putchar","putstr","putln",};

    public Table() throws AnalyzeError {
        this.functionTables = new ArrayList<>();
        this.global = new ArrayList<>();
        this.symbolEntries=new ArrayList<>();
        this.instructions = new ArrayList<>();

        initGlobal();
        init();

    }

    public List<Token> getGlobal() {
        return global;
    }

    private void initGlobal() {
        for (String s:outFunc){
            addGlobal(new Token(TokenType.IDENT,s,new Pos(-1,-1),new Pos(-1,-1)),true, NameType.Proc,null);
        }
    }
    private void init() throws AnalyzeError {
        this.symbolEntries.add(new SymbolEntry("getint",NameType.Proc, TokenType.INT_KW,1,true,true,0));
        FunctionTable getint=new FunctionTable("getint",getGlobalId(new Token(TokenType.IDENT,"getint",new Pos(-1,-1),new Pos(-1,-1))), TokenType.INT_KW);
        this.outFunctionTables.add(getint);

        this.symbolEntries.add(new SymbolEntry("getdouble",NameType.Proc, TokenType.DOUBLE_KW,1,true,true,0));
        FunctionTable getdouble=new FunctionTable("getdouble",getGlobalId(new Token(TokenType.IDENT,"getdouble",new Pos(-1,-1),new Pos(-1,-1))), TokenType.DOUBLE_KW);
        this.outFunctionTables.add(getdouble);

        this.symbolEntries.add(new SymbolEntry("getchar",NameType.Proc, TokenType.INT_KW,1,true,true,0));
        FunctionTable getchar=new FunctionTable("getchar",getGlobalId(new Token(TokenType.IDENT,"getchar",new Pos(-1,-1),new Pos(-1,-1))), TokenType.INT_KW);
        this.outFunctionTables.add(getchar);

        this.symbolEntries.add(new SymbolEntry("putint",NameType.Proc, TokenType.VOID_KW,1,true,true,0));
        FunctionTable putint=new FunctionTable("putint",getGlobalId(new Token(TokenType.IDENT,"putint",new Pos(-1,-1),new Pos(-1,-1))), TokenType.VOID_KW);
        putint.getSymbolEntries().add(new SymbolEntry("",NameType.Params, TokenType.INT_KW,2,false,true,0));
        this.outFunctionTables.add(putint);

        this.symbolEntries.add(new SymbolEntry("putdouble",NameType.Proc, TokenType.VOID_KW,1,true,true,0));
        FunctionTable putdouble=new FunctionTable("putdouble",getGlobalId(new Token(TokenType.IDENT,"putdouble",new Pos(-1,-1),new Pos(-1,-1))), TokenType.VOID_KW);
        putdouble.getSymbolEntries().add(new SymbolEntry("",NameType.Params, TokenType.DOUBLE_KW,2,false,true,0));
        this.outFunctionTables.add(putdouble);

        this.symbolEntries.add(new SymbolEntry("putchar",NameType.Proc, TokenType.VOID_KW,1,true,true,0));
        FunctionTable putchar=new FunctionTable("putchar",getGlobalId(new Token(TokenType.IDENT,"putchar",new Pos(-1,-1),new Pos(-1,-1))), TokenType.VOID_KW);
        putchar.getSymbolEntries().add(new SymbolEntry("",NameType.Params, TokenType.INT_KW,2,false,true,0));
        this.outFunctionTables.add(putchar);

        this.symbolEntries.add(new SymbolEntry("putstr",NameType.Proc, TokenType.VOID_KW,1,true,true,0));
        FunctionTable putstr=new FunctionTable("putstr",getGlobalId(new Token(TokenType.IDENT,"putstr",new Pos(-1,-1),new Pos(-1,-1))), TokenType.VOID_KW);
        putstr.getSymbolEntries().add(new SymbolEntry("",NameType.Params, TokenType.STRING_LITERAL,2,false,true,0));
        this.outFunctionTables.add(putstr);

        this.symbolEntries.add(new SymbolEntry("putln",NameType.Proc, TokenType.VOID_KW,1,true,true,0));
        FunctionTable putln=new FunctionTable("putln",getGlobalId(new Token(TokenType.IDENT,"putln",new Pos(-1,-1),new Pos(-1,-1))), TokenType.VOID_KW);
        this.outFunctionTables.add(putln);

    }


    public List<FunctionTable> getFunctionTables() {
        return functionTables;
    }

    public SymbolEntry get(String name, int deep, Pos curPos) {
   //     int off=-1;
        boolean flag=true;
        SymbolEntry s=null;
        for (SymbolEntry symbolEntry : symbolEntries) {
    //        off++;
            if (symbolEntry.getName().equals(name)) {
    //            symbolEntry.setOff(off);
                s=symbolEntry;
            }
        }
    //    off=-1;
        if(deep>1){
            List<SymbolEntry> symbolEntries=this.functionTables.get(this.functionTables.size()-1).getSymbolEntries();
            String funcname=this.functionTables.get(this.functionTables.size()-1).getName();
            if(getSymbolEntry(funcname).getTokenType()!= TokenType.VOID_KW);
    //            off++;
            for(SymbolEntry symbolEntry:symbolEntries){
     //           off++;
                if(flag&&symbolEntry.getNametype()==NameType.Var){
    //                off=0;
                    flag=false;
                }

                if(symbolEntry.getName().equals(name)){
      //              symbolEntry.setOff(off);
                    s=symbolEntry;
                }
            }
        }
        return s;
    }

    private SymbolEntry getSymbolEntry(String name) {
        for(SymbolEntry symbolEntry:symbolEntries){
            if(symbolEntry.getName().equals(name))
                return symbolEntry;
        }
        return null;
    }

    public void put(SymbolEntry symbolEntry, int deep, Token token) {
        if(deep==1){
            this.addGlobal(token,symbolEntry.isConstant(),symbolEntry.getNametype(),symbolEntry.getTokenType());
            this.symbolEntries.add(symbolEntry);
            symbolEntry.setOff(symbolEntries.size()-1);
            if(symbolEntry.getNametype()== NameType.Proc){
                this.functionTables.add(new FunctionTable(symbolEntry.getName(),this.global.size()-1,symbolEntry.getTokenType()));
            }
        }
        else{
            FunctionTable func=this.functionTables.get(this.functionTables.size()-1);
            func.addSymbolEntry(symbolEntry);
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

    public TokenType getFunctionReturnTy(String name){
        TokenType tokenType=null;
        for (FunctionTable func:functionTables) {
            if(func.getName().equals(name)){
                return func.getTokenType();
            }
        }
        return null;
    }
    public List<TokenType> getFunctionParamsType(Token nameToken) throws AnalyzeError {
        boolean flag=false;
        List<TokenType> tokenTypes=new ArrayList<>();
        for (FunctionTable func:functionTables) {
            if(func.getName().equals(nameToken.getValueString())){
                flag=true;
                for (SymbolEntry symbolEntry:func.getSymbolEntries()){
                    if(symbolEntry.getNametype()==NameType.Params){
                        tokenTypes.add(symbolEntry.getTokenType());
                    }
                    else break;
                }
                if(flag)
                    break;
            }
        }
        if(flag) return tokenTypes;

        for (FunctionTable func:outFunctionTables) {
            if(func.getName().equals(nameToken.getValueString())){
                flag=true;
                for (SymbolEntry symbolEntry:func.getSymbolEntries()){
                    if(symbolEntry.getNametype()==NameType.Params){
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

    public int getGlobalId(Token token) throws AnalyzeError {
        int id=0;
        for(Token s:global){
            if(s.getValueString().equals(token.getValueString())&&s.getTokenType()==token.getTokenType())
                return id;
            id++;
        }
        throw new AnalyzeError(ErrorCode.NotGlobal, token.getStartPos());
    }

    public boolean IsGlobal(Token token)  {
        for(Token s:global){
            if(s.getValueString().equals(token.getValueString())&&s.getTokenType()==token.getTokenType())
                return true;
        }
        return false;
    }
    public void addGlobal(Token token, boolean isConstant, NameType nameType, TokenType ty){
        token.setNameType(nameType);
        token.setTy(ty);
        this.global.add(token);
        if (isConstant)
            token.setIs_const(1);
        else
            token.setIs_const(0);
    }

    public void generate() throws AnalyzeError {
        SymbolEntry symbolEntry=getSymbolEntry("main");
        if(symbolEntry==null||symbolEntry.getNametype()!=NameType.Proc){
            throw new AnalyzeError(ErrorCode.notHaveMainFunc,new Pos(0,0));
        }
        List<TokenType> tokenTypes=getFunctionParamsType(new Token(TokenType.IDENT,"main",new Pos(-1,-1),new Pos(-1,-1)));
        if (tokenTypes.size()!=0)
            throw new AnalyzeError(ErrorCode.WrongParamsNum,new Pos(0,0));

        long id=getGlobalId(new Token(TokenType.IDENT,"main",new Pos(-1,-1),new Pos(-1,-1)));
        addGlobal(new Token(TokenType.IDENT,"_start",new Pos(-1,-1),new Pos(-1,-1)),symbolEntry.isConstant(),NameType.Proc, TokenType.VOID_KW);
        FunctionTable functionTable=new FunctionTable("_start",this.global.size()-1, TokenType.VOID_KW);

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
        for (FunctionTable functionTable:outFunctionTables){
            if(functionTable.getName().equals(name)){
                func=functionTable;
                break;
            }
        }
        List<Instruction> instructions=new ArrayList<>();
        instructions.add(new Instruction(Operation.stackalloc,(long)func.getReturnSoltNmum()));

        return instructions;
    }

    public void outDeep(int deep) {
        this.functionTables.get(functionTables.size()-1).outDeep(deep);
    }

    public FunctionTable getNowFuncTable() {
        return this.functionTables.get(functionTables.size()-1);
    }

    public void setFuncReturn(String valueString, int deep, Pos startPos, TokenType tokenType) {
        SymbolEntry symbolEntry=get(valueString,deep,startPos);
        symbolEntry.setTokenType(tokenType);
        symbolEntry.setInitialized(false);
        FunctionTable func=functionTables.get(functionTables.size()-1);
        func.setTokenType(tokenType);
        List<SymbolEntry> symbolEntries=func.getSymbolEntries();
        for (SymbolEntry s:symbolEntries)
            s.setOff(s.getOff()+1);
    }
    public void getFuncReturn() {
        FunctionTable functionTable=this.functionTables.get(this.functionTables.size()-1);
        get(functionTable.getName(),1,new Pos(-1,-1)).setInitialized(true);
    }

    public boolean isInitialized() {
        FunctionTable functionTable=this.functionTables.get(this.functionTables.size()-1);
        return get(functionTable.getName(),1,new Pos(-1,-1)).isInitialized;
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
