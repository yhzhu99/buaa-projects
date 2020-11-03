package miniplc0java.error;

import miniplc0java.util.Pos;

public class TokenizeError extends CompileError {
    // auto-generated
    private static final long serialVersionUID = 1L;

    private ErrorCode err;
    private Pos pos;

    public TokenizeError(ErrorCode err, Pos pos) {
        super();
        this.err = err;
        this.pos = pos;
    }

    public TokenizeError(ErrorCode err, Integer row, Integer col) {
        super();
        this.err = err;
        this.pos = new Pos(row, col);
    }

    public ErrorCode getErr() {
        return err;
    }

    public Pos getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Tokenize Error: ").append(err).append(", at: ").append(pos).toString();
    }
}
