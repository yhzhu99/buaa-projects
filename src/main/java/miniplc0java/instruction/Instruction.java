package miniplc0java.instruction;

import java.util.Objects;

public class Instruction {
    private Operation opt;
    private Long x;
    private int length;
    public Instruction(Operation opt) {
        this.opt = opt;
        length=0;
    }

    public Instruction(Operation opt, Long x) {
        this.opt = opt;
        this.x = x;
        length=1;
    }

    public int getLength() {
        return length;
    }

    public Long getX() {
        return x;
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

    public void setX(Long x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return switch (this.opt) {
            case loca -> String.format("%s  %s", this.opt, this.x);
            case arga -> String.format("%s  %s", this.opt, this.x);
            case globa -> String.format("%s  %s", this.opt, this.x);
            case add_i -> String.format("%s", this.opt);
            case sub_i -> String.format("%s", this.opt);
            case mul_i -> String.format("%s", this.opt);
            case div_i -> String.format("%s", this.opt);
            case store_64 -> String.format("%s", this.opt);
            case cmp_i -> String.format("%s", this.opt);
            case push -> String.format("%s  %s", this.opt, this.x);
            case set_lt -> String.format("%s", this.opt);
            case br_false -> String.format("%s  %s", this.opt, this.x);
            case br_true -> String.format("%s  %s", this.opt, this.x);
            case set_gt -> String.format("%s", this.opt);
            case br -> String.format("%s  %s", this.opt, this.x);
            case ret -> String.format("%s", this.opt);
            case callname -> String.format("%s  %s", this.opt, this.x);
            case load_64 -> String.format("%s", this.opt);
            case stackalloc -> String.format("%s  %s", this.opt, this.x);
            case call -> String.format("%s  %s", this.opt, this.x);
            case not -> String.format("%s", this.opt);
            case neg_i -> String.format("%s", this.opt);
            default -> "ILL";
        };
    }

    public int getType() {
        return switch (this.opt) {
            case loca -> 0xa;
            case arga -> 0xb;
            case globa -> 0xc;
            case add_i -> 0x20;
            case sub_i -> 0x21;
            case mul_i -> 0x22;
            case div_i -> 0x23;
            case store_64 -> 0x17;
            case cmp_i -> 0x30;
            case push -> 0x1;
            case set_lt -> 0x39;
            case br_false -> 0x42;
            case br_true -> 0x43;
            case set_gt -> 0x3a;
            case br -> 0x41;
            case ret -> 0x49;
            case callname -> 0x4a;
            case load_64 -> 0x13;
            case stackalloc -> 0x1a;
            case call -> 0x48;
            case not -> 0x2e;
            case neg_i -> 0x34;
            default -> -1;
        };
    }
}
