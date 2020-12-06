import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;
    Table table;

    int deep=1;
    /** 当前偷看的 token */
    Token peekedToken = null;

    /** 下一个变量的栈偏移 */
    int nextOffset = 0;

    public Analyser(Tokenizer tokenizer) throws AnalyzeError {
        this.tokenizer = tokenizer;
        this.table=new Table();
    }

    public Table analyse() throws CompileError {
        analyseProgram();
        return table;
    }

    /**
     * 查看下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            Token token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     * 
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        Token token = peek();
        return token.getTokenType() == tt;
    }

    private boolean checkBinaryOperator() throws TokenizeError {
        Token token = peek();
        TokenType tokentype=token.getTokenType();
        return 
        tokentype== TokenType.PLUS||
        tokentype== TokenType.MINUS||
        tokentype== TokenType.MUL||
        tokentype== TokenType.DIV||
        tokentype==TokenType.EQ||
        tokentype==TokenType.NEQ||
        tokentype==TokenType.LT||
        tokentype==TokenType.GT||
        tokentype==TokenType.LE||
        tokentype==TokenType.GE;
    }
    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     * 
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        Token token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     * 
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        Token token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    private Token expectTy() throws CompileError {
        Token token = peek();
        TokenType tokentype=token.getTokenType();
        if ( tokentype== TokenType.INT_KW) {
            return next();
        } else {
            throw new ExpectedTokenError(getList(TokenType.INT_KW), next());
        }
    }

    private Token expectReturnTy() throws CompileError {
        Token token = peek();
        TokenType tokentype=token.getTokenType();
        if ( tokentype== TokenType.INT_KW||tokentype== TokenType.VOID_KW) {
            return next();
        } else {
            throw new ExpectedTokenError(getList(TokenType.INT_KW, TokenType.VOID_KW), next());
        }
    }
    private Token expectLiteral() throws CompileError {
        Token token = peek();
        TokenType tokentype=token.getTokenType();
        if ( tokentype== TokenType.UNIT_LITERAL||tokentype== TokenType.STRING_LITERAL||tokentype==TokenType.CHAR_LITERAL) {
            return next();
        } else {
            throw new ExpectedTokenError(getList(TokenType.UNIT_LITERAL, TokenType.STRING_LITERAL,TokenType.CHAR_LITERAL), next());
        }
    }


    /**
     * 获取下一个变量的栈偏移
     * 
     * @return
     */
    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

    private Instruction getVarOrParamAddress(Token token) throws AnalyzeError {
        String value=token.getValueString();
        Pos curPos=token.getStartPos();
        SymbolEntry symbolEntry=this.table.get(value,this.deep,token.getStartPos());
        if(symbolEntry==null)
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        Long off=symbolEntry.getOff();
        if(symbolEntry.getNametype()==NameType.Proc)
            throw new AnalyzeError(ErrorCode.CantGetProcAddress, curPos);
        else if(symbolEntry.getNametype()==NameType.Params){
            return new Instruction(Operation.arga,off);
        }else if(symbolEntry.getNametype()==NameType.Var) {
            if(symbolEntry.getDeep()==1)
                return new Instruction(Operation.globa,off);
            else
                return new Instruction(Operation.loca,off);
        }else
            throw new AnalyzeError(ErrorCode.ExpectNameToken, curPos);
    }

    private Instruction getStirngAddress(Token token) throws AnalyzeError {
        if (!this.table.IsGlobal(token))
            this.table.addGlobal(token,true,NameType.String,null);
        return new Instruction(Operation.push,(long)this.table.getGlobalId(token));
    }

    private Instruction getCharAddress(Token token) throws AnalyzeError {
     //   if (!this.table.IsGlobal(token))
     //       this.table.addGlobal(token,true,NameType.Char,null);
    //    return new Instruction(Operation.push,(long)this.table.getGlobalId(token));
        return new Instruction(Operation.push,(long)((char)token.getValue()&0xff));
    }
    /**
     * 添加一个符号
     *
     * @param isInitialized 是否已赋值
     * @param isConstant    是否是常量
     * @param curPos        当前 token 的位置（报错用）
     * @throws AnalyzeError 如果重复定义了则抛异常
     */
    private void addSymbol(Token token, NameType nameType, TokenType tokenType, int deep, boolean isInitialized, boolean isConstant, Pos curPos) throws AnalyzeError {
        String name=token.getValueString();
        SymbolEntry symbolEntry=this.table.get(name,deep,token.getStartPos());
        if (symbolEntry != null&&symbolEntry.getDeep()==deep) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        }
        else{
            this.table.put(new SymbolEntry(name,nameType,tokenType,deep,isConstant, isInitialized, getNextVariableOffset()),deep,token);
        }
    }

    /**
     * 设置符号为已赋值
     * 

     * @throws AnalyzeError 如果未定义则抛异常
     */
    private void declareSymbol(Token token) throws AnalyzeError {
        String name=token.getValueString();
        Pos curPos=token.getStartPos();
        SymbolEntry entry = this.table.get(name,this.deep,token.getStartPos());
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        }else if(entry.getNametype()==NameType.Proc){
            throw new AnalyzeError(ErrorCode.AssignedToFunction, curPos);
        }else if(entry.isConstant()){
            throw new AnalyzeError(ErrorCode.AssignToConstant, curPos);
        }
        else {
            entry.setInitialized(true);
        }
    }

    /**
     * 获取变量在栈上的偏移
     * 

     * @return 栈偏移
     * @throws AnalyzeError
     */
    private int getOffset(Token token) throws AnalyzeError {
        String name=token.getValueString();
        Pos curPos=token.getStartPos();
        SymbolEntry entry = this.table.get(name,this.deep,curPos);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.getStackOffset();
        }
    }

    /**
     * 获取变量是否是常量
     * 

     * @return 是否为常量
     * @throws AnalyzeError
     */
    private boolean isConstant(Token token) throws AnalyzeError {
        String name=token.getValueString();
        Pos curPos=token.getStartPos();
        SymbolEntry entry = this.table.get(name,this.deep,curPos);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.isConstant();
        }
    }

    private void expcetNotConstant(Token token) throws AnalyzeError {
        String name=token.getValueString();
        Pos curPos=token.getStartPos();
        SymbolEntry entry = this.table.get(name,this.deep,curPos);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else if(entry.isConstant()) {
            throw new AnalyzeError(ErrorCode.AssignToConstant, curPos);
        }
    }
     /**
     * 获取变量是否已经初始化
     * 

     * @return 是否已经初始化
     * @throws AnalyzeError
     */
    private boolean isInitialized(Token token) throws AnalyzeError {
        String name=token.getValueString();
        Pos curPos=token.getStartPos();
        SymbolEntry entry = this.table.get(name,this.deep,curPos);
        if(entry==null){
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        }
        else if (entry.isInitialized) {
            return true;
        }else {
            throw new AnalyzeError(ErrorCode.NotInitialized, curPos);
        }
    }

    /**
     * <程序> ::= 'begin'<主过程>'end'
     */
    //
    private void analyseProgram() throws CompileError {
        while(check(TokenType.EOF)==false){
            if(check(TokenType.FN_KW)){
                analyseFunction();
            }else if(check(TokenType.LET_KW)||check(TokenType.CONST_KW)){
                this.table.addAllInstructions(analyseDeclStmt(),this.deep);
            }else{
                throw new ExpectedTokenError(getList(TokenType.FN_KW, TokenType.LET_KW, TokenType.CONST_KW), next());
            }
        }
        expect(TokenType.EOF);
    }
//
    private void analyseFunction() throws CompileError {
        expect(TokenType.FN_KW);
        Token nameToken = expect(TokenType.IDENT);
        List<Instruction> instructions;
        addSymbol(nameToken,NameType.Proc, TokenType.VOID_KW,this.deep,true,true,nameToken.getStartPos());
        expect(TokenType.L_PAREN);
        if(check(TokenType.R_PAREN)==false)
            analyseFunctionParamList();
        expect(TokenType.R_PAREN);
        expect(TokenType.ARROW);
        Token ty=expectReturnTy();
        if(ty.getTokenType()!= TokenType.VOID_KW){
            this.table.setFuncReturn(nameToken.getValueString(),this.deep,nameToken.getStartPos(),ty.getTokenType());
        }
        instructions=new ArrayList<>();

        instructions.addAll(analyseBlockStmt());
        instructions.add(new Instruction(Operation.ret));
        this.table.addAllInstructions(instructions,this.deep+1);
        if (!this.table.isInitialized())
            throw new AnalyzeError(ErrorCode.NotAllRoutesReturn, nameToken.getStartPos());
    }

    private List<Instruction> analyseDeclStmt() throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        if(check(TokenType.LET_KW)){
            instructions.addAll(analyseLet());
        }else if(check(TokenType.CONST_KW)){
            instructions.addAll(analyseConst());
        }else{
            throw new ExpectedTokenError(getList(TokenType.LET_KW, TokenType.CONST_KW), next());
        }
        return instructions;
    }

    private List<TokenType> getList(TokenType... tokenTypes) {
        List<TokenType> t=new ArrayList<>();
        for (TokenType tokenType:tokenTypes)
            t.add(tokenType);
        return t;
    }

    private List<Instruction> analyseLet() throws CompileError {
        List <Instruction> instructions=new ArrayList<>();
        expect(TokenType.LET_KW);
        Token nameToken = expect(TokenType.IDENT);
        expect(TokenType.COLON);
        Token ty=expectTy();
        if(nextIf(TokenType.ASSIGN)!=null){
            addSymbol(nameToken,NameType.Var,ty.getTokenType(),this.deep,true,false,nameToken.getStartPos());
            //获得变量地址
            instructions.add(getVarOrParamAddress(nameToken));
            instructions.addAll(analyseExpr());
            instructions.addAll(OperatorTree.addAllReset());
            instructions.add(new Instruction(Operation.store_64));
        }
        else if (this.deep==1)
            addSymbol(nameToken,NameType.Var,ty.getTokenType(),this.deep,true,false,nameToken.getStartPos());
        else
            addSymbol(nameToken,NameType.Var,ty.getTokenType(),this.deep,false,false,nameToken.getStartPos());
        expect(TokenType.SEMICOLON);
        return instructions;
    }


    private List<Instruction> analyseConst() throws CompileError {
        List <Instruction> instructions=new ArrayList<>();
        expect(TokenType.CONST_KW);
        Token nameToken = expect(TokenType.IDENT);
        expect(TokenType.COLON);
        Token ty=expectTy();
        addSymbol(nameToken,NameType.Var,ty.getTokenType(),this.deep,true,true,nameToken.getStartPos());
        expect(TokenType.ASSIGN);
        //获得变量地址
        instructions.add(getVarOrParamAddress(nameToken));
        instructions.addAll(analyseExpr());
        instructions.addAll(OperatorTree.addAllReset());
        instructions.add(new Instruction(Operation.store_64));
        expect(TokenType.SEMICOLON);
        return instructions;
    }

    private void analyseFunctionParamList() throws CompileError {
        analyseFunctionParam();
        while (nextIf(TokenType.COMMA) != null) {
            analyseFunctionParam();
        }
    }

    private void analyseFunctionParam() throws CompileError {
        Token token=nextIf(TokenType.CONST_KW);
        Token nameToken=expect(TokenType.IDENT);
        expect(TokenType.COLON);
        Token ty=expectTy();
        if (token==null)
            addSymbol(nameToken,NameType.Params,ty.getTokenType(),this.deep+1,true,false,nameToken.getStartPos());
        else
            addSymbol(nameToken,NameType.Params,ty.getTokenType(),this.deep+1,true,true,nameToken.getStartPos());
    }


    private List<Instruction> analyseBlockStmt() throws CompileError {
        this.deep++;
        List<Instruction> instructions=new ArrayList<>();
        expect(TokenType.L_BRACE);
        while (check(TokenType.R_BRACE)==false){
            if(check(TokenType.LET_KW)||check(TokenType.CONST_KW)){
                instructions.addAll(analyseDeclStmt());
            }
            else if(check(TokenType.IF_KW)){
                instructions.addAll(analyseIfStmt());
            }
            else if(check(TokenType.WHILE_KW)){
                instructions.addAll(analyseWhileStmt());
            }//break continue
            else if(check(TokenType.RETRUN_KW)){
                instructions.addAll(analyseReturnStmt());
            }
            else if(check(TokenType.BREAK_KW)){
                instructions.addAll(analyseBreakStmt());
            }
            else if(check(TokenType.CONTINUE_KW)){
                instructions.addAll(analyseContinueStmt());
            }
            else if(check(TokenType.L_BRACE)){
                instructions.addAll(analyseBlockStmt());
            }
            else if(check(TokenType.SEMICOLON)){
                instructions.addAll(analyseEmptyStmt());
            }
            else{
                instructions.addAll(analyseExpr());
                expect(TokenType.SEMICOLON);
                instructions.addAll(OperatorTree.addAllReset());
            }
        }
        expect(TokenType.R_BRACE);
        this.table.outDeep(this.deep);
        this.deep--;
        return instructions;
    }

    private List<Instruction> analyseBreakStmt() throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        expect(TokenType.BREAK_KW);
        instructions.add(new Instruction(Operation.break_kw,(long)0));
        expect(TokenType.SEMICOLON);
        return instructions;
    }

    private List<Instruction> analyseContinueStmt() throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        expect(TokenType.CONTINUE_KW);
        instructions.add(new Instruction(Operation.continue_kw,(long)0));
        expect(TokenType.SEMICOLON);
        return instructions;
    }
    private List<Instruction> analyseIfStmt() throws CompileError {
        expect(TokenType.IF_KW);
        BooleanTree booleanTree;
        ConditionTree conditionTree=new ConditionTree();
        booleanTree=analyseBlooeanExpr();
        booleanTree.setTrueInstructions(analyseBlockStmt());
        conditionTree.add(booleanTree);
        while(check(TokenType.ELSE_KW)){
            expect(TokenType.ELSE_KW);
            if(check(TokenType.IF_KW)){
                expect(TokenType.IF_KW);
                booleanTree=analyseBlooeanExpr();
                booleanTree.setTrueInstructions(analyseBlockStmt());
                conditionTree.add(booleanTree);
            }
            else{
                booleanTree=new BooleanTree();
                List<Instruction> instructions=new ArrayList<>();
                booleanTree.setInstructions(instructions);
                booleanTree.setOffset(new Instruction(Operation.br,(long)0));
                booleanTree.setTrueInstructions(analyseBlockStmt());
                conditionTree.add(booleanTree);
                break;
            }   
        }
        return conditionTree.generate();
    }

    private List<Instruction> analyseWhileStmt() throws CompileError {
        WhileTree whileTree=new WhileTree();
        expect(TokenType.WHILE_KW);
        BooleanTree booleanTree=analyseBlooeanExpr();
        booleanTree.setTrueInstructions(analyseBlockStmt());
        whileTree.setBooleanTree(booleanTree);
        return whileTree.generate();
    }

    private List<Instruction> analyseReturnStmt() throws CompileError {
        Token token=expect(TokenType.RETRUN_KW);
        List<Instruction> instructions=new ArrayList<>();
        if(check(TokenType.SEMICOLON)==false){
            if (this.table.getNowFuncTable().getTokenType()== TokenType.VOID_KW)
                throw new AnalyzeError(ErrorCode.WrongReturn, token.getStartPos());
            instructions.add(new Instruction(Operation.arga,(long)0));
            instructions.addAll(analyseExpr());
            instructions.addAll(OperatorTree.addAllReset());
            instructions.add(new Instruction(Operation.store_64));
            this.table.getFuncReturn();
        }
        instructions.add(new Instruction(Operation.ret));
        expect(TokenType.SEMICOLON);
        return instructions;
    }

    private List<Instruction> analyseEmptyStmt() throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        expect(TokenType.SEMICOLON);
        return instructions;
    }
    private List<Instruction> analyseExpr() throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        if(check(TokenType.MINUS)){
            instructions.addAll(analyseNegateExpr());
        }
        else if(check(TokenType.IDENT)){
            Token nameToken = expect(TokenType.IDENT);

            if (check(TokenType.L_PAREN)){
                instructions.addAll(analyseCallExpr(nameToken));
            }
            else if(check(TokenType.ASSIGN)){
                instructions.addAll(analyseAssignExpr(nameToken));
            }
            else{
                instructions.addAll(analyseIdentExpr(nameToken));
            }
        }
        else if(check(TokenType.L_PAREN)){
            instructions.addAll(analyseGroupExpr());
        }
        else{
            instructions.addAll(analyseLiteralExpr());
        }

        if(checkBinaryOperator()){
            instructions.addAll(analyseOperatorExpr());
        }
        else if(check(TokenType.AS_KW)){
            instructions.addAll(analyseAsExpr());//没有语句
        }
        return instructions;
    }

    private List<Instruction> analyseNegateExpr() throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        Token token=expect(TokenType.MINUS);
        instructions.addAll(OperatorTree.getNewOperator(TokenType.NEG));
        instructions.addAll(analyseExpr());
        return instructions;
    }

    private List<Instruction> analyseCallExpr(Token nameToken) throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        List<TokenType> tokenTypes=this.table.getFunctionParamsType(nameToken);
        instructions.addAll(this.table.addstackllocInstruction(nameToken.getValueString()));

        expect(TokenType.L_PAREN);
        if(check(TokenType.R_PAREN)==false&&tokenTypes.size()>0)
            instructions.addAll(analyseCallParamList(tokenTypes));
        else if(check(TokenType.R_PAREN)&&tokenTypes.size()==0){ }
        else{
            Token token=peek();
            throw new AnalyzeError(ErrorCode.WrongParamsNum, token.getStartPos());
        }

        expect(TokenType.R_PAREN);

        if(this.table.checkOutFunc(nameToken.getValueString())){
            instructions.add(new Instruction(Operation.callname,(long)this.table.getGlobalId(nameToken)));
        }else
            instructions.add(new Instruction(Operation.call,this.table.getFunclId(nameToken)));
        return instructions;
    }


    private List<Instruction> analyseCallParamList(List<TokenType> tokenTypes) throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        instructions.addAll(OperatorTree.getNewOperator(TokenType.L_PAREN));
        instructions.addAll(analyseExpr());
        instructions.addAll(OperatorTree.getNewOperator(TokenType.R_PAREN));
      //  instructions.addAll(OperatorTree.addAllReset());
        int i;
        for(i=1;i<tokenTypes.size();i++){
            if(check(TokenType.COMMA)){
                expect(TokenType.COMMA);
                instructions.addAll(OperatorTree.getNewOperator(TokenType.L_PAREN));
                instructions.addAll(analyseExpr());
                instructions.addAll(OperatorTree.getNewOperator(TokenType.R_PAREN));
            //    instructions.addAll(OperatorTree.addAllReset());
            }
            else{
                Token nameToken=next();
                throw new AnalyzeError(ErrorCode.WrongParamsNum, nameToken.getStartPos());
            }
        }
        return instructions;
        //没有判断类型合法性
    }

    private List<Instruction> analyseAssignExpr(Token nameToken) throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        expcetNotConstant(nameToken);
        instructions.add(getVarOrParamAddress(nameToken));
        expect(TokenType.ASSIGN);
        instructions.addAll(analyseExpr());
        instructions.addAll(OperatorTree.addAllReset());
        instructions.add(new Instruction(Operation.store_64));
        declareSymbol(nameToken);
        return instructions;
    }


    private List<Instruction> analyseGroupExpr() throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        expect(TokenType.L_PAREN);
        instructions.addAll(OperatorTree.getNewOperator(TokenType.L_PAREN));
        instructions.addAll(analyseExpr());
        expect(TokenType.R_PAREN);
        instructions.addAll(OperatorTree.getNewOperator(TokenType.R_PAREN));
        return instructions;
    }


    private List<Instruction> analyseIdentExpr(Token nameToken) throws CompileError {
        isInitialized(nameToken);
        List<Instruction> instructions=new ArrayList<>();
        instructions.add(getVarOrParamAddress(nameToken));
        instructions.add(new Instruction(Operation.load_64));
        return instructions;
    }

    private List<Instruction> analyseLiteralExpr() throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        Token nameToken=expectLiteral();
        if(nameToken.getTokenType()== TokenType.STRING_LITERAL){
            instructions.add(getStirngAddress(nameToken));
        }else if(nameToken.getTokenType()== TokenType.CHAR_LITERAL) {
            //addSymbol(nameToken, NameType.Char, TokenType.CHAR_LITERAL, 1, true, true, nameToken.getStartPos());
            instructions.add(getCharAddress(nameToken));
        }
        else if(nameToken.getTokenType()== TokenType.UNIT_LITERAL){
            instructions.add(new Instruction(Operation.push, (long)nameToken.getValue()));
        }
        return instructions;
    }

    private List<Instruction> analyseOperatorExpr() throws CompileError {
        List<Instruction> instructions=new ArrayList<>();
        Token opetator=next();
        instructions.addAll(OperatorTree.getNewOperator(opetator.getTokenType()));
        instructions.addAll(analyseExpr());
        return instructions;
    }

    private BooleanTree analyseBlooeanExpr() throws CompileError {
        BooleanTree booleanTree=new BooleanTree();
        List<Instruction> instructions=new ArrayList<Instruction>();
        Instruction b;
        instructions.addAll(analyseExpr());
        instructions.addAll(OperatorTree.addAllReset());
        if(nextIf(TokenType.EQ)!=null){
            instructions.addAll(analyseExpr());
            //true 0 false 1 -1
            instructions.add(new Instruction(Operation.cmp_i));

            b=new Instruction(Operation.br_false,(long)-1);
        }else if(nextIf(TokenType.NEQ)!=null){
            instructions.addAll(analyseExpr());
            //true 1 -1 false 0
            instructions.add(new Instruction(Operation.cmp_i));

            b=new Instruction(Operation.br_true,(long)-1);
        }else if(nextIf(TokenType.LT)!=null){
            instructions.addAll(analyseExpr());
            //true -1 false 0 1
            instructions.add(new Instruction(Operation.cmp_i));
            //true 1 false 0
            instructions.add(new Instruction(Operation.set_lt));

            b=new Instruction(Operation.br_true,(long)-1);
        }else if(nextIf(TokenType.GT)!=null){
            instructions.addAll(analyseExpr());
            //true 1 false 0 -1
            instructions.add(new Instruction(Operation.cmp_i));
            //true 1 false 0
            instructions.add(new Instruction(Operation.set_gt));

            b=new Instruction(Operation.br_true,(long)-1);
        }else if(nextIf(TokenType.LE)!=null){
            instructions.addAll(analyseExpr());
            //true -1 0 false 1
            instructions.add(new Instruction(Operation.cmp_i));
            //true 0 false 1
            instructions.add(new Instruction(Operation.set_gt));

            b=new Instruction(Operation.br_false,(long)-1);
        }else if(nextIf(TokenType.GE)!=null){
            instructions.addAll(analyseExpr());
            //true 1 0 false -1
            instructions.add(new Instruction(Operation.cmp_i));
            //true 0 false 1
            instructions.add(new Instruction(Operation.set_lt));

            b=new Instruction(Operation.br_false,(long)-1);
        }else{
            b=new Instruction(Operation.br_true,(long)-1);
        }
        booleanTree.setInstructions(instructions);
        booleanTree.setOffset(b);
        return booleanTree;
    }
    private List<Instruction> analyseAsExpr() throws CompileError {
        Token ty=null;
        List<Instruction> instructions=new ArrayList<>();
        instructions.addAll(OperatorTree.addAllReset());
        while(check(TokenType.AS_KW)){
            expect(TokenType.AS_KW);
            ty=expectTy();
        }
        return instructions;
    }
}
