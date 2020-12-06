import java.util.ArrayList;
import java.util.List;

public class OperatorTree {
    public static List<Integer> stack=new ArrayList<>();
    //+ - * / ( ) < > <= >= == != -
    static int priority[][]={
            {1,1,-1,-1,-1,1,   1,1,1,1,1,1,  -1},
            {1,1,-1,-1,-1,1,  1,1,1,1,1,1,   -1},
            {1,1,1,1,-1,1,   1,1,1,1,1,1,    -1},
            {1,1,1,1,-1,1,   1,1,1,1,1,1,   -1},

            {-1,-1,-1,-1,-1,100,   -1,-1,-1,-1,-1,-1,   -1},
            {-1,-1,-1,-1,0,0   ,    -1,-1,-1,-1,-1,-1   ,-1},

            {-1,-1,-1,-1,-1,1,  1,1,1,1,1,1,      -1},
            {-1,-1,-1,-1,-1,1,  1,1,1,1,1,1,      -1},
            {-1,-1,-1,-1,-1,1,  1,1,1,1,1,1,      -1},
            {-1,-1,-1,-1,-1,1,  1,1,1,1,1,1,      -1},
            {-1,-1,-1,-1,-1,1,  1,1,1,1,1,1,      -1},
            {-1,-1,-1,-1,-1,1,  1,1,1,1,1,1,      -1},

            {1,1,1,1,-1,1,     1,1,1,1,1,1    ,-1}

    };

    static int getInt(TokenType tokenType){
        if(tokenType== TokenType.PLUS){
            return 0;
        }else if(tokenType== TokenType.MINUS){
            return 1;
        }else if(tokenType== TokenType.MUL){
            return 2;
        }else if(tokenType== TokenType.DIV){
            return 3;
        }else if(tokenType== TokenType.L_PAREN){
            return 4;
        }else if(tokenType== TokenType.R_PAREN){
            return 5;
        }
        else if(tokenType== TokenType.LT){
            return 6;
        }
        else if(tokenType== TokenType.GT){
            return 7;
        }
        else if(tokenType== TokenType.LE){
            return 8;
        }
        else if(tokenType== TokenType.GE){
            return 9;
        }
        else if(tokenType== TokenType.EQ){
            return 10;
        }
        else if(tokenType== TokenType.NEQ){
            return 11;
        }else if(tokenType== TokenType.NEG){
            return 12;
        }
        return -1;
    }

    private static List<Instruction> addInstruction(int top) {
        List<Instruction> instructions=new ArrayList<>();
        if(top==0){
            instructions.add(new Instruction(Operation.add_i));
        }else if(top==1){
            instructions.add(new Instruction(Operation.sub_i));
        }else if(top==2){
            instructions.add(new Instruction(Operation.mul_i)) ;
        }else if (top==3){
            instructions.add(new Instruction(Operation.div_i)) ;
        }
        else if (top==6){
            //true -1 false 0 1
            instructions.add(new Instruction(Operation.cmp_i));
            //true 1 false 0
            instructions.add(new Instruction(Operation.set_lt));
        }else if (top==7){
            //true 1 false 0 -1
            instructions.add(new Instruction(Operation.cmp_i));
            //true 1 false 0
            instructions.add(new Instruction(Operation.set_gt));
        }else if (top==8){
            //true -1 0 false 1
            instructions.add(new Instruction(Operation.cmp_i));

            //true 0 false 1
            instructions.add(new Instruction(Operation.set_gt));

            //true 1 false 0
            instructions.add(new Instruction(Operation.not));
        }else if (top==9){
            //true 1 0 false -1
            instructions.add(new Instruction(Operation.cmp_i));
            //true 0 false 1
            instructions.add(new Instruction(Operation.set_lt));

            //true 1 false 0
            instructions.add(new Instruction(Operation.not));
        }else if (top==10){
            //true 0 false 1 -1
            instructions.add(new Instruction(Operation.cmp_i));

            //true 1 false 0
            instructions.add(new Instruction(Operation.not));
        }else if (top==11){
            //true 1 -1 false 0
            instructions.add(new Instruction(Operation.cmp_i));
        }else if (top==12){
            //true 1 -1 false 0
            instructions.add(new Instruction(Operation.neg_i));
        }
        return instructions;
    }

    public static List<Instruction> addAllReset(){
        List<Instruction> instructions=new ArrayList<>();
        for(int i=stack.size()-1;i>=0;i--){
            instructions.addAll(addInstruction(stack.get(i)));
            stack.remove(i);
        }
        return instructions;
    }

    public static List<Integer> getStack() {
        return stack;
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
            instructions.addAll(addInstruction(top));
            if(top==4)
                return instructions;
            else if (stack.size()==0)
                break;
            top=stack.get(stack.size()-1);
        }
        stack.add(next);
        return instructions;
    }


}
