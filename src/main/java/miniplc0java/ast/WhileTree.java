package miniplc0java.ast;

import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;

import java.util.ArrayList;
import java.util.List;

public class WhileTree {
    BooleanTree booleanTree;
    public void setBooleanTree(BooleanTree booleanTree) {
        this.booleanTree = booleanTree;
    }

    public List<Instruction> generate() {
        List<Instruction> instructions=new ArrayList<>();
        booleanTree.setTrueOffset(1);
        booleanTree.setJump(-(booleanTree.size()+1));
        instructions.addAll(booleanTree.getInstructions());
        instructions.add(booleanTree.getOffset());
        instructions.add(new Instruction(Operation.br,(long)(booleanTree.size()-instructions.size())));
        instructions.addAll(booleanTree.getTrueInstructions());
        instructions.add(booleanTree.getJump());
        return instructions;
    }
}
