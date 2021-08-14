package miniplc0java.vm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import miniplc0java.instruction.Instruction;

public class MiniVm {
    private List<Instruction> instructions;
    private PrintStream out;

    /**
     * @param instructions
     * @param out
     */
    public MiniVm(List<Instruction> instructions, PrintStream out) {
        this.instructions = instructions;
        this.out = out;
    }

    public MiniVm(List<Instruction> instructions) {
        this.instructions = instructions;
        this.out = System.out;
    }

    private ArrayList<Integer> stack = new ArrayList<>();

    private int ip;

    public void Run() {
        ip = 0;
        while (ip < instructions.size()) {
            var inst = instructions.get(ip);
            RunStep(inst);
            ip++;
        }
    }

    private Integer pop() {
        var val = this.stack.get(this.stack.size() - 1);
        this.stack.remove(this.stack.size() - 1);
        return val;
    }

    private void push(Integer i) {
        this.stack.add(i);
    }

    private void RunStep(Instruction inst) {
        switch (inst.getOpt()) {
            case ADD: {
                var a = pop();
                var b = pop();
                push(a + b);
            }
                break;
            case DIV: {
                var b = pop();
                var a = pop();
                push(a / b);
            }
                break;
            case ILL: {
                throw new Error("Illegal instruction");
            }
            case LIT: {
                push(inst.getX());
            }
                break;
            case LOD: {
                var x = stack.get(inst.getX());
                push(x);
            }
                break;
            case MUL: {
                var b = pop();
                var a = pop();
                push(a * b);
            }
                break;
            case STO: {
                var x = pop();
                stack.set(inst.getX(), x);
            }
                break;
            case SUB: {
                var b = pop();
                var a = pop();
                push(a - b);
            }
                break;
            case WRT: {
                var b = pop();
                out.printf("%d\n", b);
            }
                break;
            default:
                break;

        }
    }
}
