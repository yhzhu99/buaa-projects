package miniplc0java.ast;

import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.TokenType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OperatorTree {
    public static List<Integer> stack=new ArrayList<>();
    //+ - * / ( )
    static int priority[][]={
            {1,1,-1,-1,-1,1},
            {1,1,-1,-1,-1,1},
            {1,1,1,1,-1,1},
            {1,1,1,1,-1,1},
            {-1,-1,-1,-1,-1,100},
            {1,1,1,1,0,0}
    };

    static int getInt(TokenType tokenType){
        if(tokenType==TokenType.PLUS){
            return 0;
        }else if(tokenType==TokenType.MINUS){
            return 1;
        }else if(tokenType==TokenType.MUL){
            return 2;
        }else if(tokenType==TokenType.DIV){
            return 3;
        }else if(tokenType==TokenType.L_PAREN){
            return 4;
        }else if(tokenType==TokenType.R_PAREN){
            return 5;
        }
        return -1;
    }

    private static Instruction addInstruction(int top) {
        if(top==0){
            return new Instruction(Operation.add_i);
        }else if(top==1){
            return new Instruction(Operation.sub_i);
        }else if(top==2){
            return new Instruction(Operation.mul_i);
        }else{
            return new Instruction(Operation.div_i);
        }
    }

    public static List<Instruction> addAllReset(){
        List<Instruction> instructions=new ArrayList<>();
        for(int i=stack.size()-1;i>=0;i--){
            instructions.add(addInstruction(stack.get(i)));
        }
        return instructions;
    }

    public static List<Instruction> getNewOperator(TokenType tokenType){
        List<Instruction> instructions=new ArrayList<>();
        int next=getInt(tokenType);
        if (stack.size()<1){
            stack.add(next);
            return instructions;
        }
        int top=stack.get(stack.size()-1);

        while (priority[top][next]>0){
            stack.remove(stack.size()-1);
            if(top==0){
                instructions.add(new Instruction(Operation.add_i));
            }else if(top==1){
                instructions.add(new Instruction(Operation.sub_i));
            }else if(top==2){
                instructions.add(new Instruction(Operation.mul_i));
            }else if(top==3){
                instructions.add(new Instruction(Operation.div_i));
            }
            if (stack.size()==0)
                break;
            else if(top==4)
                return instructions;
            top=stack.get(stack.size()-1);
        }
        stack.add(next);
        return instructions;
    }


}
