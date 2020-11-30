package miniplc0java.tokenizer;

import miniplc0java.error.TokenizeError;

import java.util.concurrent.atomic.LongAccumulator;

import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

public class Tokenizer {

    private StringIter it;

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了
    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isDigit(peek)) {
           return lexUInt();
        } else if (peek=='\"') {
            return lexSTRING_LITERAL();
        } else if (Character.isAlphabetic(peek)||peek=='_') {
            return lexIdentOrKeyword();
        } else {
           return lexOperatorOrUnknown();
       }
    }

    private Token lexUInt() throws TokenizeError {
        char peek;
        Pos startPos=it.currentPos(),endPos;
        Long value=(long)0;//in1=(long)1;
        while(Character.isDigit(it.peekChar())){
            peek = it.nextChar();
            value=value*(long)10+Long.parseLong(String.valueOf(peek));
       //     if(value>(in1<<31)-1)
       //       throw new TokenizeError(ErrorCode.IntegerOverflow,it.currentPos());
        }
        endPos=it.currentPos();
        return new Token(TokenType.UINT_LITERAL,new Long(value),startPos,endPos);
        // 请填空：
        // 直到查看下一个字符不是数字为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 解析存储的字符串为无符号整数
        // 解析成功则返回无符号整数类型的token，否则返回编译错误
        //
        // Token 的 Value 应填写数字的值
    }  
    private Token lexSTRING_LITERAL() throws TokenizeError {
        Pos startPos=it.currentPos(),endPos;
        StringBuffer v=new StringBuffer();
        char peek;
        String value;
        peek = it.nextChar();
        v.append(peek);
        while(it.peekChar()!='\"'){
            peek = it.nextChar();           
            v.append(peek);
            if(peek=='\\'){
                peek = it.nextChar();
                if(peek=='n'||peek=='r'||peek=='t'||peek=='\\'||peek=='\"'||peek=='\''||peek=='\\'){
                    v.append(peek); 
                }
                else
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            }
 
        }
        peek = it.nextChar();
        v.append(peek);
        endPos=it.currentPos();
        value=new String(v);
        return new Token(TokenType.STRING_LITERAL,value,startPos,endPos);
    }
    private Token lexIdentOrKeyword() throws TokenizeError {
        Pos startPos=it.currentPos(),endPos;
        StringBuffer v=new StringBuffer();
        String value;
        char peek = it.peekChar();
        while(Character.isDigit(peek)||Character.isAlphabetic(peek)||peek=='_'){
             peek=it.nextChar();
             v.append(peek);
             peek = it.peekChar();
        }
        endPos=it.currentPos();
        value=new String(v);
        if(value.equals("fn"))
          return new Token(TokenType.FN_KW,value,startPos,endPos);
        else if(value.equals("let"))
            return new Token(TokenType.LET_KW,value,startPos,endPos);
        else if(value.equals("const"))
            return new Token(TokenType.CONST_KW,value,startPos,endPos);
        else if(value.equals("as"))
            return new Token(TokenType.AS_KW,value,startPos,endPos);
        else if(value.equals("while"))
            return new Token(TokenType.WHILE_KW,value,startPos,endPos);
        else if(value.equals("if"))
            return new Token(TokenType.IF_KW,value,startPos,endPos);
        else if(value.equals("else"))
            return new Token(TokenType.ELSE_KW,value,startPos,endPos);
        else if(value.equals("return"))
            return new Token(TokenType.RETURN_KW,value,startPos,endPos);
        else if(value.equals("int"))
            return new Token(TokenType.INT_KW,value,startPos,endPos);
        else if(value.equals("void"))
            return new Token(TokenType.VOID_KW,value,startPos,endPos);
        
        return new Token(TokenType.IDENT,value,startPos,endPos);
        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        //
        // Token 的 Value 应填写标识符或关键字的字符串
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {
        Pos startPos=it.currentPos();
        switch (it.nextChar()) {
            case '+':
                return new Token(TokenType.PLUS, "+", startPos, it.currentPos());
            case '-':
                if(it.peekChar()=='>'){
                    it.nextChar();
                    return new Token(TokenType.ARROW, "->", startPos, it.currentPos());
                }else
                    return new Token(TokenType.MINUS, "-", startPos, it.currentPos());
            case '*':
                return new Token(TokenType.MUL, "*", startPos, it.currentPos());
            case '/':
                return new Token(TokenType.DIV, "/", startPos, it.currentPos());
            case '=':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.EQ, "==", startPos, it.currentPos());
                }else
                    return new Token(TokenType.ASSIGN, "=", startPos, it.currentPos());
            case '!':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.NEQ, "!=", startPos, it.currentPos());
                }else
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            case '<':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.LE, "<=", startPos, it.currentPos());
                }else
                return new Token(TokenType.LT, "<", startPos, it.currentPos());
            case '>':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.GE, ">=", startPos, it.currentPos());
                }else
                    return new Token(TokenType.GT, ">", startPos, it.currentPos());
            case '(':
                return new Token(TokenType.L_PAREN, "(", startPos, it.currentPos());
            case ')':
                return new Token(TokenType.R_PAREN, ")", startPos, it.currentPos());
            case '{':
                return new Token(TokenType.L_BRACE, "{", startPos, it.currentPos());
            case '}':
                return new Token(TokenType.R_BRACE, "}", startPos, it.currentPos());
            case ',':
                return new Token(TokenType.COMMA, ",", startPos, it.currentPos());
            case ':':
                return new Token(TokenType.COLON, ":", startPos, it.currentPos());
            case ';':
                return new Token(TokenType.SEMICOLON, ";", startPos, it.currentPos());
            default:
                // 不认识这个输入，摸了
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private void skipSpaceCharacters() throws TokenizeError {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
