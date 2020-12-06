import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhileTree {
    BooleanTree booleanTree;
    public void setBooleanTree(BooleanTree booleanTree) {
        this.booleanTree = booleanTree;
    }

    public List<Instruction> generate() {
        int off=0;
        List<Instruction> instructions=new ArrayList<>();
        off+=1;
        instructions.add(new Instruction(Operation.br,(long)0));

        off+=booleanTree.getInstructions().size();
        instructions.addAll(booleanTree.getInstructions());

        off+=1;
        booleanTree.setTrueOffset(1);
        instructions.add(booleanTree.getOffset());

        off+=1;
        instructions.add(new Instruction(Operation.br,(long)booleanTree.getTrueInstructions().size()+1));

        instructions.addAll(check_continue_break(booleanTree.getTrueInstructions(),off));

        booleanTree.setJump(-(booleanTree.size()+1));
        instructions.add(booleanTree.getJump());
        return instructions;
    }

    private List<Instruction> check_continue_break(List<Instruction> trueInstructions, int off) {
        int total=trueInstructions.size();
        int i=0;
        for (Instruction instruction:trueInstructions) {
            i++;
            if (instruction.getOpt()==Operation.continue_kw){
                instruction.setOpt(Operation.br);
                instruction.setX((long)-(off+i));
            }else if (instruction.getOpt()==Operation.break_kw){
                instruction.setOpt(Operation.br);
                instruction.setX((long)total-i+1);
            }
        }
        return trueInstructions;
    }
}
