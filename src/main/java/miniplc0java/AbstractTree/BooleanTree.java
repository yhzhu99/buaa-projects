package miniplc0java.AbstractTree;

import miniplc0java.instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

public class BooleanTree {
    List<Instruction> instructions;
    Instruction offset;
    List<Instruction> trueInstructions;
    public BooleanTree() {
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


}
