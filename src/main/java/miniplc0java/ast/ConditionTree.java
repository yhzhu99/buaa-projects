package miniplc0java.ast;

import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;

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
        long off=0;
        if(booleanTrees.get(booleanTrees.size()-1).getOffset().getOpt()!= Operation.br){
            booleanTrees.add(new BooleanTree(new Instruction(Operation.br,(long)0)));
        }
        off=booleanTrees.get(booleanTrees.size()-1).size();
        for(int i=booleanTrees.size()-2;i>=0;i--){
            BooleanTree booleanTree=booleanTrees.get(i);
            booleanTree.setTrueOffset(off);
            off+=booleanTree.size();
        }
        long total=off;
        off=0;
        for(int i=booleanTrees.size()-1;i>=0;i--){
            BooleanTree booleanTree=booleanTrees.get(i);
            off+=booleanTree.size();
            booleanTree.setJump(total-off);
        }
        List<Instruction> instructions=new ArrayList<>();
        instructions.addAll(deep(0));
        return instructions;
    }

    public List<Instruction> deep(int i){
        List<Instruction> instructions=new ArrayList<>();
        if(i==booleanTrees.size())
            return instructions;
        BooleanTree booleanTree=booleanTrees.get(i);
        instructions.addAll(booleanTree.getInstructions());
        instructions.add(booleanTree.getOffset());
        instructions.addAll(deep(i+1));
        instructions.addAll(booleanTree.getTrueInstructions());
        instructions.add(booleanTree.getJump());
        return instructions;
    }
}
