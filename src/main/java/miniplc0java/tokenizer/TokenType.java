package miniplc0java.tokenizer;

public enum TokenType {
    /** 空 */
    None,

    /** 关键字 */
    /** fn */
    FN_KW,
    /** let */
    LET_KW,
    /** const */
    CONST_KW,
    /** as */
    AS_KW,
    /** while */
    WHILE_KW,                        
    /** if */
    IF_KW,
    /** else */
    ELSE_KW,
    /** return */
    RETRUN_KW,
    /** break */
    BREAK_KW,
    /** continue */
    CONTINUE_KW,
    /** int */
    INT_KW,
    /** double */
    DOUBLE_KW,
    /** void */
    VOID_KW,

    /** 字面量 */
    /** 无符号整数 */
    UNIT_LITERAL,
    /** 字符串常量 */
    STRING_LITERAL,
    /** 字符常量 */
    CHAR_LITERAL,

   /** 标识符 */
    IDENT,
 
    /** 运算符 */
    /** 加号 */
    PLUS,
    /** 减号 */
    MINUS,
    /** 负号 */
    NEG,
    /** 乘号 */
    MUL,
    /** 除号 */
    DIV,
    /** 等号 */
    ASSIGN,
    /** 等于 */
    EQ,
    /** 不等于 */
    NEQ,
    /** 小于 */
    LT,
    /** 大于 */
    GT,
    /** 小于等于 */
    LE,
    /** 大于等于 */
    GE,
    /** 左括号 */
    L_PAREN,
    /** 右括号 */
    R_PAREN,
    /** 左大括号 */
    L_BRACE,
    /** 右大括号 */
    R_BRACE,
    /** 箭头 */
    ARROW,
    /** 逗号 */
    COMMA,
    /** 冒号 */
    COLON,
    /** 分号 */
    SEMICOLON,
    /** 文件尾 */
    EOF;

    @Override
    public String toString() {
        return switch (this) {
            case None -> "NullToken";
            case FN_KW -> "FN_KW";
            case LET_KW -> "LET_KW";
            case CONST_KW -> "CONST_KW";
            case AS_KW -> "AS_KW";
            case WHILE_KW -> "WHILE_KW";
            case IF_KW -> "IF_KW";
            case ELSE_KW -> "ELSE_KW";
            case RETRUN_KW -> "RETRUN_KW";
            case BREAK_KW -> "BREAK_KW";
            case CONTINUE_KW -> "CONTINUE_KW";
            case INT_KW -> "INT_KW";
            case DOUBLE_KW -> "DOUBLE_KW";
            case VOID_KW -> "VOID_KW";
            case UNIT_LITERAL -> "UNIT_LITERAL";
            case STRING_LITERAL -> "STRING_LITERAL";
            case CHAR_LITERAL -> "CHAR_LITERAL";
            case IDENT -> "IDENT";
            case PLUS -> "PLUS";
            case MINUS -> "MINUS";
            case MUL -> "MUL";
            case DIV -> "DIV";
            case ASSIGN -> "ASSIGN";
            case EQ -> "EQ";
            case NEQ -> "NEQ";
            case LT -> "LT";
            case GT -> "GT";
            case LE -> "LE";
            case GE -> "GE";
            case L_PAREN -> "L_PAREN";
            case R_PAREN -> "R_PAREN";
            case L_BRACE -> "L_BRACE";
            case R_BRACE -> "R_BRACE";
            case ARROW -> "ARROW";
            case COMMA -> "COMMA";
            case COLON -> "COLON";
            case SEMICOLON -> "SEMICOLON";
            case EOF -> "EOF";
            default -> "InvalidToken";
        };
    }
}
