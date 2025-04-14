package wasm.misc;

import wasm.disassembly.instructions.Expression;
import wasm.disassembly.instructions.Instr;
import wasm.disassembly.instructions.control.BlockInstr;
import wasm.disassembly.instructions.control.IfElseInstr;

import java.util.List;

public abstract class ExpressionBlockVisitor {

    public abstract void onBlock(List<Instr> block);

    public void visit(Expression expression) {
        visit(expression.getInstructions());
    }

    public void visit(List<Instr> block) {
        if (block == null || block.isEmpty()) {
            return;
        }

        onBlock(block);

        for (final Instr instruction : block) {
            if (instruction instanceof BlockInstr) {
                final BlockInstr blockInstr = (BlockInstr) instruction;

                visit(blockInstr.getBlockInstructions());
            } else if (instruction instanceof IfElseInstr) {
                final IfElseInstr ifElseInstr = (IfElseInstr) instruction;

                visit(ifElseInstr.getIfInstructions());
                visit(ifElseInstr.getElseInstructions());
            }
        }
    }
}
