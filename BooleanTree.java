import java.util.ArrayList;
import java.util.List;

public class BooleanTree {
    List<Instruction> instructions;
    Instruction offset;
    List<Instruction> trueInstructions;
    Instruction jump=new Instruction(Operation.br,(long)0);

    public BooleanTree() {}

    public BooleanTree(Instruction offset) {
        this.offset = offset;
        this.instructions=new ArrayList<>();
        this.trueInstructions=new ArrayList<>();
    }

    public Instruction getOffset() {
        return offset;
    }

    public void setTrueInstructions(List<Instruction> trueInstructions) {
        this.trueInstructions = trueInstructions;
    }

    public void setOffset(Instruction offset) {
        this.offset = offset;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }


    public int size() {
        return instructions.size()+1+trueInstructions.size()+1;
    }

    public void setTrueOffset(long trueOffset) {
        this.offset.setX(trueOffset);
    }

    public void setJump(long jump) {
        this.jump.setX(jump);
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public List<Instruction> getTrueInstructions() {
        return trueInstructions;
    }

    public Instruction getJump() {
        return jump;
    }

    public long getTrueInstructionsSize() {
        return trueInstructions.size();
    }

}
