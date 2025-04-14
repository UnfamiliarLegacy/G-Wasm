package wasm.disassembly.instructions;

import wasm.disassembly.InvalidOpCodeException;
import wasm.disassembly.instructions.control.*;
import wasm.disassembly.instructions.memory.Mem0Instr;
import wasm.disassembly.instructions.memory.MemInstr;
import wasm.disassembly.instructions.misc.SingleByteInstr;
import wasm.disassembly.instructions.misc.ZeroByteInstr;
import wasm.disassembly.instructions.numeric.*;
import wasm.disassembly.instructions.specific.TableGetInstr;
import wasm.disassembly.instructions.specific.TableSetInstr;
import wasm.disassembly.instructions.variable.GlobalVariableInstr;
import wasm.disassembly.instructions.variable.LocalVariableInstr;
import wasm.disassembly.modules.Module;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InstrFactory {

    public static InstrType disassembleType(BufferedInputStream in) throws IOException, InvalidOpCodeException {
        final int val = in.read();
        final InstrType type = InstrType.from_val(val);
        if (type == null) {
            throw new InvalidOpCodeException(String.format("Invalid instruction prefix 0x%x", val));
        }

        return type;
    }

    private final static Map<InstrType, InstrSupplier> map;
    static {
        map = new HashMap<>();

        // control instructions
        map.put(InstrType.UNREACHABLE, ZeroByteInstr::new);
        map.put(InstrType.NOP, ZeroByteInstr::new);

        map.put(InstrType.BLOCK, BlockInstr::new);
        map.put(InstrType.LOOP, BlockInstr::new);
        map.put(InstrType.IF, IfElseInstr::new);

        map.put(InstrType.BR, BranchInstr::new);
        map.put(InstrType.BR_IF, BranchInstr::new);
        map.put(InstrType.BR_TABLE, BranchTableInstr::new);

        map.put(InstrType.RETURN, ZeroByteInstr::new);
        map.put(InstrType.CALL, CallInstr::new);
        map.put(InstrType.CALL_INDIRECT, CallIndirectInstr::new);

        map.put(InstrType.REF_NULL, SingleByteInstr::new);

        map.put(InstrType.TABLE_GET, TableGetInstr::new);
        map.put(InstrType.TABLE_SET, TableSetInstr::new);

        // parametric instructions
        map.put(InstrType.DROP, ZeroByteInstr::new);
        map.put(InstrType.SELECT, ZeroByteInstr::new);

        // variable instructions
        for (int i = 0x20; i <= 0x22; i++) map.put(InstrType.from_val(i), LocalVariableInstr::new);
        for (int i = 0x23; i <= 0x24; i++) map.put(InstrType.from_val(i), GlobalVariableInstr::new);

        // memory instructions
        for (int i = 0x28; i <= 0x3E; i++) map.put(InstrType.from_val(i), MemInstr::new);
        for (int i = 0x3F; i <= 0x40; i++) map.put(InstrType.from_val(i), Mem0Instr::new);

        // numeric instructions
        map.put(InstrType.I32_CONST, NumericI32ConstInstr::new);
        map.put(InstrType.I64_CONST, NumericI64ConstInstr::new);
        map.put(InstrType.F32_CONST, NumericF32ConstInstr::new);
        map.put(InstrType.F64_CONST, NumericF64ConstInstr::new);
        for (int i = 0x45; i <= 0xC4; i++) map.put(InstrType.from_val(i), NumericInstr::new);
        map.put(InstrType.IXX_TRUNC_SAT_FXX_SU, TruncSatInstr::new);
    }

    public static Instr disassemble(BufferedInputStream in, InstrType instrType, Module module) throws InvalidOpCodeException, IOException {
        if (instrType == InstrType.END || instrType == InstrType.ELSE) {
            throw new InvalidOpCodeException("Instruction invalid as a standalone instruction");
        }

        if (instrType == null) {
            throw new InvalidOpCodeException("Invalid instruction prefix");
        }

        final InstrSupplier supplier = map.get(instrType);

        if (supplier == null) {
            throw new InvalidOpCodeException(String.format("Missing instruction supplier for %s", instrType));
        }

        return supplier.get(in, instrType, module);
    }

}
