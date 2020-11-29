package miniplc0java.AbstractTree;

import miniplc0java.instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

public class ConditionTree {
    List<BooleanTree> booleanTrees;

    public ConditionTree() {
        this.booleanTrees=new ArrayList<>();
    }

    public void add(BooleanTree booleanTree) {
        this.booleanTrees.add(booleanTree);
    }

    public List<Instruction> generate() {
        List<Instruction> instructions=new ArrayList<>();
        return instructions;
    }
}
