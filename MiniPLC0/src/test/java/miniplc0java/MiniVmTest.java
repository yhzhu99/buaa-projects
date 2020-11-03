package miniplc0java;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.vm.MiniVm;

public class MiniVmTest {

    private String RunVm(List<Instruction> instructions) {
        var utf8 = java.nio.charset.StandardCharsets.UTF_8;
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        var output = new PrintStream(ostream, true, utf8);

        var vm = new MiniVm(instructions, output);
        vm.Run();
        output.close();

        var outString = ostream.toString(utf8);
        return outString;
    }

    @Test
    public void testPlus() {
        var instructions = new ArrayList<Instruction>();
        instructions.add(new Instruction(Operation.LIT, 1));
        instructions.add(new Instruction(Operation.LIT, 2));
        instructions.add(new Instruction(Operation.ADD));
        instructions.add(new Instruction(Operation.WRT));

        var outString = RunVm(instructions);

        assertEquals(String.format("1 + 2 should equal to 3", outString), "3\n", outString);
    }

    @Test
    public void testMinus() {
        var instructions = new ArrayList<Instruction>();
        instructions.add(new Instruction(Operation.LIT, 1));
        instructions.add(new Instruction(Operation.LIT, 2));
        instructions.add(new Instruction(Operation.SUB));
        instructions.add(new Instruction(Operation.WRT));

        var outString = RunVm(instructions);

        assertEquals(String.format("1 - 2 should equal to -1", outString), "-1\n", outString);
    }

    @Test
    public void testMult() {
        var instructions = new ArrayList<Instruction>();
        instructions.add(new Instruction(Operation.LIT, 3));
        instructions.add(new Instruction(Operation.LIT, 4));
        instructions.add(new Instruction(Operation.MUL));
        instructions.add(new Instruction(Operation.WRT));

        var outString = RunVm(instructions);

        assertEquals(String.format("3 * 4 should equal to 12", outString), "12\n", outString);
    }

    @Test
    public void testDiv() {
        var instructions = new ArrayList<Instruction>();
        instructions.add(new Instruction(Operation.LIT, 60));
        instructions.add(new Instruction(Operation.LIT, 4));
        instructions.add(new Instruction(Operation.DIV));
        instructions.add(new Instruction(Operation.WRT));

        var outString = RunVm(instructions);

        assertEquals(String.format("60 / 4 should equal to 15", outString), "15\n", outString);
    }

    @Test
    public void testStore() {
        var instructions = new ArrayList<Instruction>();
        instructions.add(new Instruction(Operation.LIT, 1));
        instructions.add(new Instruction(Operation.LIT, 2));
        instructions.add(new Instruction(Operation.LIT, 3));
        instructions.add(new Instruction(Operation.LIT, 4));
        instructions.add(new Instruction(Operation.LIT, 5)); // 1 2 3 4 5
        instructions.add(new Instruction(Operation.STO, 0)); // 5 2 3 4
        instructions.add(new Instruction(Operation.STO, 1)); // 5 4 3
        instructions.add(new Instruction(Operation.WRT));
        instructions.add(new Instruction(Operation.WRT));
        instructions.add(new Instruction(Operation.WRT));

        var outString = RunVm(instructions);

        assertEquals(String.format("Stack top should be 3, 4, 5", outString), "3\n4\n5\n", outString);
    }

    @Test
    public void testLoad() {
        var instructions = new ArrayList<Instruction>();
        instructions.add(new Instruction(Operation.LIT, 1));
        instructions.add(new Instruction(Operation.LIT, 2)); // 1 2
        instructions.add(new Instruction(Operation.LOD, 1)); // 1 2 2
        instructions.add(new Instruction(Operation.LOD, 0)); // 1 2 2 1
        instructions.add(new Instruction(Operation.WRT));
        instructions.add(new Instruction(Operation.WRT));

        var outString = RunVm(instructions);

        assertEquals(String.format("Stack top should be 1, 2", outString), "1\n2\n", outString);
    }
}
