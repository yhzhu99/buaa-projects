package miniplc0java.analyser;

import miniplc0java.tokenizer.TokenType;

public class SymbolEntry {
    String name;
    NameType nameType;
    TokenType tokenType;
    int deep;
    boolean isConstant;
    boolean isInitialized;
    int stackOffset;
    Long off=(long)-1;
    /**
     * @param isConstant
     * @param isDeclared
     * @param stackOffset
     */
    public SymbolEntry(String name,NameType nameType,TokenType tokenType,int deep,boolean isConstant, boolean isDeclared, int stackOffset) {
        this.name=name;
        //proc params var
        this.nameType=nameType;
        this.tokenType=tokenType;
        this.deep=deep;
        this.isConstant = isConstant;
        this.isInitialized = isDeclared;
        this.stackOffset = stackOffset;
    }

    public String getName() {
        return name;
    }

    public NameType getNameType() {
        return nameType;
    }

    /**
     * @return the stackOffset
     */
    public int getStackOffset() {
        return stackOffset;
    }

    /**
     * @return the isConstant
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * @return the isInitialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * @param isConstant the isConstant to set
     */
    public void setConstant(boolean isConstant) {
        this.isConstant = isConstant;
    }

    /**
     * @param isInitialized the isInitialized to set
     */
    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    /**
     * @param stackOffset the stackOffset to set
     */
    public void setStackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
    }

    public void setOff(long off) {
        this.off = off;
    }

    public long getOff() {
        return off;
    }

    public int getDeep() {
        return deep;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
}
