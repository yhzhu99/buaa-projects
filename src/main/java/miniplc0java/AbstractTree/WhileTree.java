package miniplc0java.AbstractTree;

import miniplc0java.instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

public class WhileTree {
    BooleanTree booleanTree;

    public void setBooleanTree(BooleanTree booleanTree) {
        this.booleanTree = booleanTree;
    }

    public List<Instruction> generate() {
        List<Instruction> instructions=new ArrayList<>();
        return instructions;
    }
}
