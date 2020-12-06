import java.util.ArrayList;
import java.util.List;

public class ExpectedTokenError extends CompileError {
    private static final long serialVersionUID = 1L;

    List<TokenType> expecTokenType;
    Token token;

    @Override
    public ErrorCode getErr() {
        return ErrorCode.ExpectedToken;
    }

    @Override
    public Pos getPos() {
        return token.getStartPos();
    }

    /**
     * @param expectedTokenType
     * @param token
     */
    public ExpectedTokenError(TokenType expectedTokenType, Token token) {
        this.expecTokenType = new ArrayList<>();
        this.expecTokenType.add(expectedTokenType);
        this.token = token;
    }

    /**
     * @param expectedTokenType
     * @param token
     */
    public ExpectedTokenError(List<TokenType> expectedTokenType, Token token) {
        this.expecTokenType = expectedTokenType;
        this.token = token;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Analyse error. Expected ").append(expecTokenType).append(" at ")
                .append(token.getStartPos()).append("got: ").append(token.toStringAlt()).toString();
    }
}
