package miniplc0java.instruction;

import java.util.Objects;

public class Instruction {
    private Operation opt;
    Long x;

    public Instruction(Operation opt) {
        this.opt = opt;
        this.x = (long)0;
    }

    public Instruction(Operation opt, Long x) {
        this.opt = opt;
        this.x = x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return opt == that.opt && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opt, x);
    }

    public Operation getOpt() {
        return opt;
    }

    public void setOpt(Operation opt) {
        this.opt = opt;
    }


    @Override
    public String toString() {
        switch (this.opt) {
            case loca:
                return String.format("%s  %s", this.opt,this.x);
            case arga:
                return String.format("%s  %s", this.opt,this.x);
            case globa:
                return String.format("%s  %s", this.opt,this.x);
            case add_i:
                return String.format("%s", this.opt);
            case sub_i:
                return String.format("%s", this.opt);
            case mul_i:
                return String.format("%s", this.opt);
            case div_i:
                return String.format("%s", this.opt);
            case store_64:
                return String.format("%s", this.opt);
            case cmp_i:
                return String.format("%s", this.opt);
            case push:
                return String.format("%s  %s", this.opt,this.x);
            case set_lt:
                return String.format("%s", this.opt);
            case br_false:
                return String.format("%s  %s", this.opt,this.x);
            case br_true:
                return String.format("%s  %s", this.opt,this.x);
            case set_gt:
                return String.format("%s", this.opt);
            case br:
                return String.format("%s  %s", this.opt,this.x);
            case ret:
                return String.format("%s", this.opt);
            case callname:
                return String.format("%s  %s", this.opt,this.x);
            case load_64:
                return String.format("%s", this.opt);
            case stackalloc:
                return String.format("%s  %s", this.opt,this.x);
            case call:
                return String.format("%s  %s", this.opt,this.x);
            default:
                return "ILL";
        }
    }
}
