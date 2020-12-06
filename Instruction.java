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
            case not:
                return String.format("%s", this.opt);
            case neg_i:
                return String.format("%s", this.opt);
            default:
                return "ILL";
        }
    }

    public int getType() {
        switch (this.opt) {
            case loca:
                return 0xa;
            case arga:
                return 0xb;
            case globa:
                return 0xc;
            case add_i:
                return 0x20;
            case sub_i:
                return 0x21;
            case mul_i:
                return 0x22;
            case div_i:
                return 0x23;
            case store_64:
                return 0x17;
            case cmp_i:
                return 0x30;
            case push:
                return 0x1;
            case set_lt:
                return 0x39;
            case br_false:
                return 0x42;
            case br_true:
                return 0x43;
            case set_gt:
                return 0x3a;
            case br:
                return 0x41;
            case ret:
                return 0x49;
            case callname:
                return 0x4a;
            case load_64:
                return 0x13;
            case stackalloc:
                return 0x1a;
            case call:
                return 0x48;
            case not:
                return 0x2e;
            case neg_i:
                return 0x34;
            default:
                return -1;
        }
    }
}
