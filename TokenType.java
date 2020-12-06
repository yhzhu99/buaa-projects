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
        switch (this) {
            case None:
                return "NullToken";
            case FN_KW:
                return "fn";
            case LET_KW:
                return "let";
            case CONST_KW:
                return "const";
            case AS_KW:
                return "as";
            case WHILE_KW:
                return "while";
            case IF_KW:
                return "if";
            case ELSE_KW:
                return "else";
            case RETRUN_KW:
                return "return";
            case BREAK_KW:
                return "break";
            case CONTINUE_KW:
                return "continue";
            case INT_KW:
                return "int";
            case DOUBLE_KW:
                return "double";
            case VOID_KW:
                return "void";
            case UNIT_LITERAL:
                return "UNIT_LITERAL";
            case STRING_LITERAL:
                return "STRING_LITERAL";
            case CHAR_LITERAL:
                return "CHAR_LITERAL";
            case IDENT:
                return "IDENT";
            case PLUS:
                return "PLUS";
            case MINUS:
                return "MINUS";
            case MUL:
                return "MUL";
            case DIV:
                return "DIV";
            case ASSIGN:
                return "ASSIGN";
            case EQ:
                return "EQ";
            case NEQ:
                return "NEQ";
            case LT:
                return "LT";
            case GT:
                return "GT";
            case LE:
                return "LE";
            case GE:
                return "GE";
            case L_PAREN:
                return "L_PAREN";
            case R_PAREN:
                return "R_PAREN";
            case L_BRACE:
                return "L_BRACE";
            case R_BRACE:
                return "R_BRACE";
            case ARROW:
                return "ARROW";
            case COMMA:
                return "COMMA";
            case COLON:
                return "COLON";
            case SEMICOLON:
                return "SEMICOLON";
            case EOF:
                return "EOF";
            default:
                return "InvalidToken";
        }
    }
}
