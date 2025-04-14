package wasm.disassembly.instructions.misc;

import wasm.disassembly.instructions.Instr;
import wasm.disassembly.instructions.InstrType;
import wasm.disassembly.modules.Module;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SingleByteInstr extends Instr {

    private int constValue;

    public SingleByteInstr(BufferedInputStream in, InstrType instrType, Module module) throws IOException {
        super(instrType);
        constValue = in.read();
    }

    @Override
    protected void assemble2(OutputStream out) throws IOException {
        out.write(constValue);
    }

    public int getConstValue() {
        return constValue;
    }

    public void setConstValue(int constValue) {
        this.constValue = constValue;
    }
}